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
    [dda.pallet.dda-smeagol-crate.domain.smeagol :as smeagol]
    [dda.pallet.dda-smeagol-crate.domain.git :as git]
    [dda.pallet.dda-smeagol-crate.infra :as infra]))

(def SmeagolDomain
  {(s/optional-key :tomcat-xmx-megabyte) s/Num ;; necessary on level of domain?
   (s/optional-key :smeagol-passwd) smeagol/SmeagolPasswd ;; reference from top level package is forbidden! moved to top-level
                                                          ;; not optional - passwords should be allways defined  ...
                                                          ;; smeagol-users is maybe a better name?
   :git-credential git/GitCredential
   :git-content-repo git/Repository})

(def InfraResult {infra/facility infra/SmeagolInfra})

(s/defn ^:always-validate
  tomcat-domain-configuration
  [domain-config :- SmeagolDomain]
  (let [{:keys [tomcat-xmx-megabyte smeagol-passwd]
         :or {tomcat-xmx-megabyte 2560}} domain-config]
    (smeagol/tomcat-domain-configuration tomcat-xmx-megabyte)))

(s/defn ^:always-validate
  git-infra-configuration
  [domain-config :- SmeagolDomain]
  (let [{:keys [git-credential git-content-repo]} domain-config]
    (git/infra-configuration infra/facility git-credential git-content-repo)))

(s/defn ^:always-validate
  infra-configuration
  [domain-config :- SmeagolDomain]
  (let [{:keys [smeagol-passwd]} domain-config]
    (smeagol/smeagol-infra-configuration infra/facility
      smeagol-passwd)))
