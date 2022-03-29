(ns infrastructure.api.handler.community.create
  (:require [domain.community]
            [usecase.create-community]
            [infrastructure.api.swagger-spec]
            [infrastructure.api.handler.debug]
            [clojure.spec.alpha :as s]))

(defn- http-> [request]
  (let [{:keys [name details category imageUrl]} (-> request :body-params)]
    {:community {:name name
                 :details details
                 :category category
                 :image-url (domain.community/sample-dummy-image-url category)}}))

(defn- ->http [[community-id err]]
  (cond
    (some? err) {:status (:code err) :body {:message (:message err)}}
    :else {:status 200 :body {:id (:community-id community-id)}}))

(def operation
  {:operation "createCommunity"
   :parameters {:body infrastructure.api.swagger-spec/communityInput}
   :responses {200 {:body (s/keys :req-un [:community/id])}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  infrastructure.api.handler.debug/insert-dummy-user
                  (usecase.create-community/execute (:repository request))
                  ->http
                  respond))})
