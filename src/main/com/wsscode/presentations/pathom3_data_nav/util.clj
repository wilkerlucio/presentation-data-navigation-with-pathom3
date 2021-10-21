(ns com.wsscode.presentations.pathom3-data-nav.util
  (:require
    [cheshire.core :as json]
    [clojure.string :as str]
    [com.wsscode.misc.coll :as coll]
    [org.httpkit.client :as http]
    [org.httpkit.sni-client :as sni-client]
    [promesa.core :as p]))

(defmacro p-> [x & forms]
  (if (seq forms)
    (let [fns (mapv (fn [arg]
                      (let [[f & args] (if (sequential? arg)
                                         arg
                                         (list arg))]
                        `(fn [p#] (~f p# ~@args)))) forms)]
      `(p/chain (p/promise ~x) ~@fns))
    x))

(defmacro p->> [x & forms]
  (if (seq forms)
    (let [fns (mapv (fn [arg]
                      (let [[f & args] (if (sequential? arg)
                                         arg
                                         (list arg))]
                        `(fn [p#] (~f ~@args p#)))) forms)]
      `(p/chain (p/promise ~x) ~@fns))
    x))

(alter-var-root #'org.httpkit.client/*default-client* (fn [_] sni-client/default-client))

(defn namespace-keys [entity ns]
  (coll/map-keys #(keyword ns (str/replace (name %) "_" "-")) entity))

(defn pull-one [entity k ns]
  (let [sub (get entity k)]
    (-> entity
        (dissoc k)
        (merge (namespace-keys sub ns)))))

(defn http-request [opts]
  (p/create
    (fn [resolve reject]
      (http/request opts
        (fn [{:keys [error] :as response}]
          (if error
            (reject (ex-info (str "HTTP Error:" error) {:response response}))
            (resolve response)))))))

(defn make-request [prefix]
  (fn request [{::keys [token]}
               {:keys [path method body query-params format-fn]
                :or   {format-fn json/parse-string}}]
    (p-> (http-request
           (cond-> {:url     (str prefix path)
                    :method  (or method :get)
                    :headers {"Content-Type"  "application/json"
                              "Accept"        "*/*"
                              "Authorization" (str "Bearer " token)}}
             query-params
             (assoc :query-params query-params)

             body
             (assoc :body (json/write body))))
         :body
         format-fn)))
