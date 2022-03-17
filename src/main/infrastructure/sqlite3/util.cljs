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
;;     (.prepare "SELECT * FROM communities")
;;     (.all))

;; (let [repo (interface.gateway.sqlite3.community/make-community-query-repository
;;             (db! "./db.sqlite3"))]
;;   ;; (domain.community/list-community repo)
;;   ;; (domain.community/fetch-community repo "f95bd742-86f9-48f7-b848-7d562f4c5010")
;;   ;; (domain.community/search-communities-by-name repo "sample")
;;   (count (domain.community/list-part-community repo 5 nil :created-at-desc)))

;; (let [repo (interface.gateway.sqlite3.community/make-community-command-repository
;;             (db! "./db.sqlite3"))]
;;   (domain.community/create-community repo {:name "sample4" :details "sample details brabra" :categories :gurmand}))

;; community-members
;; (-> (db! "./db.sqlite3")
;;     (.prepare "SELECT * FROM community_members")
;;     (.all))

;; (let [repo (interface.gateway.sqlite3.community-member/make-community-member-query-repository (db! "./db.sqlite3"))]
;;   (domain.community.member/list-community-member repo)
;;   (domain.community.member/fetch-community-member repo "eb86ddc9-6446-44d3-8afa-5def58bbe340")
;;   (domain.community.member/search-community-member-by-community-id repo "f61f5f38-174b-43e1-8873-4f7cdbee1c18"))

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
