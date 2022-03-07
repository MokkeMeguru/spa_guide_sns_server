(ns infrastructure.api.handler.community.list
  (:require [spec-tools.core :as st]
            [domain.community]
            [usecase.community]
            [infrastructure.api.swagger-spec]))

(defn- http-> [_] nil)

(defn- ->http [[communities err]]
  (if (nil? err)
    {:status 200 :body communities}
    {:status 500 :body err}))

(def operation
  {:responses {200 {"schema"
                    {"type" "array"
                     "items" {"$ref" "#/definitions/community"}}}
               500 {:body (st/spec {:spec string?
                                    :name "err message"})}}
   :handler (fn [request respond _]
              (-> request
                  http->
                  (usecase.community/list-community (:repository request))
                  ->http
                  respond))})
