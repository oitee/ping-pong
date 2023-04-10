(ns ping-pong.producer
  (:require [jackdaw.client :as jc]))

(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"
   "client.id" "foo"})

(def ^:const topic "test")

(def continue? (atom true))

(def users ["Alice" "Bob" "Claire" "Doyce" "Earl"])

(defn send-message [m]
  (with-open [my-producer (jc/producer producer-config)]
    @(jc/produce! my-producer {:topic-name topic} "1" m)))

(defn create-and-send-message
  []
  (let [current-ts (System/currentTimeMillis)
        user (get users (rand-int (count users)))]
    (send-message (str "User: " user ", TS: " current-ts))))

(defn send-messages-constantly
  [interval]
  (reset! continue? true)
  (loop []
    (when @continue?
      (Thread/sleep interval)
      (create-and-send-message)
      (recur))))

(defn stop-messages
  []
  (reset! continue? false))
