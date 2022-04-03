(ns infrastructure.api.handler.community.event.comment.create
  (:require [usecase.create-community-event-comment]
            [infrastructure.api.swagger-spec]
            [infrastructure.api.handler.debug]
            [clojure.spec.alpha :as s]))

(defn http-> [request]
  (let [user-id (-> request :user-id)
        {:keys [communityId eventId]} (-> request :parameters :path)
        {:keys [body]} (-> request :body-params)]
    {:user-id user-id
     :community-id communityId
     :event-id eventId
     :comment {:body body}}))

(defn- ->http [[ret err]]
  (cond
    (some? err) {:status (:code err) :body {:message (:message err)}}
    :else {:status 200 :body {:id (:community-event-comment-id ret)}}))

(def operation
  {:operationId "createCommunityEventComment"
   :parameters {:path (s/keys :req-un [:path/communityId :path/eventId])
                :body infrastructure.api.swagger-spec/communityEventCommentInput}
   :responses {200 {:body (s/keys :req-un [:community-event-comment/id])}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  infrastructure.api.handler.debug/insert-dummy-user
                  (usecase.create-community-event-comment/execute (:repository request) (:cache request))
                  ->http
                  respond))})
