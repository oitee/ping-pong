(ns ping-pong.consumer
  (:require
    [jackdaw.client :as jc]
    [jackdaw.client.log :as jl])
  (:import
    (org.apache.kafka.common.serialization Serdes)))

(def consumer-config
  {"bootstrap.servers" "localhost:9092"
   "group.id"  "com.test.my-consumer"
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

(def topic-foo
  {:topic-name "test"})

(def continue? (atom true))

(defn start-consuming []
  (with-open [my-consumer (-> (jc/consumer consumer-config)
                              (jc/subscribe [topic-foo]))]
    (reset! continue? true)
    (doseq [{:keys [key value partition timestamp offset]} (jl/log my-consumer 500 (fn [_]
                                                                                     @continue?))]
      (println "key: " key)
      (println "value: " value)
      (println "partition: " partition)
      (println "timestamp: " timestamp)
      (println "offset: " offset))))


(defn stop-consumer
  []
  (reset! continue? false))
