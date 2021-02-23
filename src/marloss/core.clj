(ns marloss.core
  (:gen-class)
  (:require [marloss.config :as config]
            [marloss.data :as data]
            [marloss.util :as util]
            [marloss.wisdom.disambig :as disambig]
            [marloss.wisdom.index :as index]
            [marloss.wisdom.object :as object]))

(defn -main
  [& args]
  (let [objects (data/load-json-directory config/cdda-json-path)
        type-id->objects (group-by (juxt :type :id) objects)
        type-id->object (into {} (map (fn [[k vs]] [k (first vs)]) type-id->objects))]
    (doseq [[k vs] type-id->objects]
      (when (> (count vs) 1)
        (println (count vs) "ID collision for" k "!!!")))
    (println "Loaded" (count objects) "objects.")
    (let [paths (concat
                 (disambig/write-disambig type-id->object)
                 (index/write-indexes type-id->object)
                 (object/write-objects type-id->object))]
      (util/write-sitemap paths))))
