(ns com.wsscode.presentations.pathom3-data-nav.micro-services.simple-demo
  (:require [com.wsscode.pathom3.interface.eql :as p.eql]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]))

(def users
  {1 {:user/first-name "Wilker"
      :user/last-name  "Silva"}
   2 {:user/first-name "Kaio"
      :user/last-name  "Teoi"}})

(pco/defresolver user-by-id [{:keys [user/id]}]
  {::pco/output
   [:user/first-name
    :user/last-name]}
  (get users id))

(pco/defresolver full-name [{:user/keys [first-name last-name]}]
  {:user/full-name (str first-name " " last-name)})

(pco/defresolver greet [{:keys [user/nome-completo]}]
  {:greet (str "Hello " nome-completo)})

(pco/defresolver all-users [{::keys []}]
  {::pco/output
   [{:user/all-users
     [:user/first-name
      :user/last-name]}]}
  {:user/all-users
   (vec (vals users))})

(def env
  (-> {}
      (pci/register [user-by-id full-name all-users greet
                     (pbir/equivalence-resolver :user/full-name :user/nome-completo)])
      ((requiring-resolve 'com.wsscode.pathom.viz.ws-connector.pathom3/connect-env)
       "debug")))

(comment
  (p.eql/process env
    {:user/nome-completo "Lucio Assis"}
    [:greet])

  (p.eql/process env
    {:user/id 1}
    [:user/nome-completo
     :user/full-name])

  (p.eql/process env
    {:user/first-name "CAScsa"
     :user/last-name  "Teoci"}
    [:user/full-name])

  (p.eql/process env
    [{:user/all-users
      [:user/full-name]}]))
