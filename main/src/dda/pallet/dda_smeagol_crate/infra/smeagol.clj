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
    [dda.pallet.dda-serverspec-crate.infra :as fact]
    [dda.pallet.dda-smeagol-crate.infra.schema :as schema]))

(s/defn create-smeagol-config
  [config :- schema/SmeagolInfra]
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

(s/defn create-passwd
  [env :- schema/SmeagolEnv
   owner :- s/Str
   passwd :- schema/SmeagolPasswd]
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
   uberjar :- schema/SmeagolUberjar]
  (let [{:keys [path url size]} uberjar
        all-facts (crate/get-settings
                   fact/fact-facility
                   {:instance-id (crate/target-node)})
        file-fact (file-fact-keyword all-facts)
        fact-path (path-to-keyword path
    ;(actions/plan-when (let [{:keys [fact-exist? fact-size-in-bytes] :as actual}
    ;                         (fact-path (:out @file-fact))
                         ;; (logging/info (pr-str {:actual actual :expected uberjar}))
    ;                     (not (and fact-exist? (= fact-size-in-bytes size)))
                       (actions/remote-file path
                                            :url url
                                            :owner owner))]))

(s/defn ->env-str
  [envs :- [schema/SmeagolEnv]]
  (string/join " "
               (map #(str (:env %) "=" (:value %)) envs)))

(s/defn initd-script
  [env :- {s/Keyword schema/SmeagolEnv}
   uberjar :- schema/SmeagolUberjar]
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
  [config :- schema/SmeagolInfra]
  (let [{:keys [uberjar passwd owner env]} config]
    ;; TODO shared :owner like `with-action-options`?!
    (create-smeagol-config config)
    (create-passwd (:passwd env) owner passwd)
    (download-uberjar owner uberjar)
    (initd-script env uberjar)
    (smeagol-service (vals env) uberjar)))
