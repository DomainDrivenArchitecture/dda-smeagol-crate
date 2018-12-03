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

(ns dda.pallet.dda-smeagol-crate.domain.schema
  (:require
   [schema.core :as s]))

; TODO: Review jem 2018_12_03: Move schema do smeagol ns - like DomainDrivenDesign
; Aggregate keep it local

(def SmeagolPasswdUser
  {:admin s/Bool
   :email s/Str
   :password s/Str}) ;; Why not keep passwords secret?!

(def SmeagolPasswd
  {s/Keyword SmeagolPasswdUser})
