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
    [pallet.script.lib :refer [etc-init config-root]]
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

(def Path s/Str)

(def SmeagolUberjar {:path Path :url s/Str :md5-url s/Str})

(def SmeagolConfigs
  {:passwd Path
   :config-edn Path})

(def SmeagolInfra
  {:passwd SmeagolPasswd
   :owner s/Str
   :content-dir s/Str
   :uberjar SmeagolUberjar
   :port s/Num
   :configs SmeagolConfigs})

(s/defn create-smeagol-config
  [config :- SmeagolInfra]
  (let [{:keys [configs owner content-dir]} config
        {:keys [config-edn passwd]} configs]
    (actions/remote-file
     config-edn
     :literal true
     :owner owner
     :mode "755"
     :content {:site-title     "DomainDrivenArchitecture"
               :default-locale "en-GB"
               :content-dir    content-dir
               :passwd         passwd
               :log-level      :info
               :formatters     {"vega"         'smeagol.formatting/process-vega
                                "vis"          'smeagol.formatting/process-vega
                                "mermaid"      'smeagol.formatting/process-mermaid
                                "backticks"    'smeagol.formatting/process-backticks}})))

(s/defn configure-smeagol-users
  [passwd-path :- s/Str
   owner :- s/Str
   passwd :- SmeagolPasswd]
  (actions/remote-file
   passwd-path
   :literal true
   :owner owner
   :mode "755"
   :content passwd))

(s/defn download-uberjar
  [owner :- s/Str
   uberjar :- SmeagolUberjar]
  (let [{:keys [path url md5-url]} uberjar]
       (actions/packages :aptitude ["curl"])
       (actions/directory "/usr/local/lib/smeagol"
                          :owner owner
                          :group "users"
                          :mode "755")
       (actions/remote-file path
                            :url url
                            :md5-url md5-url
                            :owner owner)))

(s/defn initd-script
  [config-edn :- s/Str
   owner :- s/Str
   port :- s/Num
   uberjar :- SmeagolUberjar]
  (let [jar-path (:path uberjar)]
    (actions/remote-file
     (str (etc-init) "/" "smeagol")
     :literal true
     :owner "root"
     :mode "755"
     :content (selmer/render-file
               "smeagol-initd.template"
               {:config-edn config-edn
                :owner owner
                :port port
                :jar-path jar-path}))))

(s/defn restart-smeagol-service
  []
  (service-supervisor
   :initd
   {:service-name "smeagol"}
   {:action :restart}))

(s/defn create-config-dir
  [owner :- s/Str]
  (actions/directory
   (str (config-root) "/" "smeagol")
   :owner owner))

(s/defn install-smeagol
  [config :- SmeagolInfra]
  (let [{:keys [uberjar passwd owner configs port]} config]
    (download-uberjar owner uberjar)
    (create-config-dir owner)
    (initd-script (:config-edn configs) owner port uberjar)))

(s/defn configure-smeagol
  [config :- SmeagolInfra]
  (let [{:keys [uberjar passwd owner configs]} config]
    (create-smeagol-config config)
    (configure-smeagol-users (:passwd configs) owner passwd)
    (restart-smeagol-service)))
