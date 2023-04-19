(ns ping-pong.core
  (:gen-class)
  (:require [ping-pong.producer :as producer]
            [ping-pong.consumer :as consumer]))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (str "To start sending messages, call `start-sending-messages` fn"))


;; Function to kick-start the producer
(defn start-sending-messages
  [& {:keys [timeout-interval timeout?]
      :or {timeout-interval 10000
           timeout? true}}]
  (future (producer/send-messages-constantly 1000))
  (when timeout?
    (do (Thread/sleep timeout-interval)
        (producer/stop-messages))))

;; Function to kick-start the consumer
(defn start-consumer
  []
  (future (consumer/start-consuming)))
