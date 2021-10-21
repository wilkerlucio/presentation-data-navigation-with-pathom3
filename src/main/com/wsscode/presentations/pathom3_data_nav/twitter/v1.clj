(ns com.wsscode.presentations.pathom3-data-nav.twitter.v1
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [cheshire.core :as json]
            [meander.epsilon :as m]
            [com.wsscode.pathom3.format.eql :as pf.eql]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.interface.async.eql :as p.a.eql]
            [com.wsscode.presentations.pathom3-data-nav.util :as u :refer [p-> p->>]]))

(def request (u/make-request "https://api.twitter.com/1.1"))

(defn adapt-tweet [tweet]
  (-> tweet
      (u/namespace-keys "twitter.tweet")
      (u/pull-one :twitter.tweet/user "twitter.user")))

(pco/defresolver get-tweet
  [env {:twitter.tweet/keys [id]}]
  {::pco/output
   [:twitter.tweet/contributors
    :twitter.tweet/coordinates
    :twitter.tweet/created-at
    :twitter.tweet/entities
    :twitter.tweet/favorite-count
    :twitter.tweet/favorited
    :twitter.tweet/geo
    :twitter.tweet/id
    :twitter.tweet/id-str
    :twitter.tweet/in-reply-to-screen-name
    :twitter.tweet/in-reply-to-status-id
    :twitter.tweet/in-reply-to-status-id-str
    :twitter.tweet/in-reply-to-user-id
    :twitter.tweet/in-reply-to-user-id-str
    :twitter.tweet/is-quote-status
    :twitter.tweet/lang
    :twitter.tweet/place
    :twitter.tweet/possibly-sensitive
    :twitter.tweet/possibly-sensitive-appealable
    :twitter.tweet/retweet-count
    :twitter.tweet/retweeted
    :twitter.tweet/source
    :twitter.tweet/text
    :twitter.tweet/truncated
    :twitter.user/contributors-enabled
    :twitter.user/created-at
    :twitter.user/default-profile
    :twitter.user/default-profile-image
    :twitter.user/description
    :twitter.user/entities
    :twitter.user/favourites-count
    :twitter.user/follow-request-sent
    :twitter.user/followers-count
    :twitter.user/following
    :twitter.user/friends-count
    :twitter.user/geo-enabled
    :twitter.user/has-extended-profile
    :twitter.user/id
    :twitter.user/id-str
    :twitter.user/is-translation-enabled
    :twitter.user/is-translator
    :twitter.user/lang
    :twitter.user/listed-count
    :twitter.user/location
    :twitter.user/name
    :twitter.user/notifications
    :twitter.user/profile-background-color
    :twitter.user/profile-background-image-url
    :twitter.user/profile-background-image-url-https
    :twitter.user/profile-background-tile
    :twitter.user/profile-banner-url
    :twitter.user/profile-image-url
    :twitter.user/profile-image-url-https
    :twitter.user/profile-link-color
    :twitter.user/profile-sidebar-border-color
    :twitter.user/profile-sidebar-fill-color
    :twitter.user/profile-text-color
    :twitter.user/profile-use-background-image
    :twitter.user/protected
    :twitter.user/screen-name
    :twitter.user/statuses-count
    :twitter.user/time-zone
    :twitter.user/translator-type
    :twitter.user/url
    :twitter.user/utc-offset
    :twitter.user/verified
    :twitter.user/withheld-in-countries]}
  (p-> (request env {:path         "/statuses/show.json"
                     :query-params {:id id}})
       adapt-tweet))

(pco/defresolver get-user-by-screen-name
  [env {:twitter.user/keys [screen-name]}]
  {::pco/output
   [:twitter.tweet/contributors
    :twitter.tweet/coordinates
    :twitter.tweet/created-at
    :twitter.tweet/entities
    :twitter.tweet/favorite-count
    :twitter.tweet/favorited
    :twitter.tweet/geo
    :twitter.tweet/id
    :twitter.tweet/id-str
    :twitter.tweet/in-reply-to-screen-name
    :twitter.tweet/in-reply-to-status-id
    :twitter.tweet/in-reply-to-status-id-str
    :twitter.tweet/in-reply-to-user-id
    :twitter.tweet/in-reply-to-user-id-str
    :twitter.tweet/is-quote-status
    :twitter.tweet/lang
    :twitter.tweet/place
    :twitter.tweet/retweet-count
    :twitter.tweet/retweeted
    :twitter.tweet/source
    :twitter.tweet/text
    :twitter.tweet/truncated
    :twitter.user/contributors-enabled
    :twitter.user/created-at
    :twitter.user/default-profile
    :twitter.user/default-profile-image
    :twitter.user/description
    :twitter.user/entities
    :twitter.user/favourites-count
    :twitter.user/follow-request-sent
    :twitter.user/followers-count
    :twitter.user/following
    :twitter.user/friends-count
    :twitter.user/geo-enabled
    :twitter.user/has-extended-profile
    :twitter.user/id
    :twitter.user/id-str
    :twitter.user/is-translation-enabled
    :twitter.user/is-translator
    :twitter.user/lang
    :twitter.user/listed-count
    :twitter.user/location
    :twitter.user/name
    :twitter.user/notifications
    :twitter.user/profile-background-color
    :twitter.user/profile-background-image-url
    :twitter.user/profile-background-image-url-https
    :twitter.user/profile-background-tile
    :twitter.user/profile-banner-url
    :twitter.user/profile-image-url
    :twitter.user/profile-image-url-https
    :twitter.user/profile-link-color
    :twitter.user/profile-location
    :twitter.user/profile-sidebar-border-color
    :twitter.user/profile-sidebar-fill-color
    :twitter.user/profile-text-color
    :twitter.user/profile-use-background-image
    :twitter.user/protected
    :twitter.user/screen-name
    :twitter.user/statuses-count
    :twitter.user/time-zone
    :twitter.user/translator-type
    :twitter.user/url
    :twitter.user/utc-offset
    :twitter.user/verified
    :twitter.user/withheld-in-countries]}
  (p-> (request env
         {:path         "/users/show.json"
          :query-params {:screen_name screen-name}})
       (u/namespace-keys "twitter.user")
       (u/pull-one :twitter.user/status "twitter.tweet")))

