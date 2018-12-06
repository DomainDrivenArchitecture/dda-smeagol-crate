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

(ns dda.pallet.dda-smeagol-crate.app
  (:require
    [schema.core :as s]
    [dda.pallet.commons.secret :as secret]
    [dda.config.commons.map-utils :as mu]
    [dda.pallet.core.app :as core-app]
    [dda.pallet.dda-tomcat-crate.app :as tomcat]
    [dda.pallet.dda-user-crate.app :as user]
    [dda.pallet.dda-git-crate.app :as git]
    [dda.pallet.dda-config-crate.infra :as config-crate]
    [dda.pallet.dda-smeagol-crate.infra :as infra]
    [dda.pallet.dda-smeagol-crate.domain :as domain]))

(def with-smeagol infra/with-smeagol)

(def InfraResult domain/InfraResult)

(def SmeagolDomain domain/SmeagolDomain)

(def SmeagolDomainResolved domain/SmeagolDomainResolved)

(def SmeagolAppConfig
  {:group-specific-config
   {s/Keyword (merge
                user/InfraResult
                git/InfraResult
                tomcat/InfraResult
                InfraResult)}})

(s/defn ^:always-validate
  app-configuration-resolved :- SmeagolAppConfig
  [domain-config :- SmeagolDomainResolved
   & options]
  (let [{:keys [group-key] :or {group-key infra/facility}} options]
    {:group-specific-config
     {group-key
      (domain/infra-configuration domain-config)}}))

(s/defn ^:always-validate
  app-configuration :- SmeagolAppConfig
  [domain-config :- SmeagolDomain
   & options]
  (let [resolved-domain-config (secret/resolve-secrets domain-config SmeagolDomain)]
    (apply app-configuration-resolved resolved-domain-config options)))

(s/defmethod ^:always-validate
  core-app/group-spec infra/facility
  [crate-app
   domain-config :- SmeagolDomain]
  (let [app-config (app-configuration domain-config)]
    (core-app/pallet-group-spec
      app-config [(config-crate/with-config app-config)
                  user/with-user
                  git/with-git
                  tomcat/with-tomcat
                  with-smeagol])))

(def crate-app (core-app/make-dda-crate-app
                 :facility infra/facility
                 :domain-schema SmeagolDomain
                 :domain-schema-resolved SmeagolDomain
                 :default-domain-file "smeagol.edn"))
