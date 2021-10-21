(ns com.wsscode.presentations.pathom3-data-nav.micro-services.customer
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.interface.eql :as p.eql]
            [com.wsscode.presentations.pathom3-data-nav.pathom-server :as ps]))

(def users
  {1 {:acme.user/first-name "Wilker"
      :acme.user/last-name  "Silva"
      :acme.user/cpf        "04203455464"
      :acme.user/phone      "+551141255421412"}})

(pco/defresolver user-by-id [{:acme.user/keys [id]}]
  {::pco/output
   [:acme.user/first-name
    :acme.user/last-name
    :acme.user/cpf
    :acme.user/phone]}
  (get users id))

(def registry
  [user-by-id])

(def env
  (pci/register registry))

(def request
  (p.eql/boundary-interface env))

(comment
  (ps/start-server request {::ps/port 3010})

  (p.eql/process env
    {:acme.user/id 1}
    [:acme.user/first-name
     :acme.user/last-name]))

; no lugar de:

{:meu-evento :pipo.domain/user}

; fazer:

{:meu-evento
 [:pipo.user/name
  :pipo.user/email
  {:pipo.user/address
   [:pipp.address/street-number]}]}
