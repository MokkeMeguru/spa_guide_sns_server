(ns interface.gateway.sqlite3.community-event-test
  (:require [interface.gateway.sqlite3.community-event :as sut]
            [orchestra-cljs.spec.test :as st]
            [domain.community.event]
            [cmd.migrate.core]
            [cljs.test :as t]))

(st/instrument)

;;domain mapping
(t/deftest category-map
  (t/testing "all"
    (doall
     (map
      #(t/testing (str %)
         (t/is (= % (-> sut/category-map :db->domain (get (-> sut/category-map :domain->db %))))))
      domain.community.event/category))))

(t/deftest domain->db
  (t/testing "without id"
    (t/is
     (= {:community_id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
         :owned_member_id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
         :name "社食の胡椒を使い切る会"
         :details "社食の胡椒を増やすべく、まずは需要を \"わからせ\" ていく会"
         :hold_at 1655283600000
         :category 1
         :image_url "https://picsum.photos/id/139/{width}/{height}.jpg"}
        (dissoc
         (sut/domain->db
          {:community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
           :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
           :name "社食の胡椒を使い切る会"
           :details "社食の胡椒を増やすべく、まずは需要を \"わからせ\" ていく会"
           :hold-at 1655283600000
           :category :party
           :image-url "https://picsum.photos/id/139/{width}/{height}.jpg"})
         :id :created_at :updated_at))))
  (t/testing "with id"
    (t/is
     (=  {:id "98ebcf3a-2f88-4205-aa69-ce6d9590ab3c"
          :community_id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
          :owned_member_id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
          :name "辛ラメーンを教会で食べた話"
          :details "大学時代ボッチ飯キメていたら、知らない先輩と教会に行って辛ラーメンを食べさせてもらった話"
          :hold_at 1655456400000
          :category 2
          :image_url "https://picsum.photos/id/452/{width}/{height}.jpg"}
         (dissoc
          (sut/domain->db
           {:id "98ebcf3a-2f88-4205-aa69-ce6d9590ab3c"
            :community-id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
            :owned-member-id "eb86ddc9-6446-44d3-8afa-5def58bbe340"
            :name "辛ラメーンを教会で食べた話"
            :details "大学時代ボッチ飯キメていたら、知らない先輩と教会に行って辛ラーメンを食べさせてもらった話"
            :hold-at 1655456400000
            :category :seminar
            :image-url "https://picsum.photos/id/452/{width}/{height}.jpg"})
          :created_at :updated_at)))))

(t/deftest db->domain
  (t/is (=
         {:id "687a7541-336a-43b1-8f29-a1f5412512ee"
          :community-id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
          :owned-member-id "06e45e1b-f801-47b9-9a28-485786aa85d6"
          :name "Clojureとかいう動的型付け言語に対して Golang の圧倒的優位していく会"
          :details "静的型付け言語で圧倒的安全性と可用性を見せていけ"
          :hold-at 1656579600000
          :category :seminar
          :image-url "https://picsum.photos/id/593/{width}/{height}.jpg"
          :created-at 1648195422823
          :updated-at 1648195422823}

         (sut/db->domain
          {:id "687a7541-336a-43b1-8f29-a1f5412512ee"
           :community_id "47ace9f8-55a4-4bd5-8d64-63f4d432c59e"
           :owned_member_id "06e45e1b-f801-47b9-9a28-485786aa85d6"
           :name "Clojureとかいう動的型付け言語に対して Golang の圧倒的優位していく会"
           :details "静的型付け言語で圧倒的安全性と可用性を見せていけ"
           :hold_at 1656579600000
           :category 2
           :image_url "https://picsum.photos/id/593/{width}/{height}.jpg"
           :created_at  1648195422823
           :updated_at 1648195422823}))))

