(ns infrastructure.api.handler.community.event.comment.list
  (:require [domain.user]
            [usecase.list-community-event-comment]
            [infrastructure.api.handler.debug]
            [infrastructure.api.swagger-spec]
            [clojure.spec.alpha :as s]))

(defn- http-> [request]
  {:community-id (get-in request [:parameters :path :communityId])
   :event-id (get-in request [:parameters :path :eventId])})

(defn- includes->http [includes]
  (cond-> {}
    (some? (:community-members includes))
    (assoc :communityMembers (map infrastructure.api.swagger-spec/community-member->http
                                  (:community-members includes)))))

(defn ->http [[{:keys [community-event-comments includes]} err]]
  (if (some? err)
    {:status 500 :body err}
    {:status 200 :body {:comments
                        (map infrastructure.api.swagger-spec/community-event-comment->http
                             community-event-comments)
                        :includes (includes->http includes)}}))

(def operation
  {:operationId "listCommunityEventComment"
   :parameters {:path (s/keys :req-un [:path/communityId :path/eventId])}
   :handler (fn [request respond _]
              (-> request
                  http->
                  infrastructure.api.handler.debug/insert-dummy-user
                  (usecase.list-community-event-comment/execute (:repository request) (:cache request))
                  ->http
                  respond))})
