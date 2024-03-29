(ns tenkiwi.rules.clock-list
  (:require

   [tenkiwi.util :as util :refer [inspect]]
   [tenkiwi.rules.character-sheets :as character-sheets]
   )
  )

(def $ :-clock-list)

(defn initial-state [starting-state
                     {:keys [allow-new? clocks]
                      :or   {allow-new? true clocks []}
                      :as   options}]
  (let [clocks      (into []
                          (map-indexed (fn [i x] (assoc x :id i))
                                       clocks))
        extra-state {:allow-new? allow-new?
                     :clocks     clocks}]
    (assoc starting-state $ extra-state)))

(defn ->by-name [{{:keys [clocks]} $
                  :as              game}
                 name]
  (let [clock (first (filter #(= name (:name %)) clocks))]
    (:current clock)))

(defn ->id-by-name [{{:keys [clocks]} $
                     :as              game}
                    n]
  (let [clock (first (filter #(= (name (or n ""))
                                 (name (:name % "")))
                             clocks))]
    (:id clock)))

(defn increment-clock! [{{:keys [clocks]} $
                         :as              game}
                        {:keys [clock-id] :as params}]
  (let [clock         (get-in clocks [clock-id])
        current-score (:current clock)
        score-max     (get clock :max 10)
        new-score     (min (inc current-score) score-max)]
    (update-in game
               [$ :clocks clock-id]
               assoc :current new-score)))

(defn decrement-clock! [{{:keys [clocks]} $
                         :as              game}
                        {:keys [clock-id] :as params}]
  (let [clock         (get-in clocks [clock-id])
        current-score (:current clock)
        score-min     (get clock :min 0)
        new-score     (max (dec current-score) score-min)]
    (update-in game
               [$ :clocks clock-id]
               assoc :current new-score)))

(defn update-by-tags! [{{:keys [clocks]} $
                        :as              game}
                       {:keys [increment-clock
                               decrement-clock
                               double-increment-clock
                               double-decrement-clock]}]
  (cond-> game
    (get clocks (or (->id-by-name game increment-clock) :fake))
    (increment-clock! {:clock-id (->id-by-name game increment-clock)})
    (get clocks (or (->id-by-name game decrement-clock) :fake))
    (decrement-clock! {:clock-id (->id-by-name game decrement-clock)})
    (get clocks (or (->id-by-name game double-increment-clock) :fake))
    (->
     (increment-clock! {:clock-id (->id-by-name game double-increment-clock)})
     (increment-clock! {:clock-id (->id-by-name game double-increment-clock)}))
    (get clocks (or (->id-by-name game double-decrement-clock) :fake))
    (->
     (decrement-clock! {:clock-id (->id-by-name game double-decrement-clock)})
     (decrement-clock! {:clock-id (->id-by-name game double-decrement-clock)}))
    ))

(defn- build-score-row [{:keys [title subtitle max current id colors color]
                         :as   clock}]
  {:id        id
   :label     title
   :text      subtitle
   :score     current
   :max-score max
   :shape     :clock
   :color     (get (or colors {}) current (or color :blue))
   :actions   [{:text   " - "
                :action :decrement-clock
                :params {:clock-id id}}
               {:text   " + "
                :action :increment-clock
                :params {:clock-id id}}]})

(defn render-scoreboard-display [state]
  (let [clocks     (get-in state [$ :clocks] [])
        scoreboard (mapv build-score-row clocks)]
    (-> state
        (update :display assoc :clock-list scoreboard))))
