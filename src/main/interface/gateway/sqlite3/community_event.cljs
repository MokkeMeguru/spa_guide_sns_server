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
      :order {:hold-at-desc "ORDER BY hold_at DESC"
              :hold-at-asc "ORDER BY hold_at ASC"}
      :where {:cursor {:hold-at-desc "hold_at < ?"
                       :hold-at-asc "hold_at > ?"}}}
     :size "\nSELECT COUNT(*) AS total_size FROM community_events WHERE community_id = ?"
     :before-size "\nSELECT COUNT(*) AS before_size, (SELECT COUNT(*) FROM community_events WHERE community_id = ?) AS total_size FROM community_events WHERE community_id = ? AND hold_at > ?"
     :create "
INSERT INTO community_events
 (id, community_id, owned_member_id, name, details, hold_at, category, image_url, created_at, updated_at)
VALUES (@id, @community_id, @owned_member_id, @name, @details, @hold_at, @category, @image_url, @created_at, @updated_at)"}))

(defn build-sql-search-part-community-event-by-community-id [request-size from-cursor-updated-at sort-order]
  (clojure.string/join
   "\n"
   (cond-> []
     true (conj (-> sql-map :list))
     true (conj (str "WHERE " "community_id = ?"))
     (some? from-cursor-updated-at)
     (conj (str " AND " (-> sql-map :search-part-by-community-id :where :cursor sort-order)))
     (= :hold-at-asc sort-order) (conj (-> sql-map :search-part-by-community-id :order :hold-at-asc))
     (not= :hold-at-asc sort-order) (conj (-> sql-map :search-part-by-community-id :order :hold-at-desc))
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
    (let [^js/better-sqlite3 db (:db this)
          query (build-sql-search-part-community-event-by-community-id
                 request-size from-cursor sort-order)
          args (filter some? [community-id (:hold-at from-cursor) request-size])]
      (map (comp db->domain js->clj)
           (interface.gateway.sqlite3.util/apply-all
            (-> db (.prepare query)) args))))
  (-size-community-event [this community-id]
    (let [^js/better-sqlite3 db (:db this)]
      (-> db (.prepare (-> sql-map :size)) (.get community-id) (js->clj) (get "total_size" 0))))
  (-before-size-community-event [this community-event community-id]
    (let [^js/better-sqlite3 db (:db this)
          db->domain (fn [{:keys [before_size total_size]}] {:before-size before_size :total-size total_size})]
      (-> db (.prepare (-> sql-map :before-size))
          (.get community-id community-id (:hold-at community-event))
          (js->clj)
          clojure.walk/keywordize-keys
          db->domain))))

(defrecord CommunityEventCommandRepository [db]
  ICommunityEventCommandRepository
  (-create-community-event [this event]
    (let [^js/better-sqlite3 db (:db this)
          db-model (domain->db event)]
      (try
        (-> db (.prepare (:create sql-map)) (.run (clj->js db-model)))
        (:id db-model)
        (catch js/Error e
          (warn "insert failed" e) nil)))))

(defn make-community-event-query-repository [db]
  (->CommunityEventQueryRepository db))

(defn make-community-event-command-repository [db]
  (->CommunityEventCommandRepository db))
