(ns ping-pong.producer
  (:require [jackdaw.client :as jc]
            [cheshire.core :as cc]
            [clj-http.client :as http-client]
            [clojure.walk :as walk]
            [ping-pong.users :as users]
            [ping-pong.utils :as utils]))

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
    @(jc/produce! my-producer utils/topic m)))


(defn create-and-send-messages
  [interval]
  (while @continue?
    (let [current-ts (System/currentTimeMillis)
          {:keys [name email]} (users/get-user)
          user-message (get-user-message)
          activity (:send-message utils/allowed-activities)]
      (send-message (cc/generate-string {:user name
                                         :email email
                                         :activity activity
                                         :message user-message
                                         :ts current-ts})))
    (Thread/sleep interval)))


(defn send-heartbeats
  []
  (while @continue?
    (let [current-ts (System/currentTimeMillis)
          {:keys [name email]} (users/get-user)
          min-interval 1000
          rand-interval (+ min-interval (rand-int 7000))
          activity (:heart-beat utils/allowed-activities)]
      (send-message (cc/generate-string {:user name
                                         :email email
                                         :activity activity
                                         :ts current-ts}))
      (Thread/sleep rand-interval))))

(defn send-messages-constantly
  [interval]
  (reset! continue? true)
  (future (send-heartbeats))
  (future (create-and-send-messages interval)))

(defn stop-messages
  []
  (reset! continue? false))
