(ns com.wsscode.presentations.pathom3-data-nav.imperative)

; region model

(defn order-by-id [order-id]
  )

(defn user-by-id [user-id]
  )

(defn user-full-name [user]
  )

; endregion

; region view

(defn user-view [{:keys [params/user-id]}]
  (let [user      (user-by-id user-id)
        full-name (user-full-name user)]
    [:div
     [:h1 full-name]]))

; endregion
