(ns cmd.server.router
  (:require [taoensso.timbre :refer [info]]
            [reitit.ring :as ring]
            [reitit.coercion.spec :as c]
            [macchiato.middleware.params :as params]
            [reitit.ring.coercion :as rrc]
            [macchiato.middleware.restful-format :as rf]
            [cmd.server.util]
            [infrastructure.api.middleware.cors :as cors]
            [infrastructure.api.handler.swagger-ui]
            [infrastructure.api.handler.swagger]
            [infrastructure.api.handler.test.get]
            [infrastructure.api.handler.test.post]
            [infrastructure.api.handler.user.list]
            [infrastructure.api.handler.community.list]))

(def swagger-info
  {:title       "SPA Guide: SNS API Server"
   :version     "1.0.0"
   :description "the sample of SNS API Server (TOY)"})

(def routes
  [""
   {:swagger  {:info swagger-info}
    :coercion c/coercion}
   ["/swagger.json" {:get infrastructure.api.handler.swagger/operation}]
   ["/api-docs" {:get infrastructure.api.handler.swagger-ui/operation}]
   ["/test"
    {:swagger {:tags ["test"]}
     :get infrastructure.api.handler.test.get/operation
     :post infrastructure.api.handler.test.post/operation}]
   ["/users"
    {:tags ["user"]}
    ["" {:get infrastructure.api.handler.user.list/operation}]]
   ["/communities"
    {:tags ["community"]}
    ["" {:get infrastructure.api.handler.community.list/operation}]]
   ;; ["/communities"
   ;;  {:swagger {:tags ["community"]}}
   ;;  [""
   ;;   {:get {}
   ;;    :post {}}]
   ;;  ["/:communities-id"
   ;;   ["/"
   ;;    {:get {}}]
   ;;   ["/events"
   ;;    {:get {}
   ;;     :post {}}]
   ;;   ["/members"
   ;;    {:get {}
   ;;     :post {}}]]]
   ;; ["/users"
   ;;  {:tags ["user"]}
   ;;  ["/:user-id"
   ;;   {:get {}}]]
   ])

(defn app [config repository]
  (ring/ring-handler
   (ring/router
    [routes]
    {:data {:middleware [#(cors/wrap-cors % {:access-control-allow-origin [#".*"]
                                             :access-control-allow-methods [:get :put :post :delete]})
                         params/wrap-params
                         #(rf/wrap-restful-format % {:keywordize? true})
                         cmd.server.util/wrap-body-to-params
                         #(cmd.server.util/wrap-config % config)
                         #(cmd.server.util/wrap-repository % repository)
                         cmd.server.util/wrap-coercion-exception
                         cmd.server.util/wrap-log
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware]}})
   (ring/create-default-handler)))
