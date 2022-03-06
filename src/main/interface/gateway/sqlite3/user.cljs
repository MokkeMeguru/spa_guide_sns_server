(ns interface.gateway.sqlite3.user
  (:require [domain.user :refer [IUserQueryRepository IUserCommandRepository]]
            [clojure.walk]
            [clojure.string]
            [clojure.spec.alpha :as s]
            [interface.gateway.sqlite3.util]
            [taoensso.timbre :refer [warn]]
            ["better-sqlite3" :as better-sqlite3]))

(s/fdef db->domain
  :args (s/cat :db-model map?)
  :ret ::domain.user/query)

(s/fdef domain->db
  :args (s/cat :domain-model ::domain.user/command)
  :ret map?)

(defn db->domain [db-model]
  (when db-model
    (let [{:keys [id name icon_url created_at updated_at]} (clojure.walk/keywordize-keys db-model)]
      {:id id
       :name name
       :icon-url icon_url
       :created-at created_at
       :updated-at updated_at})))

(defn domain->db [domain-model]
  (when domain-model
    (let [{:keys [id name icon-url]} domain-model]
      {:id (if id id (str (random-uuid))) ;; FOR INJECT DATA
       :name name
       :icon_url icon-url
       :created_at (interface.gateway.sqlite3.util/now)
       :updated_at (interface.gateway.sqlite3.util/now)})))

(defrecord UserQueryRepository [db]
  IUserQueryRepository
  (-list-user [this]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare "SELECT * FROM users") (.all) (js->clj)))))
  (-fetch-user [this user-id]
    (let [^js/better-sqlite3 db (:db this)]
      (-> db (.prepare "SELECT * FROM users WHERE id = ?") (.get user-id) (js->clj) (db->domain))))
  (-fetch-users [this user-ids] []
    (let [^js/better-sqlite3 db (:db this)
          params (str (clojure.string/join ", " (repeat (count user-ids)  "?")))]
      (map db->domain (-> db (.prepare (str "SELECT * FROM users WHERE id IN (" params ")")) (.all (clj->js user-ids)) (js->clj))))))

(defrecord UserCommandRepository [db]
  IUserCommandRepository
  (-create-user [this user]
    (when (s/valid? ::domain.user/command user)
      (let [^js/better-sqlite3 db (:db this)
            db-model (domain->db user)]
        (try
          (-> db (.prepare "INSERT INTO users (id, name, icon_url, created_at, updated_at) VALUES (@id, @name, @icon_url, @created_at, @updated_at)") (.run (clj->js db-model)))
          (try (-> db (.prepare "SELECT * FROM users WHERE id = ?") (.get (:id db-model)) (js->clj) (db->domain))
               (catch js/Error e
                 (warn "insert result cannot fetched" e)
                 (db->domain db-model)))
          (catch js/Error e
            (warn "insert failed" e)
            nil))))))

(defn make-user-query-repository [db]
  (->UserQueryRepository db))

(defn make-user-command-repository [db]
  (->UserCommandRepository db))
