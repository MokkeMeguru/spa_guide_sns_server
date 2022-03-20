(ns infrastructure.api.handler.community.list
  (:require [spec-tools.core :as st]
            [domain.community]
            [usecase.community]
            [infrastructure.api.swagger-spec :refer [community]]
            [clojure.spec.alpha :as s]
            [infrastructure.api.handler.community.core]
            [infrastructure.api.swagger-spec]
            [infrastructure.api.handler.debug]))

(defn- http-> [request]
  {:request-size (get-in request [:parameters :query :requestSize])
   :begin-cursor (get-in request [:parameters :query :beginCursor])
   :last-cursor (get-in request [:parameters :query :lastCursor])
   :keyword (get-in request [:parameters :query :keyword])})

(defn- ->http [[{:keys [communities before-size total-size]} err]]
  (if (nil? err)
    {:status 200 :body {:communities (map (fn [{:keys [community is-joined]}] {:community (infrastructure.api.handler.community.core/community->http community) :isJoined is-joined}) communities)
                        :beforeSize before-size
                        :totalSize total-size}}
    {:status 500 :body err}))

(def operation
  {:operationId "listCommunity"
   :parameters {:query (s/keys :req-un [::infrastructure.api.swagger-spec/requestSize]
                               :opt-un [::infrastructure.api.swagger-spec/beginCursor
                                        ::infrastructure.api.swagger-spec/lastCursor
                                        :community/keyword])}
   ;; :responses {200 {:body
   ;;                  {:communities (s/* (s/keys :req-un [::infrastructure.api.swagger-spec/community ::infrastructure.api.swagger-spec/isJoined]))
   ;;                   :beforeSize  ::infrastructure.api.swagger-spec/before-size
   ;;                   :totalSize ::infrastructure.api.swagger-spec/total-size}}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  infrastructure.api.handler.debug/insert-dummy-user
                  (usecase.community/list-community (:repository request))
                  ->http
                  respond))})
