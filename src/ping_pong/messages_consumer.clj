(ns ping-pong.messages-consumer
  (:require [ping-pong.utils :as utils]
            [ping-pong.kafka-consumer :as consumer])
  (:gen-class))

(def continue? (atom true))

(def ^:const consumer-config
  (assoc consumer/consumer-config "group.id" "com.test.messages-consumer"))


(defn print-message
  [user m]
  (println (format "%s: %s"
                   user
                   m)))

(defn start-print-messages-consumer
  []
  (let [continue-fn (fn [_] @continue?)
        consuming-fn (fn
                       [value]
                       (let [{:keys [user message activity]} (utils/keywordise-payload value)]
                         (when (= activity (:send-message utils/allowed-activities))
                           (print-message user message))))]
    (consumer/start-consuming consumer-config
                              utils/topic
                              continue-fn
                              consuming-fn)))

(defn start-consumer
  []
  (reset! continue? true)
  (future (start-print-messages-consumer)))


(defn stop-consumer
  []
  (reset! continue? false))


(defn -main
  []
  (start-consumer))
