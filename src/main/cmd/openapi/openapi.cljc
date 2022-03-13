(ns cmd.openapi.openapi
  (:require [infrastructure.api.swagger-spec]
            [spec-tools.openapi.core :as openapi]
            [spec-tools.core :as st]
            [domain.user]
            [clojure.spec.alpha :as s]))

;; for toy
(s/def ::name string?)

(def openapi-version "3.0.3")
(def info
  {:title "SPA Guide: SNS API Server"
   :description "the sample of SNS API Server (TOY)"
   :version "0.1.1"
   :termsOfService "https://github.com/MokkeMeguru/spa_guide_sns_server"
   :contact {:name "API Server support"
             :url "https://github.com/MokkeMeguru"
             :email "meguru.mokke@gmail.com"}
   :license {:name "MIT"
             :url "https://github.com/opensource-jp/licenses/blob/dc436911d1f0f150ca66c3f6ff91c60584567933/MIT/MIT.md"}})

(def servers
  [{:url "http://127.0.0.1:3000"
    :description "local server (run with `npm run start_release`)"}])

(def components
  {::openapi/schemas {:User infrastructure.api.swagger-spec/user
                      :Community infrastructure.api.swagger-spec/community
                      :Error infrastructure.api.swagger-spec/error}})

(def paths
  {"/test"
   {:get {:operationId "getTest"
          :tags ["test"]
          ::openapi/parameters {:query (s/keys :req-un [::name])}
          :responses {200 {:description "pong"
                           ::openapi/content
                           {"application/json"
                            (st/spec {:spec {:message string?}
                                      :openapi/example "Hello : User"})}}}}}
   "/users/{id}"
   {:get {:operationId "getUser"
          :tags ["user"]
          ::openapi/parameters {:path (s/keys :req-un [::domain.user/id])}
          :responses {200 {:description "find a user"
                           :content
                           {"application/json"
                            {:schema {:type "object"
                                      :properties {:user {"$ref" "#/components/schemas/User"}}}}}}
                      404 {:description "user is not found"
                           :content
                           {"application/json"
                            {:schema {"$ref" "#/components/schemas/Error"}}}}}}}})

(defn generate-openapi []
  (openapi/openapi-spec
   {:openapi openapi-version
    :info info
    :servers servers
    :components components
    :paths paths}))

(generate-openapi)





;; Developer Note
;; (def operation (get-in paths ["/test" :get]))
