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

(ns dda.pallet.dda-smeagol-crate.infra.clojure
  (:require
    [schema.core :as s]
    [clojure.tools.logging :as logging]
    [pallet.actions :as actions]
    [dda.pallet.crate.util :as util]))

(defn install-java-8
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: openjdk-8")))
  (actions/packages :aptitude ["openjdk-8-jdk"]))

(defn install-leiningen
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: leiningen")))
  "get and install lein at /bin/"
  (actions/remote-file
    "/bin/lein"
    :owner "root"
    :group "users"
    :mode "755"
    :url "https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein"))

(s/defn install-system
  [facility :- s/Keyword]
  (install-java-8 facility)
  (install-leiningen facility))
