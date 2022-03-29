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
(s/def :community/communityInput (s/keys :req-un [:community/name
                                                  :community/details
                                                  :community/category]))
(s/def :community/isJoined boolean?)
(s/def :community/keyword (s/and string? #(<= 0 (count %) 140)))

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

(def communityInput
  (st/spec
   {:spec :community/communityInput
    :name "CommunityInput"
    :description "community input model"
    :openapi/example
    {:name "辛い肩こりにPON☆と効く、ストレッチ研究会"
     :details "背筋を伸ばして寿命も伸ばそう"
     :category :sports}}))

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

(defn community-member->http [community-member]
  (let [{:keys [id community-id user role]} community-member]
    {:id id
     :communityId community-id
     :user (user->http user)
     :role role}))

(s/def :community-event/id ::domain.community.event/id)
(s/def :community-event/communityId ::domain.community/id)
(s/def :community-event/name ::domain.community.event/name)
(s/def :community-event/ownedMemberId ::domain.community.member/id)
(s/def :community-event/details ::domain.community.event/details)
(s/def :community-event/holdAt ::domain.community.event/hold-at)
(s/def :community-event/category ::domain.community.event/category)
(s/def :community-event/imageUrl ::domain.community.event/image-url)
(s/def :community-event/keyword (s/and string? #(<= 0 (count %) 140)))
(s/def :community-event/communityEvent
  (s/keys :req-un
          [:community-event/id :community-event/communityId :community-event/ownedMemberId
           :community-event/name :community-event/details
           :community-event/holdAt :community-event/category :community-event/imageUrl]))
(s/def :community-event/communityEventInput
  (s/keys :req-un
          [:community-event/name :community-event/details
           :community-event/holdAt :community-event/category]))

(def community-event {})
(def communityEvent
  (st/spec
   {:spec :community-event/communityEvent
    :name "CommunityEvent"
    :description "community event informatoion"}))

(def communityEventInput
  (st/spec
   {:spec :community-event/communityEventInput
    :name "CommunityEventInput"
    :description "community event input model"
    :openapi/example
    {:name "超激辛麻婆豆腐を味わいに冬木市に行こう"
     :details "食うか―――？"
     :holdAt 1656795600000
     :category :party}}))

(defn community-event->http [community-event]
  (let [{:keys [id community-id owned-member-id name details hold-at category image-url]} community-event]
    {:id id
     :communityId community-id
     :ownedMemberId owned-member-id
     :name name
     :details details
     :holdAt hold-at
     :category category
     :imageUrl image-url}))

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
(s/def :community-event-comment/communityEventCommentInput (s/keys :req-un
                                                                   [:community-event-comment/body]))

(def communityEventComment
  (st/spec
   {:spec :community-event-comment/communityEventComment
    :name "CommunityEventComment"
    :description "the comment on the community event"
    :openapi/example
    {:id "4ad8ef9d-8a2e-45fb-b77c-a16dd32a3746"
     :eventId "687a7541-336a-43b1-8f29-a1f5412512ee"
     :commentedMemberId "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :body "ポインタには中身がある…………そんなふうに考えていた時期が俺にもありました"
     :commentAt 1648397939347}}))

(def communityEventCommentInput
  (st/spec
   {:spec :community-event-comment/communityEventCommentInput
    :name "CommunityEventCommentInput"
    :description "the comment input model"
    :openapi/example
    {:body "深夜作業には、エナジードリンクと栄養ドリンク、どっちが向いていると思いますか？"}}))

(defn community-event-comment->http [community-event-comment]
  (let [{:keys [id event-id member-id body comment-at]} community-event-comment]
    {:id id
     :eventId event-id
     :commentedMemberId member-id
     :body body
     :commentAt comment-at}))
