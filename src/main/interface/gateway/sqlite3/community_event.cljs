(ns interface.gateway.sqlite3.community-event
  (:require ["better-sqlite3" :as better-sqlite3]
            [clojure.spec.alpha :as s]
            [domain.community.event]))

(s/fdef db->domain
  :args (s/cat :db-model map?)
  :ret ::domain.community.event/query)

(s/fdef domain->db
  :args (s/cat :domain-model ::domain.community.event/command)
  :ret map?)

;; (defn db->domain [db-model]
;;   (when db-model
;;     (let [{:keys []} db-model
;;           community {}
;;           owned-member {}]
;;       {:id id
;;        :community community
;;        :owned-member owned-member
;;        :name name
;;        :details details
;;        :hold-at hold-at
;;        })))

(defn domain->db [domain-model]
  (when domain-model
    (let [{:keys [id community-id owned-member-id name details hold-at]} domain-model]
      {:id (if id id (str (random-uuid)))
       :community_id community-id
       :owned-member-id owned-member-id
       :name name
       :details details
       :hold_at hold-at})))
