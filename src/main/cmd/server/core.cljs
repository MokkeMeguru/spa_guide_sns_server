(ns cmd.server.core
  (:require [macchiato.server :as http]
            [taoensso.timbre :refer [info]]
            [cmd.server.config :as config]
            [cmd.server.util]
            [cmd.server.router]
            [infrastructure.api.handler.swagger-ui]
            [infrastructure.api.handler.swagger]
            [infrastructure.api.handler.test.get]
            [infrastructure.api.handler.test.post]))

(defn server []
  (http/start
   {:handler    (cmd.server.router/app {:host config/HOST :port config/PORT} {})
    :host       config/HOST
    :port       config/PORT
    :on-success #(info "macchiato-test started on" config/HOST ":" config/PORT)}))


;; BUG: if query parameter has :name, we should break response...
;; ["/bad-response-bug"
;;  {:get  {:parameters {:query {:name string?}}
;;          :responses  {200 {:body {:message string?}}}
;;          :handler    (fn [request respond _]
;;                        (respond {:status 200 :body {:messag (str "Hello: " (-> request :parameters :query :name))}}))}}]
