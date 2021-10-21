(ns com.wsscode.presentations.pathom3-data-nav.micro-services.line-items
  (:require
    [com.wsscode.pathom3.connect.operation :as pco]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [com.wsscode.presentations.pathom3-data-nav.pathom-server :as ps]))

(def order-line-items-db
  {1628545763873
   {:acme.order/line-items
    [{:acme.line-item/product-id 1
      :acme.line-item/price      28.8
      :acme.line-item/quantity   1}
     {:acme.line-item/product-id 2
      :acme.line-item/price      38.9
      :acme.line-item/quantity   1}]}})

(pco/defresolver order-line-items [{:acme.order/keys [id]}]
  {::pco/output
   [{:acme.order/line-items
     [:acme.line-item/product-id
      :acme.line-item/price
      :acme.line-item/quantity]}]}
  (get order-line-items-db id))

(def registry
  [order-line-items])

(def env
  (pci/register registry))

(comment
  (ps/start-server env {::ps/port 3013})

  (p.eql/process env
    {:acme.order/id 1628545763873}
    [:acme.order/line-items]))

