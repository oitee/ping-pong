(ns ping-pong.active-users-consumer
  (:require [jackdaw.client :as jc]
            [jackdaw.client.log :as jl]
            [cheshire.core :as cc]
            [clojure.string :as cs]
            [clojure.walk :as walk])
  (:import
   (org.apache.kafka.common.serialization Serdes)
   [redis.clients.jedis JedisPooled]))


;; @TODO: Add to utils: kafka consumer config
(def consumer-config
  {"bootstrap.servers" "localhost:9092"
   "group.id"  "com.test.my-consumer"
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

;; @TODO: Add to utils: kafka topic
(def topic-foo
  {:topic-name "test"})

(def continue? (atom true))


(def store "sorted:users")

(def jedis (JedisPooled. "localhost" 6379))

(.zadd jedis store (double (System/currentTimeMillis)) "admin:admin@email.com")


(defn add-user
  [username email score]
  (let [user (format "%s::%s" username email)]
    (.zadd jedis store (double score) user)))


(defn get-active-users
  []
  (let [curr-ts (double (System/currentTimeMillis))
        cutoff-ts (double (- curr-ts 30000))
        active-users (.zrangeByScore jedis store cutoff-ts curr-ts)]
    (map #(first (cs/split % #"::")) active-users)))

;; @TOOD: Add fn to delete old keys in redis


(defn start-active-users-consumer []
  (with-open [my-consumer (-> (jc/consumer consumer-config)
                              (jc/subscribe [topic-foo]))]
    (reset! continue? true)
    (doseq [{:keys [value]} (jl/log my-consumer 500 (fn [_]
                                                      @continue?))]
      (let [{:keys [ts user email]} (->> value
                                         cc/parse-string
                                         walk/keywordize-keys)]
        (add-user user email ts)))))


(defn print-active-users
  []
  (while @continue?
    (println "\n Active Users:")
    (run! #(print (str % ", ")) (get-active-users))
    (Thread/sleep 3000))
  (println "----xx---"))


(defn start-consumer
  []
  (reset! continue? true)
  (future (start-active-users-consumer))
  (future (doall (print-active-users))))


(defn stop-consumer
  []
  (reset! continue? false))
