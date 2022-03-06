(ns domain.community.event.comment
  (:require [clojure.spec.alpha :as s]
            [domain.user]
            [domain.util]
            [domain.community.member]
            [domain.community.event]))

(s/def ::id  (s/and string? #(re-matches domain.util/id-regex %)))
(s/def ::body (s/and string? #(<= 1 (count %) 140)))
(s/def ::comment-at int?)

(s/def ::event ::domain.community.event/query)
(s/def ::member ::domain.community.member/query)
(s/def ::query (s/keys :req-un [::id ::event ::member ::body ::comment-at]))

(s/def ::member-id ::domain.community.member/id)
(s/def ::event-id ::domain.community.event/id)
(s/def ::command (s/keys :req-un [::event-id ::member-id ::body] :opt-un [::id]))

(defprotocol ICommunityEventCommentQueryRepository
  (-list-community-event-comment [this])
  (-fetch-community-event-comment-by-event-id [this event-id]))

(defprotocol ICommunityEventCommentCommandRepository
  (-create-community-event-comment [this command]))

(s/fdef list-community-event-comment
  :args (s/cat :this any?)
  :ret (s/* ::query))

(s/fdef fetch-community-event-comment-by-event-id
  :args (s/cat :this any? :event-id ::domain.community.event/id)
  :ret (s/* ::query))

(s/fdef create-community-event-comment
  :args (s/cat :this any? :command ::command)
  :ret (s/or :succeed ::query
             :failed nil?))

(defn list-community-event-comment [this] (-list-community-event-comment this))
(defn fetch-community-event-comment-by-event-id [this event-id] (-fetch-community-event-comment-by-event-id this event-id))
(defn create-community-event-comment [this command] (-create-community-event-comment this command))
