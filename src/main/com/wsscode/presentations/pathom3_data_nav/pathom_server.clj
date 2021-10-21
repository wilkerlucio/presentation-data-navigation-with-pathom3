(ns com.wsscode.presentations.pathom3-data-nav.pathom-server
  (:require
    [clojure.spec.alpha :as s]
    [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.connect.operation.transit :as pcot]
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [io.pedestal.http :as server]
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.http.params]
    [io.pedestal.http.route :as route]
    [com.wsscode.transito :as transito]
    [org.httpkit.client :as http]))

(s/def ::port pos-int?)
(s/def ::request-fn fn?)

(defonce servers* (atom {}))

(defn pathom-handler
  [{::keys [request-fn]
    :keys  [transit-params]}]
  (let [response (request-fn transit-params)]
    {:status 200
     :body   response}))

(defn pathom-env-interceptor [request-or-env]
  (let [request (cond
                  (fn? request-or-env)
                  request-or-env

                  (map? request-or-env)
                  (p.eql/boundary-interface request-or-env)

                  :else
                  (throw (ex-info "Invalid input to start server, must send an env map or a boundary interface fn"
                           {})))]
   {:name  ::pathom-env-interceptor
    :enter (fn [context]
             (update context :request assoc ::request-fn request))}))

(defn start-server [request-or-env {::keys [port]}]
  (if-let [instance (get @servers* port)]
    (server/stop instance))

  (let [server
        (-> {::server/routes
             (route/expand-routes
               #{["/graph" :post
                  [(body-params/body-params
                     (body-params/default-parser-map :transit-options {:handlers pcot/read-handlers}))
                   (server/transit-body-interceptor
                     ::server/transit-json-body
                     "application/transit+json"
                     :json
                     {:handlers pcot/write-handlers})
                   (pathom-env-interceptor request-or-env)
                   pathom-handler]
                  :route-name :graph]})

             ::server/type
             :jetty

             ::server/port
             port

             ::server/join?
             false}
            server/default-interceptors
            server/dev-interceptors
            server/create-server
            server/start)]
    (swap! servers* assoc port server)
    server))

(defn server-handler [port]
  (fn [tx]
    (-> @(http/request
           {:url     (str "http://localhost:" port "/graph")
            :headers {"Accept"       "application/transit+json"
                      "Content-Type" "application/transit+json"}
            :method  :post
            :body    (transito/write-str tx {:handlers pcot/write-handlers})})
        :body
        (transito/read-str {:handlers pcot/read-handlers}))))

(def env
  (pci/register
    (pbir/constantly-resolver :foo "bar")))

(comment
  (start-server env {::port 3020})

  ((server-handler 3020) [:foo]))
