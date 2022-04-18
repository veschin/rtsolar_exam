(ns core.server
  (:gen-class)
  (:require
   [bidi.ring :as bidi]
   [mount.core :as mount]
   [ring.adapter.jetty :as jetty]
   [clj-http.client :as http]
   [cheshire.core :as json]
   clojure.java.io))


(defn ->tags [query-string]
  (->> query-string
       (re-seq #"tag=((?U)\w*)")
       (map second)))

(defn make-req-n-parse! [tag]
  (let [res! #(http/request
               {:url          "https://api.stackexchange.com/2.2/search/"
                :method       :get
                :query-params {:pagesize 100
                               :order    "desc"
                               :sort     "creation"
                               :tagged   tag
                               :site     "stackoverflow"}})]
    
    (-> (try
          (res!)
          (catch Exception _
            (throw (Exception. "error while req to stackOverflow"))))
        :body
        (json/parse-string keyword)
        :items)))

(defn total<->answered [parsed]
  {:total    (->> parsed
                  (map :tags)
                  flatten
                  set
                  count)
   :answered (reduce + (map :answer_count parsed))})

(def http-count 25)

(defn search-handler
  "StackOverflow rps ~30
   i decide to lock rps on 25
   if u wanna, u can write u own smart seq to separate tags 
   and request after delay, 
   but i won't do it
   
   extract tags from req :query-string
   parallel request every tag
   then join into single hashmap 
   parsed into json 
   "
  [req]
  (let [tags     (-> req :query-string ->tags)
        ->result #(->> %
                       make-req-n-parse!
                       total<->answered
                       (assoc {} %))]
    (if (and (seq tags) (>= http-count (count tags)))
      {:status  200
       :body    (->> tags
                     (pmap ->result)
                     (into {})
                     (#(json/generate-string % {:pretty true})))
       :headers {"Content-Type" "application/json"}}
      {:status 500
       :body   "wrong or empty tags"})))

(def routes
  (bidi/make-handler
   ["/"
    [["search" search-handler]
     [#".*"
      (constantly
       {:status  500
        :body    "wrong req"
        :headers {"Content-Type" "text/plain"}})]]]))

(defn start []
  (def jetty-server
    (jetty/run-jetty
     routes
     {:port  8080
      :join? false})))

(declare server-)
(mount/defstate server-
  :start (start)
  :stop  (.stop jetty-server))

(defn -main [& _]
  (mount/start)
  (prn "Listening for ur tags..."))