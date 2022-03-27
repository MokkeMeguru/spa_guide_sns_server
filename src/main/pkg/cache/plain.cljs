(ns pkg.cache.plain
  (:require [pkg.cache.core :refer [ICache]]
            [taoensso.timbre :refer [info]]))

;; NOTICE: PlainCache は削除機構を持っていないので、レコードが無限大になるとメモリを無限大に使います
(defrecord PlainCache [cache]
  ICache
  (-fetch [_ k]
    (get cache k))
  (-fetch [_ k not-found]
    (get cache k not-found))
  (-has? [_ k]
    (if (contains? cache k)
      (do (info "hit cache: " k "/ cache size: " (count cache)) true)
      (do (info "miss cache: " k "/ cache size: " (count cache)) false)))
  (-hit [this _] this)
  (-miss [_ item result]
    (PlainCache. (assoc cache item result)))
  (-evict [_ key]
    (PlainCache. (dissoc cache key))))

;; (let [cache-atom (atom (->PlainCache {}))]
;;   (println (pkg.cache.core/fetch-or-miss cache-atom :a (fn [] (println "1: hello") (+ 1 1))))
;;   (println (pkg.cache.core/fetch-or-miss cache-atom :a (fn [] (println "2: hello") (+ 1 1)))))

(defn make-plain-cache-atom []
  (atom (->PlainCache {})))
