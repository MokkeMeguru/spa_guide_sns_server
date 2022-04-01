(ns domain.community
  (:require [clojure.spec.alpha :as s]
            [domain.util]
            [domain.util.url]
            [domain.user]))

(def category #{:gurmand :sports :geek :anime})

(s/def ::id (s/and string? #(re-matches domain.util/id-regex %)))
(s/def ::name (s/and string? #(<= 0 (count %) 36)))
(s/def ::details (s/and string? #(<= 0 (count %) 140)))
(s/def ::category #{:gurmand :sports :geek :anime})
(s/def ::image-url ::domain.util.url/url)
(s/def ::created-at nat-int?)
(s/def ::updated-at nat-int?)

(s/def ::query (s/keys :req-un [::id ::name ::details ::category ::image-url ::created-at ::updated-at]))
(s/def ::command (s/keys :req-un [::name ::details ::category ::image-url] :opt-un [::id]))

;; NOTE asc: 新しい順 / desc: 古い順
(s/def ::sort-order #{:updated-at-asc :updated-at-desc})
(s/def ::keyword (s/and string? #(<= 0 (count %) 36)))
(s/def ::before-size number?)
(s/def ::total-size nat-int?)

(defprotocol ICommunityQueryRepository
  (-list-community [this])
  (-list-part-community [this request-size from-cursor sort-order keyword])
  (-fetch-community [this community-id])
  (-size-community [this keyword])
  (-before-size-community [this community keyword]))

(defprotocol ICommunityCommandRepository
  (-create-community [this community])
  (-touch-community [this community-id]))

(s/fdef list-community
  :args (s/cat :this any?)
  :ret (s/* ::query))

(s/fdef list-part-community
  :args (s/cat :this any? :request-size pos-int? :from-cursor (s/nilable ::query) :sort-order ::sort-order :keyword (s/nilable ::keyword))
  :ret (s/* ::query))

(s/fdef fetch-community
  :args (s/cat :this any? :community-id ::id)
  :ret (s/or :exist ::query
             :not-exist nil?))
(s/fdef size-community
  :args (s/cat :this any? :keyword (s/nilable ::keyword))
  :ret nat-int?)

(s/fdef before-size-community
  :args (s/cat :this any? :community ::query :keyword (s/nilable ::keyword))
  :ret (s/keys :req-un [::before-size ::total-size]))

(s/fdef create-community
  :args (s/cat :this any? :community ::command)
  :ret (s/or :succeed ::id
             :failed nil?))

(s/fdef touch-community
  :args (s/cat :this any? :community-id ::id)
  :ret boolean?)

(defn list-community
  "community を全件取得します"
  [this]
  (-list-community this))

(defn list-part-community
  "community の検索を行います

  - request-size: 返しうる community の最大数
  - keyword: name, details について keyword に部分一致する community を検索します
  - from-cursor: from-cursor より `sort-order` 的に *後* のコミュニティを検索します
  - sort-order:
    - :updated-at-desc: 更新日時について新しい順
    - :updated-at-asc: 更新日時について古い順

  Example:

      (list-part-community repo 5 nil :updated-at-desc nil) ;; 最新 5 件を取得

      (list-part-community repo 5 {...} :updated-at-desc nil) ;; ... より古い community の最新5件を取得
  "
  [this request-size from-cursor sort-order keyword]
  (-list-part-community this request-size from-cursor sort-order keyword))

(defn fetch-community
  "community-id を持つ community を検索します
  存在しないときには nil を返します"
  [this community-id]
  (-fetch-community this community-id))

(defn size-community
  "現在存在する community の数を返します
  keyword が指定されたときには、 keyword を含む community のリストの中で検索します
  "
  [this keyword]
  (-size-community this keyword))

(defn before-size-community
  "community よりも前の community の数と現在存在する community の数を返します
  keyword が指定されたときには、 keyword を含む community のリストの中で検索します

  Example:

      (before-size-community repo nil nil) ;; {:before-size 0 :total-size 10}
  "
  [this community keyword]
  (-before-size-community this community keyword))

(defn create-community
  "community を作成します"
  [this community]
  (-create-community this community))

(defn touch-community
  "community を更新時間を更新します"
  [this community-id]
  (-touch-community this community-id))

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
