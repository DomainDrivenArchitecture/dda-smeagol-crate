# dda-smeagol-crate
[![Clojars Project](https://img.shields.io/clojars/v/dda/dda-smeagol-crate.svg)](https://clojars.org/dda/dda-smeagol-crate)
[![Build Status](https://travis-ci.org/DomainDrivenArchitecture/dda-smeagol-crate.svg?branch=master)](https://travis-ci.org/DomainDrivenArchitecture/dda-smeagol-crate)

[![Slack](https://img.shields.io/badge/chat-clojurians-green.svg?style=flat)](https://clojurians.slack.com/messages/#dda-pallet/) | [<img src="https://meissa-gmbh.de/img/community/Mastodon_Logotype.svg" width=20 alt="team@social.meissa-gmbh.de"> team@social.meissa-gmbh.de](https://social.meissa-gmbh.de/@team) | [Website & Blog](https://domaindrivenarchitecture.org)

This crate is part of [dda-pallet](https://domaindrivenarchitecture.org/pages/dda-pallet/).

## compatability
dda-pallet is compatible to the following versions
* pallet 0.8.x
* clojure 1.9
* (x)ubunutu 18.04

## Features
This crate provisions over ssh or local a [smeagol](https://github.com/journeyman-cc/smeagol) wiki to a plain ubuntu system. One shoot provisioning is supported but also continuous configuration application is possible.

Part of the installation is:
* apache http proxy with letsencrypt & ssl termination
* a smeagol user having ssh credentials
* a git repoy wich contains wiki contents. Repo can be autosynced using smeagols user ssh
* the smeagol server itself
* smeagol users & password salt are configurable

## Usage documentation
1. Download the jar-file from the releases page of this repository (e.g. `curl -L -o dda-smeagol-standalone.jar https://github.com/DomainDrivenArchitecture/dda-smeagol-crate/releases/download/0.1.0/dda-smeagol-standalone.jar`)
2. Deploy the jar-file on the source machine
3. Create the files `smeagol.edn` (Domain-Schema for your smeagol server) and `target.edn` (Schema for target systems to be provisioned) according to the reference and our example configurations. Please create them in the same folder where you've saved the jar-file.
4. Start the installation:
```bash
java -jar dda-smeagol-standalone.jar --targets targets.edn smeagol.edn
```
If you want to install smeagol on your localhost you don't need a target config.
```bash
java -jar dda-smeagol-standalone.jar smeagol.edn
```

## Configuration
The configuration consists of two files defining both WHERE to install the software and WHAT to install.
* `targets.edn`: describes on which target system(s) the software will be installed
* `user.edn`: describes which software/packages will be installed

You can download examples of these configuration files from
[https://github.com/DomainDrivenArchitecture/dda-smeagol-crate/blob/master/targets.edn](https://github.com/DomainDrivenArchitecture/dda-smeagol-crate/blob/master/targets.edn) and
[https://github.com/DomainDrivenArchitecture/dda-smeagol-crate/blob/master/smeagol.edn](https://github.com/DomainDrivenArchitecture/dda-smeagol-crate/blob/master/smeagol.edn) respectively.

### Targets config example
Example content of the file, `targets.edn`:
```clojure
{:existing [{:node-name "target1"                      ; semantic name (keep the default or use a name that suits you)
             :node-ip "192.168.56.104"}]               ; the ip4 address of the machine to be provisioned
             {:node-name "target2"                     ; semantic name (keep the default or use a name that suits you)
                          :node-ip "192.168.56.105"}]  ; the ip4 address of the machine to be provisioned
 :provisioning-user {:login "initial"                  ; user on the target machine, must have sudo rights
                     :password {:plain "secure1234"}}} ; password can be ommited, if a ssh key is authorized
```

### Smeagol config example
Example content of the file, `smeagol.edn`:
```clojure
{:user {:name :smeagol
        :passwd {:plain "xxx"}                                  ; smeagol user pwd on os level
        :ssh
        {:ssh-authorized-keys [{:plain "ssh-rsa AAAA..LL comment"}] ; ssh authorized keys
         :ssh-key {:public-key {:plain "ssh-rsa AAAA..LL comment"}  ; ssh-key for git sync
                   :private-key {:plain "SOME_PRIVATE_SSH_KEY"}}}}
 :git-credential
  {:host "github.com"                                         ; credentials for content repo
   :protocol :ssh
   :user-name {:plain "smeagol"}}
 :git-content-repo                                            ; the content repo spec
  {:host "github.com"                                         ; e.g. for github. Gitlab or gitblit will also work.
   :orga-path "DomainDrivenArchitecture"
   :repo-name "dda"
   :protocol :ssh
   :server-type :github}
 :server-fqdn "a.server.name"                                 ; the httpd server name
 :smeagol-users
  {:admin {:admin true,                                       ; smeagol users
           :email "admin@localhost",
           :password "admin" {:plain "admin"}}}}
```


### Watch log for debug reasons
In case of problems you may want to have a look at the log-file:
`less logs/pallet.log`

### Targets

You can define provisioning targets using the [targets-schema](https://github.com/DomainDrivenArchitecture/dda-pallet-commons/blob/master/doc/existing_spec.md)

### Domain API

You can use our conventions as a starting point:
[see domain reference](doc/reference_domain.md)

### Infra API

Or you can build your own conventions using our low level infra API. We will keep this API backward compatible whenever possible:
[see infra reference](doc/reference_infra.md)

## License

Copyright © 2018 meissa GmbH
Licensed under the [Apache License, Version 2.0](LICENSE) (the "License")
Pls. find licenses of our subcomponents [here](doc/SUBCOMPONENT_LICENSE)
