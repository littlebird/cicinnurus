(defproject littlebird/cicinnurus "0.0.15"
  :description "Generate exotic nestings of svg"
  :url "http://github.com/littlebird/cicinnurus"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :plugins [[s3-wagon-private "1.1.2"]]
  :repositories ^:replace
  [["central" {:url "http://repo1.maven.org/maven2"}]
   ["clojure" {:url "http://build.clojure.org/releases"}]
   ["clojure-snapshots" {:url "http://build.clojure.org/snapshots"}]
   ["clojars" {:url "http://clojars.org/repo/"}]
   ["private" {:url "s3p://littlebird-maven/releases/"
               :creds :gpg
               :sign-releases false}]])
