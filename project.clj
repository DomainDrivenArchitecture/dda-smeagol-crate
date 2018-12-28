(defproject dda/dda-smeagol-crate "0.1.0-SNAPSHOT"
  :description "Module for installing a server with smeagol-wiki"
  :url "https://domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[dda/dda-pallet "2.2.2"]
                 [dda/dda-git-crate "2.1.4-SNAPSHOT"]
                 [dda/dda-httpd-crate "2.1.0-SNAPSHOT"]
                 [dda/dda-user-crate "1.0.6-SNAPSHOT"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.1"]]
  :target-path "target/%s/"
  :source-paths ["main/src"]
  :resource-paths ["main/resources"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev {:source-paths ["integration/src"
                                  "test/src"
                                  "uberjar/src"]
                   :resource-paths ["integration/resources"
                                    "test/resources"]
                   :dependencies
                   [[org.clojure/test.check "0.10.0-alpha3"]
                    ;[expectations "2.2.0-SNAPSHOT"]
                    [dda/pallet "0.9.0" :classifier "tests"]
                    [ch.qos.logback/logback-classic "1.3.0-alpha4"]
                    [org.slf4j/jcl-over-slf4j "1.8.0-beta2"]]
                   :plugins
                   [[lein-sub "0.3.0"]
                    [jonase/eastwood "0.3.4" :exclusions [org.clojure/clojure]]]
                   :eastwood {:namespaces [:source-paths]}
                   :leiningen/reply
                   {:dependencies [[org.slf4j/jcl-over-slf4j "1.8.0-beta0"]]
                    :exclusions [commons-logging]}
                   :repl-options {:init-ns dda.pallet.dda-smeagol-crate.app.instantiate-existing}}
             :test {:test-paths ["test/src"]
                    :resource-paths ["test/resources"]
                    :dependencies [[dda/pallet "0.9.0" :classifier "tests"]]}
                                   ;[expectations "2.2.0-SNAPSHOT"]]}
             :uberjar {:source-paths ["uberjar/src"]
                       :resource-paths ["uberjar/resources"]
                       :aot :all
                       :main dda.pallet.dda-smeagol-crate.main
                       :uberjar-name "dda-smeagol-standalone.jar"
                       :dependencies [[org.clojure/tools.cli "0.4.1"]
                                      [ch.qos.logback/logback-classic "1.3.0-alpha4"
                                       :exclusions [com.sun.mail/javax.mail]]
                                      [org.slf4j/jcl-over-slf4j "1.8.0-beta2"]]}}
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["uberjar"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :local-repo-classpath true)
