(ns interface.gateway.sqlite3.community
  (:require [domain.community :refer [ICommunityQueryRepository ICommunityCommandRepository]]
            [clojure.spec.alpha :as s]
            [clojure.set]
            [clojure.walk]
            [clojure.string]
            [interface.gateway.sqlite3.util]
            [taoensso.timbre :refer [warn]]
            ["better-sqlite3" :as better-sqlite3]))

;; domain mapping
(s/fdef db->domain
  :args (s/cat :db-model any?)
  :ret ::domain.community/query)

(s/fdef domain->db
  :args (s/cat :domain-model ::domain.community/command)
  :ret any?)

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
    (let [{:keys [id name details category image_url created_at updated_at membership]}
          (clojure.walk/keywordize-keys db-model)]
      {:id id
       :name name
       :details details
       :category (get (:db->domain category-map) category)
       :image-url image_url
       :created-at created_at
       :updated-at updated_at
       :membership membership})))

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

;; build sql query
(def sql-map
  (let [base-select
        "\nSELECT
  communities.id AS id,
  communities.name AS name,
  communities.details AS details,
  communities.category AS category,
  communities.image_url AS image_url,
  communities.created_at AS created_at,
  communities.updated_at AS updated_at,
  COUNT(*) AS membership
FROM communities
LEFT JOIN community_members
ON communities.id=community_members.community_id"
        base-group  "GROUP BY communities.id"]
    {:base {:select base-select
            :group base-group}
     :list (clojure.string/join "\n" [base-select base-group])
     :list-part
     {:limit "LIMIT ?"
      :order {:updated-at-desc "ORDER BY communities.updated_at DESC"
              :updated-at-asc "ORDER BY communities.updated_at ASC"}
      :where {:cursor {:updated-at-desc "communities.updated_at <= ?"
                       :updated-at-asc "communities.updated_at >= ?"}
              :keyword "(communities.name LIKE ? OR communities.details LIKE ?)"}}
     :fetch (clojure.string/join "\n" [base-select "WHERE communities.id = ?" base-group])
     :size {:base "SELECT COUNT(*) AS total_size from communities"
            :keyword "WHERE (communities.name LIKE ? OR communities.details LIKE ?)"}
     :before-size {:basic "SELECT COUNT(*) AS before_size, (SELECT COUNT(*) FROM communities) AS total_size from communities WHERE updated_at > ?"
                   :with-keyword "\nSELECT
COUNT(*) AS before_size,
(SELECT COUNT(*) FROM communities WHERE (communities.name LIKE ? OR communities.details LIKE ?)) AS total_size
FROM communities WHERE updated_at > ?
AND (communities.name LIKE ? OR communities.details LIKE ?)"}
     :create "INSERT INTO communities (id, name, details, category, image_url, created_at, updated_at) VALUES (@id, @name, @details, @category, @image_url, @created_at, @updated_at)"}))

(defn build-sql-list-part-community [request-size from-cursor-updated-at sort-order keyword]
  (clojure.string/join
   "\n"
   (cond-> []
     true (conj (-> sql-map :base :select))
     (or (some? from-cursor-updated-at) (some? keyword))
     (conj (str "WHERE "
                (clojure.string/join
                 " AND "
                 (cond-> []
                   (some? from-cursor-updated-at) (conj (-> sql-map :list-part :where :cursor sort-order))
                   (some? keyword) (conj (-> sql-map :list-part :where :keyword))))))
     true (conj (-> sql-map :base :group))
     (= :updated-at-asc sort-order) (conj (-> sql-map :list-part :order :updated-at-asc))
     (not= :updated-at-asc sort-order) (conj (-> sql-map :list-part :order :updated-at-desc))
     (some? request-size) (conj (-> sql-map :list-part :limit)))))

;; impl
(defrecord CommunityQueryRepository [db]
  ICommunityQueryRepository
  (-list-community [this]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare (-> sql-map :list)) (.all) (js->clj)))))
  (-list-part-community [this request-size from-cursor sort-order keyword]
    (let [^js/better-sqlite3 db (:db this)
          from-cursor-community (domain.community/fetch-community this from-cursor)
          query-keyword (when keyword (str "%" keyword "%"))
          query (build-sql-list-part-community request-size (:updated-at from-cursor-community) sort-order query-keyword)
          args (filter some? [(:updated-at from-cursor-community) query-keyword query-keyword request-size])]
      (map db->domain (js->clj (interface.gateway.sqlite3.util/apply-all (.prepare db query) args)))))
  (-fetch-community [this community-id]
    (let [^js/better-sqlite3 db (:db this)]
      (-> db (.prepare (-> sql-map :fetch)) (.get community-id) (js->clj) (db->domain))))
  (-size-community [this keyword]
    (let [^js/better-sqlite3 db (:db this)]
      (if keyword
        (-> db (.prepare (clojure.string/join " " [(-> sql-map :size :base) (-> sql-map :size :keyword)])) (.get (str "%" keyword "%")  (str "%" keyword "%")) (js->clj) (get "total_size" 0))
        (-> db (.prepare (-> sql-map :size :base)) (.get) (js->clj) (get "total_size" 0)))))
  (-before-size-community [this community keyword]
    (let [^js/better-sqlite3 db (:db this)
          partial-match-keyword (str "%" keyword "%")
          db->domain (fn [{:keys [before_size total_size]}] {:before-size before_size :total-size total_size})]
      (cond
        (nil? keyword) (-> db (.prepare (-> sql-map :before-size :basic))
                           (.get (:updated-at community))
                           (js->clj)
                           clojure.walk/keywordize-keys
                           db->domain)
        :else (-> db (.prepare (-> sql-map :before-size :with-keyword))
                  (.get
                   partial-match-keyword
                   partial-match-keyword
                   (:updated-at community)
                   partial-match-keyword
                   partial-match-keyword)
                  (js->clj)
                  clojure.walk/keywordize-keys
                  db->domain)))))

(defrecord CommunityCommandRepository [db]
  ICommunityCommandRepository
  (-create-community [this community]
    (let [^js/better-sqlite3 db (:db this)
          db-model (domain->db community)]
      (try
        (-> db (.prepare (-> sql-map :create)) (.run (clj->js db-model)))
        (try (-> db (.prepare (-> sql-map :fetch)) (.get (:id db-model)) (js->clj) (db->domain))
             (catch js/Error e
               (warn "insert result cannot fetched" e)
               (db->domain db-model)))
        (catch js/Error e
          (warn "insert failed" e))))))

(defn make-community-query-repository [^js/better-sqlite3 db]
  (->CommunityQueryRepository db))

(defn make-community-command-repository [^js/better-sqlite3 db]
  (->CommunityCommandRepository db))
