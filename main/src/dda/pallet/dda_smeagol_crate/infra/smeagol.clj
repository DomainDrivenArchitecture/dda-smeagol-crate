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

(defn smeagol-remote-file-unzip
  "Unzip and install files from a zip from a URL"
  [target-dir download-url 
   & {:keys [owner mode]
      :or {owner "tomcat7" mode "644"}}]
  (actions/remote-directory
    target-dir
    :url download-url
    :unpack :unzip
    :recursive true
    :owner owner
    :group owner))

(def smeagol-location "/var/lib/smeagol/")

(s/defn smeagol-create-war
  [repo-location filename
   & {:keys [owner]
      :or {owner "tomcat7"}}]
  (actions/exec-checked-script
    (str "Create smeagol war file")
    ("cd" ~repo-location "&&" "lein bower install")
    ("cd" ~repo-location "&&" "lein ring uberwar" ~filename)
    ))

;TODO
(s/defn install-smeagol
  [config :- schema/SmeagolInfra]
  (let [{:keys [repo-download-source]} config]
    (smeagol-remote-file-unzip smeagol-location repo-download-source)
    (smeagol-create-war smeagol-location "smeagol.war")
    ))
