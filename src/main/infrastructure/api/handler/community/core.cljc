(ns infrastructure.api.handler.community.core)

(defn community->http [community]
  (let [{:keys [id name details category image-url created-at updated-at membership]} community]
    {:id id
     :name name
     :details details
     :category category
     :imageUrl image-url
     :createdAt created-at
     :updatedAt updated-at
     :membership membership}))
