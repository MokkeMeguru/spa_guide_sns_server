(ns domain.mock
  (:require [domain.community]
            [domain.community.event]))

(def samples
  {:user
   [{:id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :name "Meguru Mokke"
     :icon-url "https://avatars.githubusercontent.com/u/30849444?v=4"}
    {:id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :name "Yutaro Yoshikawa"
     :icon-url "https://avatars.githubusercontent.com/u/38146004?v=4"}
    {:id "235bf6b9-9c36-4c21-9893-b5d25e22ac73"
     :name "Takuya Ebata"
     :icon-url "https://yt3.ggpht.com/yti/APfAmoHFNGlL4ldSWi1PG-sceFVNObm3_qCAYrLu6SUl1g=s88-c-k-c0x00ffffff-no-rj-mo"}]
   :community
   [{:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :name "辛いものの部"
     :details "辛いものが好きな人集まれー"
     :category :gurmand
     :image-url (str domain.community/dummy-image-base-url "/id/292/{width}/{height}.jpg")}
    {:id "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :name "スイートなスイーツ"
     :details "明日の体重を気にしてはいけない"
     :category :gurmand
     :image-url (str domain.community/dummy-image-base-url "/id/999/{width}/{height}.jpg")}
    {:id "4983bd6f-0397-42ce-b17d-e394a6241e42"
     :name "肉体改造部"
     :details "筋肉は全てを解決する"
     :category :sports
     :image-url (str domain.community/dummy-image-base-url "/id/1084/{width}/{height}.jpg")}
    {:id "f1531c66-3a07-41ae-8d98-729d61b7b24a"
     :name "モン○トの集い"
     :details "運/極周回楽しい!!!"
     :category :anime
     :image-url (str domain.community/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    {:id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :name "Golang で世界を救え"
     :details "静的型付け言語にあらずんばプログラミング言語にあらず"
     :category :geek
     :image-url (str domain.community/dummy-image-base-url "/id/1010/{width}/{height}.jpg")}
    {:id "2d758edd-a646-486b-89ec-65a48a7db887"
     :name "健全なる精神は健全なる身体に宿る"
     :details "たとえプロジェクトが裏切っても筋肉は裏切らない"
     :category :sports
     :image-url (str domain.community/dummy-image-base-url "/id/844/{width}/{height}.jpg")}
    {:id "2fbbc61f-b6b2-4711-bf05-d920f42de9be"
     :name "有酸素運動クラブ"
     :details "まずはトレーニング、話はそれからだ"
     :category :sports
     :image-url (str domain.community/dummy-image-base-url "/id/1077/{width}/{height}.jpg")}
    {:id "415bfd23-4557-49ab-8192-2b34f87c26f9"
     :name "組み合わせ最適化から始める競技プログラミング"
     :details "ナップサック問題や2部マッチング問題から始める趣味プログラミング"
     :category :geek
     :image-url (str domain.community/dummy-image-base-url "/id/1082/{width}/{height}.jpg")}
    {:id "5dc8361d-dbee-4102-8331-02f47f46779b"
     :name "エンジニアが神絵師になるまで"
     :details "AI最強説へ\"実力\"で立ち向かおう"
     :category :anime
     :image-url (str domain.community/dummy-image-base-url "/id/998/{width}/{height}.jpg")}
    {:id "6e3e0622-60a3-4001-9d26-20592e09ecff"
     :name "Ruby on Rails は MVP へ通ず"
     :details "IT現場の第一線で活躍する Ruby on Rails を身につけて圧倒的成長 ↑↑↑"
     :category :geek
     :image-url (str domain.community/dummy-image-base-url "/id/1078/{width}/{height}.jpg")}
    {:id "f53386da-fd0f-42a8-8c1a-cfc7bc31da5a"
     :name "飲料からモチベを上げ隊"
     :details "その安直な栄養ドリンク摂取からの脱却を目指して"
     :category :gurmand
     :image-url (str domain.community/dummy-image-base-url "/id/1060/{width}/{height}.jpg")}
    {:id "ac297ee7-195f-47aa-8521-fb32eb3687dd"
     :name "時短飯研究同好会"
     :details "その納期前の、手っ取り早く栄養が取れる時短飯を探しにいこう"
     :category :gurmand
     :image-url (str domain.community/dummy-image-base-url "/id/835/{width}/{height}.jpg")}
    {:id "aeb7a908-766a-4253-9b58-c38829f5bda0"
     :name "生成モデルを求めし者の集い"
     :details "GANs, AutoEncoder, Flow-based Model といった生成モデルを調査し、その基礎体系をまとめていく集まりです"
     :category :geek
     :image-url (str domain.community/dummy-image-base-url "/id/896/{width}/{height}.jpg")}
    {:id "dcef25f4-af90-4fe5-a385-dc661489e63a"
     :name "和菓子のいろは"
     :details "お家でできる、大福やぼたもちといった和菓子を作っていきます"
     :category :gurmand
     :image-url (str domain.community/dummy-image-base-url "/id/674/{width}/{height}.jpg")}
    {:id "1f12489c-2135-4a6a-b49b-83fe3f240d64"
     :name "2022年度版イラストレーション研究会"
     :details "ハッチングのような美術的な技術や昨今注目されているアニメ塗りの技術を習得していく研究会です"
     :category :anime
     :image-url (str domain.community/dummy-image-base-url "/id/82/{width}/{height}.jpg")}]
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
    {:id "2f7c3359-9d4f-4873-b281-67a46cb8ae1e"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :user-id "235bf6b9-9c36-4c21-9893-b5d25e22ac73"
     :role :member}
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
    {:id "3a06e0d5-7a13-4df8-a150-d6b4fb39efe6"
     :community-id "f1531c66-3a07-41ae-8d98-729d61b7b24a"
     :user-id "235bf6b9-9c36-4c21-9893-b5d25e22ac73"
     :role :member}
    ;; Golang で世界を救え
    {:id "06e45e1b-f801-47b9-9a28-485786aa85d6"
     :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :owner}
    {:id "bdb28225-6231-4998-a37b-f1c572a81008"
     :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :member}
    ;; 健全なる精神は健全なる身体に宿る
    {:id "10a2167e-7c6e-46d2-8964-0e969e01d2a4"
     :community-id "2d758edd-a646-486b-89ec-65a48a7db887"
     :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :owner}
    ;; 有酸素運動クラブ
    {:id "d9013a2a-ff40-4392-9fe5-99a39e71b101"
     :community-id "2fbbc61f-b6b2-4711-bf05-d920f42de9be"
     :user-id  "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :owner}
    ;; 組み合わせ最適化から始める競技プログラミング
    {:id "3b247c12-ed77-4aa3-accc-67c034743af5"
     :community-id "415bfd23-4557-49ab-8192-2b34f87c26f9"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :member}
    {:id "00262340-a5db-4607-b90d-49223eb38730"
     :community-id "415bfd23-4557-49ab-8192-2b34f87c26f9"
     :user-id "235bf6b9-9c36-4c21-9893-b5d25e22ac73"
     :role :owner}
    ;; エンジニアが神絵師になるまで
    {:id "5dd531bf-e78e-49e6-9929-44c236bfce9a"
     :community-id "5dc8361d-dbee-4102-8331-02f47f46779b"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :owner}
    ;; Ruby on Rails は MVP へ通ず
    {:id "f94b2700-0507-41bc-9bc7-92aaad30bd7a"
     :communtiy-id "6e3e0622-60a3-4001-9d26-20592e09ecff"
     :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"
     :role :member}
    {:id "7cad0e2d-0858-40c3-906b-6e75eded06ee"
     :community-id "6e3e0622-60a3-4001-9d26-20592e09ecff"
     :user-id "235bf6b9-9c36-4c21-9893-b5d25e22ac73"
     :role :owner}
    ;; 飲料からモチベを上げ隊
    {:id "dff1c891-4d23-4175-a3fe-64c3bac642ca"
     :community-id "f53386da-fd0f-42a8-8c1a-cfc7bc31da5a"
     :user-id "235bf6b9-9c36-4c21-9893-b5d25e22ac73"
     :role :owner}
    ;; 時短飯研究同好会
    {:id "e05d2d15-6dcf-4355-aaf5-f4142d0a3caf"
     :community-id "ac297ee7-195f-47aa-8521-fb32eb3687dd"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :owner}
    ;; 生成モデルを求めし者の集い
    {:id "fdfd8eaf-2481-41cb-a40b-24871435b58c"
     :community-id "aeb7a908-766a-4253-9b58-c38829f5bda0"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :owner}
    {:id "e1c9d298-0d03-4e75-b818-f405ce679d41"
     :community-id  "aeb7a908-766a-4253-9b58-c38829f5bda0"
     :user-id "235bf6b9-9c36-4c21-9893-b5d25e22ac73"
     :role :member}
    ;; 和菓子のいろは
    {:id "162dc9f9-1038-4d2e-84ba-e6387d7e4fe0"
     :community-id "dcef25f4-af90-4fe5-a385-dc661489e63a"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :owner}
    ;; 2022年度版イラストレーション研究会
    {:id "83652fe0-9a01-44b8-982d-4abd0083b957"
     :community-id "1f12489c-2135-4a6a-b49b-83fe3f240d64"
     :user-id "6e803bdf-55a7-4a31-849e-8489cc76a457"
     :role :owner}]
   :community-event
   [;; 辛いものの部
    {:id "8e7a264d-7023-4029-bdd4-becadfcf75f2"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id  "08da804a-8ec9-494c-bd28-f67218e30851"
     :name "研修を打ち上げていく会"
     :details "辛さを調整できるお手軽カレー屋でパーッといこう"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 2 18 30 0)))
     :category :party
     :image-url  (str domain.community.event/dummy-image-base-url "/id/249/{width}/{height}.jpg")}
    {:id "d6889952-b566-4ac0-a201-5be14fca97d9"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "08da804a-8ec9-494c-bd28-f67218e30851"
     :name "中華とワインを楽しめるおすすめレストラン"
     :details "合うんです"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 6 18 30 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/249/{width}/{height}.jpg")}
    {:id "9a0570db-0c1a-4558-8ac2-505147431d29"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "片手間に食べれるお手軽カレーパンを買いに行く"
     :details "なんやて！？"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 11 13 30 0)))
     :category :party
     :image-url  (str domain.community.event/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    {:id "1790d7f0-b4fc-4440-b476-8e2b953ef506"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "激辛ラーメンを求め、レビュー平均5点なあの店へ"
     :details "情報を食いに行く"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 12 12 30 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/139/{width}/{height}.jpg")}
    {:id "b6ee37fe-9191-417d-a932-f2d3d0e307dd"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "社食の胡椒を使い切る会"
     :details "社食の胡椒を増やすべく、まずは需要を \"わからせ\" ていく会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 16 12 0 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/139/{width}/{height}.jpg")}
    {:id "98ebcf3a-2f88-4205-aa69-ce6d9590ab3c"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "辛ラメーンを教会で食べた話"
     :details "大学時代ボッチ飯キメていたら、知らない先輩と教会に行って辛ラーメンを食べさせてもらった話"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 17 13 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    {:id "8c340942-15cf-40e2-8568-44f336639adc"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :name "ペペロンチーノのコツ"
     :details "茹で汁とオリーブオイルの効率的な扱い方を解説"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 27 18 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    {:id "a849c8ae-1519-4119-b646-d0a846fdc94d"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "2f7c3359-9d4f-4873-b281-67a46cb8ae1e"
     :name "今夜もう一度来てください、本当のエビチリを食べさせてあげますよ"
     :details "素材からこだわったスペシャルなディナー、作ります"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 29 19 30 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/249/{width}/{height}.jpg")}
    {:id "399370c4-2187-45a9-bc76-c348f518fda9"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "2f7c3359-9d4f-4873-b281-67a46cb8ae1e"
     :name "夏バテに効くおすすめお家辛味レシピ20選"
     :details "俺の好きなカレー知ってる？ナスカレー"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 30 18 00 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    {:id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
     :owned-member-id "08da804a-8ec9-494c-bd28-f67218e30851"
     :name "会社近くの美味しい韓国料理店を周る集まり"
     :details "会社近くにある辛味の聞いた美味しいチーズタッカルビを出すお店を見つけたので行きましょうの会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 6 3 19 30 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/249/{width}/{height}.jpg")}
    ;; スイートなスイーツ
    {:id "ea9f9d9d-6422-4ea7-8897-5b3d15a7004b"
     :community-id  "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :owned-member-id "0500f01e-47cd-4753-bc5d-74716b9b32a5"
     :name "コンビニで買える、ンまぁ〜いプリンを布教したい"
     :details "プリンなんてチャンチャラおかしくて…"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 5 15 00 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    {:id "6b63c4f3-3725-48c5-b9aa-738ed7e7e03f"
     :community-id  "9ec955a0-a016-4939-b64d-69d514ac4e55"
     :owned-member-id  "7b0497e0-c6ba-4fde-8788-2c3abf3d241f"
     :name "港区アフタヌーンティーを体験しにいく会"
     :details "上流階級の嗜みを体験すべく、我々はアマゾンの奥地へと向かった"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 7 11 00 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/452/{width}/{height}.jpg")}
    ;; 肉体改造部
    {:id "db20df08-4fdc-4489-9610-c73fd7a560e7"
     :community-id "4983bd6f-0397-42ce-b17d-e394a6241e42"
     :owned-member-id "a93f7653-0cd9-498a-bf5b-8822d81047c5"
     :name "お家でできる有酸素運動"
     :details "お手軽でお財布に優しい健康促進メソッド"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 13 19 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/1084/{width}/{height}.jpg")}
    ;; モン○トの集い
    ;; Golang で世界を救え
    {:id "687a7541-336a-43b1-8f29-a1f5412512ee"
     :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :owned-member-id "06e45e1b-f801-47b9-9a28-485786aa85d6"
     :name "Clojureとかいう動的型付け言語に対して Golang の圧倒的優位性を見出していく会"
     :details "静的型付け言語で圧倒的安全性と可用性を見せていけ"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 3 13 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/593/{width}/{height}.jpg")}
    {:id  "14cd0825-04ee-4f27-a2d5-05bda29fb98c"
     :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :owned-member-id "06e45e1b-f801-47b9-9a28-485786aa85d6"
     :name "Python で研究して Ruby を研修した僕が 3日で Golang を身に着けた話"
     :details "インタプリタ駆動開発の体験で柔らかくなった脳をコンパイル駆動開発で引き締めよう"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 4 15 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/1082/{width}/{height}.jpg")}
    {:id "c4d1a191-6d2a-425f-826c-391370c823da"
     :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
     :owned-member-id "06e45e1b-f801-47b9-9a28-485786aa85d6"
     :name "GitHub Actions を Golang で書き直す会"
     :details "あの有名 Actions も静的型付け言語でリプレース"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 5 10 0 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/1078/{width}/{height}.jpg")}
    ;; 健全なる精神は健全なる身体に宿る
    {:id "96ad4a93-2bfe-45e7-88ce-f23aeb221513"
     :community-id "2d758edd-a646-486b-89ec-65a48a7db887"
     :owned-member-id "10a2167e-7c6e-46d2-8964-0e969e01d2a4"
     :name "肩こりをほぐしながらメンタルアップ"
     :details "デスクワークで固まったのは体ですか？頭ですか？"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 9 12 15 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/844/{width}/{height}.jpg")}
    ;; 有酸素運動クラブ
    {:id "6045366a-45e5-4b07-a299-4fc836788490"
     :community-id "2fbbc61f-b6b2-4711-bf05-d920f42de9be"
     :owned-member-id "d9013a2a-ff40-4392-9fe5-99a39e71b101"
     :name "仕事終わりの一発をキメる会"
     :details "サンドバックパンチ！サンドバックパンチ！"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 10 19 15 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/844/{width}/{height}.jpg")}
    ;; 組み合わせ最適化から始める競技プログラミング
    {:id "3a4f4f49-3558-4f8c-9f26-0ed930b969fb"
     :community-id "415bfd23-4557-49ab-8192-2b34f87c26f9"
     :owned-member-id "00262340-a5db-4607-b90d-49223eb38730"
     :name "深さ優先探索の基礎とテンプレ"
     :details "初級の壁の一つ、DFSの典型問題と、回答を加速させるテンプレートを紹介"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 6 15 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/896/{width}/{height}.jpg")}
    {:id "8a6966fc-4ba5-485c-9358-3a7a279e92b4"
     :community-id "415bfd23-4557-49ab-8192-2b34f87c26f9"
     :owned-member-id "3b247c12-ed77-4aa3-accc-67c034743af5"
     :name "競プロで1秒でも長く考えるための環境設定"
     :details "自動テスト、スニペットなどのお役立ち情報をシェアする会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 6 18 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/1082/{width}/{height}.jpg")}
    ;; エンジニアが神絵師になるまで
    {:id "1937ee20-6c18-41fa-8981-58b6681a9c28"
     :community-id "5dc8361d-dbee-4102-8331-02f47f46779b"
     :owned-member-id "5dd531bf-e78e-49e6-9929-44c236bfce9a"
     :name "仕事とプライベートを切り分けるベストプラクティス"
     :details "デュアルブートのすゝめ"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 6 15 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/998/{width}/{height}.jpg")}
    {:id "e50bf396-f31c-4177-8b76-25e2b5315648"
     :community-id "5dc8361d-dbee-4102-8331-02f47f46779b"
     :owned-member-id "5dd531bf-e78e-49e6-9929-44c236bfce9a"
     :name "あなたの描き筋はどっち？"
     :details "自分にあった描き方で快適に描く"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 11 15 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/1082/{width}/{height}.jpg")}
    {:id "417916ab-132a-426f-8a66-dd53b4113fbb"
     :community-id "5dc8361d-dbee-4102-8331-02f47f46779b"
     :owned-member-id "5dd531bf-e78e-49e6-9929-44c236bfce9a"
     :name "正しい線の描き方"
     :details "地道な反復練習から確実なステップアップを目指す会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 13 15 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/896/{width}/{height}.jpg")}
    ;; Ruby on Rails は MVP へ通ず
    {:id "34087ab7-3adc-4b72-bd8a-8edf870bcc42"
     :community-id "6e3e0622-60a3-4001-9d26-20592e09ecff"
     :owned-member-id "7cad0e2d-0858-40c3-906b-6e75eded06ee"
     :name "Rails の教典の最新版、Ruby on Rails チュートリアルを読む"
     :details "Rails を信じよ"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 6 1 17 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/1078/{width}/{height}.jpg")}
    {:id "b8a92f94-66c4-472c-a0ea-7addf8b792ce"
     :community-id "6e3e0622-60a3-4001-9d26-20592e09ecff"
     :owned-member-id "f94b2700-0507-41bc-9bc7-92aaad30bd7a"
     :name "renovate との戦い"
     :details "適切な運用をして長期的なプロダクトを維持していくための秘訣、教えます"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 6 2 10 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/1010/{width}/{height}.jpg")}
    ;; 飲料からモチベを上げ隊
    {:id "9fd44bcc-a77d-4aed-bcae-b483bba00c7e"
     :community-id "f53386da-fd0f-42a8-8c1a-cfc7bc31da5a"
     :owned-member-id "dff1c891-4d23-4175-a3fe-64c3bac642ca"
     :name "実はカフェインの高いドリンク10選"
     :details "毎日コーヒーとエナジードリンクで飽きたら、こんな選択肢が"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 9 16 0 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/1060/{width}/{height}.jpg")}
    {:id "78f07645-1537-421a-a448-fca07a8d664d"
     :community-id "f53386da-fd0f-42a8-8c1a-cfc7bc31da5a"
     :owned-member-id "dff1c891-4d23-4175-a3fe-64c3bac642ca"
     :name "深夜稼働を生き残るための完全栄養ドリンク試飲会"
     :details "限界のその先へ"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 10 13 30 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/1060/{width}/{height}.jpg")}
    ;; 時短飯研究同好会
    ;; 生成モデルを求めし者の集い
    {:id "083cbcaf-b974-4b20-895d-aec868cc0b85"
     :community-id "aeb7a908-766a-4253-9b58-c38829f5bda0"
     :owned-member-id "fdfd8eaf-2481-41cb-a40b-24871435b58c"
     :name "AutoEncoder で遊ぶ潜在表現操作"
     :details "ディープラーニングで 0 を 1 に書き換えてみる"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 11 16 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/896/{width}/{height}.jpg")}
    {:id "509f5497-b85d-44ed-835e-59934d290ab7"
     :community-id "aeb7a908-766a-4253-9b58-c38829f5bda0"
     :owned-member-id "e1c9d298-0d03-4e75-b818-f405ce679d41"
     :name "GANs で描く UMA"
     :details "説得力のある画像の生成を生成する理論的なお話"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 12 16 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/998/{width}/{height}.jpg")}
    {:id "68789f57-44a0-4c25-a145-e1dadda2decd"
     :community-id "aeb7a908-766a-4253-9b58-c38829f5bda0"
     :owned-member-id "fdfd8eaf-2481-41cb-a40b-24871435b58c"
     :name "Flow-based Model で始める可逆ネットワーク"
     :details "可逆変換を駆使して、あっちの世界とこっちの世界を行ったり来たりしてみる"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 13 16 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/998/{width}/{height}.jpg")}
    ;; 和菓子のいろは
    {:id "09ce5134-adf0-42f6-a13f-fa73dce69101"
     :community-id "dcef25f4-af90-4fe5-a385-dc661489e63a"
     :owned-member-id "162dc9f9-1038-4d2e-84ba-e6387d7e4fe0"
     :name "こどもの日を楽しむ持ち寄りパーチー"
     :details "※柏餅の皮は食べれません"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 5 11 30 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/674/{width}/{height}.jpg")}
    {:id "61add728-37d2-485e-b5ca-0d4226fd981c"
     :community-id "dcef25f4-af90-4fe5-a385-dc661489e63a"
     :owned-member-id "162dc9f9-1038-4d2e-84ba-e6387d7e4fe0"
     :name "わらびもち！！"
     :details "やわらかぷるぷるフレッシュなわらび餅を作る"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 15 11 30 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/998/{width}/{height}.jpg")}
    {:id "34c596a8-7444-4313-855f-e96abc84413e"
     :community-id "dcef25f4-af90-4fe5-a385-dc661489e63a"
     :owned-member-id "162dc9f9-1038-4d2e-84ba-e6387d7e4fe0"
     :name "求肥から始めるもちもち大福錬成"
     :details "新鮮いちごを添えて"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 21 12 30 0)))
     :category :party
     :image-url (str domain.community.event/dummy-image-base-url "/id/674/{width}/{height}.jpg")}
    ;; 2022年度版イラストレーション研究会
    {:id "b24b6562-e223-41d5-968d-a438890c7adb"
     :community-id "1f12489c-2135-4a6a-b49b-83fe3f240d64"
     :owned-member-id "83652fe0-9a01-44b8-982d-4abd0083b957"
     :name "プロダクトから見る、イラストの情報量"
     :details "プロダクトテーマと画風の活用法を学ぶ会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 18 16 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/82/{width}/{height}.jpg")}
    {:id "94acd1dd-e045-41aa-8342-511601baa962"
     :community-id "1f12489c-2135-4a6a-b49b-83fe3f240d64"
     :owned-member-id "83652fe0-9a01-44b8-982d-4abd0083b957"
     :name "デバイスをを考えた、イラストのルール"
     :details "誰がどのデバイスで見られるかを意識した筆設定を学ぶ会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 19 16 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/82/{width}/{height}.jpg")}
    {:id "badc3c95-f000-4cd4-b1cd-5cda4edb283e"
     :community-id "1f12489c-2135-4a6a-b49b-83fe3f240d64"
     :owned-member-id "83652fe0-9a01-44b8-982d-4abd0083b957"
     :name "ユーザ層を意識した、イラストのルール"
     :details "メッセージを持たせるためのテクニックを学ぶ会"
     :hold-at (.getTime (js/Date. (js/Date.UTC 2022 5 20 16 30 0)))
     :category :seminar
     :image-url (str domain.community.event/dummy-image-base-url "/id/82/{width}/{height}.jpg")}]
   :community-event-comment
   [;; 辛いものの部 > 会社近くの美味しい韓国料理店を周る集まり
    {:id "24dc624c-fc35-46fb-86d0-f60c74c5ae6e"
     :event-id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :body "僕も新卒の皮を被って参加したいです :inori:"}
    {:id "884a9d75-2f8e-4687-804b-3e5bc4804b23"
     :event-id "383503d8-dea4-4f26-85b5-afe00f29184b"
     :member-id "08da804a-8ec9-494c-bd28-f67218e30851"
     :body ":roger:"}
    ;; Golang で世界を救え > Clojureとかいう動的型付け言語に対して Golang の圧倒的優位性を見出していく会
    {:id "4ad8ef9d-8a2e-45fb-b77c-a16dd32a3746"
     :event-id "687a7541-336a-43b1-8f29-a1f5412512ee"
     :member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
     :body "ポインタには中身がある…………そんなふうに考えていた時期が俺にもありました"}]})
