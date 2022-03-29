(ns pkg.sql.transaction
  (:require [clojure.spec.alpha :as s]))

(defprotocol ITransactionRepository
  (-transaction [this f]))

(s/fdef transaction
  :args (s/cat :this any? :f fn?)
  :ret fn?)

(defn transaction
  "db transaction のラッパ
  transaction にラップされた関数を返し、
  失敗時には exception を投げます"
  [this f]
  (-transaction this f))
