(ns infrastructure.api.handler.community.get
  (:require [domain.community]
            [usecase.community]
            [infrastructure.api.swagger-spec]
            [clojure.spec.alpha :as s]
            [infrastructure.api.handler.debug]))

(defn- http-> [request]
  {:community-id (-> request :parameters :path :communityId)})

(defn- ->http [[{:keys [community is-joined members]} err]]
  (cond
    (some? community)
    {:status 200 :body {:community (infrastructure.api.swagger-spec/community->http community)
                        :isJoined is-joined
                        :members (map (fn [member] (infrastructure.api.swagger-spec/community-member->http member)) members)}}
    (nil? err) {:status 500 :body "unknown error"}
    (= 404 (:code err)) {:status 404 :body err}
    :else {:status 500 :body (str "unknown error" err)}))

(def operation
  {:operationId "getCommunity"
   :parameters {:path (s/keys :req-un [:path/communityId])}
   :responses {200
               {:body {:community infrastructure.api.swagger-spec/community
                       :isJoined (s/nilable :community/isJoined)
                       :members (s/* :community-member/communityMember)}}}
   :handler (fn [request respond _]
              (->  request
                   http->
                   infrastructure.api.handler.debug/insert-dummy-user
                   (usecase.community/get-community (:repository request))
                   ->http
                   respond))})
