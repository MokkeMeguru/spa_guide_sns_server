(ns interface.gateway.sqlite3.util)

(defn now []
  (.getTime (js/Date.)))
