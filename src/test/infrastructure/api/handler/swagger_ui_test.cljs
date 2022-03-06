(ns infrastructure.api.handler.swagger-ui-test
  (:require [infrastructure.api.handler.swagger-ui :as sut]
            [cljs.spec.test.alpha :as st]
            [cljs.test :as t]))

(def localhost-swagger-ui "
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
      url: 'http://localhost:3000/swagger.json',
      dom_id: '#swagger-ui',
    });
  };
</script>
</body>
</html>
")

(t/deftest test-swagger-ui
  (st/instrument `sut/swagger-ui)
  (let [tests
        [;; TODO
         ;; I think we shouldn't need the test about invalid spec
         ;; since we always implement on the spec.
         {:name "invalid spec"
          :exception true
          :expected  nil
          :actual #(sut/swagger-ui "localhost" 3000)}
         {:name "localhost"
          :exception false
          :expected localhost-swagger-ui
          :actual #(sut/swagger-ui "localhost" "3000")}]]
    (doseq [{:keys [name expected actual exception]} tests]
      (t/testing name
        (if exception
          (t/is (thrown? js/Error (actual)))
          (t/is (= expected (actual))))))))
