(ns cmd.openapi.openapi
  (:require [infrastructure.api.swagger-spec :refer [community path community-event]]
            [spec-tools.openapi.core :as openapi]
            [spec-tools.core :as st]
            [domain.user]
            [clojure.spec.alpha :as s]
            [clojure.string]))

;; for toy
(s/def ::name string?)

(def openapi-version "3.0.3")
(def info
  {:title "SPA Guide: SNS API Server"
   :description "the sample of SNS API Server (TOY)"
   :version "0.1.1"
   :termsOfService "https://github.com/MokkeMeguru/spa_guide_sns_server"
   :contact {:name "API Server support"
             :url "https://github.com/MokkeMeguru"
             :email "meguru.mokke@gmail.com"}
   :license {:name "MIT"
             :url "https://github.com/opensource-jp/licenses/blob/dc436911d1f0f150ca66c3f6ff91c60584567933/MIT/MIT.md"}})

(def servers
  [{:url "http://127.0.0.1:3000"
    :description "local server (run with `npm run start_release`)"}])

(def components
  (-> {:components {::openapi/schemas {:User infrastructure.api.swagger-spec/user
                                       :Community infrastructure.api.swagger-spec/community
                                       :CommunityMember infrastructure.api.swagger-spec/communityMember
                                       :CommunityEvent infrastructure.api.swagger-spec/communityEvent
                                       :CommunityEventComment infrastructure.api.swagger-spec/communityEventComment
                                       :Error infrastructure.api.swagger-spec/error}}}
      openapi/openapi-spec
      :components
      (update-in [:schemas :CommunityMember :properties]
                 #(cond-> %
                    (get % "community") (assoc "community" {"$ref" "#/components/schemas/Community"})
                    (get % "user") (assoc "user" {"$ref" "#/components/schemas/User"})))))

