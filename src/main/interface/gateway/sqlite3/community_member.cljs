(ns interface.gateway.sqlite3.community-member
  (:require ["better-sqlite3" :as better-sqlite3]
            [clojure.spec.alpha :as s]
            [clojure.walk]
            [clojure.set]
            [taoensso.timbre :refer [warn]]
            [interface.gateway.sqlite3.util]
            [interface.gateway.sqlite3.community]
            [domain.community.member :refer [ICommunityMemberQueryRepository ICommunityMemberCommandRepository]]))

(s/fdef db->domain
  :args (s/cat :db-model map?)
  :ret ::domain.community.member/query)

(s/fdef domain->db
  :args (s/cat :domain-model ::domain.community.member/command)
  :ret map?)

(def role-map
  (let [domain->db {:owner 1
                    :member 2}
        db->domain (clojure.set/map-invert domain->db)]
    {:db->domain db->domain
     :domain->db domain->db}))

(defn db->domain [db-model]
  (let [{:keys [community_member_id community_member_role community_member_created_at community_member_updated_at
                community_id community_name community_details community_category community_created_at community_updated_at
                user_id user_name user_icon_url user_created_at user_updated_at]} (clojure.walk/keywordize-keys db-model)]
    {:id community_member_id
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
     :role (get (:db->domain role-map) community_member_role)
     :created-at community_member_created_at
     :updated-at community_member_updated_at}))

(defn domain->db [domain-model]
  (when domain-model
    (let [{:keys [id community-id user-id role]} domain-model]
      {:id (if id id (str (random-uuid)))
       :community_id community-id
       :user_id user-id
       :role (get (:domain->db role-map) role)
       :created_at (interface.gateway.sqlite3.util/now)
       :updated_at (interface.gateway.sqlite3.util/now)})))

(def sql-map
  {:list "
SELECT
 community_members.id AS community_member_id,
 community_members.role AS community_member_role,
 community_members.created_at AS community_member_created_at,
 community_members.updated_at AS community_member_updated_at,
 communities.id AS community_id,
 communities.name AS community_name,
 communities.details AS community_details,
 communities.category AS community_category,
 communities.created_at AS community_created_at,
 communities.updated_at AS community_updated_at,
 users.id AS user_id,
 users.name AS user_name,
 users.icon_url AS user_icon_url,
 users.created_at AS user_created_at,
 users.updated_at AS user_updated_at
FROM community_members
INNER JOIN communities ON community_members.community_id = communities.id
INNER JOIN users ON community_members.user_id = users.id"
   :fetch "
SELECT
 community_members.id AS community_member_id,
 community_members.role AS community_member_role,
 community_members.created_at AS community_member_created_at,
 community_members.updated_at AS community_member_updated_at,
 communities.id AS community_id,
 communities.name AS community_name,
 communities.details AS community_details,
 communities.category AS community_category,
 communities.created_at AS community_created_at,
 communities.updated_at AS community_updated_at,
 users.id AS user_id,
 users.name AS user_name,
 users.icon_url AS user_icon_url,
 users.created_at AS user_created_at,
 users.updated_at AS user_updated_at
FROM community_members
INNER JOIN communities ON community_members.community_id = communities.id
INNER JOIN users ON community_members.user_id = users.id
WHERE community_members.id= ?"
   :search-by-community-id "
SELECT
 community_members.id AS community_member_id,
 community_members.role AS community_member_role,
 community_members.created_at AS community_member_created_at,
 community_members.updated_at AS community_member_updated_at,
 communities.id AS community_id,
 communities.name AS community_name,
 communities.details AS community_details,
 communities.category AS community_category,
 communities.created_at AS community_created_at,
 communities.updated_at AS community_updated_at,
 users.id AS user_id,
 users.name AS user_name,
 users.icon_url AS user_icon_url,
 users.created_at AS user_created_at,
 users.updated_at AS user_updated_at
FROM community_members
INNER JOIN communities ON community_members.community_id = communities.id
INNER JOIN users ON community_members.user_id = users.id
WHERE community_members.community_id = ?"
   :create "INSERT INTO community_members (id, community_id, user_id, role, created_at, updated_at)
VALUES (@id, @community_id, @user_id, @role, @created_at, @updated_at)"})

(defrecord CommunityMemberQueryRepository [db]
  ICommunityMemberQueryRepository
  (-list-community-member [this]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare (:list sql-map)) (.all) (js->clj)))))
  (-fetch-community-member [this member-id]
    (let [^js/better-sqlite3 db (:db this)]
      (-> db (.prepare (:fetch sql-map)) (.get member-id) (js->clj) (db->domain))))
  (-search-community-member-by-community-id [this community-id]
    (let [^js/better-sqlite3 db (:db this)]
      (map db->domain (-> db (.prepare (:search-by-community-id sql-map)) (.all community-id) (js->clj))))))

(defrecord CommunityMemberCommandRepository [db]
  ICommunityMemberCommandRepository
  (-create-community-member [this member]
    (let [^js/better-sqlite3 db (:db this)
          db-model (domain->db member)]
      (try
        (-> db (.prepare (:create sql-map)) (.run (clj->js db-model)))
        ;; TODO F**King ANSISQL
        (try (-> db (.prepare (:fetch sql-map)) (.get (:id db-model)) (js->clj))
             (catch js/Error e
               (warn "insert result cannot fetched" e)
               ;; TODO below cannot join with other model, so we need the response includes err like tuple[query, err]
               {:id (:id db-model)}))
        (catch js/Error e
          (warn "insert failed" e)
          nil)))))

(defn make-community-member-query-repository [db]
  (->CommunityMemberQueryRepository db))

(defn make-community-member-command-repository [db]
  (->CommunityMemberCommandRepository db))
