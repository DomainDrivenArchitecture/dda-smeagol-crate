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
    [schema.core :as s]))

(def facility :dda-smeagol)

(def SmeagolInfra
  {:repo-download-source s/Str
   :tomcat-xmx-megabyte s/Num})

(s/defmethod core-infra/dda-init facility
  [dda-crate config])

(s/defmethod core-infra/dda-install facility
  [core-infra config])

(s/defmethod core-infra/dda-configure facility
  [core-infra config])

(s/defmethod core-infra/dda-test facility
  [core-infra config])

(def dda-smeagol-crate
  (core-infra/make-dda-crate-infra
    :facility facility))

(def with-smeagol
  (core-infra/create-infra-plan dda-smeagol-crate))
