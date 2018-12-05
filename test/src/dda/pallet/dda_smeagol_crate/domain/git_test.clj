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
  {:input {:git-credential {:host "github.com"
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
              :synced-repo {:smeagol [{:host "github.com"
                                       :repo-name "a-private-repo"
                                       :protocol :https
                                       :server-type :github}]}}}})

(deftest test-domain-creation
  (testing
    (is (= (:output git-domain)
           (sut/domain-configuration
             "domain"
             (get-in git-domain [:input :git-credential])
             (get-in git-domain [:input :git-content-repo]))))))

(def git-infra
  {:output {:dda-git
             {:smeagol
              {:config {:email "smeagol@domain"},
               :file-fact-keyword
               :dda.pallet.dda-serverspec-crate.infra.fact.file/file,
               :trust
               [{:pin-fqdn-or-ip {:host "github.com", :port 443}}],
               :repo
               [{:repo
                 "https://smeagol@github.com:443//a-private-repo.git",
                 :local-dir
                 "/home/smeagol/repo/smeagol/a-private-repo",
                 :settings #{:sync}}]}},
             :dda-servertest
             {:file-fact
              {:_home_smeagol_repo_smeagol_a-private-repo
               {:path
                "/home/smeagol/repo/smeagol/a-private-repo"}}}}})


(deftest test-infra-creation
  (testing
    (is (= (:output git-infra)
           (sut/infra-configuration
             "domain"
             (get-in git-domain [:input :git-credential])
             (get-in git-domain [:input :git-content-repo]))))))
