(ns usecase.list-user
  (:require [domain.user]))

(defn execute [_ repo]
  [(domain.user/list-user (:user-query-repository repo)) nil])
