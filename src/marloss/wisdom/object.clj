(ns marloss.wisdom.object
  (:require [me.raynes.fs :as fs]
            [marloss.config :as config]
            [marloss.util :as util]
            [clojure.string :as s]))

(defn object->html [x id->objects]
  (cond
   (map? x) [:table (map
                          (fn [[k v]] [:tr [:td k] [:td (object->html v id->objects)]])
                          (sort-by first x))]
   (sequential? x) [:table (map
                            (fn [o] [:tr [:td (object->html o id->objects)]])
                            x)]
   (< 1 (count (seq (id->objects x)))) (util/link (str "/objects/disambig/" x ".html") x)
   (id->objects x) (util/link (str "/objects/" (:type (first (id->objects x))) "/" x ".html") x)
   :else (str x)))

(defn object->pointees [id->objects x]
  (cond
   (map? x) (mapcat (partial object->pointees id->objects) (vals x))
   (sequential? x) (mapcat (partial object->pointees id->objects) x)
   (id->objects x) [x]
   :else nil))

(defn backrefs->html [backrefs id->objects]
  (let [objects (sort-by util/object->title (set (mapcat id->objects backrefs)))]
    [:ul
     (map (fn [o]
            [:li (util/link (str "/objects/" (:type o) "/" (:id o) ".html")
                            (util/object->title o))])
          objects)]))

(defn write-objects [type-id->object]
  (let [id->objects (group-by :id (vals type-id->object))
        id->backrefs (apply merge-with concat
                            (map (fn [o] {(:id o) (object->pointees id->objects o)})
                                 (vals type-id->object)))]
    (doseq [[t os] (group-by :type (vals type-id->object))]
      (let [base-path (str config/output-base-path "/objects/" (s/lower-case t) "/")]
        (fs/mkdirs base-path)
        (let [writes (reduce + 0 (map #(if
                                           (util/write-templated
                                            (str "/objects/" (:type %) "/" (:id %) ".html")
                                            (util/object->title %)
                                            [:h1 (util/object->title %)]
                                            (object->html % id->objects)
                                            [:h2 "Backrefs"]
                                            (count id->backrefs)
                                            (backrefs->html (id->backrefs (:id %)) id->objects))
                                         1
                                         0)
                                      os))]
          (println "Checked wisdom for" (count os) t "objects, with" writes "disk writes needed"))))))
