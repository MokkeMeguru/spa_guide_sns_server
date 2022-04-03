(ns usecase.create-community
  (:require [domain.user]
            [domain.community]
            [domain.community.member]
            [pkg.sql.transaction]
            [taoensso.timbre :refer [error]]
            [clojure.spec.alpha :as s]))

(defn- create-community [community repo]
  (when-not (s/valid? :domain.community/command community) (throw (ex-info "spec failed: invalid community" {:code 403 :community community})))
  (if-let [community-id (domain.community/create-community (:community-command-repository repo) community)]
    community-id
    (throw (ex-info "failed: create community" {:code 500 :community community}))))

(defn- create-community-member [member repo]
  (when-not (s/valid? :domain.community.member/command member)
    (throw (ex-info "spec failed: invalid community-member" {:code 403 :community-member member})))
  (if-let [member-id (domain.community.member/create-community-member
                      (:community-member-command-repository repo) member)]
    member-id
    (throw (ex-info "failed: create community-member" {:code 500 :community-member member}))))

(defn execute [{:keys [user-id community]} repo]
  (let [user (domain.user/fetch-user (:user-query-repository repo) user-id)]
    (if-not user
      [nil {:code 403 :message (str "user is not found: " user-id)}]
      (let [tx-fn (pkg.sql.transaction/transaction
                   (:transaction-repository repo)
                   (fn [user-id community repo]
                     (let [community-id (create-community community repo)]
                       (create-community-member
                        {:user-id user-id
                         :community-id community-id
                         :role :owner} repo)
                       {:community-id community-id})))]
        (try
          [(tx-fn user-id community repo) nil]
          (catch #?(:clj Throwable
                    :cljs js/Error) e
            (error e)
            [nil {:code (or (:code (ex-data e)) 500) :message (or (ex-message e) "unknown exception")}]))))))
