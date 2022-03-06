(ns domain.util.url-test
  (:require [domain.util.url :as sut]
            #?(:clj [clojure.test :as t]
               :cljs [cljs.test :as t :include-macros true])))

(def ^:private equality-tests
  "List of equality tests to execute where `:name` is the name of test, `:actual`
  is actual value, and `:expected` is the expected value."
  [{:name     "basic url"
    :actual   (sut/parse "http://evanlouie.com")
    :expected #:domain.util.url
               {:hash     nil
                :host     "evanlouie.com"
                :hostname "evanlouie.com"
                :href     "http://evanlouie.com/"
                :origin   "http://evanlouie.com"
                :password nil
                :pathname "/"
                :port     nil
                :protocol "http:"
                :search   nil
                :username nil}}

   {:name     "complex url"
    :actual   (sut/parse "https://evan:my-password@some.sub.domain.evanlouie.com:1337/path/to/page?query1=foo&query2=bar#header2")
    :expected #:domain.util.url
               {:hash     "#header2"
                :host     "some.sub.domain.evanlouie.com:1337"
                :hostname "some.sub.domain.evanlouie.com"
                :href     "https://evan:my-password@some.sub.domain.evanlouie.com:1337/path/to/page?query1=foo&query2=bar#header2"
                :origin   "https://some.sub.domain.evanlouie.com:1337"
                :password "my-password"
                :pathname "/path/to/page"
                :port     "1337"
                :protocol "https:"
                :search   "?query1=foo&query2=bar"
                :username "evan"}}

   {:name     "auth with username but no password"
    :actual   (sut/parse "https://evan@some.sub.domain.evanlouie.com:1337/path/to/page?query1=foo&query2=bar#header2")
    :expected #:domain.util.url
               {:hash     "#header2"
                :host     "some.sub.domain.evanlouie.com:1337"
                :hostname "some.sub.domain.evanlouie.com"
                :href     "https://evan@some.sub.domain.evanlouie.com:1337/path/to/page?query1=foo&query2=bar#header2"
                :origin   "https://some.sub.domain.evanlouie.com:1337"
                :password nil
                :pathname "/path/to/page"
                :port     "1337"
                :protocol "https:"
                :search   "?query1=foo&query2=bar"
                :username "evan"}}

   {:name     "basic url -- no namespace"
    :actual   (sut/parse "http://evanlouie.com"
                         :namespaced false)
    :expected {:hash     nil
               :host     "evanlouie.com"
               :hostname "evanlouie.com"
               :href     "http://evanlouie.com/"
               :origin   "http://evanlouie.com"
               :password nil
               :pathname "/"
               :port     nil
               :protocol "http:"
               :search   nil
               :username nil}}

   {:name     "complex url -- no namespace"
    :actual   (sut/parse "https://evan:my-password@some.sub.domain.evanlouie.com:1337/path/to/page?query1=foo&query2=bar#header2"
                         :namespaced false)
    :expected {:hash     "#header2"
               :host     "some.sub.domain.evanlouie.com:1337"
               :hostname "some.sub.domain.evanlouie.com"
               :href     "https://evan:my-password@some.sub.domain.evanlouie.com:1337/path/to/page?query1=foo&query2=bar#header2"
               :origin   "https://some.sub.domain.evanlouie.com:1337"
               :password "my-password"
               :pathname "/path/to/page"
               :port     "1337"
               :protocol "https:"
               :search   "?query1=foo&query2=bar"
               :username "evan"}}

   {:name     "auth with username but no password -- no namespace"
    :actual   (sut/parse "https://evan@some.sub.domain.evanlouie.com:1337/path/to/page?query1=foo&query2=bar#header2"
                         :namespaced false)
    :expected {:hash     "#header2"
               :host     "some.sub.domain.evanlouie.com:1337"
               :hostname "some.sub.domain.evanlouie.com"
               :href     "https://evan@some.sub.domain.evanlouie.com:1337/path/to/page?query1=foo&query2=bar#header2"
               :origin   "https://some.sub.domain.evanlouie.com:1337"
               :password nil
               :pathname "/path/to/page"
               :port     "1337"
               :protocol "https:"
               :search   "?query1=foo&query2=bar"
               :username "evan"}}])

(t/deftest test-parse
  ;; run the equality tests
  (doseq [{:keys [name expected actual]} equality-tests]
    (t/testing name
      (t/is (= expected actual))))

  ;; (t/testing "invalid URL throws"
  ;;   (let [exception-pattern (if (s/check-asserts?)
  ;;                             #"(?s)^Spec assertion failed.+$"
  ;;                             #"^Invalid URL$")]
  ;;     (t/is (thrown-with-msg? Exception exception-pattern
  ;;                             (sut/parse "some-illegal-url")))))
  )
