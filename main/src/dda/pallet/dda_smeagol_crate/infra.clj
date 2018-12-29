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

(ns dda.pallet.dda-smeagol-crate.infra
  (:require
    [schema.core :as s]
    [clojure.tools.logging :as logging]
    [pallet.actions :as actions]
    [dda.pallet.core.infra :as core-infra]
    [dda.pallet.dda-smeagol-crate.infra.smeagol :as smeagol]))

(def facility :dda-smeagol)

(def SmeagolInfra smeagol/SmeagolInfra)

(defn- install-java-8
  [facility]
  (actions/as-action
   (logging/info (str facility "-install system: openjdk-8")))
  (actions/packages :aptitude ["openjdk-8-jdk"]))

(s/defmethod core-infra/dda-init facility
  [core-infra config])

(s/defmethod core-infra/dda-install facility
  [core-infra config]
  (install-java-8 facility)
  (smeagol/install-smeagol config))

(s/defmethod core-infra/dda-configure facility
  [core-infra config]
  (smeagol/configure-smeagol config))

(s/defmethod core-infra/dda-test facility
  [core-infra config])

(def dda-smeagol-crate
  (core-infra/make-dda-crate-infra
    :facility facility))

(def with-smeagol
  (core-infra/create-infra-plan dda-smeagol-crate))
