(ns marloss.util
  (:require [clojure.string :as s]
            [me.raynes.fs :as fs]
            [marloss.config :as config]
            [hiccup.core :as h]))


(defn object->name [object]
  (s/capitalize
   (or
    (:str (:name object))
    (:str_sp (:name object))
    (:name object)
    (:id object)
    (str "unnamed " (:type object) " object"))))

(defn object->title [object]
  (str (s/capitalize (:type object)) " - " (object->name object)))

(defn link [path text]
  (assert (.startsWith path "/") (str "THAT DOESNT START WITH /" path))
  [:a {:href (s/lower-case (str "https://marloss.xyz" path))} text])

(defn write-templated
  "Returns true if a write was performed, nil if no write was necessary"
  [path title & contents]
  (let [full-path (str config/output-base-path (s/lower-case path))
        current-contents (when (fs/exists? full-path) (slurp full-path))
        full-body (h/html [:html
                           [:head
                            [:title title]]
                           [:body
                            [:h1 (link "/" "Marloss")]
                            [:h4
                             (link "/indexes/armor.html" "Armor") " "
                             (link "/indexes/book.html" "Furniture") " "
                             (link "/indexes/faction.html" "Faction") " "
                             (link "/indexes/furniture.html" "Furniture") " "
                             (link "/indexes/gun.html" "Gun") " "
                             (link "/indexes/martial_art.html" "Martial Art") " "
                             (link "/indexes/monster.html" "Monster") " "
                             (link "/indexes/mutation.html" "Mutation") " "
                             (link "/indexes/npc.html" "NPC") " "
                             (link "/indexes/profession.html" "Profession") " "
                             (link "/indexes/scenario.html" "Scenario") " "
                             (link "/indexes/skill.html" "Skill") " "
                             (link "/indexes/species.html" "Species") " "
                             (link "/indexes/tool.html" "Tool") " "
                             (link "/indexes/vehicle.html" "Vehicle") " "
                             (link "/index.html" "Everything")]
                            [:div contents]
                            [:br]
                            [:br]
                            [:br]
                            [:p "footer? i hardly know her!"]]])]
    (when-not (= full-body current-contents)
      (println "Updating" full-path)
      (spit full-path full-body)
      true)))
