(ns infrastructure.api.handler.my.profile.get
  (:require
   [domain.user]
   [infrastructure.api.swagger-spec]
   [usecase.get-my-profile]
   [infrastructure.api.handler.debug]))

(defn- http-> [request]
  {})

;; TODO refine
(defn- ->http [[user err]]
  (cond
    (some? user) {:status 200 :body {:user (infrastructure.api.swagger-spec/user->http user)}}
    ;; TODO domain.model.err -> http.err mapping
    (nil? err) {:status 500 :body "unknown error"}
    (= 404 (:code err))  {:status 404 :body err}
    :else {:status 500 :body (str "unknown error " err)}))

(def operation
  {:operationId "getMyProfile"
   :responses {200 {:body {:user infrastructure.api.swagger-spec/user}}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  infrastructure.api.handler.debug/insert-dummy-user
                  (usecase.get-my-profile/execute (:repository request))
                  ->http
                  respond))})
