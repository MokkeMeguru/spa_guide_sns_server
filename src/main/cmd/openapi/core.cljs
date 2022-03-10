(ns cmd.openapi.core
  (:require [cmd.openapi.openapi]
            ["fs" :as fs]
            ["yaml" :as yaml]))

(defn gen []
  (.writeFileSync fs "resources/openapi.yaml"
                  (.stringify yaml (clj->js (cmd.openapi.openapi/generate-openapi)))
                  "utf8"))
