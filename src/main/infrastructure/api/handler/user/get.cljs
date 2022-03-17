(ns infrastructure.api.handler.user.get
  (:require
   [domain.user]
   [infrastructure.api.swagger-spec]
   [usecase.user]
   [clojure.spec.alpha :as s]))

;; reitit spec によって request の内容は domain.model でバリデーションされている
(defn- http-> [request]
  {:user-id (-> request :parameters :path :id)})

;; NOTE usecase を通過した時点で user と err が domian.model でバリデーションされている
(defn- ->http [[user err]]
  (cond
    (some? user) {:status 200 :body {:user (infrastructure.api.swagger-spec/user->http user)}}
    ;; TODO domain.model.err -> http.err mapping
    (nil? err) {:status 500 :body "unknown error"}
    (= 404 (:code err))  {:status 404 :body err}
    :else {:status 500 :body (str "unknown error " err)}))

(def operation
  {:operationId "getUser"
   :parameters {:path (s/keys :req-un [::domain.user/id])}
   :responses {200 {:body {:user infrastructure.api.swagger-spec/user}}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  (usecase.user/get-user (:repository request))
                  ->http
                  respond))})
