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
    (let [{:keys [community_event_comment_id  community_event_comment_event_id community_event_comment_body community_event_comment_comment_at community_event_comment_created_at community_event_comment_updated_at
                  community_member_id community_member_role community_member_created_at community_member_updated_at
                  community_id community_name community_details community_category community_created_at community_updated_at
                  user_id user_name user_icon_url user_created_at user_updated_at]}
          (clojure.walk/keywordize-keys db-model)]
      {:id community_event_comment_id
       :event-id community_event_comment_event_id
       :member {:id community_member_id
                :community {:id community_id
                            :name community_name
                            :details community_details
                            :category (get (:db->domain interface.gateway.sqlite3.community/category-map) community_category)
                            :created_at community_created_at
                            :updated_at community_updated_at}
                :user {:id user_id
                       :name user_name
                       :icon-url user_icon_url
                       :created-at user_created_at
                       :updated-at user_updated_at}
                :role (get (:db->domain interface.gateway.sqlite3.community-member/role-map) community_member_role)
                :created-at community_member_created_at
                :updated-at community_member_updated_at}
       :body community_event_comment_body
       :comment-at community_event_comment_comment_at
       :created-at community_event_comment_created_at
       :updated-at community_event_comment_updated_at})))

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

;; TODO think which is better 1) use summarization of repeated code 2) flat data
(def sql-map
  (let [list "
SELECT
 community_event_comments.id AS community_event_comment_id,
 community_event_comments.event_id AS community_event_comment_event_id,
 community_event_comments.body AS community_event_comment_body,
 community_event_comments.comment_at AS community_event_comment_comment_at,
 community_event_comments.created_at AS community_event_comment_created_at,
 community_event_comments.updated_at AS community_event_comment_updated_at,
 community_members.id AS community_member_id,
 community_members.role AS community_member_role,
 community_members.created_at AS community_member_created_at,
 community_members.updated_at AS community_member_updated_at,
 communities.id AS community_id,
 communities.details AS community_details,
 communities.created_at AS community_created_at,
 communities.updated_at AS community_updated_at,
 users.id AS user_id,
 users.name AS user_name,
 users.icon_url AS user_icon_url,
 users.created_at AS user_created_at,
 users.updated_at AS user_updated_at
FROM community_event_comments
INNER JOIN community_members ON community_event_comments.member_id = community_members.id
INNER JOIN communities ON community_members.community_id = communities.id
INNER JOIN users ON community_members.user_id = users.id"]
    {:list list
     :fetch (str list " WHERE community_event_comments.id = ?")
     :fetch-by-event-id (str list " WHERE community_event_comments.event_id = ?")
     :fetch-by-event-ids (fn [event-ids] (let [params (str (clojure.string/join ", " (repeat (count event-ids)  "?")))]
                                           (str list " WHERE community_event_comments.event_id IN (" params ")")))
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
        (try
          (-> db (.prepare (:fetch sql-map)) (.get (:id db-model)) (js->clj) (db->domain))
          (catch js/Error e
            (warn "insert result cannot fetched" e)
            {:id (:id db-model)}))
        (catch js/Error e
          (warn "insert failed" e)
          nil)))))

(defn make-community-event-comment-query-repository [db]
  (->CommunityEventCommentQueryRepository db))

(defn make-community-event-comment-command-repository [db]
  (->CommunityEventCommentCommandRepository db))
