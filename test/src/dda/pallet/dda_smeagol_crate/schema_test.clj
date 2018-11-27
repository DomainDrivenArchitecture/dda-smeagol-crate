(ns dda.pallet.dda-smeagol-crate.schema-test
  (:require
   [clojure.test :refer :all]
   [schema.core :as s]
   [dda.pallet.dda-smeagol-crate.schema :as sut]))

(def passwd
  {:admin {:admin true, :email "admin@localhost", :password "admin"}})

(deftest test-schema
  (testing "test the smeagol schema"
    (is (s/validate sut/SmeagolPasswd passwd))
    (is (thrown? Exception (s/validate sut/SmeagolPasswd {:unsuported-key :unsupported-value})))))
