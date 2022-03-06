(ns interface.gateway.sqlite3.community-event
  (:require ["better-sqlite3" :as better-sqlite3]
            [clojure.set]
            [clojure.walk]
            [clojure.spec.alpha :as s]
            [domain.community.event]))

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

;; (defn db->domain [db-model]
;;   (let [{:keys [community_event_id community_]}]))