(pco/defresolver user-timeline-by-screen-name
  [env {:twitter.user/keys [screen-name]}]
  {::pco/output
   [{:twitter.user/recent-timeline
     [:twitter.tweet/contributors
      :twitter.tweet/coordinates
      :twitter.tweet/created-at
      :twitter.tweet/entities
      :twitter.tweet/favorite-count
      :twitter.tweet/favorited
      :twitter.tweet/geo
      :twitter.tweet/id
      :twitter.tweet/id-str
      :twitter.tweet/in-reply-to-screen-name
      :twitter.tweet/in-reply-to-status-id
      :twitter.tweet/in-reply-to-status-id-str
      :twitter.tweet/in-reply-to-user-id
      :twitter.tweet/in-reply-to-user-id-str
      :twitter.tweet/is-quote-status
      :twitter.tweet/lang
      :twitter.tweet/place
      :twitter.tweet/possibly-sensitive
      :twitter.tweet/possibly-sensitive-appealable
      :twitter.tweet/retweet-count
      :twitter.tweet/retweeted
      :twitter.tweet/source
      :twitter.tweet/text
      :twitter.tweet/truncated
      :twitter.user/contributors-enabled
      :twitter.user/created-at
      :twitter.user/default-profile
      :twitter.user/default-profile-image
      :twitter.user/description
      :twitter.user/entities
      :twitter.user/favourites-count
      :twitter.user/follow-request-sent
      :twitter.user/followers-count
      :twitter.user/following
      :twitter.user/friends-count
      :twitter.user/geo-enabled
      :twitter.user/has-extended-profile
      :twitter.user/id
      :twitter.user/id-str
      :twitter.user/is-translation-enabled
      :twitter.user/is-translator
      :twitter.user/lang
      :twitter.user/listed-count
      :twitter.user/location
      :twitter.user/name
      :twitter.user/notifications
      :twitter.user/profile-background-color
      :twitter.user/profile-background-image-url
      :twitter.user/profile-background-image-url-https
      :twitter.user/profile-background-tile
      :twitter.user/profile-banner-url
      :twitter.user/profile-image-url
      :twitter.user/profile-image-url-https
      :twitter.user/profile-link-color
      :twitter.user/profile-sidebar-border-color
      :twitter.user/profile-sidebar-fill-color
      :twitter.user/profile-text-color
      :twitter.user/profile-use-background-image
      :twitter.user/protected
      :twitter.user/screen-name
      :twitter.user/statuses-count
      :twitter.user/time-zone
      :twitter.user/translator-type
      :twitter.user/url
      :twitter.user/utc-offset
      :twitter.user/verified
      :twitter.user/withheld-in-countries]}]}
  (p->>
    (request env
      {:path         "/statuses/user_timeline.json"
       :query-params {:screen_name screen-name}})
    (mapv adapt-tweet)
    (array-map :twitter.user/recent-timeline)))

(def token (System/getenv "TWITTER_TOKEN"))

(def env
  (-> {::u/token           token
       ::p.a.eql/parallel? true}
      (pci/register [get-tweet
                     get-user-by-screen-name
                     user-timeline-by-screen-name])
      ((requiring-resolve 'com.wsscode.pathom.viz.ws-connector.pathom3/connect-env)
       "twitter")))

(comment

  ; region query demo tweet view

  @(p.a.eql/process env
     {:twitter.tweet/id "1445078208190291973"}
     [:twitter.user/profile-image-url
      :twitter.user/screen-name
      :twitter.user/name
      :twitter.user/verified
      :twitter.tweet/text
      :twitter.tweet/created-at
      :twitter.tweet/source
      :twitter.tweet/retweet-count
      :twitter.tweet/favorite-count])

  ; endregion

  ; region query demo relevant user

  @(p.a.eql/process env
     {:twitter.user/screen-name "Twitter"}
     [:twitter.user/profile-image-url
      :twitter.user/screen-name
      :twitter.user/name
      :twitter.user/verified

      ; new fields
      :twitter.user/description
      :twitter.user/following])

  ; endregion

  ; region query demo user view

  @(p.a.eql/process env
     {:twitter.user/screen-name "Twitter"}
     [:twitter.user/profile-image-url
      :twitter.user/screen-name
      :twitter.user/name
      :twitter.user/verified])

  ; endregion

  (m/rewrite res
    (m/map-of
      (m/pred string? ?k) (m/or (m/pred map? (m/cata ?v))
                            ?v))
    {(keyword ?k) ?v})

  (-> res
      (namespace-keys "twitter.user")
      (pull-one :twitter.user/status "twitter.tweet")
      (pf.eql/data->query))

  @(p-> (request env {:path         "https://api.twitter.com/1.1/statuses/show.json"
                      :query-params {:id "210462857140252672"}})
        :body
        json/parse-string
        (namespace-keys "twitter.tweet")
        (pull-one :twitter.tweet/user "twitter.user"))

  (mapv adapt-tweet res)

  (def res
    (let [screen-name "Twitter"]
      @(request env
         {:path         "/statuses/user_timeline.json"
          :query-params {:screen_name screen-name}})))

  (def res
    @(request env
       {:path         "/users/show.json"
        :query-params {:screen_name "Twitter"}})))
