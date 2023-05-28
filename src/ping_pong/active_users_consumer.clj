(ns ping-pong.active-users-consumer
  (:require [clojure.string :as cs]
            [ping-pong.utils :as utils])
  (:import
   (org.apache.kafka.common.serialization Serdes)
   [redis.clients.jedis JedisPooled]))



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

(defn start-active-users-consumer
  []
  (let [continue-fn (fn [_] @continue?)
        consuming-fn (fn
                       [value]
                       (let [{:keys [ts user email]} (utils/keywordise-payload value)]
                         (add-user user email ts)))]
    (utils/start-consuming utils/consumer-config
                           utils/topic
                           continue-fn
                           consuming-fn)))

(defn print-active-users
  []
  (while @continue?
    (let [active-users (get-active-users)]
      (if (empty? active-users)
        (println "No Active Users...")
        (do (println "Active Users:")
            (run! #(print (str % ", ")) (get-active-users))
            (println "\n---"))))
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
