(ns ping-pong.users
  (:require [clojure.string :as cs]
            [ping-pong.utils :refer [user-names email-domains]]))

(defonce users (atom []))

(defn- get-users
  []
  (when (empty? @users)
    (run! (fn [u] (let [email-domain (get email-domains (rand-int (count email-domains)))
                        u-name (-> u
                                   cs/lower-case
                                   (cs/replace #" " "."))
                        email (format "%s@%s"
                                      u-name
                                      email-domain)]
                    (swap! users conj {:name u
                                       :email email})))
          user-names))
  @users)


(defn get-user
  []
  (let [users (get-users)
        user-count (count users)]
    (get users (rand-int user-count))))
