(ns interface.gateway.sqlite3.community
  (:require [domain.community :refer [ICommunityQueryRepository ICommunityCommandRepository]]
            [clojure.spec.alpha :as s]
            [clojure.set]
            [clojure.walk]
            [clojure.string]
            [interface.gateway.sqlite3.util]
            [taoensso.timbre :refer [warn]]
            ["better-sqlite3" :as better-sqlite3]))

(def sql-map
  {:list "SELECT * FROM communities"
   :list-part-community
   {:limit "LIMIT ?"
    :order {:updated-at-desc "ORDER BY updated_at DESC"
            :updated-at-asc "ORDER BY updated_at ASC"}
    :where {:cursor {:updated-at-desc "updated_at < ?"
                     :updated-at-asc "updated_at > ?"}
            :keyword "(name LIKE ? OR details LIKE ?)"}}})

(defn build-sql-list-part-community [request-size from-cursor-updated-at sort-order keyword]
  (clojure.string/join
   " "
   (cond-> []
     true (conj (-> sql-map :list))
     (or (some? from-cursor-updated-at) (some? keyword))
     (conj (str "WHERE "
                (clojure.string/join
                 " AND "
                 (cond-> []
                   (some? from-cursor-updated-at) (conj (-> sql-map :list-part-community :where :cursor sort-order))
                   (some? keyword) (conj (-> sql-map :list-part-community :where :keyword))))))
     (= :updated-at-asc sort-order) (conj (-> sql-map :list-part-community :order :updated-at-asc))
     (not= :updated-at-asc sort-order) (conj (-> sql-map :list-part-community :order :updated-at-desc))
     (some? request-size) (conj (-> sql-map :list-part-community :limit)))))

(s/fdef db->domain
  :args map?
  :ret ::domain.community/query)

(s/fdef domain->db
  :args ::domain.community/query
  :ret map?)

(def category-map
  (let [domain->db {:gurmand 1
                    :sports 2
                    :geek 3
                    :anime 4}
        db->domain (clojure.set/map-invert domain->db)]
    {:db->domain db->domain
     :domain->db domain->db}))

(defn db->domain [db-model]
  (when db-model
    (let [{:keys [id name details category image_url created_at updated_at]} (clojure.walk/keywordize-keys db-model)]
      {:id id
       :name name
       :details details
       :category (get (:db->domain category-map) category)
       :image-url image_url
       :created-at created_at
       :updated-at updated_at})))

(defn domain->db [domain-model]
  (when domain-model
    (let [{:keys [id name details category image-url]} domain-model]
      {:id (if id id (str (random-uuid)))
       :name name
       :details details
       :category (get (:domain->db category-map) category)
       :image_url image-url
       :created_at (interface.gateway.sqlite3.util/now)
       :updated_at (interface.gateway.sqlite3.util/now)})))

(defrecord CommunityQueryRepository [db]
  ICommunityQueryRepository
  (-list-community [this]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare "SELECT * FROM communities") (.all) (js->clj)))))
  (-list-part-community [this request-size from-cursor sort-order keyword]
    (let [^js/better-sqlite3 db (:db this)
          from-cursor-community (domain.community/fetch-community this from-cursor)
          keyword (when keyword (str "%" keyword "%"))
          query (build-sql-list-part-community request-size (:updated-at from-cursor-community) sort-order keyword)
          prepare (.prepare db query)]
      (map db->domain
           (js->clj
            ;; TODO 何故か apply で解決できなかったので原因を調べる
            (cond
              (and (some? from-cursor) (some? keyword)) (-> prepare (.all (:updated-at from-cursor-community) keyword keyword request-size))
              (some? from-cursor) (-> prepare (.all (:updated-at from-cursor-community) request-size))
              (some? keyword) (-> prepare (.all keyword keyword request-size))
              :else (-> prepare (.all request-size)))))))
  (-fetch-community [this community-id]
    (let [^js/better-sqlite3 db (:db this)]
      (-> db (.prepare "SELECT * FROM communities WHERE id = ?") (.get community-id) (js->clj) (db->domain))))
  (-search-communities-by-name [this like]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare "SELECT * FROM communities WHERE name like ?") (.all (str "%" like "%")) (js->clj)))))
  (-size-community [this] nil)
  (-before-size-community [this before-cursor] nil))

(defrecord CommunityCommandRepository [db]
  ICommunityCommandRepository
  (-create-community [this community]
    (let [^js/better-sqlite3 db (:db this)
          db-model (domain->db community)]
      (try
        (-> db (.prepare "INSERT INTO communities (id, name, details, category, image_url, created_at, updated_at) VALUES (@id, @name, @details, @category, @image_url, @created_at, @updated_at)") (.run (clj->js db-model)))
        (try (-> db (.prepare "SELECT * FROM communities WHERE id = ?") (.get (:id db-model)) (js->clj) (db->domain))
             (catch js/Error e
               (warn "insert result cannot fetched" e)
               (db->domain db-model)))
        (catch js/Error e
          (warn "insert failed" e))))))

(defn make-community-query-repository [^js/better-sqlite3 db]
  (->CommunityQueryRepository db))

(defn make-community-command-repository [^js/better-sqlite3 db]
  (->CommunityCommandRepository db))
