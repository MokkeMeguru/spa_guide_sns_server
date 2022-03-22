(ns cmd.server.util
  (:require  [taoensso.timbre :refer [info]]))

(defn wrap-log
  [handler]
  (fn [request respond _]
    (info (:scheme request) (:request-method request) (:uri request) "query:" (:query-string request) "path:" (:path-params request) "body:" (:body request))
    (handler request respond _)))

(defn wrap-config
  [handler config]
  (fn [request respond _]
    (handler (assoc request :config config) respond _)))

(defn wrap-repository
  [handler repository]
  (fn [request respond _]
    (handler (assoc request :repository repository) respond _)))

(defn wrap-cache
  "clojure.core.atom を使った cache"
  [handler cache]
  (fn [request respond _]
    (handler (assoc request :cache cache) respond _)))

(defn wrap-coercion-exception
  "Catches potential synchronous coercion exception in middleware chain"
  [handler]
  (fn [request respond _]
    (try
      (handler request respond _)
      (catch :default e
        (let [exception-type (:type (.-data e))]
          (cond
            (= exception-type :reitit.coercion/request-coercion)
            (respond {:status 400
                      :body   {:message "Bad Request" :details (-> e ex-data :problems :cljs.spec.alpha/problems)}})

            (= exception-type :reitit.coercion/response-coercion)
            (respond {:status 500
                      :body   {:message "Bad Response" :details (-> e ex-data :problems :cljs.spec.alpha/problems)}})
            :else
            (respond {:status 500
                      :body   {:message "Truly internal server error" :details (str e)}})))))))

(defn wrap-body-to-params
  [handler]
  (fn [request respond raise]
    (handler (-> request
                 (assoc-in [:params :body-params] (:body request))
                 (assoc :body-params (:body request))) respond raise)))
