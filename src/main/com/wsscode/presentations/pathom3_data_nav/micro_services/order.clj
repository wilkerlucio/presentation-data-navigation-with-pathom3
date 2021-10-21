(ns com.wsscode.presentations.pathom3-data-nav.micro-services.order
  (:require
    [com.wsscode.pathom3.connect.operation :as pco]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [com.wsscode.presentations.pathom3-data-nav.pathom-server :as ps]))

(def orders-db
  {1628545763873
   {:acme.order/delivery-state :acme.order.delivery-state/waiting-transport
    :acme.order/delivery-fee   11
    :acme.order/discount       0
    :acme.user/id              1}})

(pco/defresolver order-by-id [{:acme.order/keys [id]}]
  {::pco/output
   [:acme.order/delivery-state
    :acme.order/delivery-fee
    :acme.order/discount
    :acme.user/id]}
  (get orders-db id))

(pco/defresolver order-items-total [{:acme.order/keys [line-items]}]
  {::pco/input
   [{:acme.order/line-items
     [:acme.line-item/price-total]}]}
  {:acme.order/items-total
   (transduce (map :acme.line-item/price-total) + line-items)})

(pco/defresolver order-grand-total
  [{:acme.order/keys [items-total delivery-fee discount]}]
  {:acme.order/grand-total (-> (+ items-total delivery-fee)
                               (- discount))})

(def registry
  [order-by-id
   order-items-total
   order-grand-total])

(def env
  (-> {::pci/index-source-id 'order}
      (pci/register registry)))

(def request
  (p.eql/boundary-interface env))

(comment
  (ps/start-server env {::ps/port 3012})

  (p.eql/process env
    {:acme.order/id 1628545763873}
    [:acme.order/delivery-fee
     :acme.order/items-total]))

