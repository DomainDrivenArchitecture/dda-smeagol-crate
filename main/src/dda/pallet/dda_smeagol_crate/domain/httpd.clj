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

(ns dda.pallet.dda-smeagol-crate.domain.httpd
  (:require
    [schema.core :as s]
    [dda.pallet.dda-httpd-crate.domain :as httpd-domain]))

(def VhostSettings httpd-domain/VhostSettings)

(s/defn
  domain-configuration :- httpd-domain/HttpdDomainConfig
  [server-fqdn :- s/Str
   proxy-port :- s/Str
   settings-contained? :- s/Bool
   settings :- VhostSettings]
  {:single-proxy
   (merge
     {:domain-name server-fqdn
      :proxy-target-port proxy-port}
     (when settings-contained?
       {:settings settings}))})

(s/defn
  infra-configuration
  [server-fqdn :- s/Str
   proxy-port :- s/Str]
  (httpd-domain/single-proxy-configuration
   (domain-configuration server-fqdn proxy-port)))
