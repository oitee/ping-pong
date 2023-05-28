(ns ping-pong.utils
  (:require [cheshire.core :as cc]
            [clojure.walk :as walk]
            [jackdaw.client :as jc]
            [jackdaw.client.log :as jl]))

(def consumer-config
  {"bootstrap.servers" "localhost:9092"
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

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


(defn start-consuming
  [config topic continue-fn consuming-fn]
  (with-open [my-consumer (-> (jc/consumer config)
                              (jc/subscribe [topic]))]

    (doseq [{:keys [value]} (jl/log my-consumer 500 continue-fn)]
      (consuming-fn value))))
