(ns ping-pong.core
  (:gen-class)
  (:require [ping-pong.producer :as producer]))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (str "To start sending messages, call `start-sending-messages` fn"))

(defn start-sending-messages
  [& {:keys [timeout-interval timeout?]
      :or {timeout-interval 10000
           timeout? true}}]
  (future (producer/send-messages-constantly 1000))
  (when timeout?
    (do (Thread/sleep timeout-interval)
        (producer/stop-messages))))
