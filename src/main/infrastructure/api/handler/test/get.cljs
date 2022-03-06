(ns infrastructure.api.handler.test.get)

(def operation
  {:parameters {:query {:name string?}}
   :responses  {200 {:body {:message string?}}}
   :handler    (fn [request respond _]
                 (respond {:status 200 :body {:message (str "Hello: " (-> request :parameters :query :name))}}))})
