(ns infrastructure.sqlite3.util
  (:require ["better-sqlite3" :as better-sqlite3]
            [clojure.string]))

(def activate-foreign-key
  "PRAGMA foreign_keys = ON;")

(defn db!
  ([^string path]
   (db! path false))
  ([^string path ^boolean verbose?]
   (better-sqlite3.
    path
    (if verbose?
      #js{"verbose" (fn [query] (js/console.log (.toISOString (js/Date.)) "INFO" "execute query:" (str query)))}
      #js{}))))
