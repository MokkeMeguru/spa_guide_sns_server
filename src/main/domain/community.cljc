(ns domain.community
  (:require [clojure.spec.alpha :as s]
            [domain.util]
            [domain.util.url]
            [domain.user]))

(s/def ::id (s/and string? #(re-matches domain.util/id-regex %)))
(s/def ::name (s/and string? #(<= 0 (count %) 36)))
(s/def ::details (s/and string? #(<= 0 (count %) 140)))
(s/def ::category #{:gurmand :sports :geek :anime})
(s/def ::image-url ::domain.util.url/url)
(s/def ::created-at nat-int?)
(s/def ::updated-at nat-int?)

(s/def ::query (s/keys :req-un [::id ::name ::details ::category ::image-url ::created-at ::updated-at]))
(s/def ::command (s/keys :req-un [::name ::details ::category ::image-url] :opt-un [::id]))

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

;; dummy utility
(s/fdef sample-dummy-image-url
  :args (s/cat :category ::category)
  :ret ::domain.util.url/url)

(def dummy-image-base-url "https://picsum.photos")
(def dummy-image-id-map
  "サンプル画像の URL. community 作成時に指定されなければこの中からランダムに選ぶ"
  {:gurmand ["292" "999" "1060" "835"]
   :sports ["1084" "844" "1077"]
   :geek ["1010" "1082" "1078" "896" "674"]
   :anime ["452" "998" "82"]})

(defn sample-dummy-image-url [category]
  (str dummy-image-base-url "/id/" (rand-nth (get dummy-image-id-map category ["292"])) "/{width}/{height}.jpg"))
