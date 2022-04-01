(ns infrastructure.api.handler.community.join
  (:require [clojure.spec.alpha :as s]
            [infrastructure.api.swagger-spec]
            [infrastructure.api.handler.debug]
            [usecase.join-community]))

(defn- http-> [request]
  (let [community-id (-> request :parameters :path :communityId)]
    {:community-id community-id}))

(defn- ->http [[ret err]]
  (cond
    (and (some? err) (= (:code err) 409)) {:status 409 :body {:id (-> err :details :member-id)}}
    (some? err) {:status (:code err) :body {:message (:message err)}}
    :else {:status 200 :body {:id (:member-id ret)}}))

(def operation
  {:operationId "joinCommunity"
   :parameters {:path (s/keys :req-un [:path/communityId])}
   :responses {200 {:body (s/keys :req-un [:community-member/id])}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  infrastructure.api.handler.debug/insert-dummy-user
                  (usecase.join-community/execute (:repository request))
                  ->http
                  respond))})
