(ns infrastructure.sqlite3.util
  (:require ["better-sqlite3" :as better-sqlite3]))

(def activate-foreign-key
  "PRAGMA foreign_keys = ON;")

(defn db! [^string path]
  (better-sqlite3.
   path
   ;; TODO in production, we don't need below logging right?
   #js{"verbose" (fn [query] (js/console.log (.toISOString (js/Date.)) "INFO" "execute query:" query))}))

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
;;   (domain.community/search-communities-by-name repo "sample"))

;; (let [repo (interface.gateway.sqlite3.community/make-community-command-repository
;;             (db! "./db.sqlite3"))]
;;   (domain.community/create-community repo {:name "sample4" :details "sample details brabra" :categories :gurmand}))
