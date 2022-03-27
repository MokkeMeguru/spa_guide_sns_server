(ns interface.gateway.sqlite3.util)

(defn now []
  (.getTime (js/Date.)))

(defn apply-all
  "
  better-sqlite3 の (statement).all に可変長の引数を与えたいときに使う関数

  Example:


      (.all prepare args_0 args_1 args_2)

  "
  [statement args]
  (condp = (count args)
    0 (.all statement)
    1 (.all statement (nth args 0))
    2 (.all statement (nth args 0) (nth args 1))
    3 (.all statement (nth args 0) (nth args 1) (nth args 2))
    4 (.all statement (nth args 0) (nth args 1) (nth args 2) (nth args 3))
    5 (.all statement (nth args 0) (nth args 1) (nth args 2) (nth args 3) (nth args 4))))
