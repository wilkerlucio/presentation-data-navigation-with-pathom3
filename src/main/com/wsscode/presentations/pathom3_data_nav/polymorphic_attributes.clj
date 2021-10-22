(ns com.wsscode.presentations.pathom3-data-nav.polymorphic-attributes
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.interface.eql :as p.eql]
            [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]))

(def spotify-tracks
  {1 {:spotify.track/id 1
      :spotify.track/title "Ai Se Sesse"}
   2 {:spotify.track/id 2
      :spotify.track/title "Chega de Saudade"}})

(pco/defresolver spotify-track [{:keys [spotify.track/id]}]
  {::pco/output
   [:spotify.track/title]}
  (get spotify-tracks id))

(def apple-tracks
  {1 {:apple.music.track/id    1
      :apple.music.track/title "People"}})

(pco/defresolver apple-track [{:keys [apple.music.track/id]}]
  {::pco/output
   [:apple.music.track/title]}
  (get apple-tracks id))

(pco/defresolver all-tracks []
  {::pco/output
   [{:music.mesh/all-tracks
     [:spotify.track/id
      :apple.music.track/id]}]}
  {:music.mesh/all-tracks
   [{:spotify.track/id 1}
    {:apple.music.track/id 1}
    {:spotify.track/id 2}]})

(def env
  (pci/register
    [apple-track
     spotify-track
     all-tracks
     (pbir/alias-resolver :spotify.track/title :music.mesh.track/title)
     (pbir/alias-resolver :apple.music.track/title :music.mesh.track/title)]))

(comment
  (p.eql/process env
    [{:music.mesh/all-tracks
      [:music.mesh.track/title]}]))
