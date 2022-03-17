(ns usecase.user
  (:require [domain.user]))

;; TODO spec
(defn list-user [_ repo]
  [(domain.user/list-user (:user-query-repository repo)) nil])

(defn get-user [{:keys [user-id]} repo]
  (let [user (domain.user/fetch-user (:user-query-repository repo) user-id)]
    (if user
      [user nil]
      [nil {:code 404 :message (str "user is not found: " user-id)}])))
