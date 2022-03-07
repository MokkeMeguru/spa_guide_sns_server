(ns infrastructure.api.handler.swagger
  (:require [reitit.swagger :as swagger]
            [domain.user]
            [usecase.user]
            [spec-tools.swagger.core :as sc]
            [infrastructure.api.swagger-spec]))

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
   :swagger {:definitions {:user (sc/transform infrastructure.api.swagger-spec/user)
                           :community (sc/transform infrastructure.api.swagger-spec/community)}}
   :handler handler})
