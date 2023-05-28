(ns ping-pong.core
  (:gen-class)
  (:require [ping-pong.producer :as producer]
            [ping-pong.active-users-consumer :as active-users]
            [ping-pong.messages-consumer :as messages]))


;; Function to kick-start the producer
(defn start-sending-messages
  [& {:keys [timeout-interval timeout?]
      :or {timeout-interval 10000
           timeout? true}}]
  (future (producer/send-messages-constantly 1000))
  (when timeout?
    (Thread/sleep timeout-interval)
    (producer/stop-messages)))


(defn start-producer-and-consumer
  []
  (future (start-sending-messages :timeout? false))
  (Thread/sleep 1000)
  (future (active-users/start-consumer))
  (future (messages/start-consumer)))


(defn stop-producer-and-consumers
  []
  (producer/stop-messages)
  (active-users/stop-consumer)
  (messages/stop-consumer))


(defn -main
  "Temporary fn to kick-start and stop the consumers and producer"
  [& args]
  (start-producer-and-consumer))
