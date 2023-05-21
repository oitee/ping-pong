(ns ping-pong.users
  (:require [clojure.string :as cs]))

;; List of The Office (US) characters.
;; Taken from: https://en.wikipedia.org/wiki/List_of_The_Office_(American_TV_series)_characters
(def user-names
  ["Michael Scott" "Dwight Schrute" "Jim Halpert" "Pam Beesly" "Ryan Howard"
   "Andy Bernard" "Robert California" "Stanley Hudson" "Kevin Malone"
   "Meredith Palmer" "Angela Martin" "Oscar Martinez" "Phyllis Lappin" "Roy Anderson"
   "Jan Levinson" "Toby Flenderson" "Kelly Kapoor" "Creed Bratton" "Darryl Philbin"
   "Erin Hannon" "Gabe Lewis" "Holly Flax" "Nellie Bertram" "Clark Green"
   "Pete Miller" "Devon White" "Lonny Collins" "Katy Moore" "Todd Packer" "Hank Doyle"
   "Carol Stills" "Bob Vance" "David Wallace" "Mose Schrute" "Helene Beesly"
   "Josh Porter" "Karen Filippelli" "Nick Lynn" "Charles Miner" "Isabel Poreba"
   "Matt Donna" "Jo Bennet" "Robert Lipton" "Nate Nickerson" "Deangelo Vickers"
   "Jordan Garfield" "Val Johnson" "Cathy Simms" "Jessica" "Irene" "Brian"
   "Ziek Schrute" "Esther Breugger"])

(def email-domains
  ["gmail.com" "hotmail.com" "yahoo.com" "aol.com" "msn.com" "live.com" "rediffmail.com"])

(def users (atom []))

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
