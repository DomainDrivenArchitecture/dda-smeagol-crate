(ns dda.pallet.dda-smeagol-crate.schema
  (:require [schema.core :as s]))

(def SmeagolPasswdUser
  {:admin s/Bool
   :email s/Str
   :password s/Str})

(def SmeagolPasswd
  {s/Keyword SmeagolPasswdUser})
