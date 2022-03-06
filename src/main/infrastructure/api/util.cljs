(ns infrastructure.api.util
  (:require [clojure.spec.alpha :as s]))

(s/def ::http-status #(<= 100 % 500))
(s/def ::content-type #{"application/json" "application/html"})
(s/def ::header (s/keys :req-un [] :opt-un [::content-type]))
(s/def ::http-response (s/cat
                        :status ::http-status
                        :header ::header
                        :body any?))
