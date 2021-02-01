(ns marloss.config
  (:require [environ.core :as environ]))

(def cdda-base-path (environ/env :cdda-base-path "../Cataclysm-DDA"))
(def cdda-data-path (str cdda-base-path "/data"))
(def cdda-json-path (str cdda-data-path "/json"))

(def output-base-path (environ/env :output-base-path "_output"))

(def marloss-url (environ/env :marloss-url "https://marloss.xyz"))
