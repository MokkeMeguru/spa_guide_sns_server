(ns infrastructure.api.handler.user.list
  (:require [spec-tools.core :as st]
            [domain.user]
            [usecase.user]
            [infrastructure.api.swagger-spec]))

(defn- http-> [_] nil)

(defn- ->http [[users err]]
  (if (nil? err)
    {:status 200 :body users}
    {:status 500 :body err}))

(def operation
  {:responses {200 {"schema"
                    {"type" "array"
                     "items" {"$ref" "#/definitions/user"}}}
               500 {:body (st/spec {:spec string?
                                    :name "err message"})}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  (usecase.user/list-user (:repository request))
                  ->http
                  respond))})
