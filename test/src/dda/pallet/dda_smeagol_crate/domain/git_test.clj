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
   [dda.pallet.dda-smeagol-crate.domain.git :as sut]))

(s/set-fn-validation! true)

(def git-domain
  {:input {:user-name :smeagol
           :git-credential {:host "github.com"
                            :protocol :https
                            :user-name "smeagol"}
           :git-content-repo {:host "github.com"
                              :repo-name "a-private-repo"
                              :protocol :https
                              :server-type :github}}
   :output {:smeagol
             {:user-email "smeagol@domain"
              :credential [{:host "github.com"
                            :protocol :https
                            :user-name "smeagol"}]
              :repo {}
              :synced-repo {:content [{:host "github.com"
                                       :repo-name "a-private-repo"
                                       :protocol :https
                                       :server-type :github}]}}}})

(deftest test-domain-creation
  (testing
    (is (= (:output git-domain)
           (sut/domain-configuration
             (get-in git-domain [:input :user-name])
             "domain"
             (get-in git-domain [:input :git-credential])
             (get-in git-domain [:input :git-content-repo]))))))
