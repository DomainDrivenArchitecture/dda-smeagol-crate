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
    [dda.pallet.core.infra :as core-infra]
    [dda.pallet.dda-smeagol-crate.infra.schema :as schema]
    [dda.pallet.dda-smeagol-crate.infra.java-script :as js]
    [dda.pallet.dda-smeagol-crate.infra.clojure :as clj]
    [dda.pallet.dda-smeagol-crate.infra.smeagol :as smeagol]))

(def facility :dda-smeagol)

(def SmeagolInfra schema/SmeagolInfra)

(s/defmethod core-infra/dda-init facility
  [core-infra config])
  ;(js/init-system facility))

(s/defmethod core-infra/dda-install facility
  [core-infra config]
  (clj/install-system facility)
  ;(js/install-system facility)
  (smeagol/install-smeagol config))


(s/defmethod core-infra/dda-configure facility
  [core-infra config])

(s/defmethod core-infra/dda-test facility
  [core-infra config])

(def dda-smeagol-crate
  (core-infra/make-dda-crate-infra
    :facility facility))

(def with-smeagol
  (core-infra/create-infra-plan dda-smeagol-crate))
