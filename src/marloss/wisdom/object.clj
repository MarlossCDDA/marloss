(ns marloss.wisdom.object
  (:require [me.raynes.fs :as fs]
            [marloss.config :as config]
            [marloss.util :as util]
            [clojure.string :as s]))

(defn object->html [x id->objects]
  (cond
   (map? x) [:dl (map
                          (fn [[k v]] [:dt k [:dd (object->html v id->objects)]])
                          (sort-by first x))]
   (sequential? x) [:ul (map
                            (fn [o] [:li (object->html o id->objects)])
                            x)]
   (< 1 (count (seq (id->objects x)))) (util/link (str "/objects/disambig/" x ".html") x)
   (id->objects x) (util/link (str "/objects/" (:type (first (id->objects x))) "/" x ".html") x)
   :else (str x)))

(defn object->pointees-helper [id->objects x]
  (cond
   (map? x) (mapcat (partial object->pointees-helper id->objects) (vals x))
   (sequential? x) (mapcat (partial object->pointees-helper id->objects) x)
   (id->objects x) [x]
   :else nil))

(defn object->pointees [id->objects x]
  (filter #(not= % (:id x)) ;; ignore id field pointing at yourself
           (object->pointees-helper id->objects x)))

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
                            (map (fn [o]
                                   (into {} (map (fn [x] [x [(:id o)]])
                                                 (object->pointees id->objects o))))
                                 (vals type-id->object)))]
    (apply concat
           (for [[t os] (group-by :type (vals type-id->object))]
             (let [base-path (str config/output-base-path "/objects/" (s/lower-case t) "/")]
               (fs/mkdirs base-path)
               (map #(util/write-templated
                      (str "/objects/" (:type %) "/" (:id %) ".html")
                      (util/object->title %)
                      [:h1 (util/object->title %)]
                      (object->html % id->objects)
                      [:h2 "Backrefs"]
                      (backrefs->html (id->backrefs (:id %)) id->objects)
                      [:h2 "Category:" (util/link (str "/indexes/" (:type %) ".html") (:type %))])
                    os))))))
