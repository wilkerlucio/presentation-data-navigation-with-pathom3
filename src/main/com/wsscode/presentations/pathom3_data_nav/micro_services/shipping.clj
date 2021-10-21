(ns com.wsscode.presentations.pathom3-data-nav.micro-services.shipping
  (:require
    [com.wsscode.pathom3.connect.operation :as pco]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.interface.eql :as p.eql]))

(def address-db
  {1 {:acme.address/zipcode        "234155-11"
      :acme.address/street-address "Rua dos Pinheiros"
      :acme.address/number         324
      :acme.address/city           "São Paulo"
      :acme.address/state          "Capital/SP"
      :acme.address/complement     "apt 35"}})

(pco/defresolver user-shipping-address [{:acme.user/keys [id]}]
  {::pco/output
   [{:acme.user/shipping-address
     [:acme.address/zipcode
      :acme.address/street-address
      :acme.address/number
      :acme.address/city
      :acme.address/state
      :acme.address/complement]}]}
  {:acme.user/shipping-address
   (get address-db id)})

(def registry
  [user-shipping-address])

(def env
  (pci/register registry))

(def request
  (p.eql/boundary-interface env))

(comment
  (reset! cache* {})

  (user-shipping-address)
  (p.eql/process env
    {:acme.user/id 1}
    [:acme.user/shipping-address])

  (p.eql/process env
    {:acme.user/full-name "Wilker Lucio da Silva"}
    [:acme.user/last-name])

  (->> ((requiring-resolve 'faker.address /))
       (take 10)))

