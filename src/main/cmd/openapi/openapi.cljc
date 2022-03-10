(ns cmd.openapi.openapi
  (:require [infrastructure.api.swagger-spec]
            [spec-tools.openapi.core :as openapi]
            [spec-tools.core :as st]
            [clojure.spec.alpha :as s]))

;; for toy
(s/def ::name string?)

(def openapi-version "3.0.3")
(def info
  {:title "SPA Guide: SNS API Server"
   :description "the sample of SNS API Server (TOY)"
   :version "0.1.1"
   :termOfService "https://github.com/MokkeMeguru/spa_guide_sns_server"
   :contact {:name "API Server support"
             :url "https://github.com/MokkeMeguru"
             :email "meguru.mokke@gmail.com"}
   :license {:name "MIT"
             :url "https://github.com/opensource-jp/licenses/blob/dc436911d1f0f150ca66c3f6ff91c60584567933/MIT/MIT.md"}})

(def servers
  [{:url "http://127.0.0.1:3000"
    :description "local server (run with `npm run start_release`)"}])

(def components
  {::openapi/schemas {:user infrastructure.api.swagger-spec/user
                      :community infrastructure.api.swagger-spec/community}})

(def paths
  {"/test"
   {:tags ["test"]
    :get {:operationId         "getTest"
          ::openapi/parameters {:query (s/keys :req-un [::name])}
          :responses {200 {::openapi/content
                           {"application/json"
                            (st/spec {:spec
                                      {:message string?}
                                      :openapi/example "Hello : User"})}}}}}})

(defn generate-openapi []
  (openapi/openapi-spec
   {:openapi openapi-version
    :info info
    :servers servers
    :components components
    :paths paths}))

;; Developer Note
;; (def operation (get-in paths ["/test" :get]))
