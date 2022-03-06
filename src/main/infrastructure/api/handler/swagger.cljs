(ns infrastructure.api.handler.swagger
  (:require [reitit.swagger :as swagger]))

(defn- handler
  [req respond _]
  (let [handler (swagger/create-swagger-handler)]
    (-> req
        ;; handler = controller + usecase + presenter
        (handler
         (fn [result]
           (respond (assoc-in result [:headers :content-type] "application/json"))) _))))

(def operation
  {:no-doc true
   :handler handler})
