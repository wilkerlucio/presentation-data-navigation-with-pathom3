(ns com.wsscode.presentations.pathom3-data-nav.micro-services.product
  (:require
    [com.wsscode.pathom3.connect.operation :as pco]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [com.wsscode.presentations.pathom3-data-nav.pathom-server :as ps]))

(def products-db
  {1
   {:acme.product/title "Quadradinho de Tapioca"}

   2
   {:acme.product/title "Sopa de Cebola"}})

(pco/defresolver product-by-id [{:acme.product/keys [id]}]
  {::pco/output
   [:acme.product/title]}
  (get products-db id))

(def registry
  [product-by-id])

(def env
  (-> {::pci/index-source-id 'product}
      (pci/register registry)))

(def request
  (p.eql/boundary-interface env))

(comment
  (p.eql/process env
    {:acme.product/id 2}
    [:acme.product/title]))

