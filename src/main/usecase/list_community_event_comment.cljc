(ns usecase.list-community-event-comment
  (:require [domain.community.member]
            [domain.community.event]
            [domain.community.event.comment]
            [pkg.cache.core]))

(defn- includes [member-ids community-event repo _]
  (let [community-members (domain.community.member/fetch-community-members (:community-member-query-repository repo) member-ids)]
    {:community-members community-members
     :community-event community-event}))

(defn execute [{:keys [user-id community-id event-id]} repo cache]
  (if
   (-> (domain.community.member/check-joined (:community-member-query-repository repo) user-id [community-id])
       count
       zero?)
    [nil {:code 403 :message "user is not join the community"}]
    (let [community-event (domain.community.event/fetch-community-event (:community-event-query-repository repo) event-id)
          community-event-comments (pkg.cache.core/fetch-or-miss
                                    cache
                                    (keyword (str "community-event-comments:" event-id))
                                    (fn [] (->> (domain.community.event.comment/fetch-community-event-comment-by-event-id
                                                 (:community-event-comment-query-repository repo) event-id)
                                                (sort-by :comment-at)
                                                reverse)))
          member-ids (distinct (map :member-id community-event-comments))]
      (if (or (nil? community-event) (not= community-id (:community-id community-event)))
        [nil {:code 404 :message "community event is not found"}]
        [{:community-event-comments community-event-comments
          :includes (includes member-ids community-event repo cache)} nil]))))
