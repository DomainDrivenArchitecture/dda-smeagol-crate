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

(ns dda.pallet.dda-smeagol-crate.domain.smeagol
  (:require
    [schema.core :as s]
    [schema-tools.core :refer [open-schema]]
    [pallet.script.lib :refer [config-root]]
    [clj-http.client :as http]
    [clojure.string :as string]
    [dda.pallet.commons.secret :as secret]
    [dda.pallet.dda-serverspec-crate.infra :as serverspec-infra]))

(def SmeagolPasswdUser
  {:admin s/Bool
   :email s/Str
   :password secret/Secret})

(def SmeagolPasswd
  {s/Keyword SmeagolPasswdUser})

(def SmeagolPasswdResolved
  (secret/create-resolved-schema SmeagolPasswd))

(defn- path-join [& paths]
  (-> (string/join "/" (vec paths))
      (string/replace ,  #"[\\/]+" "/")))

(s/defn config-locations
  [root :- s/Str]
  {:passwd (path-join root "passwd.edn")
   :config-edn (path-join root "config.edn")})

(s/defn smeagol-infra-configuration
  [facility :- s/Keyword
   owner :- s/Keyword
   content-dir :- s/Str
   passwd :- SmeagolPasswdResolved
   port :- s/Num]
  (let [uberjar-config {:path "/usr/local/lib/smeagol/smeagol-standalone.jar"
                        :url "https://github.com/DomainDrivenArchitecture/smeagol/releases/download/1.0.3-snap3/smeagol-standalone.jar"
                        :md5-url "https://github.com/DomainDrivenArchitecture/smeagol/releases/download/1.0.3-snap3/smeagol-standalone.jar.md5"}]

    {facility
     {:passwd passwd
      :owner (name owner)
      :content-dir content-dir
      :uberjar uberjar-config
      :port port
      :configs (config-locations "/etc/smeagol")}}))
