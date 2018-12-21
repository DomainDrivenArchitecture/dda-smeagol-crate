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
(ns dda.pallet.dda-smeagol-crate.domain
  (:require
    [schema.core :as s]
    [dda.pallet.commons.secret :as secret]
    [dda.pallet.dda-smeagol-crate.domain.smeagol :as smeagol]
    [dda.pallet.dda-smeagol-crate.domain.git :as git]
    [dda.pallet.dda-smeagol-crate.domain.user :as user]
    [dda.pallet.dda-smeagol-crate.domain.httpd :as httpd]
    [dda.pallet.dda-smeagol-crate.infra :as infra]))

(def SmeagolDomain
  {:server-fqdn s/Str
   :user-passwd user/ClearPassword
   :user-ssh user/Ssh
   :git-credential git/GitCredential
   :git-content-repo git/Repository
   :smeagol-users smeagol/SmeagolPasswd
   (s/optional-key :settings) httpd/VhostSettings})

(def SmeagolDomainResolved (secret/create-resolved-schema SmeagolDomain))

(def InfraResult {infra/facility infra/SmeagolInfra})

(s/defn ^:always-validate
  user-domain-configuration
  [domain-config :- SmeagolDomainResolved]
  (let [{:keys [user-passwd user-ssh]} domain-config]
    (user/domain-configuration user-passwd user-ssh)))

(s/defn ^:always-validate
  git-domain-configuration
  [domain-config :- SmeagolDomainResolved]
  (let [{:keys [git-credential git-content-repo server-fqdn]} domain-config]
    (git/domain-configuration server-fqdn git-credential git-content-repo)))

(s/defn ^:always-validate
  httpd-domain-configuration
  [domain-config :- SmeagolDomainResolved]
  (let [{:keys [server-fqdn proxy-port settings] :or {proxy-port "8080"}} domain-config]
    (httpd/domain-configuration server-fqdn proxy-port (contains? domain-config :settings) settings)))

(s/defn ^:always-validate
  infra-configuration
  [domain-config :- SmeagolDomainResolved]
  (let [{:keys [git-credential git-content-repo server-fqdn
                user-passwd user-ssh
                smeagol-users]} domain-config]
    (smeagol/smeagol-infra-configuration infra/facility smeagol-users)))
