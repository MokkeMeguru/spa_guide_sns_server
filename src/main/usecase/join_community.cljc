(ns usecase.join-community
  (:require [domain.user]
            [domain.community]
            [pkg.sql.transaction]
            [taoensso.timbre :refer [error]]
            [domain.community.member]))

(defn check-joined [user-id community-id repo]
  (let [joined (domain.community.member/check-joined
                (:community-member-query-repository repo)
                user-id
                [community-id])]
    (if (zero? (count joined))
      nil
      (throw (ex-info
              (str "user is already joined the community: user-id " user-id " community-id " community-id)
              {:code 409 :details {:user-id user-id :community-id community-id :member-id (-> joined first :id)}})))))

(defn fetch-user [user-id repo]
  (if-let [user (domain.user/fetch-user (:user-query-repository repo) user-id)]
    user
    (throw (ex-info (str "user is not found: " user-id)  {:code 404 :user-id user-id}))))

(defn create-community-member [user-id community-id repo]
  (if-let [member-id (domain.community.member/create-community-member
                      (:community-member-command-repository repo)
                      {:community-id community-id
                       :user-id user-id
                       :role :member})]
    member-id
    (throw (ex-info (str "failed to create member") {:code 500 :user-id user-id :communtiy-id community-id}))))

;; (defn touch-community [communtiy-id repo]
;;   (try (if (>= (domain.community/touch-community (:community-command-repository repo) communtiy-id) 1)
;;          nil
;;          (error (str "communtiy not updated at updated-at with unknown reason: " communtiy-id)))
;;        (catch #?(:clj Throwable
;;                  :cljs js/Error) e
;;          (error e)
;;          nil)))

(defn fetch-community [community-id repo]
  (if-let [community (domain.community/fetch-community (:community-query-repository repo) community-id)]
    community
    (throw (ex-info (str "community is not found: " community-id)  {:code 404 :community-id community-id}))))

(defn execute [{:keys [user-id community-id]} repo]
  (try
    (check-joined user-id community-id repo)
    (let [user (fetch-user user-id repo)
          community (domain.community/fetch-community (:community-query-repository repo) community-id)
          tx-fn (pkg.sql.transaction/transaction
                 (:transaction-repository repo)
                 (fn [user-id community-id repo]
                   (if-let [member-id (create-community-member user-id community-id repo)]
                    ;; (touch-community community-id repo)
                     {:member-id member-id
                      :community-id community-id
                      :user-id user-id}
                     (throw (ex-info "create member failed with unknown reason" {:code 500})))))]
      [(tx-fn (:id user) (:id community) repo) nil])
    (catch #?(:clj Throwable
              :cljs js/Error) e
      (error e)
      [nil {:code (or (:code (ex-data e)) 500)
            :message (or (ex-message e) "unknown exception")
            :details (get (ex-data e) :details {})}])))
