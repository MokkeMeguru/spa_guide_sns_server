(ns infrastructure.api.handler.swagger-ui
  (:require [goog.string :as gstring]
            [goog.string.format]
            [clojure.spec.alpha :as s]
            [domain.util.url]
            [cljs.spec.test.alpha]
            [infrastructure.api.util]))

(s/fdef swagger-ui
  :args (s/cat :host (s/or :localhost (partial = "localhost")
                           :default ::domain.util.url/host)
               :port ::domain.util.url/port)
  :ret string?)

(s/fdef handler
  :args (s/cat  :host (s/or :localhost (partial = "localhost")
                            :default ::domain.util.url/host)
                :port ::domain.util.url/port)
  :ret ::infrastructure.api.util/http-response)

(defn- swagger-ui [^string host ^string port]
  (gstring/format "
<!DOCTYPE html>
<html lang=\"en\">
<head>
  <meta charset=\"utf-8\" />
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
  <meta
    name=\"description\"
    content=\"SwaggerIU\"
  />
  <title>SwaggerUI</title>
  <link rel=\"stylesheet\" href=\"https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui.css\" />
</head>
<body>
<div id=\"swagger-ui\"></div>
<script src=\"https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui-bundle.js\" crossorigin></script>
<script>
  window.onload = () => {
    window.ui = SwaggerUIBundle({
      url: 'http://%s:%d/swagger.json',
      dom_id: '#swagger-ui',
    });
  };
</script>
</body>
</html>
" host port))

(defn- presenter [swagger-ui]
  {:status 200
   :body swagger-ui
   :header {:content-type "text/html"}})

(defn- handler
  [{{:keys [host port]} :config} respond _]
  (->
   ;; controller
   ;; usecase
   (swagger-ui host port)
   ;; presenter
   presenter
   respond))

(def operation
  {:no-doc true
   :handler handler})
