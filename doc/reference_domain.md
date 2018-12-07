```clojure
(def ClearPassword secret/Secret)
(def Ssh user-domain/Ssh)

(def SmeagolPasswdUser
  {:admin s/Bool
   :email s/Str
   :password s/Str}) ;; Why not keep passwords secret?!

(def Repository git-domain/Repository)
(def GitCredential git-domain/GitCredential)

(def SmeagolPasswd
  {s/Keyword SmeagolPasswdUser})

(def SmeagolDomain
  {(s/optional-key :tomcat-xmx-megabyte) s/Num ;; necessary on level of domain?
   (s/optional-key :smeagol-passwd) smeagol/SmeagolPasswd ;; reference from top level package is forbidden! moved to top-level
                                                          ;; not optional - passwords should be allways defined  ...
                                                          ;; smeagol-users is maybe a better name?
   :server-fqdn s/Str
   :user-passwd user/ClearPassword
   :user-ssh user/Ssh
   :git-credential git/GitCredential
   :git-content-repo git/Repository})
```

Referenced user [domain elements can be found in dda-user-crate](https://github.com/DomainDrivenArchitecture/dda-user-crate) and [git fomain elements can be found in dda-git-crate](https://github.com/DomainDrivenArchitecture/dda-git-crate/blob/master/doc/reference_domain.md)
