(ns marloss.wisdom.index
  (:require [me.raynes.fs :as fs]
            [marloss.config :as config]
            [marloss.util :as util]
            [clojure.string :as s]))


(defn write-indexes [type-id->object]
  (let [type->objects (group-by :type (vals type-id->object))]
    (fs/mkdirs (str config/output-base-path "/indexes"))
    (let [paths (for [[t os] type->objects]
                  (util/write-templated
                   (str "/indexes/" t ".html")
                   (str "Index - " (s/capitalize t))
                   [:p "Everybody loves a circus. But what everybody REALLY loves is a " (s/lower-case t) "!!!"]
                   [:ul
                    (map (fn [o] [:li (util/link
                                       (str "/objects/" t "/" (:id o) ".html")
                                       (util/object->title o))])
                         (sort-by util/object->title os))]))]
      (conj paths
            (util/write-templated
             (str "/everything.html")
             "Index - Everything"
             [:p "Everybody loves a circus. But what everybody REALLY loves is an index of every single object in the game!!!!!!"]
             [:ul
              (map (fn [o] [:li (util/link
                                 (str "/objects/" (:type o) "/" (:id o) ".html")
                                 (util/object->title o))])
                   (sort-by util/object->title (vals type-id->object)))])
            (util/write-templated
             (str "/index.html")
             "Types Index"
             [:p "Everybody loves a circus. But what everybody REALLY loves is an index of indexes!!!!!!"]
             [:ul
              (map (fn [t] [:li (util/link
                                 (str "/indexes/" t ".html")
                                 (s/capitalize t))])
                   (sort-by s/capitalize (keys type->objects)))])))))
