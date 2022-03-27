(ns usecase.list-community-event
  (:require [domain.community.event]
            [domain.community.member]
            [domain.community.event.comment]
            [pkg.cache.core]))

(defn- includes [member-ids repo _]
  (let [community-members  (domain.community.member/fetch-community-members (:community-member-query-repository repo) member-ids)]
    {:community-members community-members}))

(defn execute [{:keys [user-id community-id request-size begin-cursor last-cursor]} repo cache]
  (if (-> (domain.community.member/check-joined (:community-member-query-repository repo) user-id [community-id])
          count
          zero?)
    [nil {:code 403 :message "user is not join the community"}]
    (let [from-cursor (cond
                        (some? begin-cursor) (domain.community.event/fetch-community-event (:community-event-query-repository repo) begin-cursor)
                        (some? last-cursor)  (domain.community.event/fetch-community-event (:community-event-query-repository repo) last-cursor)
                        :else nil)
          sort-order (if (and (nil? begin-cursor) (some? last-cursor) (some? from-cursor)) :hold-at-asc :hold-at-desc)
          community-events (->> (domain.community.event/search-part-community-event-by-community-id
                                 (:community-event-query-repository repo) community-id request-size from-cursor sort-order)
                                (sort-by :hold-at)
                                reverse)
          community-event-comments-list
          (map
           (fn [{:keys [id]}]
             (pkg.cache.core/fetch-or-miss
              cache
              (keyword (str "community-event-comments:" id))
              (fn [] (->> (domain.community.event.comment/fetch-community-event-comment-by-event-id
                           (:community-event-comment-query-repository repo) id)
                          (sort-by :comment-at)))))

           community-events)
          member-ids (distinct (concat (map :owned-member-id community-events) (apply concat (map (fn [community-event-comment] (map :member-id community-event-comment)) community-event-comments-list))))
          {:keys [total-size before-size]} (if (zero? (count community-events))
                                             {:total-size (domain.community.event/size-community-event (:community-event-query-repository repo) community-id)
                                              :before-size 0}
                                             (domain.community.event/before-size-community-event
                                              (:community-event-query-repository repo)
                                              (first community-events)
                                              community-id))]
      [{:community-events community-events
        :representative-comments-list (map #(take 1 %) community-event-comments-list)
        :total-size total-size
        :before-size before-size
        :includes  (includes member-ids repo cache)}
       nil])))
