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
    [clj-http.client :as http]
    [clojure.string :as string]
    [dda.pallet.dda-serverspec-crate.infra :as serverspec-infra]
    [dda.pallet.dda-smeagol-crate.domain.schema :as schema]))

(defn- path-join [& paths]
  (-> (string/join "/" (vec paths))
      (string/replace ,  #"[\\/]+" "/")))

(defn- resource-location-helper
  [smeagol-location]
  {:passwd {:name "passwd" :source (str smeagol-location "resources/passwd") :destination "/usr/local/etc/passwd"}
   :config-edn {:name "config-edn" :source (str smeagol-location "resources/config.edn") :destination "/usr/local/etc/config.edn"}
   :content-dir {:name "content-dir" :source (str smeagol-location "resources/public/content") :destination "/usr/local/etc/content"}})

(def SmeagolPasswd schema/SmeagolPasswd)

(def environment-variables
  [{:name "SMEAGOL_CONFIG" :value "/usr/local/etc/config.edn"}
   {:name "SMEAGOL_CONTENT_DIR" :value "/usr/local/etc/content"}
   {:name "SMEAGOL_PASSWD" :value "/usr/local/etc/passwd"}
   {:name "TIMBRE_DEFAULT_STACKTRACE_FONTS" :value "{}"}
   {:name "TIMBRE_LEVEL" :value ":info"}
   {:name "PORT" :value "80"}])

(def smeagol-releases "https://api.github.com/repos/DomainDrivenArchitecture/smeagol/releases")

(def ReleaseAsset
  (open-schema {:browser_download_url s/Str :name s/Str :content_type s/Str :label s/Str}))

(s/defn uberjar-release-asset :- ReleaseAsset
  []
  (->> (http/get smeagol-releases {:as :json})
       :body
       first ;; TODO find by git sha?
       :assets
       ;; TODO filter by -> :content-type (= "application/x-java-archive") or by :label?
       (filter #(-> % :name (string/ends-with? ".jar")))
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
   smeagol-passwd :- schema/SmeagolPasswd]
  (let [smeagol-parent-dir "/var/lib/"
        smeagol-dir "smeagol-master/"
        smeagol-owner "smeagol"
        smeagol-asset (uberjar-release-asset)
        {:keys [path] :as uberjar-config} (uberjar-infra smeagol-parent-dir smeagol-asset)]
    {facility
     {:smeagol-parent-dir smeagol-parent-dir
      :smeagol-passwd smeagol-passwd
      :smeagol-owner smeagol-owner
      :uberjar (assoc uberjar-config :owner smeagol-owner)
      :resource-locations (resource-location-helper (str smeagol-parent-dir smeagol-dir))
      :environment-variables environment-variables}
     serverspec-infra/facility
     {:file-fact {:uberjar {:path path}}}}))
