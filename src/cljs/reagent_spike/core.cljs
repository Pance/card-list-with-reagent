(ns reagent-spike.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; State
(defonce app-state (atom {:text "Hello, this is: "}))

(defonce card-list (atom ["Bile Blight"
                          "Seeker of the Way"
                          "Magma Jet"]))

(defn get-state [k & [default]]
  (clojure.core/get @app-state k default))

(defn put! [k v]
  (swap! app-state assoc k v))

;; -------------------------
;; Views

(defn card-list-view [cards]
  [:ul
    (for [card cards]
      ^{:key card} [:li card])])

(defmulti page identity)

(defmethod page :page1 [_]
  [:div [:h2 (get-state :text) "Page 1"]
   [:div [:a {:href "#/page2"} "go to page 2"]]
   [:div [card-list-view @card-list]]]
  )

(defmethod page :page2 [_]
  [:div [:h2 (get-state :text) "Page 2"]
   [:div [:a {:href "#/"} "go to page 1"]]])

(defmethod page :default [_]
  [:div "Invalid/Unknown route"])

(defn main-page []
  [:div [page (get-state :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (put! :current-page :page1))

(secretary/defroute "/page2" []
  (put! :current-page :page2))

;; -------------------------
;; Initialize app
(defn init! []
  (reagent/render-component [main-page] (.getElementById js/document "app")))

;; -------------------------
;; History
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))
;; need to run this after routes have been defined
(hook-browser-navigation!)
