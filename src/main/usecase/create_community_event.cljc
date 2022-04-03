(ns usecase.create-community-event
  (:require [domain.user]
            [domain.community]
            [domain.community.member]
            [domain.community.event]
            [taoensso.timbre :refer [error]]
            [pkg.sql.transaction]
            [clojure.spec.alpha :as s]))

(defn- check-user [user-id repo]
  (let [user (domain.user/fetch-user (:user-query-repository repo) user-id)]
    (when-not user
      (throw (ex-info "user is not found" {:code 404 :user-id user-id})))
    user))

(defn- check-community [community-id repo]
  (let [community (domain.community/fetch-community (:community-query-repository repo) community-id)]
    (when-not community
      (throw (ex-info "community is not found" {:code 404 :community-id community-id})))
    community))

(defn- check-community-member [user-id community-id repo]
  (let [community-members
        (domain.community.member/check-joined (:community-member-query-repository repo) user-id [community-id])]
    (when (-> community-members count zero?)
      (throw (ex-info "user is not joined the community" {:user-id user-id :community-id community-id})))
    (first community-members)))

(defn- create-communtiy-event [event repo]
  (when-not (s/valid? :domain.community.event/command event)
    (throw (ex-info "spec failed: invalid community-event"
                    {:code 403 :community-event event :reason (s/explain-data :domain.community.event/command event)})))
  (if-let [event-id (domain.community.event/create-community-event (:community-event-command-repository repo) event)]
    event-id
    (throw (ex-info "failed: create community-event" {:code 500 :community-event event}))))

(defn- touch-community [communtiy-id repo]
  (try (if (>= (domain.community/touch-community (:community-command-repository repo) communtiy-id) 1)
         nil
         (error (str "communtiy not updated at updated-at with unknown reason: " communtiy-id)))
       (catch #?(:clj Throwable
                 :cljs js/Error) e
         (error e)
         nil)))

(defn execute [{:keys [user-id community-id event]} repo]
  (try
    (check-user user-id repo)
    (let [community-event (-> event
                              (assoc :community-id (:id (check-community community-id repo)))
                              (assoc :owned-member-id (:id (check-community-member user-id community-id repo)))
                              (assoc :image-url (domain.community.event/sample-dummy-image-url (:category event))))
          tx-fn (pkg.sql.transaction/transaction
                 (:transaction-repository repo)
                 (fn [community-event repo]
                   (if-let [event-id (create-communtiy-event community-event repo)]
                     (do
                       (touch-community (:community-id community-event) repo)
                       {:community-event-id event-id})
                     (throw (ex-info "create community-event failed with unknown reason" {:code 500})))))]
      [(tx-fn community-event repo) nil])
    (catch #?(:clj Throwable
              :cljs js/Error) e
      (error e)
      (error {:raw (str e)
              :data (ex-data e)})
      [nil {:code (or (:code (ex-data e)) 500)
            :message (or (ex-message e) "unknown exception")
            :details (get (ex-data e) :details {})}])))
