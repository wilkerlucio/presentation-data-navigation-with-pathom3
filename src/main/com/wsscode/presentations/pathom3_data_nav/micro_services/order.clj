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
    :acme.order/discount       0}})

(pco/defresolver order-by-id [{:acme.order/keys [id]}]
  {::pco/output
   [:acme.order/delivery-state
    :acme.order/delivery-fee
    :acme.order/discount]}
  (get orders-db id))

(def registry
  [order-by-id])

(def env
  (pci/register registry))

(comment
  (ps/start-server env {::ps/port 3012})

  (p.eql/process env
    {:acme.order/id 1628545763873}
    [:acme.order/delivery-fee])

  (p.eql/process env
    {:acme.user/full-name "Wilker Lucio da Silva"}
    [:acme.user/last-name])

  (->> ((requiring-resolve 'faker.address /))
       (take 10)))

