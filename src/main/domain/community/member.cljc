(ns domain.community.member
  (:require [clojure.spec.alpha :as s]
            [domain.user]
            [domain.util]
            [domain.community]))

(s/def ::id (s/and string? #(re-matches domain.util/id-regex %)))
(s/def ::role #{:owner :member})

(s/def ::community ::domain.community/query)
(s/def ::user ::domain.user/query)
(s/def ::query (s/keys :req-un [::id ::community ::user ::role]))

(s/def ::community-id ::domain.community/id)
(s/def ::user-id ::domain.user/id)
(s/def ::command (s/keys :req-un [::community-id ::user-id ::role] :opt-un [::id]))

(defprotocol ICommunityMemberQueryRepository
  (-list-community-member [this])
  (-fetch-community-member [this member-id])
  (-check-joined [this user-id community-ids])
  (-search-community-member-by-community-id [this community-id]))

(defprotocol ICommunityMemberCommandRepository
  (-create-community-member [this member]))

(s/fdef list-community-member
  :args (s/cat :this any?)
  :ret (s/* ::query))

(s/fdef fetch-community-member
  :args (s/cat :this any? :member-id ::id)
  :ret (s/or :exist ::query
             :not-exist nil?))

(s/fdef check-joined
  :args (s/cat :this any? :user-id ::domain.user/id
               :community-ids (s/+ ::domain.community/id))
  :ret (s/* ::domain.community/id))

(s/fdef search-community-member-by-community-id
  :args (s/cat :this any? :community-id ::domain.community/id)
  :ret (s/* ::query))

(s/fdef create-community-member
  :args (s/cat :this any? :member ::command)
  :ret (s/or :succeed ::query
             :failed nil?))

(defn list-community-member [this] (-list-community-member this))
(defn fetch-community-member [this member-id] (-fetch-community-member this member-id))
(defn check-joined [this user-id community-ids] (-check-joined this user-id community-ids))
(defn search-community-member-by-community-id [this community-id] (-search-community-member-by-community-id this community-id))
(defn create-community-member [this member] (-create-community-member this member))
