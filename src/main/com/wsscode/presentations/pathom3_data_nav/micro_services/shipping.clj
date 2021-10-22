(ns com.wsscode.presentations.pathom3-data-nav.micro-services.shipping
  (:require
    [com.wsscode.pathom3.connect.operation :as pco]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.interface.eql :as p.eql]
    [com.wsscode.presentations.pathom3-data-nav.pathom-server :as ps]))

(def address-db
  {1 {:acme.address/zipcode        "234155-11"
      :acme.address/street-address "Rua dos Pinheiros"
      :acme.address/number         324
      :acme.address/city           "São Paulo"
      :acme.address/state          "Capital/SP"
      :acme.address/complement     "apt 35"}})

(pco/defresolver user-shipping-address [{:acme.user/keys [id]}]
  {::pco/output
   [:acme.address/zipcode
    :acme.address/street-address
    :acme.address/number
    :acme.address/city
    :acme.address/state
    :acme.address/complement]}
  (get address-db id))

(def registry
  [user-shipping-address])

(def env
  (-> {::pci/index-source-id 'shipping}
      (pci/register registry)))

(def request
  (p.eql/boundary-interface env))

(comment
  (p.eql/process env
    {:acme.user/id 1}
    [:acme.address/street-address]))

