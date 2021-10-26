(ns com.wsscode.presentations.pathom3-data-nav.monolit
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.customer :as mr.customer]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.line-items :as mr.line-items]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.order :as mr.order]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.product :as mr.product]
            [com.wsscode.pathom3.interface.eql :as p.eql]
            [com.wsscode.presentations.pathom3-data-nav.pathom-server :as ps]
            [acme.order :as-alias order]
            [acme.user :as-alias user]))

(defonce plan-cache* (atom {}))

(def env
  (-> {:com.wsscode.pathom3.connect.planner/plan-cache* plan-cache*}
      (pci/register
        [mr.customer/registry
         mr.line-items/registry
         mr.order/registry
         mr.product/registry])
      ((requiring-resolve 'com.wsscode.pathom.viz.ws-connector.pathom3/connect-env)
       "mono")))

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

     :acme.user/cpf
     :acme.user/phone])

  (p.eql/process env
    {:records
     [{:amount 1000}
      {:precise-amount 23.4M}
      {:precise-amount 25.4M}
      {:amount 400}]}
    [{:records
      [:precise-amount]}]))
