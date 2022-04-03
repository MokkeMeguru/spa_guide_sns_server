(ns infrastructure.api.handler.community.event.create
  (:require [domain.community.event]
            [infrastructure.api.swagger-spec]
            [clojure.spec.alpha :as s]
            [usecase.create-community-event]
            [infrastructure.api.handler.debug]))

(defn- http-> [request]
  (let [user-id (-> request :user-id)
        {:keys [communityId]} (-> request :parameters :path)
        {:keys [name details holdAt category]} (-> request :body-params)]
    {:user-id user-id
     :community-id communityId
     :event
     {:name name
      :details details
      :hold-at holdAt
      :category category}}))

(defn- ->http [[ret err]]
  (cond
    (some? err) {:status (:code err) :body {:message (:message err)}}
    :else {:status 200 :body {:id (:community-event-id ret)}}))

(def operation
  {:operationId "createCommunityEvent"
   :parameters {:path (s/keys :req-un [:path/communityId])
                :body infrastructure.api.swagger-spec/communityEventInput}
   :responses {200 {:body (s/keys :req-un [:community-event/id])}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  infrastructure.api.handler.debug/insert-dummy-user
                  (usecase.create-community-event/execute (:repository request))
                  ->http
                  respond))})
