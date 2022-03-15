(ns cmd.openapi.openapi
  (:require [infrastructure.api.swagger-spec]
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
  {::openapi/schemas {:User infrastructure.api.swagger-spec/user
                      :Community infrastructure.api.swagger-spec/community
                      ;; :CommunityMember infrastructure.api.swagger-spec/communityMember
                      ;; :CommunityEvent infrastructure.api.swagger-spec/communityEvent
                      ;; :CommunityEventComment infrastructure.api.swagger-spec/communityEventComment
                      :Error infrastructure.api.swagger-spec/error}})

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
   "/users"
   {:get {:operationId "listUser"
          :description "全てのユーザのリストを返します (debug)"
          :tags ["user"]
          :responses {200 {:description "全てのユーザ"
                           :content
                           {"application/json"
                            {:schema {:type "object"
                                      :properties {:users {:type "array"
                                                           :items {"$ref" "#/components/schemas/User"}}
                                                   :total_size (openapi/transform infrastructure.api.swagger-spec/total-size)}}}}}}}}
   "/users/{id}"
   {:get {:operationId "getUser"
          :tags ["user"]
          ::openapi/parameters {:path (s/keys :req-un [::domain.user/id])}
          :responses {200 {:description "id を持つユーザ"
                           :content
                           {"application/json"
                            {:schema {:type "object"
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
                                                       "- begin_cursor: 指定された community_id より後のコミュニティリストを返す"
                                                       "- last_cursor : 指定された community_id より前のコミュニティリストを返す"
                                                       "- request_size: 指定されたサイズ以下ののコミュニティリストを返す"
                                                       "(指定なしで全ての要素を返す / 最終ページなどでは request_size 未満のサイズのリストを返す)"])
          ::openapi/parameters {:query (s/keys :req-un [::infrastructure.api.swagger-spec/request_size]
                                               :opt-un [::infrastructure.api.swagger-spec/begin_cursor ::infrastructure.api.swagger-spec/last_cursor])}
          :responses {200 {:description "コミュニティのリスト"
                           :content
                           {"application/json"
                            {:schema {:type "object"
                                      :properties {:communities {:type "array"
                                                                 :items {"$ref" "#/components/schemas/Community"}}
                                                   :before_size (openapi/transform infrastructure.api.swagger-spec/before-size)
                                                   :total_size (openapi/transform infrastructure.api.swagger-spec/total-size)}}}}}}}
    ;; :post {:operationId "createCommunity"
    ;;        :description "コミュニティを作成します"
    ;;        :requestBody {}}
    }})

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
                           (println key value)
                           {key (dissoc value :title)}))
                    (into {})))))

;; Developer Note
;; (def operation (get-in paths ["/test" :get]))