;; build sql query
(t/deftest build-sql-list
  (t/is
   (= (-> sut/sql-map :list)
      "
SELECT * FROM community_events")))

(t/deftest build-sql-fetch
  (t/is
   (= (-> sut/sql-map :fetch)
      "
SELECT * FROM community_events
WHERE id = ?")))

(t/deftest build-sql-search-by-community-id
  (t/is
   (= (-> sut/sql-map :search-by-community-id)
      "
SELECT * FROM community_events
WHERE community_id = ?")))

(t/deftest build-sql-search-part-community-event-by-community-id
  (t/testing "desc"
    (t/is
     (= (sut/build-sql-search-part-community-event-by-community-id
         10
         "687a7541-336a-43b1-8f29-a1f5412512ee"
         :hold-at-desc)
        "
SELECT * FROM community_events
WHERE community_id = ?
 AND hold_at < ?
ORDER BY hold_at DESC
LIMIT ?")))
  (t/testing "asc"
    (t/is
     (= (sut/build-sql-search-part-community-event-by-community-id
         10
         "687a7541-336a-43b1-8f29-a1f5412512ee"
         :hold-at-asc)
        "
SELECT * FROM community_events
WHERE community_id = ?
 AND hold_at > ?
ORDER BY hold_at ASC
LIMIT ?"))))

;; runtime check
(t/deftest runtime-check
  (let [repo (cmd.migrate.core/migrate ":memory:" false)]
    (t/testing "list"
      (t/is (= 4 (count (domain.community.event/list-community-event
                         (:community-event-query-repository repo))))))
    (t/testing "fetch"
      (t/is (= "98ebcf3a-2f88-4205-aa69-ce6d9590ab3c"
               (:id (domain.community.event/fetch-community-event
                     (:community-event-query-repository repo)
                     "98ebcf3a-2f88-4205-aa69-ce6d9590ab3c")))))
    (t/testing "search-by-community-id"
      (t/is (= 3 (count (domain.community.event/search-community-event-by-community-id
                         (:community-event-query-repository repo)
                         "f61f5f38-174b-43e1-8873-4f7cdbee1c18")))))
    (t/testing "search-part-by-community-id"
      (t/testing "no args"
        (t/is (= 3
                 (count (domain.community.event/search-part-community-event-by-community-id
                         (:community-event-query-repository repo)
                         "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
                         10
                         nil
                         :hold-at-desc))
                 (count (domain.community.event/search-part-community-event-by-community-id
                         (:community-event-query-repository repo)
                         "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
                         10
                         nil
                         :hold-at-asc)))))
      (t/testing "limit by request size"
        (t/is (> 1656838800000 1655283600000))
        (t/testing "oldest"
          (let [events (domain.community.event/search-part-community-event-by-community-id
                        (:community-event-query-repository repo)
                        "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
                        1
                        nil
                        :hold-at-asc)]
            (t/is (= 1 (count events)))
            (t/is (= 1655283600000 (:hold-at (first events))))))
        (t/testing "newer"
          (let [events (domain.community.event/search-part-community-event-by-community-id
                        (:community-event-query-repository repo)
                        "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
                        1
                        nil
                        :hold-at-desc)]
            (t/is (= 1 (count events)))
            (t/is (= 1656838800000 (:hold-at (first events)))))))
      (t/testing "with cursor"
        (let [base
              (->>
               (domain.community.event/search-part-community-event-by-community-id
                (:community-event-query-repository repo)
                "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
                10
                nil
                :hold-at-desc)
               (sort-by :hold-at))
              [base0 base1 base2] [(nth base 0) (nth base 1) (nth base 2)]]
          (t/is (< (:hold-at base0) (:hold-at base1) (:hold-at base2))) ;; 2022/01/10 < 2022/01/11 < 2022/01/12
          (t/testing "newer events from cursor: 0 | 1 | -> 2"
            (let [target (sort-by
                          :hold-at
                          (domain.community.event/search-part-community-event-by-community-id
                           (:community-event-query-repository repo)
                           "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
                           10
                           base1
                           :hold-at-asc))]
              (t/is (= base2 (first target)))))
          (t/testing "older events from cursor: 2 | 1 | -> 0"
            (let [target (domain.community.event/search-part-community-event-by-community-id
                          (:community-event-query-repository repo)
                          "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
                          10
                          base1
                          :hold-at-desc)]
              (t/is (= base0 (first target))))))))))
