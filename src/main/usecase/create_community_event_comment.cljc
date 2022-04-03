(ns usecase.create-community-event-comment
  (:require [domain.user]
            [domain.community]
            [domain.community.member]
            [domain.community.event]
            [domain.community.event.comment]
            [taoensso.timbre :refer [error]]
            [pkg.sql.transaction]
            [pkg.cache.core]
            [clojure.spec.alpha :as s]))

(defn- check-user [user-id repo]
  (let [user (domain.user/fetch-user (:user-query-repository repo) user-id)]
    (when-not user
      (throw (ex-info "user is not found" {:code 404 :user-id user-id})))
    user))

(defn- check-community-event [community-id community-event-id repo]
  (let [community-event (domain.community.event/fetch-community-event
                         (:community-event-query-repository repo)
                         community-event-id)]
    (cond
      (nil? community-event)
      (throw (ex-info "community event is not found" {:code 404 :community-event-id community-event-id}))
      (not= community-id (:community-id community-event))
      (throw (ex-info "community event is not in the community" {:code 404 :community-event-id community-event-id
                                                                 :community-id community-id}))
      :else community-event)))

(defn- check-community-member [user-id community-id repo]
  (let [community-members
        (domain.community.member/check-joined
         (:community-member-query-repository repo) user-id [community-id])]
    (when (-> community-members count zero?)
      (throw (ex-info "user is not joined the community" {:user-id user-id :community-id community-id})))
    (first community-members)))

(defn- create-community-event-comment [comment repo]
  (when-not (s/valid? :domain.community.event.comment/command comment)
    (throw (ex-info "spec failed: invalid community-event-comment"
                    {:code 403 :community-event comment :reason (s/explain-data :domain.community.event.comment/command comment)})))
  (if-let [comment-id (domain.community.event.comment/create-community-event-comment
                       (:community-event-comment-command-repository repo) comment)]
    comment-id
    (throw (ex-info "failed: create community-event-comment" {:code 500 :community-event-comment comment}))))

(defn- touch-community [communtiy-id repo]
  (try (if (>= (domain.community/touch-community (:community-command-repository repo) communtiy-id) 1)
         nil
         (error (str "communtiy not updated at updated-at with unknown reason: " communtiy-id)))
       (catch #?(:clj Throwable
                 :cljs js/Error) e
         (error e)
         nil)))

(defn execute [{:keys [user-id community-id event-id comment]} repo cache]
  (try
    (check-user user-id repo)
    (let [community-event-comment (-> {:body (:body comment)}
                                      (assoc :event-id (:id (check-community-event community-id event-id repo)))
                                      (assoc :member-id (:id (check-community-member user-id community-id repo))))
          tx-fn (pkg.sql.transaction/transaction
                 (:transaction-repository repo)
                 (fn [community-event-comment community-event-id repo cache]
                   (if-let [comment-id (create-community-event-comment community-event-comment repo)]
                     (do
                       (pkg.cache.core/evict cache (domain.community.event.comment/list-cache-key community-event-id)) ;; TODO function
                       {:community-event-comment-id comment-id})
                     (throw (ex-info "create community-event-comment failed with unknown reason" {:code 500})))))]
      [(tx-fn community-event-comment event-id repo cache) nil])
    (catch #?(:clj Throwable
              :cljs js/Error) e
      (error e)
      (error {:raw (str e)
              :data (ex-data e)})
      [nil {:code (or (:code (ex-data e)) 500)
            :message (or (ex-message e) "unknown exception")
            :details (get (ex-data e) :details {})}])))
