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
(ns dda.pallet.dda-smeagol-crate.domain.git-test
  (:require
   [clojure.test :refer :all]
   [schema.core :as s]
   [dda.pallet.dda-smeagol-crate.domain.smeagol :as sut]))

(def git-multiuser
  {:input {:git-credential [{:host "github.com"
                             :protocol :ssh
                             :user-name "githubtest"}]
           :content-git-repo {:host "rgithub.com"
                              :repo-name "a-private-repo"
                              :protocol :ssh
                              :server-type :github}}
   :output {:dda-git
            {:smeagol-user
             {:config {:email "test-user1@domain"},
              :synced-repo {:folder1 [{:host "repositories.website.com"
                                       :repo-name "a-private-repo"
                                       :protocol :ssh
                                       :server-type :github}]}
              :repo []
              :trust [{:pin-fqdn-or-ip {:port 443
                                        :host "github.com",}}]}}}})
