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

(ns dda.pallet.dda-smeagol-crate.domain.git
  (:require
    [schema.core :as s]
    [dda.pallet.dda-git-crate.domain :as git-domain]
    ;; TODO: expose used fn on git-domain level as only git-domain serves as public api
    [dda.pallet.dda-git-crate.domain.repo :as git-domain.repo]))

(def Repository git-domain/Repository)
(def GitCredential git-domain/GitCredential)
(def GitCredentialResolved git-domain/GitCredentialResolved)

(s/defn repo-directory-name
  [user-name :- s/Keyword
   repo :- Repository]
  ;; where comes `:content` aka orga-group (not orga-path)?
  (git-domain.repo/repo-directory-name user-name :content repo))

(s/defn
  domain-configuration :- git-domain/GitDomainResolved
  [user-name :- s/Keyword
   domain :- s/Str
   git-credential :- GitCredentialResolved
   git-content-repo :- Repository]
  {user-name
   {:user-email (str (name user-name) "@" domain)
    :credential [git-credential]
    :repo {}
    :synced-repo {:content [git-content-repo]}}})
