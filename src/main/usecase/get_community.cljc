(ns usecase.get-community
  (:require [domain.community]
            [domain.community.member]))

(defn execute [{:keys [user-id community-id]} repo]
  (let [community (domain.community/fetch-community (:community-query-repository repo) community-id)
        members (if (nil? community) []
                    (domain.community.member/search-community-member-by-community-id
                     (:community-member-query-repository repo)
                     (:id community)))]
    (if (nil? community)
      [nil {:code 404 :message (str "community is not exist:" community-id)}]
      [{:community community
        :is-joined (if (nil? user-id) nil
                       (-> (filter (fn [member] (-> member :user :id (= user-id))) members)
                           count zero? not))
        :members members}
       nil])))
