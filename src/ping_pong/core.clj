(ns ping-pong.core
  (:gen-class)
  (:require [ping-pong.producer :as producer]
            [overtone.at-at :as at]))

(defn initialise-pool
  []
  (def my-pool (at/mk-pool)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Initialising pool....")
  (initialise-pool)
  (str "To start sending messages, call `start-sending-messages` fn"))

(def my-pool (at/mk-pool))

(defn start-sending-messages
  [& {:keys [timeout-interval timeout?]
      :or {timeout-interval 3000
           timeout? true}}]
  (producer/send-messages-constantly 1000 my-pool)
  (when timeout?
    (do (Thread/sleep timeout-interval)
        (producer/stop-messages my-pool))))
