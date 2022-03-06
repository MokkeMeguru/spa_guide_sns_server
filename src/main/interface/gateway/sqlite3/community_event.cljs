(ns interface.gateway.sqlite3.community-event
  (:require ["better-sqlite3" :as better-sqlite3]
            [clojure.set]
            [clojure.walk]
            [clojure.spec.alpha :as s]
            [domain.community.event]
            [interface.gateway.sqlite3.community]
            [interface.gateway.sqlite3.community-member]))

(s/fdef db->domain
  :args (s/cat :db-model map?)
  :ret ::domain.community.event/query)

(s/fdef domain->db
  :args (s/cat :domain-model ::domain.community.event/command)
  :ret map?)

(def category-map
  (let [domain->db {:party 1}
        db->domain (clojure.set/map-invert domain->db)]
    {:db->domain db->domain
     :domain->db domain->db}))

(defn db->domain [db-model]
  (when db-model
    (let [{:keys [community_event_id community_event_name community_event_details community_event_category community_event_hold_at community_event_created_at community_event_updated_at
                  community_id community_name community_details community_category community_created_at community_updated_at
                  community_member_id community_member_role community_member_created_at community_member_updated_at
                  user_id user_name user_icon_url user_created_at user_updated_at]} db-model
          community {:id community_id
                     :name community_name
                     :details community_details
                     :category (get (:db->domain interface.gateway.sqlite3.community/categories-map) community_category)
                     :created_at community_created_at
                     :updated_at community_updated_at}
          user {:id user_id
                :name user_name
                :icon_url user_icon_url
                :created_at user_created_at
                :updated_at user_updated_at}]
      {:id community_event_id
       :community community
       :owned-member {:id community_member_id
                      :community community ;; Note: logical equal
                      :user user
                      :role (get (:db->domain interface.gateway.sqlite3.community-member/role-map) community_member_role)
                      :created_at community_member_created_at
                      :updated_at community_member_updated_at}
       :name community_event_name
       :details community_event_details
       :hold-at community_event_hold_at
       :category (get (:db->domain category-map) community_event_category)
       :created-at community_event_created_at
       :updated-at community_event_updated_at})))

(def sql-map
  {:list "
SELECT
 community_events.id AS community_event_id,
 community_events.name AS community_event_name,
 community_events.details AS community_event_details,
 community_events.hold_at AS community_event_hold_id,
 community_events.category AS community_event_category,
 community_events.created_at AS community_event_created_at,
 community_events.updated_at AS community_event_updated_at,
 communities.id AS community_id,
 communities.name AS community_name,
 communities.details AS community_details,
 communities.category AS community_category,
 communities.created_at AS community_created_at,
 communities.updated_at AS community_updated_at,
 community_members.id AS community_member_id,
 community_members.role AS community_member_role,
 community_members.created_at AS community_member_created_at,
 community_members.updated_at AS community_member_updated_at,
 users.id AS user_id,
 users.name AS user_name,
 users.icon_url AS user_icon_url,
 users.created_at AS user_created_at,
 users.updated_at AS user_updated_at
FROM community_events
INNER JOIN communities ON community_events.community_id = communities.id
INNER JOIN community_members ON community_events.owned_member_id = community_mebmers.id
INNER JOIN users ON community_members.user_id = users.id"
   :fetch "
SELECT
 community_events.id AS community_event_id,
 community_events.name AS community_event_name,
 community_events.details AS community_event_details,
 community_events.hold_at AS community_event_hold_id,
 community_events.category AS community_event_category,
 community_events.created_at AS community_event_created_at,
 community_events.updated_at AS community_event_updated_at,
 communities.id AS community_id,
 communities.name AS community_name,
 communities.details AS community_details,
 communities.category AS community_category,
 communities.created_at AS community_created_at,
 communities.updated_at AS community_updated_at,
 community_members.id AS community_member_id,
 community_members.role AS community_member_role,
 community_members.created_at AS community_member_created_at,
 community_members.updated_at AS community_member_updated_at,
 users.id AS user_id,
 users.name AS user_name,
 users.icon_url AS user_icon_url,
 users.created_at AS user_created_at,
 users.updated_at AS user_updated_at
FROM community_events
INNER JOIN communities ON community_events.community_id = communities.id
INNER JOIN community_members ON community_events.owned_member_id = community_mebmers.id
INNER JOIN users ON community_members.user_id = users.id
WHERE community_events.id = ?"
   :seach-by-community-id "
SELECT
 community_events.id AS community_event_id,
 community_events.name AS community_event_name,
 community_events.details AS community_event_details,
 community_events.hold_at AS community_event_hold_id,
 community_events.category AS community_event_category,
 community_events.created_at AS community_event_created_at,
 community_events.updated_at AS community_event_updated_at,
 communities.id AS community_id,
 communities.name AS community_name,
 communities.details AS community_details,
 communities.category AS community_category,
 communities.created_at AS community_created_at,
 communities.updated_at AS community_updated_at,
 community_members.id AS community_member_id,
 community_members.role AS community_member_role,
 community_members.created_at AS community_member_created_at,
 community_members.updated_at AS community_member_updated_at,
 users.id AS user_id,
 users.name AS user_name,
 users.icon_url AS user_icon_url,
 users.created_at AS user_created_at,
 users.updated_at AS user_updated_at
FROM community_events
INNER JOIN communities ON community_events.community_id = communities.id
INNER JOIN community_members ON community_events.owned_member_id = community_mebmers.id
INNER JOIN users ON community_members.user_id = users.id
WHERE community_events.community_id = ?"
   :create "
INSERT INTO community_events
 (id, community_id, owned_member_id, name, details, hold_at, category, created_at, updated_at)
VALUES (@id, @community_id, @owned_member_id, @name, @details, @hold_at, @category, @created_at, @updated_at)"})

(defrecord CommunityEventQueryRepository [db])
(defrecord CommunityEventCommandRepository [db])

(defn make-community-event-query-repository [db]
  (->CommunityEventQueryRepository db))

(defn make-community-event-command-repository [db]
  (->CommunityEventCommandRepository db))
