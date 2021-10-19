(ns com.wsscode.presentations.pathom3-data-nav.micro-services.shipping
  (:require [acme.user :as-alias user]
            [acme.address :as-alias address]
            [faker.address]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.interface.eql :as p.eql]))

(pco/defresolver user-shipping-address [{::user/keys [id]}]
  {::pco/output
   [{::user/shipping-address
     [::address/zipcode
      ::address/street-address
      ::address/city]}]

   ::pco/cache-store
   :cache/persistent}
  {::user/shipping-address
   {::address/zipcode        (faker.address/zip-code)
    ::address/street-address (faker.address/street-address)
    ::address/city           (faker.address/city)}})

(def registry
  [user-shipping-address])

(defonce cache* (atom {}))

(def env
  (-> {:cache/persistent cache*}
      (pci/register registry)))

(def request
  (p.eql/boundary-interface env))

(comment
  (reset! cache* {})

  (user-shipping-address)
  (p.eql/process env
    {::user/id 1}
    [{::user/shipping-address
      [::address/zipcode
       ::address/street-address
       ::address/city]}])

  (p.eql/process env
    {::user/full-name "Wilker Lucio da Silva"}
    [::user/last-name])

  (->> ((requiring-resolve 'faker.address /))
       (take 10)))

