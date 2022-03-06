(ns infrastructure.sqlite3.core
  (:require [interface.gateway.sqlite3.user]
            [interface.gateway.sqlite3.community]
            [interface.gateway.sqlite3.community-member]
            ["better-sqlite3" :as better-sqlite3]))

(defn make-repository [^better-sqlite3 sqlite3]
  {:user-query-repository (interface.gateway.sqlite3.user/->UserQueryRepository sqlite3)
   :user-command-repository (interface.gateway.sqlite3.user/->UserCommandRepository sqlite3)
   :community-query-repository (interface.gateway.sqlite3.community/->CommunityQueryRepository sqlite3)
   :community-command-repository (interface.gateway.sqlite3.community/->CommunityCommandRepository sqlite3)
   :community-member-command-repository (interface.gateway.sqlite3.community-member/->CommunityMemberCommandRepository sqlite3)})
