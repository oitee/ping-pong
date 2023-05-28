(ns ping-pong.consumer
  (:require
    [jackdaw.client :as jc]
    [jackdaw.client.log :as jl]
    [cheshire.core :as cc]
    [clojure.walk :as walk])
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

(defn start-consumer []
  (with-open [my-consumer (-> (jc/consumer consumer-config)
                              (jc/subscribe [topic-foo]))]
    (reset! continue? true)
    (doseq [{:keys [value]} (jl/log my-consumer 500 (fn [_]
                                                      @continue?))]
      (let [zmap (->> value
                      cc/parse-string
                      walk/keywordize-keys)]
        (println zmap)
        (println "---")))))


(defn stop-consumer
  []
  (reset! continue? false))
