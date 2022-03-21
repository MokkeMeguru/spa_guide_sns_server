(ns interface.gateway.sqlite3.community-test
  (:require [interface.gateway.sqlite3.community :as sut]
            [infrastructure.sqlite3.util]
            [cmd.migrate.core]
            [domain.mock]
            [domain.community]
            [cljs.test :as t]
            [orchestra-cljs.spec.test :as st]
            [clojure.spec.alpha :as s]
            [clojure.data]))

(st/instrument)

;; domain mapping
(t/deftest category-map
  (t/testing "gurmand"
    (doall
     (map
      #(t/testing (str %)
         (t/is (= % (-> sut/category-map :db->domain (get (-> sut/category-map :domain->db %))))))
      domain.community/category))))

(t/deftest domain->db
  (t/testing "without id"
    (t/is
     (=
      {:name "辛いものの部"
       :details "辛いものが好きな人集まれー"
       :category 1
       :image_url "https://picsum.photos/id/292/{width}/{height}.jpg"}
      (dissoc
       (sut/domain->db
        {:name "辛いものの部"
         :details "辛いものが好きな人集まれー"
         :category :gurmand
         :image-url "https://picsum.photos/id/292/{width}/{height}.jpg"})
       :id :created_at :updated_at))))
  (t/testing "with id"
    (t/is
     (=
      {:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
       :name "辛いものの部"
       :details "辛いものが好きな人集まれー"
       :category 1
       :image_url "https://picsum.photos/id/292/{width}/{height}.jpg"}
      (dissoc
       (sut/domain->db
        {:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
         :name "辛いものの部"
         :details "辛いものが好きな人集まれー"
         :category :gurmand
         :image-url "https://picsum.photos/id/292/{width}/{height}.jpg"})
       :created_at :updated_at)))))

(t/deftest db->domain
  (t/is
   (= {:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
       :name "辛いものの部"
       :details "辛いものが好きな人集まれー"
       :category :gurmand
       :image-url "https://picsum.photos/id/292/{width}/{height}.jpg"
       :created-at 1647864195324
       :updated-at 1647864195324
       :membership 10}
      (sut/db->domain
       {:id "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
        :name "辛いものの部"
        :details "辛いものが好きな人集まれー"
        :category 1
        :image_url "https://picsum.photos/id/292/{width}/{height}.jpg"
        :membership 10
        :created_at 1647864195324
        :updated_at 1647864195324}))))

;; build sql query
(t/deftest build-sql-list
  (t/is
   (= (-> sut/sql-map :list)
      "
SELECT
  communities.id AS id,
  communities.name AS name,
  communities.details AS details,
  communities.category AS category,
  communities.image_url AS image_url,
  communities.created_at AS created_at,
  communities.updated_at AS updated_at,
  COUNT(*) AS membership
FROM communities
LEFT JOIN community_members
ON communities.id=community_members.community_id
GROUP BY communities.id")))

(t/deftest build-sql-list-part-community
  (t/testing "query: cursor / updated-at-desc"
    (t/is
     (= (sut/build-sql-list-part-community 5 "cursor" :updated-at-desc nil)
        "
SELECT
  communities.id AS id,
  communities.name AS name,
  communities.details AS details,
  communities.category AS category,
  communities.image_url AS image_url,
  communities.created_at AS created_at,
  communities.updated_at AS updated_at,
  COUNT(*) AS membership
FROM communities
LEFT JOIN community_members
ON communities.id=community_members.community_id
WHERE communities.updated_at <= ?
GROUP BY communities.id
ORDER BY communities.updated_at DESC
LIMIT ?")))
  (t/testing "query: cursor / updated-at-asc"
    (t/is
     (= (sut/build-sql-list-part-community 5 "cursor" :updated-at-asc nil)
        "
SELECT
  communities.id AS id,
  communities.name AS name,
  communities.details AS details,
  communities.category AS category,
  communities.image_url AS image_url,
  communities.created_at AS created_at,
  communities.updated_at AS updated_at,
  COUNT(*) AS membership
FROM communities
LEFT JOIN community_members
ON communities.id=community_members.community_id
WHERE communities.updated_at >= ?
GROUP BY communities.id
ORDER BY communities.updated_at ASC
LIMIT ?")))
  (t/testing "query: updated-at-desc"
    (t/is
     (= (sut/build-sql-list-part-community 5 nil :updated-at-desc nil)
        "
SELECT
  communities.id AS id,
  communities.name AS name,
  communities.details AS details,
  communities.category AS category,
  communities.image_url AS image_url,
  communities.created_at AS created_at,
  communities.updated_at AS updated_at,
  COUNT(*) AS membership
FROM communities
LEFT JOIN community_members
ON communities.id=community_members.community_id
GROUP BY communities.id
ORDER BY communities.updated_at DESC
LIMIT ?")))
  (t/testing "query: updated-at-asc"
    (t/is
     (= (sut/build-sql-list-part-community 5 nil :updated-at-asc nil)
        "
SELECT
  communities.id AS id,
  communities.name AS name,
  communities.details AS details,
  communities.category AS category,
  communities.image_url AS image_url,
  communities.created_at AS created_at,
  communities.updated_at AS updated_at,
  COUNT(*) AS membership
FROM communities
LEFT JOIN community_members
ON communities.id=community_members.community_id
GROUP BY communities.id
ORDER BY communities.updated_at ASC
LIMIT ?")))
  (t/testing "query: updated-at-desc / keyword"
    (t/is
     (= (sut/build-sql-list-part-community 5 nil :updated-at-desc "keyword")
        "
SELECT
  communities.id AS id,
  communities.name AS name,
  communities.details AS details,
  communities.category AS category,
  communities.image_url AS image_url,
  communities.created_at AS created_at,
  communities.updated_at AS updated_at,
  COUNT(*) AS membership
FROM communities
LEFT JOIN community_members
ON communities.id=community_members.community_id
WHERE (communities.name LIKE ? OR communities.details LIKE ?)
GROUP BY communities.id
ORDER BY communities.updated_at DESC
LIMIT ?"))))

;; runtime check
(t/deftest runtime-check
  (let [repo (cmd.migrate.core/migrate ":memory:" false)]
    (t/testing "list"
      (t/is (= 15 (count (domain.community/list-community
                          (:community-query-repository repo))))))
    (t/testing "fetch"
      (t/is (= "f61f5f38-174b-43e1-8873-4f7cdbee1c18"
               (:id (domain.community/fetch-community
                     (:community-query-repository repo)
                     "f61f5f38-174b-43e1-8873-4f7cdbee1c18")))))
    ;; TODO list part community
    (t/testing "size"
      (t/is (= 15 (domain.community/size-community
                   (:community-query-repository repo) nil)))
      (t/is (= 1 (domain.community/size-community
                  (:community-query-repository repo) "辛い"))))
    ;; TODO before size community
    ))
