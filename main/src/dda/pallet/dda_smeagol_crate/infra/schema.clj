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

(def SmeagolPasswdUser
  {:admin s/Bool
   :email s/Str
   :password s/Str})

(def SmeagolPasswd
  {s/Keyword SmeagolPasswdUser})

(def SmeagolUberjar {:path s/Str :url s/Str :size s/Int}) ;TODO: Review jem 2018_12_19: lets use here a hash (sha256 / sha512) instead of size
(def SmeagolEnv {:env s/Str :value s/Str})

; TODO: simplify smeagol-parent-dir and smeagol-dir to one directory
(def SmeagolInfra
  {:passwd SmeagolPasswd
   :owner s/Str
   :uberjar SmeagolUberjar
   :env {s/Keyword SmeagolEnv}})
