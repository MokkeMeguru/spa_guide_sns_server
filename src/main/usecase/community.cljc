(ns usecase.community
  (:require [domain.community]
            [domain.community.member]))


;; TODO cursor の値が存在しなかったときの振る舞いを考える (※物理削除は行わない設計にしていく予定なので、クライアントのクエリミスが前提)
;; => 今の所最初のページからになる


(defn list-community [{:keys [user-id request-size begin-cursor last-cursor keyword]} repo]
  (println user-id request-size begin-cursor last-cursor)
  ;; TODO やる気があれば transaction の設計をする
  (let [sort-order (if (and (nil? begin-cursor) (some? last-cursor)) :created-at-asc :created-at-desc)
        communities (cond
                      ;; 最新 n 件
                      (every? nil? [begin-cursor last-cursor])
                      (domain.community/list-part-community (:community-query-repository repo) request-size nil sort-order keyword)
                      ;; begin-cursor より前 最新 n 件
                      (some? begin-cursor)
                      (domain.community/list-part-community (:community-query-repository repo) request-size begin-cursor sort-order keyword)
                      ;; last-cursor より後 古い順 n 件
                      :else
                      (reverse (domain.community/list-part-community (:community-query-repository repo) request-size last-cursor sort-order keyword)))
        total-size (domain.community/size-community (:community-query-repository repo))
        before-size (cond
                      ;; コミュニティリストが空 && 最新順で取ってきている => 1ページ目
                      (and (zero? (count communities)) (= :created-at-desc sort-order)) 0
                      ;; コミュニティリストが空でない => 一番新しいデータを渡す
                      :else (domain.community/before-size-community (:community-query-repository repo) (:id (first communities))))
        is-joined-set (when user-id (->> communities (map :id) (domain.community.member/check-joined (:community-member-repository repo) user-id) set))]
    [{:communities (map (fn [community] {:community community
                                         :is-joined (if-not is-joined-set nil (contains? is-joined-set (:id community)))}) communities)
      :total-size total-size
      :before-size before-size} nil]))

;; TODO 3/17
;; impl sql
;; - list-part-community
;; select * from communities where community_id > @from_cursor order by @order limit @request_size
;; select * from communities order by @order limit @request_size
;; before-size-community
;; - size-community
;; select count(*) from communities;
;; - check-joined
;; select community_id in members where user_id = @user_id and community_id IN @community_ids
