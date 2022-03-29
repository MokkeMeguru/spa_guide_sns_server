(ns interface.gateway.sqlite3.transaction
  (:require [pkg.sql.transaction :refer [ITransactionRepository]]))

(defrecord TransactionRepository [db]
  ITransactionRepository
  (-transaction [this f]
    (let [^js/better-sqlite3 db (:db this)]
      (.transaction db f))))
