(ns util.string
  (:require [clojure.string :as str]
            [cuerdas.regexp :refer [regexp?]]
            [clojure.spec.alpha :as s]))

(s/fdef strings->string-list
  :args (s/cat :strings string?)
  :ret (s/* string?))

(s/fdef string-list->lower-case-set
  :args (s/cat :string-list (s/* string?)
               :split-regex (s/? regexp?))
  :ret (s/and (s/* string?) set?))

(s/fdef capitalize-words
  :args (s/cat :s string?)
  :ret string?)

(defn strings->string-list
  "comma-separeted string -> string list"
  [strings]
  (->> strings
       (str/split (str strings) #",")
       (remove str/blank?)))

(defn string-list->lower-case-set
  "string list -> lower-case set"
  [string-list]
  (->> string-list
       (map str/trim)
       (map str/lower-case)
       set))

(defn capitalize-words
  "Capitalize every word in a string

  Examples:

  ```
  (= (capitalize-words (name :access-control-request-headers) #\"-\")
     \"Access-Control-Request-Headers\")
  ```
  "
  ([s]
   (capitalize-words s #"\b"))
  ([s split-regex]
   (->> (str/split s split-regex)
        (map str/capitalize)
        (str/join (str/replace (str split-regex) "/" "")))))
