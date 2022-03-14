(ns domain.mock
  (:require [domain.community]
            [domain.community.event]))

(def samples
  {:user
   [{:id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :name "Meguru Mokke"
     :icon_url "https://avatars.githubusercontent.com/u/30849444?v=4"}
    {:id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :name "Yutaro Yoshikawa"
     :icon_url "https://avatars.githubusercontent.com/u/38146004?v=4"}]
   :community
   [{:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :name "辛いものの部"
     :details "辛いものが好きな人集まれー"
     :category :gurmand
     :image_url (str domain.community/dummy-image-base-url "/id/292/{width}/{height}.jpg")}
    {:id "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :name "スイートなスイーツ"
     :details "明日の体重を気にしてはいけない"
     :category :gurmand
     :image_url (str domain.community/dummy-image-base-url "/id/999/{width}/{height}.jpg")}
    {:id "4983bd6f-0397-42ce-b17d-e394a6241e42"
     :name "肉体改造部"
     :details "筋肉は全てを解決する"
     :category :sports
     :image_url (str domain.community/dummy-image-base-url "/id/1084/{width}/{height}.jpg")}
    {:id "f1531c66-3a07-41ae-8d98-729d61b7b24a"
     :name "モン○トの集い"
     :details "運/極周回楽しい!!!"
     :category :anime
     :image_url (str domain.community/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    {:id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :name "Golang で世界を救え"
     :details "静的型付け言語にあらずんばプログラミング言語にあらず"
     :category :geek
     :image_url (str domain.community/dummy-image-base-url "/id/1010/{width}/{height}.jpg")}
    {:id "2d758edd-a646-486b-89ec-65a48a7db887"
     :name "健全なる精神は健全なる身体に宿る"
     :details "たとえプロジェクトが裏切っても筋肉は裏切らない"
     :category :sports
     :image_url (str domain.community/dummy-image-base-url "/id/844/{width}/{height}.jpg")}
    {:id "2fbbc61f-b6b2-4711-bf05-d920f42de9be"
     :name "有酸素運動クラブ"
     :details "まずはトレーニング、話はそれからだ"
     :category :sports
     :image_url (str domain.community/dummy-image-base-url "/id/1077/{width}/{height}.jpg")}
    {:id "415bfd23-4557-49ab-8192-2b34f87c26f9"
     :name "組み合わせ最適化から始める競技プログラミング"
     :details "ナップサック問題や2部マッチング問題から始める趣味プログラミング"
     :category :geek
     :image_url (str domain.community/dummy-image-base-url "/id/1082/{width}/{height}.jpg")}
    {:id "5dc8361d-dbee-4102-8331-02f47f46779b"
     :name "エンジニアが神絵師になるまで"
     :details "AI最強説へ\"実力\"で立ち向かおう"
     :category :anime
     :image_url (str domain.community/dummy-image-base-url "/id/998/{width}/{height}.jpg")}
    {:id "6e3e0622-60a3-4001-9d26-20592e09ecff"
     :name "Ruby on Rails は MVP へ通ず"
     :details "IT現場の第一線で活躍する Ruby on Rails を身につけて圧倒的成長 ↑↑↑"
     :category :geek
     :image_url (str domain.community/dummy-image-base-url "/id/1078/{width}/{height}.jpg")}
    {:id "f53386da-fd0f-42a8-8c1a-cfc7bc31da5a"
     :name "飲料からモチベを上げ隊"
     :details "その安直な栄養ドリンク摂取からの脱却を目指して"
     :category :gurmand
     :image_url (str domain.community/dummy-image-base-url "/id/1060/{width}/{height}.jpg")}
    {:id "ac297ee7-195f-47aa-8521-fb32eb3687dd"
     :name "時短飯研究同好会"
     :details "その納期前の、手っ取り早く栄養が取れる時短飯を探しにいこう"
     :category :gurmand
     :image_url (str domain.community/dummy-image-base-url "/id/835/{width}/{height}.jpg")}
    {:id "aeb7a908-766a-4253-9b58-c38829f5bda0"
     :name "生成モデルを求めし者の集い"
     :details "GANs, AutoEncoder, Flow-based Model といった生成モデルを調査し、その基礎体系をまとめていく集まりです"
     :category :geek
     :image_url (str domain.community/dummy-image-base-url "/id/896/{width}/{height}.jpg")}
    {:id "dcef25f4-af90-4fe5-a385-dc661489e63a"
     :name "和菓子のいろは"
     :details "お家でできる、大福やぼたもちといった和菓子を作っていきます"
     :category :gurmand
     :image_url (str domain.community/dummy-image-base-url "/id/674/{width}/{height}.jpg")}
    {:id "1f12489c-2135-4a6a-b49b-83fe3f240d64"
     :name "2022年度版イラストレーション研究会"
     :details "ハッチングのような美術的な技術や昨今注目されているアニメ塗りの技術を習得していく研究会です"
     :category :anime
     :image_url (str domain.community/dummy-image-base-url "/id/82/{width}/{height}.jpg")}]
   :community-member
   [;; 辛いものの部
    {:id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :community_id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :user_id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "08da804a-8ec9-494c-bd28-f67218e30851"
     :community_id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :user_id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :owner}
    ;; スイートなスイーツ
    {:id "7b0497e0-c6ba-4fde-8788-2c3abf3d241f"
     :community_id "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :user_id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "0500f01e-47cd-4753-bc5d-74716b9b32a5"
     :community_id "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :user_id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :owner}
    ;; 肉体改造部
    {:id "a93f7653-0cd9-498a-bf5b-8822d81047c5"
     :community_id "4983bd6f-0397-42ce-b17d-e394a6241e42"
     :user_id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "dd5bf9cf-db09-4c6f-a3bd-383f086df349"
     :community_id "4983bd6f-0397-42ce-b17d-e394a6241e42"
     :user_id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :member}
    ;; モン○トの集い
    {:id "fc591c7b-71e6-48b5-b748-fc416263ea4a"
     :community_id "f1531c66-3a07-41ae-8d98-729d61b7b24a"
     :user_id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "3984f670-6e67-4390-a570-4f4b2a78b3b2"
     :community_id "f1531c66-3a07-41ae-8d98-729d61b7b24a"
     :user_id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :member}
    ;; Golang で世界を救え
    {:id "06e45e1b-f801-47b9-9a28-485786aa85d6"
     :community_id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :user_id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :owner}
    {:id "bdb28225-6231-4998-a37b-f1c572a81008"
     :community_id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :user_id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :member}]
   :community-event
   [;; 辛いものの部
    {:id "b6ee37fe-9191-417d-a932-f2d3d0e307dd"
     :community_id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned_member_id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "社食の胡椒を使い切る会"
     :details "社食の胡椒を増やすべく、まずは需要を \"わからせ\" ていく会"
     :hold_at (.getTime (js/Date. (js/Date.UTC 2022 5 15 9 0 0)))
     :category :party
     :image_url (str domain.community.event/dummy-image-base-url "/id/139/{width}/{height}.jpg")}
    {:id "98ebcf3a-2f88-4205-aa69-ce6d9590ab3c"
     :community_id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned_member_id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "辛ラメーンを教会で食べた話"
     :details "大学時代ボッチ飯キメていたら、知らない先輩と教会に行って辛ラーメンを食べさせてもらった話"
     :hold_at (.getTime (js/Date. (js/Date.UTC 2022 5 17 9 0 0)))
     :category :seminar
     :image_url (str domain.community.event/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    {:id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :community_id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned_member_id "08da804a-8ec9-494c-bd28-f67218e30851"
     :name "会社近くの美味しい韓国料理店を周る集まり"
     :details "会社近くにある辛味の聞いた美味しいチーズタッカルビを出すお店を見つけたので行きましょうの会"
     :hold_at (.getTime (js/Date. (js/Date.UTC 2022 6 3 9 0 0)))
     :category :party
     :image_url (str domain.community.event/dummy-image-base-url "/id/249/{width}/{height}.jpg")}
    ;; Golang で世界を救え
    {:id "687a7541-336a-43b1-8f29-a1f5412512ee"
     :community_id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :owned_member_id "06e45e1b-f801-47b9-9a28-485786aa85d6"
     :name "Clojureとかいう動的型付け言語に対して Golang の圧倒的優位性を見出していく会"
     :details "静的型付け言語で圧倒的安全性と可用性を見せていけ"
     :hold_at (.getTime (js/Date. (js/Date.UTC 2022 5 30 9 0 0)))
     :category :seminar
     :image_url (str domain.community.event/dummy-image-base-url "/id/593/{width}/{height}.jpg")}]
   :community-event-comment
   [;; 辛いものの部 > 会社近くの美味しい韓国料理店を周る集まり
    {:id "24dc624c-fc35-46fb-86d0-f60c74c5ae6e"
     :event_id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :member_id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :body "僕も新卒の皮を被って参加したいです :inori:"}
    {:id "884a9d75-2f8e-4687-804b-3e5bc4804b23"
     :event_id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :member_id "08da804a-8ec9-494c-bd28-f67218e30851"
     :body ":roger:"}
    ;; Golang で世界を救え > Clojureとかいう動的型付け言語に対して Golang の圧倒的優位性を見出していく会
    {:id "4ad8ef9d-8a2e-45fb-b77c-a16dd32a3746"
     :event_id "687a7541-336a-43b1-8f29-a1f5412512ee"
     :member_id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :body "ポインタには中身がある…………そんなふうに考えていた時期が俺にもありました"}]})
