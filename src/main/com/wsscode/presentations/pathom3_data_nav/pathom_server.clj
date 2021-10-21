(ns com.wsscode.presentations.pathom3-data-nav.pathom-server
  (:require
    [clojure.spec.alpha :as s]
    [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.connect.operation.transit :as pcot]
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [io.pedestal.http :as http]
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.http.params]
    [io.pedestal.http.route :as route]))

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
    (http/stop instance))

  (let [server
        (-> {::http/routes
             (route/expand-routes
               #{["/graph" :post
                  [(body-params/body-params
                     (body-params/default-parser-map :transit-options {:handlers pcot/read-handlers}))
                   (http/transit-body-interceptor
                     ::transit-json-body
                     "application/transit+json"
                     :json
                     {:handlers pcot/write-handlers})
                   (pathom-env-interceptor request-or-env)
                   pathom-handler]
                  :route-name :graph]})

             ::http/type
             :jetty

             ::http/port
             port

             ::http/join?
             false}
            http/default-interceptors
            http/dev-interceptors
            http/create-server
            http/start)]
    (swap! servers* assoc port server)
    server))

(def env
  (pci/register
    (pbir/constantly-resolver :foo "bar")))

(comment
  (start-server env {::port 3020}))
