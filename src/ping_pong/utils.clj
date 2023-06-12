(ns ping-pong.utils
  (:require [cheshire.core :as cc]
            [clojure.walk :as walk]))

(def ^:const allowed-activities
  {:heart-beat 0
   :send-message 1})


(def topic
  {:topic-name "test"})


(defn keywordise-payload
  [p]
  (->> p
       cc/parse-string
       walk/keywordize-keys))

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
