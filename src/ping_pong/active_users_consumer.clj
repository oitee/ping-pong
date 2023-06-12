(ns ping-pong.active-users-consumer
  (:require [clojure.string :as cs]
            [ping-pong.kafka-consumer :as consumer]
            [ping-pong.utils :as utils])
  (:import
   (org.apache.kafka.common.serialization Serdes)
   [redis.clients.jedis JedisPooled])
  (:gen-class))


(def continue? (atom true))

(def ^:const consumer-config (assoc consumer/consumer-config "group.id" "com.test.active-users-consumer"))

(def store "sorted:users")

(def jedis nil)

(defn add-user
  [username email score]
  (let [user (format "%s::%s" username email)]
    (.zadd jedis store (double score) user)))


(defn get-active-users
  []
  (let [curr-ts (double (System/currentTimeMillis))
        cutoff-ts (double (- curr-ts 30000))
        active-users (.zrangeByScore jedis store cutoff-ts curr-ts)]
    ;; Remove users who were active before the cutoff time-stamp
    (.zremrangeByScore jedis store Double/NEGATIVE_INFINITY (dec cutoff-ts))
    (map #(first (cs/split % #"::")) active-users)))


(defn start-active-users-consumer
  []
  (let [continue-fn (fn [_] @continue?)
        consuming-fn (fn
                       [value]
                       (let [{:keys [ts user email]} (utils/keywordise-payload value)]
                         (add-user user email ts)))]
    (consumer/start-consuming consumer-config
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
            (println (clojure.string/join "," active-users))
            (println "\n---"))))
    (Thread/sleep 3000))
  (println "----xx---"))


(defn setup-jedis
  []
  (alter-var-root #'jedis (constantly (JedisPooled. utils/redis-host utils/redis-port))))

(defn start-consumer
  []
  (reset! continue? true)
  (future (start-active-users-consumer))
  (future (doall (print-active-users))))


(defn stop-consumer
  []
  (reset! continue? false))


(defn -main
  []
  (start-consumer))
