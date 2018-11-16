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

(def smeagol-dir "smeagol-master/")
(def tomcat8-location "/var/lib/tomcat8/")
(def tomcat-webapps (str tomcat8-location "webapps/"))

(defn smeagol-create-war
  [repo-location filename]
  (print repo-location)
  (actions/exec-checked-script
    (str "Create smeagol war file")
    ("cd" ~repo-location "&&" "lein bower install")
    ("cd" ~repo-location "&&" "lein ring uberwar" ~filename)))

(defn deploy-smeagol
  [smeagol-war-file-location tomcat-webapps-location]
  (actions/exec-checked-script
    (str "Deploy smeagol war file to tomcat")
    ("cp" ~smeagol-war-file-location ~tomcat-webapps-location)))

(s/defn create-dir
  "create directories
   -p no error if existing, make parent directories as needed"
  [config :- schema/SmeagolInfra]
  (actions/exec-checked-script
    (str "Create direcotires for configuration files")
    (doseq [resource (:resource-locations config)]
      ("mkdir" "-p" (:destination resource)))))

(s/defn move-resources-to-directories
  "Move the resources in the git repository to the newly created
   directories"
  [config :- schema/SmeagolInfra]
  (actions/exec-checked-script
    (str config)
    (doseq [resource (:resource-locations config)]
      ("cp" "-r" (:source resource) (:destination resource)))))

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
  (let [{:keys [repo-download-source smeagol-location]} config
        smeagol-repo (str smeagol-location smeagol-dir)
        war-filename "smeagol.war"
        war-location (str smeagol-repo "target/" war-filename)]
    (print config)
    (smeagol-remote-directory-unzip smeagol-location repo-download-source)
    (smeagol-create-war smeagol-repo war-filename)
    (deploy-smeagol war-location tomcat-webapps)
    (create-dir config)
    (move-resources-to-directories config)
    (create-environment-variables config)))
