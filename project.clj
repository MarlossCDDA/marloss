(defproject marloss "0.1.0-SNAPSHOT"
  :description "Cataclysm: Dark Days Ahead wisdom generator"
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "todo"}
  :dependencies [[environ "1.2.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [hiccup "1.0.5"]
                 [me.raynes/fs "1.4.6"]
                 [sitemap "0.4.0"]]
  :plugins [[lein-environ "1.2.0"]]
  :main ^:skip-aot marloss.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
