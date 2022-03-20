(ns interface.gateway.sqlite3.community-test
  (:require [interface.gateway.sqlite3.community :as sut]
            [cljs.test :as t]))

;; domain mapping

;; build sql query
(t/deftest build-sql-list
  (t/is
   (= (-> sut/sql-map :list)
      "\nSELECT
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
        "\nSELECT
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
WHERE communities.updated_at < ?
GROUP BY communities.id
ORDER BY communities.updated_at DESC
LIMIT ?")))
  (t/testing "query: cursor / updated-at-asc"
    (t/is
     (= (sut/build-sql-list-part-community 5 "cursor" :updated-at-asc nil)
        "\nSELECT
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
WHERE communities.updated_at > ?
GROUP BY communities.id
ORDER BY communities.updated_at ASC
LIMIT ?")))
  (t/testing "query: updated-at-desc"
    (t/is
     (= (sut/build-sql-list-part-community 5 nil :updated-at-desc nil)
        "\nSELECT
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
        "\nSELECT
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
        "\nSELECT
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
