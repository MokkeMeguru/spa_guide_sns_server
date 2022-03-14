(ns domain.user
  (:require [clojure.spec.alpha :as s]
            [domain.util.url]
            [domain.util]))

(s/def ::id (s/and string? #(re-matches domain.util/id-regex  %)))
(s/def ::name (s/and string? #(<= 5 (count %) 30)))
(s/def ::icon_url ::domain.util.url/url)

(s/def ::query (s/keys :req-un [::id ::name ::icon_url]))
(s/def ::command (s/keys :req-un [::name ::icon_url] :opt-un [::id]))

;; Proposal Note:
;; graphql ID like github graphql system
;; it's on only graphql controller / presentation layer (in domain layer, we use the id which removed the prefix)
;; (clojure.string/split (str "user/" (str (random-uuid))) "/")

(defprotocol IUserQueryRepository
  (-list-user [this])
  (-fetch-user [this user-id])
  (-fetch-users [this user-ids]))

(defprotocol IUserCommandRepository
  (-create-user [this user]))

(s/fdef list-user
  :args (s/cat :this any?)
  :ret (s/* ::query))

(s/fdef fetch-user
  :args (s/cat :this any? :user-id ::id)
  :ret (s/or :exist ::query
             :not-exist nil?))

(s/fdef fetch-users
  :args (s/cat :this any? :user-ids (s/* ::id))
  :ret (s/* ::query))

(s/fdef create-user
  :args (s/cat :this any? :user ::command)
  :ret (s/or :succeed ::query
             :failed nil?))

(defn list-user [this] (-list-user this))
(defn fetch-user [this user-id] (-fetch-user this user-id))
(defn fetch-users [this user-ids] (-fetch-users this user-ids))
(defn create-user [this user] (-create-user this user))

;; Developer's Note:
;; We cannot apply clojure.spec into defprotocol directory
;; https://clojure.atlassian.net/browse/CLJ-2109
