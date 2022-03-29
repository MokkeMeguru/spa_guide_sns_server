(ns infrastructure.api.handler.community.create
  (:require [domain.community]
            [usecase.create-community]
            [infrastructure.api.swagger-spec]
            [infrastructure.api.handler.debug]))

(defn- http-> [request]
  {:community {}})

(defn- ->http [[community-id err]]
  (cond
    (some? err) {:status (:code err) :message (:message err)}
    :else {:status 200 :body (:community-id community-id)}))
