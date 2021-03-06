(ns cmd.server.router
  (:require [taoensso.timbre :refer [info]]
            [domain.user]
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
            [infrastructure.api.handler.user.get]
            [infrastructure.api.handler.community.list]
            [infrastructure.api.handler.community.get]
            [infrastructure.api.handler.community.create]
            [infrastructure.api.handler.my.profile.get]
            [infrastructure.api.handler.community.event.list]
            [infrastructure.api.handler.community.event.create]
            [infrastructure.api.handler.community.event.comment.list]
            [infrastructure.api.handler.community.event.comment.create]
            [infrastructure.api.handler.community.join]
            [infrastructure.api.swagger-spec]
            [clojure.spec.alpha :as s]
            [macchiato.util.response :as r]))

(def swagger-info
  {:title       "SPA Guide: SNS API Server"
   :version     "1.0.0"
   :description "the sample of SNS API Server (TOY)"})

;; TODO get some infomation from cmd/openapi/openapi.cljc
(def routes
  [""
   {:swagger  {:info swagger-info}
    :coercion c/coercion}
   ["/swagger.json" {:get infrastructure.api.handler.swagger/operation}]
   ["/api-docs" {:get infrastructure.api.handler.swagger-ui/operation}]
   ["/test"
    {:get infrastructure.api.handler.test.get/operation
     :post infrastructure.api.handler.test.post/operation}]
   ["/my/profile"
    {:get infrastructure.api.handler.my.profile.get/operation}]
   ["/users"
    {:get infrastructure.api.handler.user.list/operation}]
   ["/users/{id}"
    {:get infrastructure.api.handler.user.get/operation}]
   ["/communities"
    {:get infrastructure.api.handler.community.list/operation
     :post infrastructure.api.handler.community.create/operation}]
   ["/communities/{communityId}"
    {:get infrastructure.api.handler.community.get/operation}]
   ["/communities/{communityId}/join"
    {:post infrastructure.api.handler.community.join/operation}]
   ["/communities/{communityId}/events"
    {:get infrastructure.api.handler.community.event.list/operation
     :post infrastructure.api.handler.community.event.create/operation}]
   ["/communities/{communityId}/events/{eventId}/comments"
    {:get infrastructure.api.handler.community.event.comment.list/operation
     :post infrastructure.api.handler.community.event.comment.create/operation}]])

(defn extra-middleware [handler]
  (fn [request respond raise]
    (let [request-method (-> request :request-method)
          uri (get-in request [:reitit.core/match :template])]
      (handler
       (cond-> request
         (and (= "/communities" uri) (= :post request-method))
         (update-in [:body-params :category] #(if (nil? %) nil (keyword %)))
         (and (= "/communities/{communityId}/events" uri) (= :post request-method))
         (update-in [:body-params :category] #(if (nil? %) nil (keyword %))))
       respond
       raise))))

(defn app [config repository cache]
  (ring/ring-handler
   (ring/router
    [routes]
    {:syntax :bracket
     :data {:middleware [#(cors/wrap-cors % {:allowed-origins [#".*"]
                                             :allowed-methods [:get :put :post :delete :options]})
                         params/wrap-params
                         #(rf/wrap-restful-format % {:keywordize? true})
                         cmd.server.util/wrap-body-to-params
                         extra-middleware
                         #(cmd.server.util/wrap-config % config)
                         #(cmd.server.util/wrap-repository % repository)
                         #(cmd.server.util/wrap-cache % cache)
                         cmd.server.util/wrap-coercion-exception
                         cmd.server.util/wrap-log
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware]}})
   (ring/create-default-handler)))
