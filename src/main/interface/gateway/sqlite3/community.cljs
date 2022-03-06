(ns interface.gateway.sqlite3.community
  (:require [domain.community :refer [ICommunityQueryRepository ICommunityCommandRepository]]
            [clojure.spec.alpha :as s]
            [clojure.set]
            [clojure.walk]
            [interface.gateway.sqlite3.util]
            [taoensso.timbre :refer [warn]]
            ["better-sqlite3" :as better-sqlite3]))

(s/fdef db->domain
  :args map?
  :ret ::domain.community/query)

(s/fdef domain->db
  :args ::domain.community/query
  :ret map?)

(def categories-map
  (let [domain->db {:gurmand 1
                    :sports 2
                    :geek 3
                    :anime 4}
        db->domain (clojure.set/map-invert domain->db)]
    {:db->domain db->domain
     :domain->db domain->db}))

(defn db->domain [db-model]
  (when db-model
    (let [{:keys [id name details categories created_at updated_at]} (clojure.walk/keywordize-keys db-model)]
      {:id id
       :name name
       :details details
       :categories (get (:db->domain categories-map) categories)
       :created-at created_at
       :updated-at updated_at})))

(defn domain->db [domain-model]
  (when domain-model
    (let [{:keys [id name details categories]} domain-model]
      {:id (if id id (str (random-uuid)))
       :name name
       :details details
       :categories (get (:domain->db categories-map) categories)
       :created_at (interface.gateway.sqlite3.util/now)
       :updated_at (interface.gateway.sqlite3.util/now)})))

(defrecord CommunityQueryRepository [db]
  ICommunityQueryRepository
  (-list-community [this]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare "SELECT * FROM communities") (.all) (js->clj)))))
  (-fetch-community [this community-id]
    (let [^js/better-sqlite3 db (:db this)]
      (-> db (.prepare "SELECT * FROM communities WHERE id = ?") (.get community-id) (js->clj) (db->domain))))
  (-search-communities-by-name [this like]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare "SELECT * FROM communities WHERE name like ?") (.all (str "%" like "%")) (js->clj))))))

(defrecord CommunityCommandRepository [db]
  ICommunityCommandRepository
  (-create-community [this community]
    (let [^js/better-sqlite3 db (:db this)
          db-model (domain->db community)]
      (try
        (-> db (.prepare "INSERT INTO communities (id, name, details, categories, created_at, updated_at) VALUES (@id, @name, @details, @categories, @created_at, @updated_at)") (.run (clj->js db-model)))
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
