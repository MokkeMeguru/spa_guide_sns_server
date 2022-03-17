(ns cmd.server.router-test
  (:require [cljs.test :as t]
            [reitit.core]
            [cmd.server.router :as sut]))

(t/deftest test-routes
  (let [router (reitit.core/router sut/routes)
        ->match (fn [path] (reitit.core/match-by-path router path))
        ->operationId (fn [path method] (get-in (->match path) [:data method :operationId]))
        ->path-params (fn [path] (get-in (->match path) [:path-params]))]
    (t/testing "getTest"
      (t/is (= "getTest" (->operationId "/test" :get))))
    (t/testing "postTest"
      (t/is (= "postTest" (->operationId "/test" :post))))
    (t/testing "listUser"
      (t/is (= "listUser" (->operationId "/users" :get))))
    (t/testing "getUser"
      (t/is  (= "getUser" (->operationId "/users/6e803bdf-55a7-4a31-849e-8489cc76a457" :get)))
      (t/is (= {:id "6e803bdf-55a7-4a31-849e-8489cc76a457"}
               (->path-params "/users/6e803bdf-55a7-4a31-849e-8489cc76a457"))))
    (t/testing "listCommunity"
      (t/is (= "listCommunity" (->operationId "/communities" :get))))))
