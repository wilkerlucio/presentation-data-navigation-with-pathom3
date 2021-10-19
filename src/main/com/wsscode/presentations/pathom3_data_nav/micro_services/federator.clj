(ns com.wsscode.presentations.pathom3-data-nav.micro-services.federator
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.customer :as mr.customer]
            [com.wsscode.presentations.pathom3-data-nav.micro-services.shipping :as mr.shipping]
            [acme.user :as-alias user]
            [acme.address :as-alias address]
            [com.wsscode.pathom3.connect.foreign :as pcf]
            [com.wsscode.pathom3.interface.eql :as p.eql]
            [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]))

(def env
  (-> {}
      (pci/register
        [(pcf/foreign-register mr.customer/request)
         (pcf/foreign-register mr.shipping/request)
         (pbir/equivalence-resolver :usuario/full-name ::user/full-name)
         (pbir/single-attr-resolver :amount :precise-amount #(-> % (/ 100) bigdec))
         (pbir/single-attr-resolver :precise-amount :amount #(-> % (* 100) int))])
      ((requiring-resolve 'com.wsscode.pathom.viz.ws-connector.pathom3/connect-env)
       "debug")))

(comment

  (p.eql/process env
    [{::user/all-users
      [::user/full-name
       {::user/shipping-address
        [::address/city]}]}])

  (p.eql/process env
    {:records
     [{:amount 1000}
      {:precise-amount 23.4M}
      {:precise-amount 25.4M}
      {:amount 400}]}
    [{:records
      [:precise-amount]}]))
