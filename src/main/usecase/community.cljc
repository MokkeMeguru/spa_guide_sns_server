(ns usecase.community
  (:require [domain.community]))

(defn list-community [_ repo]
  [(domain.community/list-community (:community-query-repository repo)) nil])
