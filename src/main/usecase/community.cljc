(ns usecase.community
  (:require [domain.community]
            [domain.community.member]))


;; TODO cursor の値が存在しなかったときの振る舞いを考える (※物理削除は行わない設計にしていく予定なので、クライアントのクエリミスが前提)
;; => 今の所最初のページからになる


(defn list-community [{:keys [user-id request-size begin-cursor last-cursor keyword]} repo]
  ;; TODO やる気があれば transaction の設計をする
  (let [sort-order (if (and (nil? begin-cursor) (some? last-cursor)) :updated-at-asc :updated-at-desc)
        communities
        (reverse
         (sort-by
          :updated-at
          (cond
            ;; 最新 n 件
            (every? nil? [begin-cursor last-cursor])
            (domain.community/list-part-community (:community-query-repository repo) (inc request-size) nil sort-order keyword)
            (some? begin-cursor)
            (domain.community/list-part-community (:community-query-repository repo) (inc request-size) begin-cursor sort-order keyword)
            :else
            (domain.community/list-part-community (:community-query-repository repo) (inc request-size) last-cursor sort-order keyword))))
        {:keys [total-size before-size]} (if (zero? (count communities))
                                           {:total-size (domain.community/size-community (:community-query-repository repo) keyword)
                                            :before-size 0}
                                           (domain.community/before-size-community
                                            (:community-query-repository repo)
                                            (first communities)
                                            keyword))
        is-joined-set (when user-id (->> communities (map :id) (domain.community.member/check-joined
                                                                (:community-member-query-repository repo) user-id) set))]
    [{:communities (map (fn [community] {:community community
                                         :is-joined (if (nil? is-joined-set) nil (contains? is-joined-set (:id community)))})
                        (cond
                          (some? begin-cursor) (rest communities)
                          (some? last-cursor) (drop-last communities)
                          :else communities))
      :total-size total-size
      :before-size before-size} nil]))

(defn get-community [{:keys [user-id community-id] :as req} repo]
  (let [community (domain.community/fetch-community (:community-query-repository repo) community-id)
        members (if (nil? community) []
                    (domain.community.member/search-community-member-by-community-id
                     (:community-member-query-repository repo)
                     (:id community)))]
    (println members)
    (if (nil? community)
      [nil {:code 404 :message (str "community is not exist:" community-id)}]
      [{:community community
        :is-joined (if (nil? user-id) nil
                       (-> (filter (fn [member] (-> member :user :id (= user-id))) members)
                           count zero? not))
        :members members}
       nil])))
