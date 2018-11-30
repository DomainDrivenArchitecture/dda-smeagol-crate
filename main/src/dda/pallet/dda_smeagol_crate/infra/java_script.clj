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

(ns dda.pallet.dda-smeagol-crate.infra.java-script
  (:require
    [schema.core :as s]
    [clojure.tools.logging :as logging]
    [pallet.actions :as actions]))

(defn install-npm
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-npm")))
  (actions/packages
    :aptitude ["npm"]))

(s/defn install-bower
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-install-system: install-bower")))
  (actions/exec-checked-script
    "install bower"
    ("npm" "install" "--global" "bower")))

(s/defn
  init-nodejs
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-init system: init-nodejs")))
  (actions/package-source (str "nodejs_8.x")
    :aptitude
    {:url (str "https://deb.nodesource.com/node_8.x")
     :release "bionic"
     :scopes ["main"]
     :key-url "https://deb.nodesource.com/gpgkey/nodesource.gpg.key"}))

(s/defn
  install-nodejs
  "get and install install-nodejs"
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-install system: install-nodejs")))
  (actions/packages :aptitude ["nodejs"]))

(s/defn init-system
  [facility :- s/Keyword]
  (init-nodejs facility))

;TODO: Fix npm dependency problem
; comment jem / 2018.11.30: at https://github.com/DomainDrivenArchitecture/dda-managed-ide/blob/master/main/src/dda/pallet/dda_managed_ide/infra/java_script.clj
; we've a consistant node & npm installation ... if the maintained version of node is good enough. Mixed installations will cause many depenency issues and npm itself often needs
; strange workarounds on linux systems ...
(s/defn install-system
  [facility :- s/Keyword]
  (install-nodejs facility)
  ;(install-npm facility)
  (install-bower facility))
