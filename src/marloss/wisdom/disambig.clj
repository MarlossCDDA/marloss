(ns marloss.wisdom.disambig
  (:require [me.raynes.fs :as fs]
            [marloss.config :as config]
            [marloss.util :as util]
            [clojure.string :as s]))

(defn write-disambig [type-id->object]
  (fs/mkdirs (str config/output-base-path "/objects/disambig"))
  (doseq [[id objects] (group-by (comp s/lower-case :id) (vals type-id->object))]
    (when (> (count objects) 1)
      (util/write-templated
       (str "/objects/disambig/" (s/lower-case id) ".html")
       (str "Disambiguation - " (s/capitalize id))
       [:p "There are so many kinds of " id "! Did you mean:"]
       [:ul
        (map (fn [o] [:li (util/link
                           (str "/objects/" (:type o) "/" (:id o) ".html")
                           (util/object->title o))])
             (sort-by util/object->title objects))]))))
