(ns interface.gateway.sqlite3.community-event
  (:require ["better-sqlite3" :as better-sqlite3]
            [clojure.string]
            [clojure.set]
            [clojure.walk]
            [clojure.spec.alpha :as s]
            [taoensso.timbre :refer [warn]]
            [domain.community.event :refer [ICommunityEventQueryRepository ICommunityEventCommandRepository]]
            [interface.gateway.sqlite3.util]
            [interface.gateway.sqlite3.community]
            [interface.gateway.sqlite3.community-member]))

(s/fdef db->domain
  :args (s/cat :db-model map?)
  :ret ::domain.community.event/query)

(s/fdef domain->db
  :args (s/cat :domain-model ::domain.community.event/command)
  :ret map?)

(def category-map
  (let [domain->db {:party 1 :seminar 2}
        db->domain (clojure.set/map-invert domain->db)]
    {:db->domain db->domain
     :domain->db domain->db}))

(defn db->domain [db-model]
  (when db-model
    (let [{:keys [id community_id owned_member_id name details
                  hold_at category image_url created_at updated_at]}
          (clojure.walk/keywordize-keys db-model)]
      {:id id
       :community-id community_id
       :owned-member-id owned_member_id
       :name name
       :details details
       :hold-at hold_at
       :category (get (:db->domain category-map) category)
       :image-url image_url
       :created-at created_at
       :updated-at updated_at})))

(defn domain->db [domain-model]
  (when domain-model
    (let [{:keys [id community-id owned-member-id name details hold-at category image-url]} domain-model]
      {:id (if id id (str (random-uuid)))
       :community_id community-id
       :owned_member_id owned-member-id
       :name name
       :details details
       :hold_at hold-at
       :category (get (:domain->db category-map) category)
       :image_url image-url
       :created_at (interface.gateway.sqlite3.util/now)
       :updated_at (interface.gateway.sqlite3.util/now)})))

(def sql-map
  (let [base "\nSELECT * FROM community_events"]
    {:list base
     :fetch (clojure.string/join "\n" [base "WHERE id = ?"])
     :search-by-community-id (clojure.string/join "\n" [base "WHERE community_id = ?"])
     :search-part-by-community-id
     {:limit "LIMIT ?"
      :order {:updated-at-desc "ORDER BY updated_at DESC"
              :updated-at-asc "ORDER BY updated_at ASC"}
      :where {:cursor {:updated-at-desc "updated_at <= ?"
                       :updated-at-asc "updated_at >= ?"}}}
     :create "
INSERT INTO community_events
 (id, community_id, owned_member_id, name, details, hold_at, category, image_url, created_at, updated_at)
VALUES (@id, @community_id, @owned_member_id, @name, @details, @hold_at, @category, @image_url, @created_at, @updated_at)"}))

(defn build-sql-list-part-community-event-by-community-id [request-size from-cursor-updated-at sort-order]
  (clojure.string/join
   "\n"
   (cond-> []
     true (conj (-> sql-map :list))
     (some? from-cursor-updated-at)
     (conj (str "WHERE " (-> sql-map :search-part-by-community-id :where :cursor sort-order)))
     (= :updated-at-asc sort-order) (conj (-> sql-map :search-part-by-community-id :order :updated-at-asc))
     (not= :updated-at-asc sort-order) (conj (-> sql-map :search-part-by-community-id :order :updated-at-desc))
     (some? request-size) (conj (-> sql-map :search-part-by-community-id :limit)))))

(defrecord CommunityEventQueryRepository [db]
  ICommunityEventQueryRepository
  (-list-community-event [this]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare (:list sql-map)) (.all) (js->clj)))))
  (-fetch-community-event [this event-id]
    (let [^js/better-sqlite3 db (:db this)]
      (-> db (.prepare (:fetch sql-map)) (.get event-id) (js->clj) (db->domain))))
  (-search-community-event-by-community-id [this community-id]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare (:search-by-community-id sql-map)) (.all community-id) (js->clj)))))
  (-search-part-community-event-by-community-id [this community-id request-size from-cursor sort-order]
    (let [^js/better-sqlite3 db (:db this)]
      [])))

(defrecord CommunityEventCommandRepository [db]
  ICommunityEventCommandRepository
  (-create-community-event [this event]
    (let [^js/better-sqlite3 db (:db this)
          db-model (domain->db event)]
      (try
        (-> db (.prepare (:create sql-map)) (.run (clj->js db-model)))
        (try (-> db (.prepare (:fetch sql-map)) (.get (:id db-model)) (js->clj) (db->domain))
             (catch js/Error e
               (warn "insert result cannot fetched" e)
               {:id (:id db-model)}))
        (catch js/Error e
          (warn "insert failed" e)
          nil)))))

(defn make-community-event-query-repository [db]
  (->CommunityEventQueryRepository db))

(defn make-community-event-command-repository [db]
  (->CommunityEventCommandRepository db))
