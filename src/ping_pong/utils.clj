(ns ping-pong.utils
  (:require [cheshire.core :as cc]
            [clojure.walk :as walk]))

(def ^:const allowed-activities
  {:heart-beat 0
   :send-message 1})


(def topic
  {:topic-name "test"})


(defn keywordise-payload
  [p]
  (->> p
       cc/parse-string
       walk/keywordize-keys))

