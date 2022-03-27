(ns domain.community.event
  (:require [clojure.spec.alpha :as s]
            [domain.util]
            [domain.util.url]
            [domain.user]
            [domain.community]
            [domain.community.member]))

(def category #{:party :seminar})

(s/def ::id (s/and string? #(re-matches domain.util/id-regex %)))
(s/def ::name string?)
(s/def ::details string?)
(s/def ::hold-at int?) ;; TODO apply regex YYYY/MM/DD
(s/def ::category category)
(s/def ::image-url ::domain.util.url/url)

(s/def ::community-id ::domain.community/id)
(s/def ::owned-member-id ::domain.community.member/id)
(s/def ::query
  (s/keys
   :req-un [::id ::community-id ::owned-member-id ::name ::details ::hold-at ::category ::image-url]))

(s/def ::command
  (s/keys
   :req-un [::community-id ::owned-member-id ::name ::details ::hold-at ::category ::image-url]
   :opt-un [::id]))

(s/def ::sort-order #{:hold-at-asc :hold-at-desc})
(s/def ::before-size number?)
(s/def ::total-size nat-int?)

(defprotocol ICommunityEventQueryRepository
  (-list-community-event [this])
  (-fetch-community-event [this event-id])
  (-search-community-event-by-community-id [this community-id])
  (-search-part-community-event-by-community-id [this community-id request-size from-cursor sort-order])
  (-size-community-event [this community-id])
  (-before-size-community-event [this community-event community-id]))

(defprotocol ICommunityEventCommandRepository
  (-create-community-event [this event]))

(s/fdef list-community-event
  :args (s/cat :this any?)
  :ret (s/* ::query))

(s/fdef fetch-community-event
  :args (s/cat :this any? :event-id ::id)
  :ret (s/or :exist ::query
             :not-exist nil?))

(s/fdef search-community-event-by-community-id
  :args (s/cat :this any? :community-id ::domain.community/id)
  :ret (s/* ::query))

(s/fdef search-part-community-event-by-community-id
  :args (s/cat :this any? :community-id ::domain.community/id :request-size pos-int? :from-cursor (s/nilable ::query) :sort-order ::sort-order)
  :ret (s/* ::query))

(s/fdef create-community-event
  :args (s/cat :this any? :event ::command)
  :ret (s/or :succeed ::id
             :failed nil?))

(s/fdef size-community-event
  :args (s/cat :this any?)
  :ret nat-int?)

(s/fdef before-size-community-event
  :args (s/cat :this any? :community-event ::query)
  :ret (s/keys :req-un [::before-size ::total-size]))

(defn list-community-event
  "community-event を全件取得します"
  [this]
  (-list-community-event this))

(defn fetch-community-event
  "event-id を持つ community-event を検索します
  存在しないときには nil を返します"
  [this event-id]
  (-fetch-community-event this event-id))

(defn search-community-event-by-community-id
  "community-id に所属する event のリストを取得します"
  [this community-id]
  (-search-community-event-by-community-id this community-id))

(defn search-part-community-event-by-community-id
  "community-id に所属する event のリストを検索します

  - request-size: 返しうる event の最大数
  - from-cursor: from-cursor より `sort-order` 的に *後* のコミュニティを検索します
  - sort-order:
    - :hold-at-desc: 更新日時について新しい順
    - :hold-at-asc: 更新日時について古い順

  Example:

      (search-part-community-event-by-community-id repo \"f61f5f38-174b-43e1-8873-4f7cdbee1c18\" 5 nil :hold-at-desc) ;; 最新 5 件を取得

      (search-part-community-event-by-community-id repo \"f61f5f38-174b-43e1-8873-4f7cdbee1c18\" 5 {...} :hold-at-desc) ;; ... より古い community-event の最新5件を取得
  "
  [this community-id request-size from-cursor sort-order]
  (-search-part-community-event-by-community-id this community-id request-size from-cursor sort-order))

(defn size-community-event
  [this community-id]
  (-size-community-event this community-id))

(defn before-size-community-event
  [this community-event community-id]
  (-before-size-community-event this community-event community-id))

(defn create-community-event
  "community-event を作成します"
  [this event]
  (-create-community-event this event))

;; dummy utility
(s/fdef sample-dummy-image-url
  :args (s/cat :category ::category)
  :ret ::domain.util.url/url)

(def dummy-image-base-url "https://picsum.photos")
(def dummy-image-id-map
  "サンプル画像の URL. community 作成時に指定されなければこの中からランダムに選ぶ"
  {:party ["139" "249" "517"]
   :seminar ["452" "593" "20"]})

(defn sample-dummy-image-url [category]
  (str dummy-image-base-url "/id/" (rand-nth (get dummy-image-id-map category ["292"])) "/{width}/{height}.jpg"))
