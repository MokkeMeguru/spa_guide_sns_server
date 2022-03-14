(ns infrastructure.api.swagger-spec
  (:require
   [domain.util]
   [domain.user]
   [domain.community]
   [spec-tools.core :as st]
   [clojure.spec.alpha :as s]))

(s/def ::code int?)
(s/def ::message string?)
(s/def ::error (s/keys :req-un [::code ::message]))
(s/def ::begin_cursor (s/and string? #(re-matches domain.util/id-regex  %)))
(s/def ::last_cursor (s/and string? #(re-matches domain.util/id-regex  %)))
(s/def ::request_size pos-int?)
(s/def ::total_size nat-int?)
(def error (st/spec {:spec ::error
                     :name "Error"}))

(def before-size (st/spec {:spec nat-int?
                           :name "before-size"
                           :description "レスポンスのリストより前の要素数"}))
(def total-size (st/spec {:spec nat-int?
                          :name "total-size"}))

(def user
  (st/spec
   {:spec ::domain.user/query
    :name "User"
    :description "user information"
    :openapi/example
    ;; fetch from sample code
    {:id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :name "Meguru Mokke"
     :icon_url "https://avatars.githubusercontent.com/u/30849444?v=4"}}))

(def community
  (st/spec
   {:spec ::domain.community/query
    :name "Community"
    :description "community information"
    :openapi/example
    {:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :name "辛いものの部"
     :details "辛いものが好きな人集まれー"
     :category :gurmand
     :image_url (str domain.community/dummy-image-base-url "/id/292/{width}/{height}.jpg")}}))
