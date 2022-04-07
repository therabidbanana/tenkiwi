(ns tenkiwi.rules.player-order
  )

(defn- previous-player-by-order [player-order current-player]
  (let [curr-id    (:id current-player)
        curr-index (.indexOf (mapv :id player-order) curr-id)
        prev-index (dec curr-index)
        prev-index (if (> 0 prev-index)
                     (dec (count player-order))
                     prev-index)]
    (nth player-order prev-index)))

(defn- next-player-by-order [player-order current-player]
  (let [curr-id    (:id current-player)
        curr-index (.indexOf (mapv :id player-order) curr-id)
        next-index (inc curr-index)
        next-index (if (>= next-index (count player-order))
                     0
                     next-index)]
    (nth player-order next-index)))

(defn initial-state [starting-state
                     {:keys [players]
                      :as   options}]
  (let [order (into [] players)
        active-player (first players)
        next-player (next-player-by-order order active-player)
        order-state {:order         order
                     :active-player active-player}]
    (merge
     starting-state
     {:-player-order order-state
      :active-player active-player})))

(defn next-player [{{:keys [active-player
                            order]}
                    :-player-order}]
  (next-player-by-order order active-player))

(defn previous-player [{{:keys [active-player
                                order]}
                        :-player-order}]
  (previous-player-by-order order active-player))

(defn active-player [{{:keys [active-player]}
                      :-player-order}]
  active-player)

(defn player-order [{{:keys [order]}
                      :-player-order}]
  order)

(defn is-active? [{{:keys [active-player]}
                   :-player-order}
                  {:keys [id]}]
  (= id (:id active-player)))

(defn activate-next-player! [{:keys [-player-order]
                              :as   game}]
  (let [next-up (next-player game)
        updated (-> game
                    (assoc-in [:-player-order :active-player] next-up)
                    (assoc-in [:active-player] next-up))]
    updated))