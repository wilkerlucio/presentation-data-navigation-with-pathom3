(ns com.wsscode.presentations.pathom3-data-nav.twitter.v2
  (:require [com.wsscode.pathom3.connect.operation :as pco]
            [cheshire.core :as json]
            [meander.epsilon :as m]
            [com.wsscode.pathom3.format.eql :as pf.eql]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.interface.async.eql :as p.a.eql]
            [com.wsscode.presentations.pathom3-data-nav.util :as u :refer [p-> p->>]]
            [clojure.string :as str]
            [com.wsscode.misc.coll :as coll]
            [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]))

(def request (u/make-request "https://api.twitter.com/2"))

(defn adapt-tweet [tweet]
  (-> tweet
      (u/namespace-keys "twitter.tweet")
      (u/pull-one :twitter.tweet/user "twitter.user")))

(defn expects->fields [expects expansions]
  (->> (reduce
         (fn [fields k]
           (let [n (str/replace (name k) #"-" "_")
                 t (-> k namespace (str/split #"\.") last)]
             (cond-> (update fields (str t ".fields") conj n)
               (contains? expansions t)
               (update "expansions" (fnil conj #{}) (get expansions t)))))
         {}
         (keys expects))
       (coll/map-vals #(str/join "," %))))

(defn convert-back [response expects expansions]
  (reduce
    (fn [out k]
      (let [n (str/replace (name k) #"-" "_")
            t (-> k namespace (str/split #"\.") last)]
        (if-let [expand (get expansions t)]
          (assoc out k (get (expand (get response "includes")) n))
          (assoc out k (get-in response ["data" n])))))
    {}
    (keys expects)))

(pco/defresolver get-tweet
  [env {:twitter.tweet/keys [id]}]
  {::pco/output
   [:twitter.tweet/attachments
    :twitter.tweet/author-id
    :twitter.tweet/context-annotations
    :twitter.tweet/conversation-id
    :twitter.tweet/created-at
    :twitter.tweet/entities
    :twitter.tweet/geo
    :twitter.tweet/id
    :twitter.tweet/in-reply-to-user-id
    :twitter.tweet/lang
    :twitter.tweet/non-public-metrics
    :twitter.tweet/organic-metrics
    :twitter.tweet/possibly-sensitive
    :twitter.tweet/promoted-metrics
    :twitter.tweet/public-metrics
    :twitter.tweet/referenced-tweets
    :twitter.tweet/reply-settings
    :twitter.tweet/source
    :twitter.tweet/text
    :twitter.tweet/withheld

    :twitter.user/created-at
    :twitter.user/description
    :twitter.user/entities
    :twitter.user/id
    :twitter.user/location
    :twitter.user/name
    :twitter.user/pinned-tweet-id
    :twitter.user/profile-image-url
    :twitter.user/protected
    :twitter.user/public-metrics
    :twitter.user/url
    :twitter.user/username
    :twitter.user/verified
    :twitter.user/withheld]}
  (let [expects      (-> env
                         :com.wsscode.pathom3.connect.planner/node
                         :com.wsscode.pathom3.connect.planner/expects)
        field-params (expects->fields expects
                       {"user" "author_id"})]
    (clojure.pprint/pprint field-params)
    (p-> (request env
           {:path         (str "/tweets/" id)
            :query-params field-params})
         (convert-back expects
           {"user" (comp first #(get % "users"))}))))

(pco/defresolver tweet-stats [{:keys [twitter.tweet/public-metrics]}]
  {:twitter.tweet/retweet-count (get public-metrics "retweet_count")
   :twitter.tweet/like-count (get public-metrics "like_count")})

(def token (System/getenv "TWITTER_TOKEN"))

(def env
  (-> {::u/token           token
       ::p.a.eql/parallel? true}
      (pci/register
        [get-tweet
         tweet-stats
         (pbir/equivalence-resolver :twitter.user/screen-name :twitter.user/username)
         (pbir/equivalence-resolver :twitter.tweet/favorite-count :twitter.tweet/like-count)])
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

  @(p.a.eql/process env
     {:twitter.tweet/id "1445078208190291973"}
     [:twitter.tweet/text
      :twitter.tweet/created-at
      :twitter.user/name])

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
  "https://api.twitter.com/2/tweets/:id?tweet.fields=created_at,public_metrics,source,text&user.fields=profile_image_url,name,username,verified"

  (def res
    (let [screen-name "Twitter"]
      @(request env
         {:path         "/statuses/user_timeline.json"
          :query-params {:screen_name screen-name}})))

  @(request env
     {:path         "/tweets/1445078208190291973"
      :query-params {}}))
