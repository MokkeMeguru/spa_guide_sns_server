(ns cmd.migrate.core
  (:require [domain.mock]
            [domain.user]
            [domain.community]
            [domain.community.member]
            [domain.community.event]
            [domain.community.event.comment]
            [infrastructure.sqlite3.util]
            [infrastructure.sqlite3.up]
            [taoensso.timbre :refer [error info]]
            ["better-sqlite3" :as better-sqlite3]
            [infrastructure.sqlite3.core]))

(defn migrate! [^string db-path]
  (try
    (let [db (infrastructure.sqlite3.util/db! db-path)]
      (.exec db infrastructure.sqlite3.up/users-table)
      (.exec db infrastructure.sqlite3.up/communities-table)
      (.exec db infrastructure.sqlite3.up/community-members-table)
      (.exec db infrastructure.sqlite3.up/community-events-table)
      (.exec db infrastructure.sqlite3.up/community-event-comments-table)
      (.exec db infrastructure.sqlite3.util/activate-foreign-key))
    (catch :default e
      (error (.-message e))
      nil)))

(defn inject-samples [samples repository]
  (info "inject samples: user")
  (doall
   (map
    (fn [user]
      (domain.user/create-user
       (:user-command-repository repository)
       user))
    (:user samples)))
  (info "inject samples: community")
  (doall
   (map
    (fn [community]
      (domain.community/create-community
       (:community-command-repository repository)
       community))
    (:community samples)))
  (info "inject samples: member")
  (doall
   (map
    (fn [community-member]
      (domain.community.member/create-community-member
       (:community-member-command-repository repository)
       community-member))
    (:community-member samples)))
  (info "inject samples: event")
  (doall
   (map
    (fn [community-event]
      (domain.community.event/create-community-event
       (:community-event-command-repository repository)
       community-event))
    (:community-event samples)))
  (info "inject samples: comment")
  (doall
   (map
    (fn [community-event-comment]
      (domain.community.event.comment/create-community-event-comment
       (:community-event-comment-command-repository repository)
       community-event-comment))
    (:community-event-comment samples))))

(defn migrate []
  (let [db-path  "db.sqlite3"]
    (when (migrate! db-path)
      (let [repository (infrastructure.sqlite3.core/make-repository (infrastructure.sqlite3.util/db! db-path))]
        (inject-samples domain.mock/samples repository)))))

;; (migrate)
