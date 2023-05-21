(ns ping-pong.producer
  (:require [jackdaw.client :as jc]
            [cheshire.core :as cc]
            [ping-pong.users :as users]))

(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"
   "client.id" "foo"})

(def ^:const topic "test")

(def continue? (atom true))

(defn send-message [m]
  (with-open [my-producer (jc/producer producer-config)]
    @(jc/produce! my-producer {:topic-name topic} m)))

(defn create-and-send-message
  []
  (let [current-ts (System/currentTimeMillis)
        {:keys [name email] :as user} (users/get-user)]
    (send-message (cc/generate-string {:user name
                                       :email email
                                       :ts current-ts}))))

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
