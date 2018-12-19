; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(ns dda.pallet.dda-smeagol-crate.infra.smeagol
  (:require
    [schema.core :as s]
    [clojure.string :as string]
    [pallet.crate :as crate]
    [pallet.crate.initd] ;; service-supervisor impl
    [pallet.crate.service :refer [service-supervisor-config service-supervisor]]
    [pallet.script.lib :refer [etc-init]]
    [selmer.parser :as selmer]
    [pallet.actions :as actions]
    [clojure.tools.logging :as logging]
    [dda.pallet.dda-serverspec-crate.infra :as fact]))

(def SmeagolPasswdUser
  {:admin s/Bool
   :email s/Str
   :password s/Str})

(def SmeagolPasswd
  {s/Keyword SmeagolPasswdUser})

(def SmeagolUberjar {:path s/Str :url s/Str :size s/Int}) ;TODO: Review jem 2018_12_19: lets use here a hash (sha256 / sha512) instead of size
(def SmeagolEnv {:env s/Str :value s/Str})

; TODO: simplify smeagol-parent-dir and smeagol-dir to one directory
(def SmeagolInfra
  {:passwd SmeagolPasswd
   :owner s/Str
   :uberjar SmeagolUberjar
   :env {s/Keyword SmeagolEnv}})

(s/defn create-smeagol-config
  [config :- SmeagolInfra]
  (let [{:keys [env owner passwd]} config
        {:keys [config-edn passwd content-dir]} env
        config-edn-path (:value config-edn)]
    (actions/remote-file
     config-edn-path
     :literal true
     :owner owner
     :mode "755"
     :content (selmer/render-file
               "config_edn.template"
               {:content-dir (:value content-dir)
                :passwd-path (:value passwd)}))))

(s/defn configure-smeagol-users
  [env :- SmeagolEnv
   owner :- s/Str
   passwd :- SmeagolPasswd]
  (actions/remote-file
   (:value env)
   :literal true
   :owner owner
   :mode "755"
   :content passwd))

(s/defn path-to-keyword :- s/Keyword
  [path :- s/Str]
  (keyword (string/replace path #"[/]" "_")))

; TODO where should it come from?
(def file-fact-keyword :dda.pallet.dda-serverspec-crate.infra.fact.file/file)

(s/defn download-uberjar
  [owner :- s/Str
   uberjar :- SmeagolUberjar]
  (let [{:keys [path url size]} uberjar
        all-facts (crate/get-settings
                   fact/fact-facility
                   {:instance-id (crate/target-node)})
        file-fact (file-fact-keyword all-facts)
        fact-path (path-to-keyword path)]
    ;(actions/plan-when (let [{:keys [fact-exist? fact-size-in-bytes] :as actual}
    ;                         (fact-path (:out @file-fact))
                         ;; (logging/info (pr-str {:actual actual :expected uberjar}))
    ;                     (not (and fact-exist? (= fact-size-in-bytes size)))
       (actions/packages :aptitude ["curl"])
       (actions/directory "/usr/local/lib/smeagol"
                          :owner owner
                          :group "users"
                          :mode "755")
       (actions/remote-file "/usr/local/lib/smeagol/smeagol-standalone.jar"
                            :url url
                            :owner owner)))

(s/defn ->env-str
  [envs :- [SmeagolEnv]]
  (string/join " "
               (map #(str (:env %) "=" (:value %)) envs)))

(s/defn initd-script
  [env :- {s/Keyword SmeagolEnv}
   uberjar :- SmeagolUberjar]
  (let [env-str (-> env vals ->env-str)
        jar-path (:path uberjar)]
    (actions/remote-file
     (str (etc-init) "/" "smeagol")
     :literal true
     :owner "root"
     :mode "755"
     :content (selmer/render-file
               "smeagol-initd.template"
               {:env-str env-str
                :jar-path jar-path}))))

(s/defn smeagol-service
  [env uberjar]
  (do
    (service-supervisor ;-config
     :initd
     {:service-name "smeagol"}
     ;; TODO leave only `service-supervisor-config`, place action in proper phase
     {:action :restart})))

;; TODO ugly imperative style
(s/defn install-smeagol
  [config :- SmeagolInfra]
  (let [{:keys [uberjar passwd owner env]} config]
    ;; TODO shared :owner like `with-action-options`?!
    (download-uberjar owner uberjar)
    (initd-script env uberjar)
    (smeagol-service (vals env) uberjar)))

(s/defn configure-smeagol
  [config :- SmeagolInfra]
  (let [{:keys [uberjar passwd owner env]} config]
    ;; TODO shared :owner like `with-action-options`?!
    (create-smeagol-config config)
    (configure-smeagol-users (:passwd env) owner passwd)))
