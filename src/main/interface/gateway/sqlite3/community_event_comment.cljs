(ns interface.gateway.sqlite3.community-event-comment
  (:require ["better-sqlite3" :as better-sqlite3]
            [clojure.set]
            [clojure.string]
            [clojure.walk]
            [clojure.spec.alpha :as s]
            [domain.community.event.comment :refer [ICommunityEventCommentQueryRepository ICommunityEventCommentCommandRepository]]
            [interface.gateway.sqlite3.util]
            [interface.gateway.sqlite3.community]
            [interface.gateway.sqlite3.community-member]
            [taoensso.timbre :refer [warn]]))

(s/fdef db->domain
  :args (s/cat :db-model map?)
  :ret ::domain.community.event.comment/query)

(s/fdef domain->db
  :args (s/cat :domain-model ::domain.community.event.comment/command)
  :ret map?)

(defn db->domain [db-model]
  (when db-model
    (let [{:keys [id event_id member_id body comment_at created_at updated_at]}
          (clojure.walk/keywordize-keys db-model)]
      {:id id
       :event-id event_id
       :member-id member_id
       :body body
       :comment_at comment_at
       :created_at created_at
       :updated_at updated_at})))

(defn domain->db [domain-model]
  (when domain-model
    (let [{:keys [id event-id member-id body]} domain-model]
      {:id (if id id (str (random-uuid)))
       :event_id event-id
       :member_id member-id
       :body body
       :comment_at (interface.gateway.sqlite3.util/now)
       :created_at (interface.gateway.sqlite3.util/now)
       :updated_at (interface.gateway.sqlite3.util/now)})))

(def sql-map
  (let [list "
SELECT
 *
FROM community_event_comments"]
    {:list list
     :fetch (str list " WHERE id = ?")
     :fetch-by-event-id (str list " WHERE event_id = ?")
     :fetch-by-event-ids
     (fn [event-ids] (let [params (str (clojure.string/join ", " (repeat (count event-ids)  "?")))]
                       (str list " WHERE event_id IN (" params ")")))
     :create "
INSERT INTO community_event_comments
 (id, event_id, member_id, body, comment_at, created_at, updated_at)
VALUES (@id, @event_id, @member_id, @body, @comment_at, @created_at, @updated_at)"}))

(defrecord CommunityEventCommentQueryRepository [db]
  ICommunityEventCommentQueryRepository
  (-list-community-event-comment [this]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare (:list sql-map)) (.all) (js->clj)))))
  (-fetch-community-event-comment [this comment-id]
    (let [^js/better-sqlite3 db (:db this)]
      (-> db (.prepare (:fetch sql-map)) (.get comment-id) (js->clj) (db->domain))))

  (-fetch-community-event-comment-by-event-id [this event-id]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare (:fetch-by-event-id sql-map)) (.all event-id) (js->clj)))))
  (-fetch-community-event-comment-by-event-ids [this event-ids]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare ((:fetch-by-event-ids sql-map) event-ids)) (.all (clj->js event-ids)) (js->clj))))))

(defrecord CommunityEventCommentCommandRepository [db]
  ICommunityEventCommentCommandRepository
  (-create-community-event-comment [this command]
    (let [^js/better-sqlite3 db (:db this)
          db-model (domain->db command)]
      (try
        (-> db (.prepare (:create sql-map)) (.run (clj->js db-model)))
        (:id db-model)
        (catch js/Error e
          (warn "insert failed" e) nil)))))

(defn make-community-event-comment-query-repository [db]
  (->CommunityEventCommentQueryRepository db))

(defn make-community-event-comment-command-repository [db]
  (->CommunityEventCommentCommandRepository db))