(def paths
  {"/test"
   {:get {:operationId "getTest"
          :description "ping pong"
          :tags ["test"]
          ::openapi/parameters {:query (s/keys :req-un [::name])}
          :responses {200 {:description "pong"
                           ::openapi/content
                           {"application/json"
                            (st/spec {:spec {:message string?}
                                      :openapi/example "Hello : User"})}}}}}
   "/my/profile"
   {:get {:operationId "getMyProfile"
          :description "ユーザ情報を返します"
          :tags ["my"]
          :responses  {200 {:description "ログインユーザ"
                            :content
                            {"application/json"
                             {:schema {:type "object"
                                       :required [:user]
                                       :properties {:user {"$ref" "#/components/schemas/User"}}}}}}}}}
   "/users"
   {:get {:operationId "listUser"
          :description "全てのユーザのリストを返します (debug)"
          :tags ["user"]
          :responses {200 {:description "全てのユーザ"
                           :content
                           {"application/json"
                            ;; TODO 後から replace する仕組みを作る
                            {:schema {:type "object"
                                      :required [:users :totalSize]
                                      :properties {:users {:type "array"
                                                           :items {"$ref" "#/components/schemas/User"}}
                                                   :totalSize (openapi/transform infrastructure.api.swagger-spec/total-size)}}}}}}}}
   "/users/{id}"
   {:get {:operationId "getUser"
          :tags ["user"]
          ::openapi/parameters {:path (s/keys :req-un [::domain.user/id])}
          :responses {200 {:description "id を持つユーザ"
                           :content
                           {"application/json"
                            {:schema {:type "object"
                                      :required [:user]
                                      :properties {:user {"$ref" "#/components/schemas/User"}}}}}}
                      404 {:description "user is not found"
                           :content
                           {"application/json"
                            {:schema {"$ref" "#/components/schemas/Error"}}}}}}}
   "/communities"
   {:get {:operationId "listCommunity"
          :tags ["community"]
          :description (clojure.string/join "<br/>\n" ["全てのコミュニティのリストを返します"
                                                       "クエリパラメータは、begin_cursor>last_cursorの順で評価されます"
                                                       "cursor に存在しない communityId が指定されたときには cursor が指定されなかったときと同じ挙動をします"
                                                       "- beginCursor: 指定された communityId より後のコミュニティリストを返す"
                                                       "- lastCursor : 指定された communityId より前のコミュニティリストを返す"
                                                       "- requestSize: 指定されたサイズ以下ののコミュニティリストを返す"
                                                       "- keyword: 指定されたキーワードに部分一致するコミュニティリストを返す"])
          ::openapi/parameters {:query (s/keys :req-un [::infrastructure.api.swagger-spec/requestSize]
                                               :opt-un [::infrastructure.api.swagger-spec/beginCursor
                                                        ::infrastructure.api.swagger-spec/lastCursor
                                                        :community/keyword])}
          :responses {200 {:description "コミュニティのリスト"
                           :content
                           {"application/json"
                            {:schema {:type "object"
                                      :required [:communities :beforeSize :totalSize]
                                      :properties {:communities {:type "array"
                                                                 :items
                                                                 {:type "object"
                                                                  :required [:community]
                                                                  :properties {:community {"$ref" "#/components/schemas/Community"}
                                                                               :isJoined (assoc (openapi/transform infrastructure.api.swagger-spec/community-is-joined)
                                                                                                :nullable true)}}}
                                                   :beforeSize (openapi/transform infrastructure.api.swagger-spec/before-size)
                                                   :totalSize (openapi/transform infrastructure.api.swagger-spec/total-size)}}}}}}}}
   "/communities/{communityId}"
   {:get {:operationId "getCommunity"
          :tags ["community"]
          :description (clojure.string/join "<br/>\n" ["コミュニティの情報を返します"])
          ::openapi/parameters {:path (s/keys :req-un [:path/communityId])}
          :responses {200 {:description "コミュニティと所属メンバー一覧"
                           :content
                           {"application/json"
                            {:schema {:type "object"
                                      :required [:community :members]
                                      :properties {:community {"$ref" "#/components/schemas/Community"}
                                                   :isJoined (assoc (openapi/transform infrastructure.api.swagger-spec/community-is-joined)
                                                                    :nullable true)
                                                   :members {:type "array"
                                                             :items {"$ref" "#/components/schemas/CommunityMember"}}}}}}}}}}
   "/communities/{communityId}/events"
   {:get {:operationId "listCommunityEvent"
          :tags ["communityEvent"]
          :description (clojure.string/join "<br/>\n" ["コミュニティの全てのイベントを返します"
                                                       "includes の設計は Twitter と同じで、重複しうる参照をまとめて返します (簡単のために required にしています)"
                                                       "see.  https://developer.twitter.com/en/docs/twitter-api/tweets/lookup/api-reference/get-tweets-id#Optional"
                                                       "cursor に存在しない eventId が指定されたときには cursor が指定されなかったときと同じ挙動をします"
                                                       "- beginCursor: 指定された communityId より後のコミュニティリストを返す"
                                                       "- lastCursor : 指定された communityId より前のコミュニティリストを返す"
                                                       "- requestSize: 指定されたサイズ以下ののコミュニティリストを返す"
                                                       "- keyword: 指定されたキーワードに部分一致するコミュニティリストを返す"])
          ::openapi/parameters {:path (s/keys :req-un [:path/communityId])
                                :query (s/keys :req-un [::infrastructure.api.swagger-spec/requestSize]
                                               :opt-un [::infrastructure.api.swagger-spec/beginCursor
                                                        ::infrastructure.api.swagger-spec/lastCursor])}
          :responses {200 {:description "コミュニティの全てのイベント"
                           :content {"application/json"
                                     {:schema {:type "object"
                                               :required [:events :beforeSize :totalSize :includes]
                                               :properties {:events {:type "array"
                                                                     :items
                                                                     {:type "object"
                                                                      :required [:communityEvent :representativeComment]
                                                                      :properties {:communityEvent {"$ref" "#/components/schemas/CommunityEvent"}
                                                                                   :representativeComment {:type "array"
                                                                                                           :items {"$ref" "#/components/schemas/CommunityEventComment"}}}}}
                                                            :includes {:type "object"
                                                                       :required [:communityMembers]
                                                                       :properties
                                                                       {:communityMembers {:type "array"
                                                                                           :items {"$ref" "#/components/schemas/CommunityMember"}}}}
                                                            :beforeSize (openapi/transform infrastructure.api.swagger-spec/before-size)
                                                            :totalSize (openapi/transform infrastructure.api.swagger-spec/total-size)}}}}}}}}

   "/communityes/{communityId}/events/{eventId}/comments"
   {:get {:operationId "listCommunityEventComment"
          :tags ["communityEventComment"]
          ::openapi/parameters {:path (s/keys :req-un [:path/communityId :path/eventId])}
          :description (clojure.string/join "<br/>\n" ["コミュニティイベントについた全てのコメントを返します"])
          :responses {200 {:description "コミュニティイベントについたコメント"
                           :content {"application/json"
                                     {:schema {:type "object"
                                               :required [:comments :includes]
                                               :properties
                                               {:comments {:type "array"
                                                           :items {"$ref" "#/components/schemas/CommunityEventComment"}}
                                                :includes {:type "object"
                                                           :required [:communityMembers]
                                                           :properties
                                                           {:communityMembers {:type "array"
                                                                               :items {"$ref" "#/components/schemas/CommunityMember"}}}}}}}}}}}}})

(defn generate-openapi []
  (update-in (openapi/openapi-spec
              {:openapi openapi-version
               :info info
               :servers servers
               :components components
               :paths paths})
             [:components :schemas]
             (fn [schemas]
               (->> schemas
                    (map (fn [[key value]]
                           {key (dissoc value :title)}))
                    (into {})))))

;; (cljs.pprint/pprint
;;  (generate-openapi))
;; Developer Note
;; (def operation (get-in paths ["/test" :get]))
