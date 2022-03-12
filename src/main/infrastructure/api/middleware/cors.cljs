(ns infrastructure.api.middleware.cors
  (:require
   [taoensso.timbre :refer [info]]
   [macchiato.util.response :as r]
   [clojure.spec.alpha :as s]
   [util.string]
   [clojure.set :as set]
   [clojure.string :as str]))

(s/def ::request-method #{:get :post :delete :put :options})
(s/def ::allowed-headers (s/nilable (s/* string?)))
(s/def ::allowed-methods (s/nilable (s/* ::request-method)))
(s/def ::allowed-origins (s/+ regexp?))
(s/def ::access-control-config (s/keys :req-un [::allowed-headers
                                                ::allowed-origins
                                                ::allowed-methods]))
(s/fdef origin
  :args (s/cat :request map?)
  :ret string?)

(s/fdef preflight?
  :args (s/cat :request map?)
  :ret boolean?)

(s/fdef access-control-request-headers
  :args (s/cat :request map?)
  :ret string?)

(s/fdef allow-preflight-access-control-request-headers?
  :args (s/cat :request map?
               :allowed-headers ::allowed-headers)
  :ret boolean?)

(s/fdef allow-access-control-request-method?
  :args (s/cat :request map?
               :allowed-methods ::allowed-methods)
  :ret boolean?)

(s/fdef allow-request?
  :args (s/cat :request map?
               :access-control-config ::access-control-config)
  :ret boolean?)

(s/fdef add-headers
  :args (s/cat :request map?
               :access-control-config ::access-control-config
               :response map?)
  :ret map?)

(s/fdef add-allowed-headers
  :args (s/cat :request map?
               :allowed-headers ::allowed-headers
               :response map?)
  :ret map?)

(s/fdef normalize-headers
  :args (s/cat :headers map?)
  :ret map?)

(s/fdef add-access-control
  :args (s/cat :request map?
               :access-control-config ::access-control-config
               :response map?)
  :ret map?)

;; request parser
(defn- origin
  "get the origin of request header

  Examples:

    - null
    - \"https://developer.mozilla.org\"
    - \"http://localhost:8080\"

  "
  [request]
  (r/get-header request "origin"))

(defn- access-control-request-headers
  "access-control-request headers from request

  Examples:

    - \"X-PINGOTHER, Content-Type\"

  "
  [request]
  (r/get-header request "access-control-request-headers"))

;; request validator
(defn- preflight?
  "whether the preflight or not"
  [request]
  (-> request :request-method (= :options)))

(defn- allow-preflight-access-control-request-headers?
  "validate preflight access controll request headers are acceptable for the rule

  if `allowed-headers` are nil, accept all headers
  "
  [request allowed-headers]
  (or (nil? allowed-headers)
      (set/subset?
       (-> request
           access-control-request-headers
           util.string/strings->string-list
           util.string/string-list->lower-case-set)
       (-> allowed-headers
           util.string/string-list->lower-case-set))))

(defn- allow-access-control-request-method?
  "validate request methods are acceptable for the rule

  if `methods` are nil, accept all methods
  "
  [request allowed-methods]
  (let [preflight-name [:headers "access-control-request-method"]
        request-method (if (preflight? request)
                         (keyword (str/lower-case (get-in request preflight-name "")))
                         (:request-method request))]
    (or (nil? methods)
        (contains? (set allowed-methods) request-method))))

(defn- allow-request?
  [request access-control-config]
  (let [origin (origin request)
        {:keys [allowed-headers
                allowed-origins
                allowed-methods]} access-control-config]
    (and origin
         (some #(re-matches % origin) allowed-origins)
         (allow-access-control-request-method? request allowed-methods)
         (if (preflight? request)
           (allow-preflight-access-control-request-headers?
            request
            allowed-headers)
           true))))

;; edit response
(defn- add-headers
  "add the access control headers using th request's origin to the response"
  [request access-control-config response]
  (if-let [origin (origin request)]
    (update-in response [:headers]
               merge {:access-control-allow-origin origin
                      :access-control-allow-headers (:allowed-headers access-control-config)
                      :access-control-allow-methods (:allowed-methods access-control-config)})
    response))

(defn- add-allowed-headers
  "add the allowed headers into the response"
  [request allowed-headers response]
  (if (preflight? request)
    (let [request-headers (r/get-header request "access-control-request-headers")
          allowed-headers (if (nil? allowed-headers)
                            (-> request-headers util.string/strings->string-list util.string/string-list->lower-case-set)
                            allowed-headers)]
      (if allowed-headers
        (update-in response [:headers] merge {:access-control-allow-headers allowed-headers})
        response))
    response))

;; format response header
(defn- normalize-headers
  [headers]
  (let [upcase #(str/join ", " (sort (map (fn [s] (-> s name str/upper-case)) %)))
        ->header-names #(str/join ", " (sort (map (fn [s] (-> s name (util.string/capitalize-words #"-"))) %)))]
    (reduce
     (fn [acc [k v]]
       (assoc acc (util.string/capitalize-words (name k) #"-")
              (case k
                :access-control-allow-methods (upcase v)
                :access-control-allow-headers (->header-names v)
                v)))
     {} headers)))

(defn- add-access-control
  "add the access control heades to the response based on the rules
  and what came on the header"
  [request access-control-config response]
  (let [unnormalized-resp (->> response
                               (add-headers request access-control-config)
                               (add-allowed-headers request (:allowed-headers access-control-config)))]
    (update-in unnormalized-resp [:headers] normalize-headers)))

(defn wrap-cors
  "middleware which adds Cross-Origin Resouce Sharing headers"
  ([handler]
   (wrap-cors handler {}))
  ([handler access-control-config]
   (fn [request respond raise]
     (info "got request at cors middleware: " "preflight?:" (preflight? request) "origin:" (origin request))
     (info (-> request (dissoc :reitit.core/match) (dissoc :reitit.core/router)))
     (info (keys request))
     (cond
       (preflight? request) (if (allow-request? request access-control-config)
                              (respond (add-access-control request access-control-config {:status 200 :headers {} :body "preflight complete"}))
                              (handler request respond raise))
       (origin request) (if (allow-request? request access-control-config)
                          (handler request #(respond (add-access-control request access-control-config %)) raise)
                          (handler request respond raise))
       :else (handler request respond raise)))))

;; (re-matches #"http://127.0.0.1:3000" "http://localhost:3000")
;; (re-matches #".*" "http://localhost:3000")

;; sample options request
;; {:ssl-client-cert nil
;;  :protocol "HTTP/1.1"
;;  :subdomains nil
;;  :cookies nil
;;  :remote-addr "127.0.0.1"
;;  :secure? nil
;;  :params nil
;;  :stale? nil
;;  :hostname "127.0.0.1"
;;  :node/request {}
;;  :xhr? nil
;;  :headers {"host" "127.0.0.1:3000"
;;            "user-agent" "curl/7.81.0"
;;            "accept" "*/*"
;;            "origin" "http://localhost:8080"
;;            "access-control-request-method" "GET"
;;            "access-control-request-headers" "X-Requested-With"}
;;  :server-port 3000
;;  :content-length nil
;;  :signed-cookies nil
;;  :url "/test?name=hello"
;;  :content-type nil
;;  :uri "/test"
;;  :fresh? nil
;;  :server-name "127.0.0.1"
;;  :query-string "name=hello"
;;  :path-params {}
;;  :body {}
;;  :scheme :http
;;  :request-method :options
;;  :node/response {}}

;; sample get request
;; {:ssl-client-cert nil
;;  :protocol "HTTP/1.1"
;;  :subdomains nil
;;  :cookies nil
;;  :remote-addr "127.0.0.1"
;;  :secure? nil
;;  :params nil
;;  :stale? nil
;;  :hostname "127.0.0.1"
;;  :node/request {}
;;  :xhr? nil
;;  :headers {"host" "127.0.0.1:3000"
;;            "user-agent" "curl/7.81.0"
;;            "accept" "*/*"
;;            "origin" "http://localhost:8080"
;;            "access-control-request-method" "GET"
;;            "access-control-request-headers" "X-Requested-With"}
;;  :server-port 3000
;;  :content-length nil
;;  :signed-cookies nil
;;  :url "/test?name=hello"
;;  :content-type nil
;;  :uri "/test"
;;  :fresh? nil
;;  :server-name "127.0.0.1"
;;  :query-string "name=hello"
;;  :path-params {}
;;  :body {}
;;  :scheme :http
;;  :request-method :get
;;  :node/response {}}
