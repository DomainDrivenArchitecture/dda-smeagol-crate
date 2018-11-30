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
    [pallet.actions :as actions]
    [dda.pallet.dda-smeagol-crate.infra.schema :as schema]))

(defn smeagol-remote-directory-unzip
  "Unzip and install files from a zip from a URL"
  [target-dir download-url 
   & {:keys [owner mode]
      :or {owner "root" mode "644"}}]
  (actions/remote-directory
    target-dir
    :url download-url
    :unpack :unzip))

;; lets embed config directly into war by replacing `resources/config.edn`
(s/defn create-smeagol-config
  [config :- schema/SmeagolInfra]
  (let [{:keys [resource-locations smeagol-owner]} config
        {:keys [config-edn passwd content-dir]} resource-locations
        {:keys [source]} config-edn
        ;; consider read config contents and rewrite back
        smeagol-config {:content-dir (:destination content-dir)
                        :passwd (:destination passwd)
                        :site-title "Smeagol"
                        :default-locale "en-GB"
                        :formatters {"vega" 'smeagol.formatting/process-vega
                                     "vis" 'smeagol.formatting/process-vega
                                     "mermaid" 'smeagol.formatting/process-mermaid
                                     "backticks" 'smeagol.formatting/process-backticks}
                        :log-level :info}]
    (if source
      (actions/remote-file
       source
       :owner smeagol-owner
       :mode "755"
       :content (pr-str smeagol-config)))))

(defn smeagol-create-war
  [repo-location filename]
  (actions/exec-checked-script
    (str "Create smeagol war file")
    ("cd" ~repo-location "&&" "lein bower install")
    ("cd" ~repo-location "&&" "lein ring uberwar" ~filename)))

(s/defn create-dirs
  "create directories
   -p no error if existing, make parent directories as needed"
  [config :- schema/SmeagolInfra]
  (let [directories (map :destination (:resource-locations config))
        owner (:smeagol-owner config)]
    (actions/directories directories :owner owner)))

(s/defn move-resources-to-directories
  "Move the resources in the git repository to the newly created
   directories"
  [config :- schema/SmeagolInfra]
  (let [{:keys [smeagol-owner]} config]
    (doseq [[_ resource] (:resource-locations config)]
      (let [{:keys [source destination]} resource]
        (actions/exec-checked-script
           (str "Move the resources in the git repository to the newly created directories\n" config)
           ("cp" "-r" ~source ~destination)
           ("chown" "-R" ~smeagol-owner ~destination)
           ("chgrp" "-R" ~smeagol-owner ~destination))))))

(s/defn install-smeagol
  [config :- schema/SmeagolInfra]
  (let [{:keys [repo-download-source smeagol-parent-dir smeagol-dir]} config
        smeagol-repo (str smeagol-parent-dir smeagol-dir)
        war-filename "smeagol.war"
        war-location (str smeagol-repo "target/" war-filename)]
    (smeagol-remote-directory-unzip smeagol-parent-dir repo-download-source)
    (create-smeagol-config config)
    (smeagol-create-war smeagol-repo war-filename)
    (create-dirs config)
    (move-resources-to-directories config)))
