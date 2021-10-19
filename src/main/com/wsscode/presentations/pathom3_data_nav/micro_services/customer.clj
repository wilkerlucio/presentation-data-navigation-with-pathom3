(ns com.wsscode.presentations.pathom3-data-nav.micro-services.customer
  (:require [acme.user :as-alias user]
            [faker.name]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.interface.eql :as p.eql]))

(pco/defresolver user-by-id [{::user/keys [id]}]
  {::pco/output
   [::user/first-name
    ::user/last-name]

   ::pco/cache-store
   :cache/persistent}
  {::user/first-name (faker.name/first-name)
   ::user/last-name  (faker.name/last-name)})

(pco/defresolver full-name [{::user/keys [first-name last-name]}]
  {::user/full-name (str first-name " " last-name)})

(pco/defresolver all-users []
  {::pco/output
   [{::user/all-users
     [::user/id]}]}
  {::user/all-users
   (into []
         (map #(array-map ::user/id %))
         (range 10))})

(def registry
  [user-by-id full-name all-users])

(defonce cache* (atom {}))

(def env
  (-> {:cache/persistent cache*}
      (pci/register registry)))

(def request
  (p.eql/boundary-interface env))

(comment
  (p.eql/process env
    [{::user/all-users
      [::user/full-name]}])

  (p.eql/process env
    {::user/id 1}
    [::user/first-name
     ::user/last-name])

  (p.eql/process env
    {::user/full-name "Wilker Lucio da Silva"}
    [::user/last-name])

  (->> ((requiring-resolve 'faker.name/names))
       (take 10)))

