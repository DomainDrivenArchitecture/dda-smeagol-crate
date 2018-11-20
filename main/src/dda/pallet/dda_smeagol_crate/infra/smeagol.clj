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
    :unpack :unzip
    :recursive true
    :owner owner
    :group owner))

(defn smeagol-create-war
  [repo-location filename]
  (print repo-location)
  (actions/exec-checked-script
    (str "Create smeagol war file")
    ("cd" ~repo-location "&&" "lein bower install")
    ("cd" ~repo-location "&&" "lein ring uberwar" ~filename)))

(s/defn create-dirs
  "create directories
   -p no error if existing, make parent directories as needed"
  [config :- schema/SmeagolInfra]
  (actions/exec-checked-script
    (str "Create directories for configuration files")
    (doseq [resource (:resource-locations config)]
      ("mkdir" "-p" (:destination resource)))))

(s/defn move-resources-to-directories
  "Move the resources in the git repository to the newly created
   directories"
  [config :- schema/SmeagolInfra]
  (doseq [resource (:resource-locations config)]
    (let [source (:source resource)
          destination (:destination resource)]
    (actions/exec-checked-script
      (str "Move the resources in the git repository to the newly created directories\n" config)
      ("cp" "-r" ~source ~destination)))))

;TODO needs info about user for .bashrc
(s/defn create-environment-variables
  [config :- schema/SmeagolInfra]
  (actions/exec-checked-script
    (str "Create environment variables")
    (doseq [env (:environment-variables config)]
      ("export" (str (:name env) "=" (:value env))))))

;TODO
(s/defn install-smeagol
  [config :- schema/SmeagolInfra]
  (let [{:keys [repo-download-source smeagol-parent-dir smeagol-dir]} config
        smeagol-repo (str smeagol-parent-dir smeagol-dir)
        war-filename "smeagol.war"
        war-location (str smeagol-repo "target/" war-filename)]
    (print config)
    (smeagol-remote-directory-unzip smeagol-parent-dir repo-download-source)
    (smeagol-create-war smeagol-repo war-filename)
    (create-dirs config)
    (move-resources-to-directories config)
    (create-environment-variables config)))
