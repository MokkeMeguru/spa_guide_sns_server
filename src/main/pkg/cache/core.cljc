(ns pkg.cache.core)

(defprotocol ICache
  (-fetch
    [cache k]
    [cache k not-found])
  (-has? [this k])
  (-hit [this k])
  (-miss [this k value])
  (-evict [this k]))

(defn- through-cache
  [cache k value-fn]
  (if (-has? cache k)
    (-hit cache k)
    (-miss cache k (value-fn))))

(defn fetch-or-miss
  "`k` のキャッシュデータを cache-atom から検索します
  ないときには `value-fn` を遅延評価して取得します"
  [cache-atom k value-fn]
  (let [new-value (delay (value-fn))]
    (loop [n 0
           v (-fetch (swap! cache-atom
                            through-cache
                            k
                            (fn [_] @new-value))
                     k
                     ::expired)]
      (when (< n 10)
        (if (= ::expired v)
          (recur (inc n)
                 (-fetch (swap! cache-atom
                                through-cache
                                k
                                (fn [_] @new-value))
                         k
                         ::expired))
          v)))))

(defn evict
  "`k` のキャッシュデータを cache-atom から削除します"
  [cache-atom k]
  (swap! cache-atom -evict k))
