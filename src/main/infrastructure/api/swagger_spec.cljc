(ns infrastructure.api.swagger-spec
  (:require
   [domain.user]
   [domain.community]
   [spec-tools.core :as st]
   [clojure.spec.alpha :as s]))

(s/def ::user ::domain.user/query)
(s/def ::community ::domain.community/query)

(def user
  (st/spec
   {:spec ::user
    :name "user"
    :description "user information"
    :swagger/example
    ;; fetch from sample code
    {:id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :name "Meguru Mokke"
     :icon-url "https://avatars.githubusercontent.com/u/30849444?v=4"}}))

(def community
  (st/spec
   {:spec ::community
    :name "community"
    :description "community information"
    :swagger/example
    {:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :name "辛いものの部"
     :details "辛いものが好きな人集まれー"
     :category :gurmand}}))
