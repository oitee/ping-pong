(ns ping-pong.producer
  (:require [jackdaw.client :as jc]
            [cheshire.core :as cc]
            [clj-http.client :as http-client]
            [clojure.walk :as walk]
            [ping-pong.users :as users]))

(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"
   "client.id" "foo"})

(def ^:const topic "test")

(def continue? (atom true))

(def quotes-url "https://favqs.com/api/qotd")

(defn get-user-message
  []
  (let [response (http-client/get quotes-url)]
    (if (= (:status response) 200)
      (-> response
          :body
          cc/parse-string
          walk/keywordize-keys
          (get-in [:quote :body]))
      "")))

(defn send-message [m]
  (with-open [my-producer (jc/producer producer-config)]
    @(jc/produce! my-producer {:topic-name topic} m)))

(defn create-and-send-message
  []
  (let [current-ts (System/currentTimeMillis)
        {:keys [name email] :as user} (users/get-user)
        user-message (get-user-message)]
    (send-message (cc/generate-string {:user name
                                       :email email
                                       :message user-message
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
