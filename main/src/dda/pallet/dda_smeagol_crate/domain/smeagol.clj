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

(def ReleaseAsset
  (open-schema {:browser_download_url s/Str :name s/Str :content_type s/Str :label (s/maybe s/Str)}))

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

(def smeagol-releases "https://api.github.com/repos/DomainDrivenArchitecture/smeagol/releases")

;; TODO maybe search by label? can travis specify content-type?
(s/defn jar-asset?
  [asset :- ReleaseAsset]
  (-> asset :content_type (= "application/x-java-archive")))

(s/defn uberjar-release-asset ; :- ReleaseAsset
  []
  (->> (http/get smeagol-releases {:as :json})
       :body
       (filter #(some jar-asset? (:assets %)))
       first
       :assets
       (filter jar-asset?)
       first))


(s/defn uberjar-infra
  [smeagol-parent-dir :- s/Str
   release-asset :- ReleaseAsset]
  (let [{:keys [name size browser_download_url]} release-asset]
    {:path (path-join smeagol-parent-dir name)
     :url browser_download_url
     :size size}))

(s/defn smeagol-infra-configuration
  [facility :- s/Keyword
   repo-name :- s/Str
   passwd :- SmeagolPasswdResolved]
  (let [smeagol-owner "smeagol"
        smeagol-parent-dir (path-join "/home" smeagol-owner)
        uberjar-config {:path "/usr/local/lib/smeagol/smeagol-1.0.2-SNAPSHOT-standalone.jar"
                        :url "https://github.com/DomainDrivenArchitecture/smeagol/releases/download/1.0.2-snap1/smeagol-1.0.2-SNAPSHOT-standalone.jar"
                        :md5-url "https://github.com/DomainDrivenArchitecture/smeagol/releases/download/1.0.2-snap1/smeagol-1.0.2-SNAPSHOT-standalone.jar.md5"}]

        ;{:keys [path] :as uberjar-config} (uberjar-infra smeagol-parent-dir (uberjar-release-asset))]
    {facility
     {:passwd passwd
      :owner smeagol-owner
      :uberjar uberjar-config
      :port 8080
      :configs (config-locations "/etc/smeagol")}}))
    ; TODO: make things as simple as possible - just download always
    ;serverspec-infra/facility
     ;{:file-fact {:uberjar {:path path}}}})) ;; TODO make use of `:uberjar` kw in infra/download-uberjar
