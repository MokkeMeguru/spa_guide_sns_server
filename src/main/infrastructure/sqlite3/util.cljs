(ns infrastructure.sqlite3.util
  (:require ["better-sqlite3" :as better-sqlite3]
            [clojure.string]))

(def activate-foreign-key
  "PRAGMA foreign_keys = ON;")

(defn db! [^string path]
  (better-sqlite3.
   path
   ;; TODO in production, we don't need below logging right?
   #js{"verbose" (fn [query] (js/console.log (.toISOString (js/Date.)) "INFO" "execute query:" (str query)))}))

;; REPL
;; users
;; (-> (db! "./db.sqlite3")
;;     (.prepare "SELECT * FROM users")
;;     (.all))

;; (let [repo (interface.gateway.sqlite3.user/make-user-query-repository
;;             (db! "./db.sqlite3"))]
;;   ;; (domain.user/list-user repo)
;;   ;; (println (domain.user/fetch-user repo "6e803bdf-55a7-4a31-849e-8489cc76a457"))
;;   (domain.user/fetch-users repo ["6e803bdf-55a7-4a31-849e-8489cc76a457"]))

;; (let [repo (interface.gateway.sqlite3.user/make-user-command-repository (db! "./db.sqlite3"))]
;;   (domain.user/insert-user
;;    repo
;;    {:name "sample2" :icon-url "https://yt3.ggpht.com/yti/APfAmoHFNGlL4ldSWi1PG-sceFVNObm3_qCAYrLu6SUl1g=s108-c-k-c0x00ffffff-no-rj"}))

;; communities
;; (-> (db! "./db.sqlite3")
;;     (.prepare
;;      "
;; SELECT
;;   communities.id AS id,
;;   communities.name AS name,
;;   communities.details AS details,
;;   communities.category AS category,
;;   communities.image_url AS image_url,
;;   communities.created_at AS created_at,
;;   communities.updated_at AS updated_at,
;;   COUNT(*) AS membership
;; FROM communities
;; LEFT JOIN community_members
;; ON communities.id=community_members.community_id
;; WHERE communities.id='f61f5f38-174b-43e1-8873-4f7cdbee1c18'
;; GROUP BY communities.id
;; ")
;;     (.all))

;; (let [repo (interface.gateway.sqlite3.community/make-community-query-repository
;;             (db! "./db.sqlite3"))]
;;   ;; (domain.community/list-community repo)
;;   ;; (domain.community/fetch-community repo
;;   ;;                                   "2fbbc61f-b6b2-4711-bf05-d920f42de9be")

;;   ;; (domain.community/search-communities-by-name repo "sample")
;;   ;; (cljs.pprint/pprint
;;   ;;  (sort :created-at (domain.community/list-part-community repo 5 "f53386da-fd0f-42a8-8c1a-cfc7bc31da5a" :updated-at-desc "ン")))
;;   ;; (cljs.pprint/pprint
;;   ;;  (sort :created-at (domain.community/list-part-community repo 5 nil :updated-at-desc nil)))
;;   ;; (domain.community/size-community repo)
;;   (domain.community/before-size-community repo {:updated-at 0} "辛いものの部"))

;; (let [repo (interface.gateway.sqlite3.community/make-community-command-repository
;;             (db! "./db.sqlite3"))]
;;   (domain.community/create-community repo {:name "sample4" :details "sample details brabra" :categories :gurmand}))

;; community-members
;; (-> (db! "./db.sqlite3")
;;     (.prepare "SELECT * FROM community_members")
;;     (.all)
;;     js->clj
;;     (cljs.pprint/pprint))

;; (let [repo (interface.gateway.sqlite3.community-member/make-community-member-query-repository (db! "./db.sqlite3"))]
;;   ;; (domain.community.member/list-community-member repo)
;;   ;; (domain.community.member/fetch-community-member repo "eb86ddc9-6446-44d3-8afa-5def58bbe340")
;;   ;; (domain.community.member/search-community-member-by-community-id repo "f61f5f38-174b-43e1-8873-4f7cdbee1c18")
;;   (domain.community.member/check-joined
;;    repo "82bbb43c-5564-487c-9a21-7416fc6ed357"
;;    ["f61f5f38-174b-43e1-8873-4f7cdbee1c18"
;;     "9ec955a0-a016-4939-b64d-69d514ac4e55"
;;     "1f12489c-2135-4a6a-b49b-83fe3f240d64"]))

;; community-events
;; (-> (db! "./db.sqlite3")
;;     (.prepare "SELECT * FROM community_events")
;;     (.all))

;; (let [repo (interface.gateway.sqlite3.community-event/make-community-event-query-repository (db! "./db.sqlite3"))]
;;   (domain.community.event/list-community-event repo)
;;   (domain.community.event/fetch-community-event repo "b6ee37fe-9191-417d-a932-f2d3d0e307dd")
;;   (domain.community.event/search-community-event-by-community-id repo "f61f5f38-174b-43e1-8873-4f7cdbee1c1a8")
;; )

;; community-event-comments
;; (let [repo (interface.gateway.sqlite3.community-event-comment/make-community-event-comment-query-repository (db! "./db.sqlite3"))]
;;    (domain.community.event.comment/list-community-event-comment repo)
;;    (domain.community.event.comment/fetch-communtiy-event-comment repo "24dc624c-fc35-46fb-86d0-f60c74c5ae6e")
;;    (domain.community.event.comment/fetch-community-event-comment-by-event-id repo "383503d8-dea4-4f26-85b5-afe00f29184b")
;;    (domain.community.event.comment/fetch-community-event-comment-by-event-ids repo ["383503d8-dea4-4f26-85b5-afe00f29184b"])
;;   )
