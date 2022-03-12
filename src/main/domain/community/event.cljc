(ns domain.community.event
  (:require [clojure.spec.alpha :as s]
            [domain.util]
            [domain.util.url]
            [domain.user]
            [domain.community]
            [domain.community.member]))

(s/def ::id (s/and string? #(re-matches domain.util/id-regex %)))
(s/def ::name string?)
(s/def ::details string?)
(s/def ::hold-at int?) ;; TODO apply regex YYYY/MM/DD
(s/def ::category #{:party :seminar})
(s/def ::image-url ::domain.util.url/url)

(s/def ::community ::domain.community/query)
(s/def ::owned-member ::domain.community.member/query)
(s/def ::query (s/keys :req-un [::id ::community ::owned-member ::name ::details ::hold-at ::category ::image-url]))

(s/def ::owned-member-id ::domain.community.member/id)
(s/def ::community-id ::domain.community/id)
(s/def ::command (s/keys :req-un [::community-id ::owned-member-id ::name ::details ::hold-at ::category ::image-url] :opt-un [::id]))

(def dummy-image-base-url "https://picsum.photos")

(defprotocol ICommunityEventQueryRepository
  (-list-community-event [this])
  (-fetch-community-event [this event-id])
  (-search-community-event-by-community-id [this community-id]))

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

(s/fdef create-community-event
  :args (s/cat :this any? :event ::command)
  :ret (s/or :succeed ::query
             :failed nil?))

(defn list-community-event [this] (-list-community-event this))
(defn fetch-community-event [this event-id] (-fetch-community-event this event-id))
(defn search-community-event-by-community-id [this community-id] (-search-community-event-by-community-id this community-id))
(defn create-community-event [this event] (-create-community-event this event))
