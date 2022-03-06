(ns domain.community
  (:require [clojure.spec.alpha :as s]
            [domain.util]
            [domain.user]))

(s/def ::id (s/and string? #(re-matches domain.util/id-regex %)))
(s/def ::name (s/and string? #(<= 0 (count %) 36)))
(s/def ::details (s/and string? #(<= 0 (count %) 140)))
(s/def ::category #{:gurmand :sports :geek :anime})

(s/def ::query (s/keys :req-un [::id ::name ::details ::category]))
(s/def ::command (s/keys :req-un [::name ::details ::category] :opt-un [::id]))

(defprotocol ICommunityQueryRepository
  (-list-community [this])
  (-fetch-community [this community-id])
  (-search-communities-by-name [this like]))

(defprotocol ICommunityCommandRepository
  (-create-community [this community]))

(s/fdef list-community
  :args (s/cat :this any?)
  :ret (s/* ::query))

(s/fdef fetch-community
  :args (s/cat :this any? :community-id ::id)
  :ret (s/or :exist ::query
             :not-exist nil?))

(s/fdef search-communities-by-name
  :args (s/cat :this any? :like ::name)
  :ret (s/* ::query))

(s/fdef create-community
  :args (s/cat :this any? :community ::command)
  :ret (s/or :succeed ::query
             :failed nil?))

(defn list-community [this] (-list-community this))
(defn fetch-community [this community-id] (-fetch-community this community-id))
(defn search-communities-by-name [this like] (-search-communities-by-name this like))
(defn create-community [this community] (-create-community this community))
