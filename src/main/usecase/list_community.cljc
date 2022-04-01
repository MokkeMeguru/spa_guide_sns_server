(ns usecase.list-community
  (:require [domain.community]
            [domain.community.member]))

(defn execute [{:keys [user-id request-size begin-cursor last-cursor keyword]} repo]
  (let [from-cursor (cond
                      (some? begin-cursor) (domain.community/fetch-community (:community-query-repository repo) begin-cursor)
                      (some? last-cursor) (domain.community/fetch-community (:community-query-repository repo) last-cursor)
                      :else nil)
        sort-order (if (and (nil? begin-cursor) (some? last-cursor) (some? from-cursor)) :updated-at-asc :updated-at-desc)
        communities (->> (domain.community/list-part-community (:community-query-repository repo) request-size from-cursor sort-order keyword)
                         (sort-by :updated-at)
                         reverse)
        {:keys [total-size before-size]} (if (zero? (count communities))
                                           {:total-size (domain.community/size-community (:community-query-repository repo) keyword)
                                            :before-size 0}
                                           (domain.community/before-size-community
                                            (:community-query-repository repo)
                                            (first communities)
                                            keyword))
        is-joined-set (when user-id (->> communities
                                         (map :id)
                                         (domain.community.member/check-joined (:community-member-query-repository repo) user-id)
                                         (map :community-id)
                                         set))]
    [{:communities (map
                    (fn [community]
                      {:community community
                       :is-joined (if (nil? is-joined-set) nil (contains? is-joined-set (:id community)))})
                    communities)
      :total-size total-size
      :before-size before-size} nil]))
