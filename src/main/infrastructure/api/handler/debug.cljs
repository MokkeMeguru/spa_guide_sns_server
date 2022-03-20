(ns infrastructure.api.handler.debug)

(defn insert-dummy-user
  [domain]
  (assoc domain :user-id "82bbb43c-5564-487c-9a21-7416fc6ed357"))
