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
(ns dda.pallet.dda-smeagol-crate.domain.user-test
  (:require
   [clojure.test :refer :all]
   [schema.core :as s]
   [dda.pallet.dda-smeagol-crate.domain.user :as sut]))

(s/set-fn-validation! true)

(def user-domain
  {:input {:user {:name :smeagol
                  :password "xxx"
                  :ssh {:ssh-authorized-keys ["ssh-rsa AAAA..LL comment"]
                        :ssh-key {:public-key "ssh-rsa AAAA..LL comment"
                                  :private-key "SOME_PRIVATE_SSH_KEY"}}}}
   :output {:smeagol
            {:clear-password "xxx",
             :settings #{:bashrc-d},
             :ssh-authorized-keys ["ssh-rsa AAAA..LL comment"],
             :ssh-key
             {:public-key "ssh-rsa AAAA..LL comment",
              :private-key "SOME_PRIVATE_SSH_KEY"}}}})


(deftest test-domain-creation
  (testing
    (is (= (:output user-domain)
           (sut/domain-configuration
             (get-in user-domain [:input :user :name])
             (get-in user-domain [:input :user :password])
             (get-in user-domain [:input :user :ssh]))))))
