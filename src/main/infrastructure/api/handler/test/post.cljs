(ns infrastructure.api.handler.test.post)

(def operation
  {:operationId "postTest"
   :parameters {:body {:my-body string?}}
   :responses {200 {:body {:message string?}}}
   :handler    (fn [request respond _]
                 (respond {:status 200 :body {:message (str "Hello: " (-> request :parameters :body :my-body))}}))})
