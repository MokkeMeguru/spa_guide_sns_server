(ns usecase.my.profile
  (:require [domain.user]))

(defn get-profile [{:keys [user-id]} repo]
  (let [user (domain.user/fetch-user (:user-query-repository repo) user-id)]
    (if user
      [user nil]
      [nil {:code 404 :message (str "user is not found: " user-id)}])))
