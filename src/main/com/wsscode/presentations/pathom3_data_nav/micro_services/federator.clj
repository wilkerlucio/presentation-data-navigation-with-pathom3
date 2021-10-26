(ns com.wsscode.presentations.pathom3-data-nav.micro-services.federator
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.customer :as mr.customer]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.line-items :as mr.line-items]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.order :as mr.order]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.product :as mr.product]
            [com.wsscode.pathom3.connect.foreign :as pcf]
            [com.wsscode.pathom3.interface.eql :as p.eql]
            [com.wsscode.presentations.pathom3-data-nav.pathom-server :as ps]))

(defn start-all []
  (ps/start-server mr.customer/env {::ps/port 3011})
  (ps/start-server mr.line-items/env {::ps/port 3012})
  (ps/start-server mr.order/env {::ps/port 3013})
  (ps/start-server mr.product/env {::ps/port 3014}))

(defonce initial-start (start-all))

(defonce plan-cache* (atom {}))

(def env
  (-> {:com.wsscode.pathom3.connect.planner/plan-cache* plan-cache*}
      (pci/register
        [(pcf/foreign-register (ps/http-handler "http://localhost:3011/graph"))
         (pcf/foreign-register (ps/http-handler "http://localhost:3012/graph"))
         (pcf/foreign-register (ps/http-handler "http://localhost:3013/graph"))
         (pcf/foreign-register (ps/http-handler "http://localhost:3014/graph"))])
      ((requiring-resolve 'com.wsscode.pathom.viz.ws-connector.pathom3/connect-env)
       "federated")))

(comment
  (p.eql/process env
    {:acme.order/id 1628545763873}
    [:acme.order/id
     :acme.user/first-name
     :acme.order/delivery-state
     {:acme.order/line-items
      [:acme.line-item/quantity
       :acme.product/title
       :acme.line-item/price-total]}
     :acme.order/delivery-fee
     :acme.order/discount
     :acme.order/items-total
     :acme.order/grand-total
     :acme.user/phone
     :acme.user/cpf])

  (p.eql/process env
    {:acme.order/id 1628545763873}
    [{:acme.order/line-items
      [:acme.product/title]}

     :acme.user/phone
     :acme.user/cpf]))
