(ns infrastructure.api.swagger-spec
  (:require
   [domain.util]
   [domain.util.url]
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
(s/def ::beginCursor (s/and string? #(re-matches domain.util/id-regex  %)))
(s/def ::lastCursor (s/and string? #(re-matches domain.util/id-regex  %)))
(s/def ::requestSize pos-int?)
(s/def ::total_size nat-int?)
(def error (st/spec {:spec ::error
                     :name "Error"}))

(def before-size (st/spec {:spec nat-int?
                           :name "beforeSize"
                           :description "レスポンスのリストより前の要素数"}))
(def total-size (st/spec {:spec nat-int?
                          :name "totalSize"}))

(s/def :path/communityId ::domain.community/id)
(s/def :path/eventId ::domain.community.event/id)

(def path {})

;; user
(s/def :user/id ::domain.user/id)
(s/def :user/name ::domain.user/name)
(s/def :user/iconUrl ::domain.user/icon-url)
(s/def :user/user (s/keys :req-un [:user/id :user/name :user/iconUrl]))

(def user
  (st/spec
   {:spec :user/user
    :title "User"
    :description "user information"
    :openapi/example
    ;; fetch from sample code
    {:id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :name "Meguru Mokke"
     :iconUrl "https://avatars.githubusercontent.com/u/30849444?v=4"}}))

(defn user->http [user]
  (let [{:keys [id name icon-url]} user]
    {:id id
     :name name
     :iconUrl icon-url}))

;; community
(s/def :community/id ::domain.community/id)
(s/def :community/name ::domain.community/name)
(s/def :community/details ::domain.community/details)
(s/def :community/category ::domain.community/category)
(s/def :community/imageUrl ::domain.community/image-url)
(s/def :community/membership pos-int?)
(s/def :community/createdAt ::domain.community/created-at)
(s/def :community/updatedAt ::domain.community/updated-at)
(s/def :community/community (s/keys :req-un [:community/id :community/name :community/details
                                             :community/category :community/imageUrl
                                             :community/membership
                                             :community/createdAt
                                             :community/updatedAt]))
(s/def :community/isJoined boolean?)
(s/def :community/keyword string?)

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
     :imageUrl (str domain.community/dummy-image-base-url "/id/292/{width}/{height}.jpg")
     :membership 10
     :createdAt 1647307406
     :updatedAt 1647307406}}))

(defn community->http [community]
  (let [{:keys [id name details category image-url created-at updated-at membership]} community]
    {:id id
     :name name
     :details details
     :category category
     :imageUrl image-url
     :membership membership
     :createdAt created-at
     :updatedAt updated-at}))

(def community-is-joined
  (st/spec
   {:spec :community/isJoined
    :name "IsJoined"
    :description "the logined user is joined the community"}))

;; member
(s/def :community-member/id ::domain.community.member/id)
(s/def :community-member/role ::domain.community.member/role)
(s/def :community-member/communityId ::domain.community/id)
(s/def :community-member/communityMember
  (s/keys :req-un [:community-member/id :user/user
                   :community-member/communityId :community-member/role]))

(def communityMember
  (st/spec
   {:spec :community-member/communityMember
    :name "CommunityMember"
    :description "community member information"}))

(s/def :community-event/id ::domain.community.event/id)
(s/def :community-event/communityId ::domain.community/id)
(s/def :community-event/name ::domain.community.event/name)
(s/def :community-event/ownedMemberId ::domain.community.member/id)
(s/def :community-event/details ::domain.community.event/details)
(s/def :community-event/holdAt ::domain.community.event/hold-at)
(s/def :community-event/category ::domain.community.event/category)
(s/def :community-event/imageUrl ::domain.community.event/image-url)

(s/def :community-event/communityEvent
  (s/keys :req-un
          [:community-event/id :community-event/communityId :community-event/ownedMemberId
           :community-event/name :community-event/details
           :community-event/holdAt :community-event/category :community-event/imageUrl]))

(def communityEvent
  (st/spec
   {:spec :community-event/communityEvent
    :name "CommunityEvent"
    :description "community event informatoion"}))

(s/def :community-event-comment/id ::domain.community.event.comment/id)
(s/def :community-event-comment/eventId ::domain.community.event/id)
(s/def :community-event-comment/commentedMemberId ::domain.community.member/id)
(s/def :community-event-comment/body ::domain.community.event.comment/body)
(s/def :community-event-comment/commentAt ::domain.community.event.comment/comment-at)

(s/def :community-event-comment/communityEventComment (s/keys :req-un
                                                              [:community-event-comment/id
                                                               :community-event-comment/eventId
                                                               :community-event-comment/commentedMemberId
                                                               :community-event-comment/body
                                                               :community-event-comment/commentAt]))
(def communityEventComment
  (st/spec
   {:spec :community-event-comment/communityEventComment
    :name "communityEventComment"
    :description "the comment on the community event"}))
