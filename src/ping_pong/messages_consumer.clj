(ns ping-pong.messages-consumer
  (:require [ping-pong.utils :as utils]))

(def continue? (atom true))

(def ^:const consumer-config
  (assoc utils/consumer-config "group.id" "com.test.messages-consumer"))


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
                       (let [{:keys [user message]} (utils/keywordise-payload value)]
                         (print-message user message)))]
    (utils/start-consuming consumer-config
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
