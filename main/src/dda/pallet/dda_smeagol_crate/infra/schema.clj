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

(ns dda.pallet.dda-smeagol-crate.infra.schema
  (:require
    [schema.core :as s]))

; TODO: simplify smeagol-parent-dir and smeagol-dir to one directory
(def SmeagolInfra
  {:smeagol-parent-dir s/Str
   :smeagol-dir s/Str
   :repo-download-source s/Str
   :resource-locations [{:name s/Str :source s/Str :destination s/Str}]
   :environment-variables [{:name s/Str :value s/Str}]})
