(ns cmd.server.core
  (:require [macchiato.server :as http]
            [taoensso.timbre :refer [info]]
            [cmd.server.config :as config]
            [cmd.server.util]
            [cmd.server.router]
            [infrastructure.sqlite3.core]
            [infrastructure.sqlite3.util]
            [infrastructure.api.handler.swagger-ui]
            [infrastructure.api.handler.swagger]
            [infrastructure.api.handler.test.get]
            [infrastructure.api.handler.test.post]
            [pkg.cache.plain]))

(defn server
  ([]
   (server config/HOST config/PORT))
  ([host port]
   (let [repository (infrastructure.sqlite3.core/make-repository (infrastructure.sqlite3.util/db! "db.sqlite3"))
         cache (pkg.cache.plain/make-plain-cache-atom)]
     (http/start
      {:handler    (cmd.server.router/app {:host host :port port} repository cache)
       :host       host
       :port       port
       :on-success #(info "macchiato-test started on" host ":" port)}))))


;; BUG: if query parameter has :name, we should break response...
;; ["/bad-response-bug"
;;  {:get  {:parameters {:query {:name string?}}
;;          :responses  {200 {:body {:message string?}}}
;;          :handler    (fn [request respond _]
;;                        (respond {:status 200 :body {:messag (str "Hello: " (-> request :parameters :query :name))}}))}}]
