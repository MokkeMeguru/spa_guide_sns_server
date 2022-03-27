(ns dev
  (:require [cmd.server.core]
            [taoensso.timbre :refer [info]]))


;; TODO cljs の reloaded workflow はちょっと難しい…


(def state #{:stop :running})
(defonce server (atom {:state :stop :server {}}))

(defn start
  ([]
   (start {:host "127.0.0.1" :port 3000}))
  ([{:keys [host port] :as options}]
   (if (= (:state @server) :stop)
     (do
       (info "server start with: " options)
       (swap! server assoc :server (cmd.server.core/server host port))
       (swap! server assoc :state :running)
       :running)
     :already-running)))

(defn stop []
  (if (= (:state @server) :running)
    (do (.close (:server @server))
        (swap! server assoc :state :stop)
        (swap! server assoc :server {})
        :stop)
    :already-stop))
