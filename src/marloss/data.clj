(ns marloss.data
  (:require [clojure.data.json :as json]
            [me.raynes.fs :as fs]
            [clojure.string :as s]))

(def bad-types #{"talk_topic" "overmap_terrain"})

(defn load-json-file
  "[{:id 'blah', ...}, {...}]"
  [filename]
  (filter #(and (:id %) (:type %) (not (bad-types (:type %))))
          (json/read-str (slurp filename) :key-fn keyword)))

(defn load-json-directory
  "[{:id 'blah', ...}, {...}]"
  [root-path]
  (let [loaded-files (fs/walk (fn [root dirs files]
                                (mapcat #(if (.endsWith % "json")
                                           (load-json-file (str root "/" %))
                                           nil)
                                        files))
                              root-path)]
    (apply concat loaded-files)))
