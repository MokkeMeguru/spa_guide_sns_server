(ns infrastructure.api.handler.community.event.list
  (:require [infrastructure.api.swagger-spec]
            [clojure.spec.alpha :as s]
            [infrastructure.api.handler.debug]
            [usecase.list-community-event]))

(defn- http-> [request]
  {:community-id (get-in request [:parameters :path :communityId])
   :request-size (get-in request [:parameters :query :requestSize])
   :begin-cursor (get-in request [:parameters :query :beginCursor])
   :last-cursor (get-in request [:parameters :query :lastCursor])})

(defn- includes->http [includes]
  (cond-> {}
    (some? (:community-members includes))
    (assoc :communityMembers (map infrastructure.api.swagger-spec/community-member->http
                                  (:community-members includes)))))

(defn- ->http [[{:keys [community-events representative-comments-list total-size before-size includes]} err]]
  (if (nil? err)
    {:status 200 :body {:events
                        (let [representative-comments-map (->> representative-comments-list
                                                               (filter #(-> % count pos-int?))
                                                               (map (fn [comments] [(:event-id (first comments)) comments]))
                                                               (into (sorted-map)))]
                          (map (fn [community-event]
                                 {:communityEvent (infrastructure.api.swagger-spec/community-event->http community-event)
                                  :representativeComment (map infrastructure.api.swagger-spec/community-event-comment->http
                                                              (get representative-comments-map (:id community-event) []))})
                               community-events))
                        :totalSize total-size
                        :beforeSize before-size
                        :includes (includes->http includes)}}
    {:status 500 :body err}))

(def operation
  {:operationId "listCommunityEvent"
   :parameters {:query (s/keys :req-un [::infrastructure.api.swagger-spec/requestSize]
                               :opt-un [::infrastructure.api.swagger-spec/beginCursor
                                        ::infrastructure.api.swagger-spec/lastCursor])
                :path (s/keys :req-un [:path/communityId])}
   :handler (fn [request respond _]
              (-> request
                  http->
                  infrastructure.api.handler.debug/insert-dummy-user
                  (usecase.list-community-event/execute (:repository request) (:cache request))
                  ->http
                  respond))})
