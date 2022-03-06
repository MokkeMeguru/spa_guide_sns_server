(ns domain.util.url
  (:require [clojure.spec.alpha :as s]))
;; code is the copy of https://www.evanlouie.com/gists/clojure/src/com/evanlouie/net/url.cljc

(def ^:private url-regex
  "A regex to parse a URL.

  Note: matching this regex does not imply a valid href as it allows for an
  empty pathname to be provided (i.e. no trailing slash when at root domain).
  Hrefs require a non empty pathname (at least a trailing `/`). For a regex which
  validates for hrefs, refer to the `::href` spec.

  e.g. This regex will match `http://www.evanlouie.com` but is not a valid href

  | URL Segment           | Required/Optional |
  | --------------------- | ----------------- |
  | Protocol              | Required          |
  | Hostname              | Required          |
  | Username              | Optional          |
  | Password              | Optional          |
  | Port                  | Optional          |
  | Pathname              | Optional          |
  | Search/Get-Parameters | Optional          |
  | Hash                  | Optional          |

  @example (re-matches url-regex \"http://foobar.com\")
  @example (re-matches url-regex \"https://foo:bar@some.nested.dns.com:1337/path/to/page?query=some-value#some-header\")"
  #"^([a-zA-Z]+:)//(([^:]+)(:(.+))?@)?(([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)*)([a-zA-Z]{2,63})(:(\d+))?(/([^?#]+)?(\?([^#]+)?)?(#(.+)?)?)?$")


;;------------------------------------------------------------------------------
;; Specs


(s/def ::url (s/and string? #(re-matches url-regex %)))
(s/def ::hash (s/nilable (s/and string? #(re-matches #"^(#.+)?$" %))))
(s/def ::host (s/and string? #(re-matches #"^([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)+[a-zA-Z]{2,63}(:(\d+))?$" %)))
(s/def ::hostname (s/and string? #(re-matches #"^([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)+[a-zA-Z]{2,63}$" %)))
(s/def ::href (s/and string? #(re-matches #"^[a-zA-Z]+://([^:]+(:(.+))?@)?([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)+([a-zA-Z]{2,63})(:(\d+))?/(([^?#]+)?(\?([^#]+)?)?(#(.+)?)?)?$" %)))
(s/def ::origin (s/and string? #(re-matches #"^[a-zA-Z]+://([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)+[a-zA-Z]{2,63}(:(\d+))?$" %)))
(s/def ::password (s/nilable string?))
(s/def ::pathname (s/nilable (s/and string? #(re-matches #"^/(([^?#]+)?)?$" %))))
(s/def ::port (s/nilable (s/and string? #(re-matches #"^(\d+)?$" %))))
(s/def ::protocol (s/and string? #(re-matches #"^[a-zA-Z]+:$" %)))
(s/def ::search (s/nilable (s/and string? #(re-matches #"^(\?[^#]+)?$" %))))
(s/def ::username (s/nilable string?))
(s/def ::location (s/keys :req [::hash
                                ::host
                                ::hostname
                                ::href
                                ::origin
                                ::password
                                ::pathname
                                ::port
                                ::protocol
                                ::search
                                ::username]))

(s/fdef parse
  :args (s/cat :url ::url)
  :ret ::location)

;;------------------------------------------------------------------------------
;; Functions

(defn parse
  "Parses the provided `url` into a map modelled after the HTML Living Standard
  Location specification.

  The returned `::location` will be namespaced unless `namespaced` is set to
  false.

  @see https://developer.mozilla.org/en-US/docs/Web/API/Location
  @see https://html.spec.whatwg.org/multipage/history.html#the-location-interface
  @see https://www.w3.org/TR/html52/browsers.html#the-location-interface

  @throws when unable to parse the provided `url`
  @throws when unable to generate a valid `::location`"
  [url & {:keys [namespaced]
          :or   {namespaced true}}]

  ;; throw when url does not conform
  (when (not (s/valid? ::url url))
    (throw (ex-info "Invalid URL"
                    {:url    url
                     :regex  url-regex
                     :reason "Could not parse the provided URL with the URL regex"
                     :spec   (s/explain-str ::url url)})))

  ;; destructure the regex into location map
  ;; can alternatively done via destructuring: [_ protocol _ username _ password domains-with-dot-suffix _ tld _ port _ pathname-no-slash-prefix search _ hash _] url-match
  (let [url-match                (re-matches url-regex url) ; will always pass as `url` is a valid `::url`
        protocol                 (url-match 1)
        username                 (url-match 3)
        password                 (url-match 5)
        domains-with-dot-suffix  (url-match 6)
        tld                      (url-match 8)
        port                     (url-match 10)
        pathname-no-slash-prefix (url-match 12)
        search                   (url-match 13)
        hash                     (url-match 15)

        ;; compose the ::location requirements
        hostname (str domains-with-dot-suffix tld)
        host     (str hostname (when port (str ":" port)))
        origin   (str protocol "//" host)
        pathname (str "/" pathname-no-slash-prefix)
        auth     (when username (str username (when password (str ":" password)) "@"))
        href     (str protocol "//" auth host pathname search hash)
        location {::hash     hash
                  ::host     host
                  ::hostname hostname
                  ::href     href
                  ::origin   origin
                  ::password password
                  ::pathname pathname
                  ::port     port
                  ::protocol protocol
                  ::search   search
                  ::username username}]

    ;; throw if the generated location is not valid
    (when (not (s/valid? ::location location))
      (throw (ex-info "Generated location is not valid"
                      {:location location
                       :spec     (s/explain-str ::location location)})))

    ;; return namespaced map if `namespaced` is true
    (if namespaced
      location
      ;; return un-namespaced ::location
      (zipmap (map (comp keyword name) (keys location))
              (vals location)))))
