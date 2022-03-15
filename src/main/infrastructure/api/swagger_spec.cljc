(ns infrastructure.api.swagger-spec
  (:require
   [domain.util]
   [domain.user]
   [domain.community]
   [domain.community.event]
   [domain.community.member]
   [domain.community.event.comment]
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

(s/def :user/id ::domain.user/id)
(s/def :user/name ::domain.user/name)
(s/def :user/iconURL ::domain.user/icon-url)
(s/def :user/user (s/keys :req-un [:user/id :user/name :user/iconURL]))

(def user
  (st/spec
   {:spec :user/user
    :title "User"
    :description "user information"
    :openapi/example
    ;; fetch from sample code
    {:id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :name "Meguru Mokke"
     :iconURL "https://avatars.githubusercontent.com/u/30849444?v=4"}}))

(s/def :community/id ::domain.community/id)
(s/def :community/name ::domain.community/name)
(s/def :community/details ::domain.community/details)
(s/def :community/category ::domain.community/category)
(s/def :community/imageURL ::domain.community/image-url)
(s/def :community/createdAt ::domain.community/created-at)
(s/def :community/updatedAt ::domain.community/updated-at)
(s/def :community/community (s/keys :req-un [:community/id :community/name :community/details
                                             :community/category :community/imageURL
                                             :community/createdAt
                                             :community/updatedAt]))

(def community
  (st/spec
   {:spec :community/community
    :name "Community"
    :description "community information"
    :openapi/example
    {:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :name "辛いものの部"
     :details "辛いものが好きな人集まれー"
     :category :gurmand
     :imageURL (str domain.community/dummy-image-base-url "/id/292/{width}/{height}.jpg")
     :createdAt 1647307406
     :updatedAt 1647307406}}))

(s/def :community-member/id ::domain.community.member/id)
(s/def :community-member/role ::domain.community.member/role)
(s/def :community-member/communityMember (s/keys :req-un [:community-member/id :user/user
                                                          :community/community :community-member/role]))

;; (def communityMember
;;   (st/spec
;;    {:spec :community-member/communityMember
;;     :name "CommunityMember"
;;     :description "community member information"}))

;; (s/def :community-event/id ::domain.community.event/id)
;; (s/def :community-event/name ::domain.community.event/name)
;; (s/def :community-event/ownedMember ::communityMember)
;; (s/def :community-event/details ::domain.community.event/details)
;; (s/def :community-event/holdAt ::domain.community.event/hold-at)
;; (s/def :community-evnet/category ::domain.community.event/category)
;; (s/def :community-event/imageURL ::domain.community.event/image-url)

;; (s/def :community-event/communityEvent
;;   (s/keys :req-un
;;           [:community-event/id ::community :community-event/ownedMember
;;            :community-event/name :community-event/details
;;            :community-event/holdAt :community-event/category :community-event/imageURL]))

;; (def communityEvent
;;   (st/spec
;;    {:spec :community-event/communityEvent
;;     :name "CommunityEvent"
;;     :description "community event informatoion"}))

;; (s/def :community-event-comment/id ::domain.community.event.comment/id)
;; (s/def :community-event-comment/eventID :community-event/id)
;; (s/def :community-event-comment/body ::domain.community.event.comment/body)
;; (s/def :community-event-comment/commentAt ::domain.community.event.comment/comment-at)

;; (s/def :community-event-comment/communityEventComment (s/keys :req-un
;;                                                               [:community-event-comment/id
;;                                                                :community-event-comment/eventID
;;                                                                :community-member/communityMember
;;                                                                :community-event-comment/body
;;                                                                :community-event-comment/commentAt]))
;; (def communityEventComment
;;   (st/spec
;;    {:spec :community-event-comment/communityEventComment
;;     :name "communityEventComment"
;;     :description "the comment on the community event"}))
