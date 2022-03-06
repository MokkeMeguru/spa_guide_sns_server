(ns cmd.migrate.core
  (:require [domain.user]
            [domain.community]
            [domain.community.member]
            [infrastructure.sqlite3.util]
            [infrastructure.sqlite3.up]
            [taoensso.timbre :refer [error info]]
            ["better-sqlite3" :as better-sqlite3]
            [infrastructure.sqlite3.core]))

(def samples
  {:user
   [{:id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :name "Meguru Mokke"
     :icon-url "https://avatars.githubusercontent.com/u/30849444?v=4"}
    {:id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :name "Yutaro Yoshikawa"
     :icon-url "https://avatars.githubusercontent.com/u/38146004?v=4"}]
   :community
   [{:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :name "辛いものの部"
     :details "辛いものが好きな人集まれー"
     :category :gurmand}
    {:id "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :name "スイートなスイーツ"
     :details "明日の体重を気にしてはいけない"
     :category :gurmand}
    {:id "4983bd6f-0397-42ce-b17d-e394a6241e42"
     :name "肉体改造部"
     :details "筋肉は全てを解決する"
     :category :sports}
    {:id "f1531c66-3a07-41ae-8d98-729d61b7b24a"
     :name "モン○トの集い"
     :details "運/極周回楽しい!!!"
     :category :anime}
    {:id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :name "Golang で世界を救え"
     :details "静的型付け言語にあらずんばプログラミング言語にあらず"
     :category :geek}]
   :community-member
   [;; 辛いものの部
    {:id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "08da804a-8ec9-494c-bd28-f67218e30851"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :owner}
    ;; スイートなスイーツ
    {:id "7b0497e0-c6ba-4fde-8788-2c3abf3d241f"
     :community-id "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "0500f01e-47cd-4753-bc5d-74716b9b32a5"
     :community-id "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :owner}
    ;; 肉体改造部
    {:id "a93f7653-0cd9-498a-bf5b-8822d81047c5"
     :community-id "4983bd6f-0397-42ce-b17d-e394a6241e42"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "dd5bf9cf-db09-4c6f-a3bd-383f086df349"
     :community-id "4983bd6f-0397-42ce-b17d-e394a6241e42"
     :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :member}
    ;; モン○トの集い
    {:id "fc591c7b-71e6-48b5-b748-fc416263ea4a"
     :community-id "f1531c66-3a07-41ae-8d98-729d61b7b24a"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "3984f670-6e67-4390-a570-4f4b2a78b3b2"
     :community-id "f1531c66-3a07-41ae-8d98-729d61b7b24a"
     :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :member}
    ;; Golang で世界を救え
    {:id "06e45e1b-f801-47b9-9a28-485786aa85d6"
     :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :owner}
    {:id "bdb28225-6231-4998-a37b-f1c572a81008"
     :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :member}]
   :community-event
   [;; 辛いものの部
    {:id "b6ee37fe-9191-417d-a932-f2d3d0e307dd"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "社食の胡椒を使い切る会"
     :details "社食の胡椒を増やすべく、まずは需要を \"わからせ\" ていく会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 15 9 0 0)))
     :category :party}
    {:id "98ebcf3a-2f88-4205-aa69-ce6d9590ab3c"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "辛ラメーンを教会で食べた話"
     :details "大学時代ボッチ飯キメていたら、知らない先輩と教会に行って辛ラーメンを食べさせてもらった話"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 17 9 0 0)))
     :category :party}
    {:id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "08da804a-8ec9-494c-bd28-f67218e30851"
     :name "会社近くの美味しい韓国料理店を周る集まり"
     :details "会社近くにある辛味の聞いた美味しいチーズタッカルビを出すお店を見つけたので行きましょうの会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 6 3 9 0 0)))
     :category :party}
    ;; Golang で世界を救え
    {:id "687a7541-336a-43b1-8f29-a1f5412512ee"
     :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :owned-member-id "06e45e1b-f801-47b9-9a28-485786aa85d6"
     :name "Clojureとかいう動的型付け言語に対して Golang の圧倒的優位性を見出していく会"
     :details "静的型付け言語で圧倒的安全性と可用性を見せていけ"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 30 9 0 0)))
     :category :party}]
   :community-event-comment
   [;; 辛いものの部 > 会社近くの美味しい韓国料理店を周る集まり
    {:id "24dc624c-fc35-46fb-86d0-f60c74c5ae6e"
     :event-id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :body "僕も新卒の皮を被って参加したいで :inori:"}
    {:id "884a9d75-2f8e-4687-804b-3e5bc4804b23"
     :event-id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :member-id "08da804a-8ec9-494c-bd28-f67218e30851"
     :body ":roger:"}
    ;; Golang で世界を救え > Clojureとかいう動的型付け言語に対して Golang の圧倒的優位性を見出していく会
    {:id "4ad8ef9d-8a2e-45fb-b77c-a16dd32a3746"
     :event-id "687a7541-336a-43b1-8f29-a1f5412512ee"
     :member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :body "ポインタには中身がある…………そんなふうに考えていた時期が俺にもありました"}]})

(defn migrate! [^string db-path]
  (try
    (let [db (infrastructure.sqlite3.util/db! db-path)]
      (.exec db infrastructure.sqlite3.up/users-table)
      (.exec db infrastructure.sqlite3.up/communities-table)
      (.exec db infrastructure.sqlite3.up/community-members-table)
      (.exec db infrastructure.sqlite3.up/community-events-table)
      (.exec db infrastructure.sqlite3.up/community-event-comments-table)
      (.exec db infrastructure.sqlite3.util/activate-foreign-key))
    (catch :default e
      (error (.-message e))
      nil)))

(defn inject-samples [samples repository]
  (info "inject samples: user")
  (doall
   (map
    (fn [user]
      (domain.user/create-user
       (:user-command-repository repository)
       user))
    (:user samples)))
  (info "inject samples: community")
  (doall
   (map
    (fn [community]
      (domain.community/create-community
       (:community-command-repository repository)
       community))
    (:community samples)))
  (info "inject samples: member")
  (doall
   (map
    (fn [community-member]
      (println
       (domain.community.member/create-community-member
        (:community-member-command-repository repository)
        community-member)))
    (:community-member samples)))
  (info "inject samples: event")
  (info "inject samples: comment"))

(defn migrate []
  (let [db-path  "db.sqlite3"]
    (when (migrate! db-path)
      (let [repository (infrastructure.sqlite3.core/make-repository (infrastructure.sqlite3.util/db! db-path))]
        (inject-samples samples repository)))))



;; (.get (migrate! "./db.sqlite3")
;;       "PRAGMA foreign_keys;"
;;       (fn [err, row] (if err (js/console.log (.-message err)) (js->clj (js/Object.assign #js {} row)))))


;; (let [db-path  "db.sqlite3"]
;;   (println "result>" (migrate! db-path))
;;   (when (migrate! db-path)
;;     (println "OK")
;;     (let [repository (infrastructure.sqlite3.core/make-repository (infrastructure.sqlite3.util/db! db-path))]
;;       (println repository)
;;       (println "OK?"
;;                (doall
;;                 (mapv
;;                  (fn [community-member]
;;                    (domain.community.member/create-community-member
;;                     (:community-member-command-repository repository)
;;                     community-member))
;;                  (:community-member samples)))))))
