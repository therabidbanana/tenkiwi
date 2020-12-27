(ns tenkiwi.gamemasters.walking-deck
  "This game master runs a Walking Deck game"
  #_(:require ))

(def valid-active-actions #{:discard :done :x-card :end-game :leave-game})
(def valid-inactive-actions #{:x-card :leave-game})

(defn valid-action? [active? action]
  (if active?
    (valid-active-actions action)
    (valid-inactive-actions action)))

(def done-action
  {:action :done
   :text   "Finish Turn"})

(def leave-game-action
  {:action  :leave-game
   :confirm true
   :text    "End Game Now"})

(def discard-action
  {:action :discard
   :text   "[X] Discard this..."})

(def end-game-action
  {:action  :end-game
   :text    "End the Game"})


;; TODO: XSS danger?
(defn waiting-for
  [{:keys [user-name]}]
  {:id    "waiting"
   :state :inactive
   :text  (str "It is " user-name "'s turn...")})


(defn next-player [player-order current-player]
  (let [curr-id    (:id current-player)
        curr-index (.indexOf (mapv :id player-order) curr-id)
        next-index (inc curr-index)
        next-index (if (>= next-index (count player-order))
                     0
                     next-index)]
    (nth player-order next-index)))

(defn lookup-card [lookup-map {:keys [rank suit]}]
  (get lookup-map [suit rank]))

(def prompts
  {[:clubs :jack]     {:rank :jack, :suit :clubs, :reflect-on "If Someone Else Will Die", :encounter-something "Eternal Purgatory", :establish-something "We Can't Close It", :the-horde "Are a friend"},
   [:spades 9]        {:rank 9, :suit :spades, :reflect-on "Our Values", :encounter-something "Whispering", :establish-something "We Mustn't Reveal Ourselves", :the-horde "Are full of animals"},
   [:diamonds 2]      {:rank 2, :suit :diamonds, :reflect-on "What I Want", :encounter-something "Alarms", :establish-something "We Have A Gun", :the-horde "Get inside"},
   [:hearts :ace]     {:rank :ace, :suit :hearts, :reflect-on "Your Place in Society", :encounter-something "Smoke", :establish-something "We Need Transport", :the-horde "Are on fire"},
   [:spades 8]        {:rank 8, :suit :spades, :reflect-on "Depression", :encounter-something "A Loved One", :establish-something "We Mustn't Get Hurt", :the-horde "Show low cunning"},
   [:clubs :ace]      {:rank :ace, :suit :clubs, :reflect-on "You Being Betrayed", :encounter-something "Ashes", :establish-something "We Can't Find It", :the-horde "Are smelly"},
   [:spades 10]       {:rank 10, :suit :spades, :reflect-on "This is Without Meaning ", :encounter-something "Stone", :establish-something "We Mustn't Give In", :the-horde "Torture for fun"},
   [:hearts :queen]   {:rank :queen, :suit :hearts, :reflect-on "Who Is Coming to Save You", :encounter-something "Death", :establish-something "We Need Order", :the-horde "Appear innocent"},
   [:diamonds :queen] {:rank :queen, :suit :diamonds, :reflect-on "Who Isn't Coming to Save You", :encounter-something "Dying", :establish-something "We Have a Crowbar", :the-horde "Appear helpful"},
   [:diamonds :jack]  {:rank :jack, :suit :diamonds, :reflect-on "If You'll Die", :encounter-something "A Fresh Hell", :establish-something "We Have A Tactical Edge", :the-horde "Are an enemy"},
   [:clubs :king]     {:rank :king, :suit :clubs, :reflect-on "The Last Thing You'll Ever See", :encounter-something "Debasement", :establish-something "We Can't Control It", :the-horde "Ride the bus"},
   [:hearts 10]       {:rank 10, :suit :hearts, :reflect-on "This is Fair Punishment", :encounter-something "Guns", :establish-something "We Need A Way Out", :the-horde "Torture to betrayal"},
   [:spades 2]        {:rank 2, :suit :spades, :reflect-on "What I'm Doing", :encounter-something "Silence", :establish-something "We Mustn't Be Heard", :the-horde "Almost reach"},
   [:diamonds 9]      {:rank 9, :suit :diamonds, :reflect-on "Our Culture", :encounter-something "Yelling", :establish-something "We Have A Cellphone", :the-horde "Control animals"},
   [:hearts 7]        {:rank 7, :suit :hearts, :reflect-on "Where I Should Be", :encounter-something "Praising God", :establish-something "We Need Food", :the-horde "Overwhelm us"},
   [:clubs 10]        {:rank 10, :suit :clubs, :reflect-on "This is A Blessing in Disguise", :encounter-something "Steel", :establish-something "We Can't Open It", :the-horde "Torture for fear"},
   [:diamonds 8]      {:rank 8, :suit :diamonds, :reflect-on "Anger", :encounter-something "Someone", :establish-something "We Have a Contact", :the-horde "Form a strategy"},
   [:diamonds 7]      {:rank 7, :suit :diamonds, :reflect-on "Where I Could Be", :encounter-something "Fearing God", :establish-something "We Have a Promise", :the-horde "Press in on us"},
   [:clubs :queen]    {:rank :queen, :suit :clubs, :reflect-on "Who Might Come to Silence You", :encounter-something "Aging", :establish-something "We Can't Carry It", :the-horde "Appear harmless"},
   [:spades 6]        {:rank 6, :suit :spades, :reflect-on "Blame Ourselves", :encounter-something "Fawn", :establish-something "We Mustn't Miss Our Chance", :the-horde "Break the bone"},
   [:spades :king]    {:rank :king, :suit :spades, :reflect-on "Your Last Dying Wish", :encounter-something "Desecration", :establish-something "We Mustn't Be Too Late", :the-horde "Crash cars"},
   [:spades 5]        {:rank 5, :suit :spades, :reflect-on "Journalists", :encounter-something "Falling", :establish-something "We Mustn't Be Left Behind", :the-horde "Drool and vomit"},
   [:hearts 2]        {:rank 2, :suit :hearts, :reflect-on "Who I Am", :encounter-something "Sirens", :establish-something "We Need Bandages", :the-horde "Grab someone"},
   [:hearts 6]        {:rank 6, :suit :hearts, :reflect-on "Blame the Military", :encounter-something "Flight", :establish-something "We Need More Bullets", :the-horde "Suck the juices"},
   [:hearts :king]    {:rank :king, :suit :hearts, :reflect-on "The Last People Alive", :encounter-something "Defiance", :establish-something "We Need Teamwork", :the-horde "Down airplanes"},
   [:hearts 5]        {:rank 5, :suit :hearts, :reflect-on "Cops", :encounter-something "Shattering", :establish-something "We Need Information", :the-horde "Swarm like locusts"},
   [:hearts :jack]    {:rank :jack, :suit :hearts, :reflect-on "If You'll Live", :encounter-something "A Kind of Heaven", :establish-something "We Need A Leader", :the-horde "Are a relative"},
   [:clubs 4]         {:rank 4, :suit :clubs, :reflect-on "Where This Is Leading", :encounter-something "A Judgement", :establish-something "We Can't Get Out", :the-horde "Find weapons"},
   [:diamonds 5]      {:rank 5, :suit :diamonds, :reflect-on "Scientists", :encounter-something "Collapsing", :establish-something "We Have A Pile of Cash", :the-horde "Devour the still alive"},
   [:hearts 4]        {:rank 4, :suit :hearts, :reflect-on "Where They Came From", :encounter-something "A Corpse", :establish-something "We Need Drugs", :the-horde "Take control"},
   [:spades :ace]     {:rank :ace, :suit :spades, :reflect-on "Your Chance of Survival", :encounter-something "Fire", :establish-something "We Mustn't Follow Orders", :the-horde "Are everywhere"},
   [:hearts 9]        {:rank 9, :suit :hearts, :reflect-on "Our Country", :encounter-something "Screaming", :establish-something "We Need To Rest", :the-horde "Infest animals"},
   [:diamonds :king]  {:rank :king, :suit :diamonds, :reflect-on "The Last Place To Hide", :encounter-something "Deference", :establish-something "We Have a Bible", :the-horde "Derail the train"},
   [:clubs 3]         {:rank 3, :suit :clubs, :reflect-on "The Future", :encounter-something "Gorging", :establish-something "We Can't See", :the-horde "Get back up"},
   [:spades 7]        {:rank 7, :suit :spades, :reflect-on "Where I Will Never Be", :encounter-something "Killing God", :establish-something "We Mustn't Believe Them", :the-horde "Come from behind"},
   [:spades :queen]   {:rank :queen, :suit :spades, :reflect-on "Who Has Abandoned You", :encounter-something "Rebirth", :establish-something "We Mustn't Leave a Trail", :the-horde "Appear dead"},
   [:clubs 8]         {:rank 8, :suit :clubs, :reflect-on "Bargaining", :encounter-something "Everyone", :establish-something "We Can't Fit ", :the-horde "Learn the pattern"},
   [:spades :jack]    {:rank :jack, :suit :spades, :reflect-on "If Someone Else Will Kill You", :encounter-something "Dark Revelations", :establish-something "We Mustn't Become Monsters", :the-horde "Are a lover"},
   [:clubs 6]         {:rank 6, :suit :clubs, :reflect-on "Blame the System", :encounter-something "Freeze", :establish-something "We Can't Reach", :the-horde "Crack the skull"},
   [:diamonds 6]      {:rank 6, :suit :diamonds, :reflect-on "Blame the Rich", :encounter-something "Fight", :establish-something "We Have A Moment", :the-horde "Snap the spine"},
   [:spades 4]        {:rank 4, :suit :spades, :reflect-on "What Could Stop Them", :encounter-something "An Execution", :establish-something "We Mustn't Say That", :the-horde "Learn something new"},
   [:diamonds :ace]   {:rank :ace, :suit :diamonds, :reflect-on "Your Status in this Group", :encounter-something "Explosions", :establish-something "We Have a Special Skill", :the-horde "Are toxic"},
   [:clubs 2]         {:rank 2, :suit :clubs, :reflect-on "What I Hide", :encounter-something "Bells", :establish-something "We Can't Breathe", :the-horde "Break the doors"},
   [:clubs 9]         {:rank 9, :suit :clubs, :reflect-on "Our Society", :encounter-something "Weeping", :establish-something "We Can't Stop", :the-horde "Frighten animals"},
   [:diamonds 4]      {:rank 4, :suit :diamonds, :reflect-on "What They Want", :encounter-something "A Murder", :establish-something "We Have a Rope", :the-horde "Cut the power"},
   [:diamonds 3]      {:rank 3, :suit :diamonds, :reflect-on "The Present", :encounter-something "Gouges", :establish-something "We Have A Knife", :the-horde "Just won't die"},
   [:hearts 8]        {:rank 8, :suit :hearts, :reflect-on "Denial", :encounter-something "Noone", :establish-something "We Need Water", :the-horde "Become sentient"},
   [:clubs 5]         {:rank 5, :suit :clubs, :reflect-on "Doctors", :encounter-something "Destruction", :establish-something "We Can't Swim", :the-horde "Eat flesh"},
   [:hearts 3]        {:rank 3, :suit :hearts, :reflect-on "The Past", :encounter-something "Gashes", :establish-something "We Need Barricades", :the-horde "Still crawl forward"},
   [:diamonds 10]     {:rank 10, :suit :diamonds, :reflect-on "This is Unfair Punishment", :encounter-something "Germs", :establish-something "We Have a Choice To Make", :the-horde "Torture to control"},
   [:spades 3]        {:rank 3, :suit :spades, :reflect-on "The End ", :encounter-something "Guts", :establish-something "We Mustn't Trust Others", :the-horde "Ignore the blow"},
   [:clubs 7]         {:rank 7, :suit :clubs, :reflect-on "Where I Might Have Been", :encounter-something "Becoming God", :establish-something "We Can't Help Them", :the-horde "Surround us"}})

(defn interpret-draw [game card]
  (let [prompt-ideas (lookup-card prompts card)]
    (str "You drew a " card "\n\n" prompt-ideas)))

(defn build-active-card [{:keys [players-by-rank
                                 act
                                 active-player]
                          :as   game-state}
                         {:keys [text type]
                          :as   card}]
  (let [new-card (assoc card
                        :type (or type :prompt)
                        :text (or text (interpret-draw game-state card)))]
    {:card          new-card
     :extra-actions [leave-game-action]
     :actions       (case act
                      4 [end-game-action]
                      0 [done-action]
                      1 [done-action]
                      2 [done-action]
                      3 [done-action])}))

(defn build-inactive-card [{:keys [players-by-rank
                                   act
                                   active-player]
                            :as game-state}
                           extra-text]
  (let [waiting (waiting-for active-player)
        waiting (if extra-text
                        (update waiting
                               :text
                               (partial str extra-text "\n\n"))
                        waiting)]

    {:card          waiting
     :extra-actions [leave-game-action]}))

(def card-suits #{:clubs :hearts :spades :diamonds})

(def card-ranks #{:ace 2 3 4 5 6 7 8 9 10 :jack :queen :king})

;; TODO - finish normalize rank / suits + slurpped data below
(defn normalize-rank [string]
  (let [to-rank (merge {
                        "k" :king
                        "a" :ace
                        "q" :queen
                        "j" :jack}
                       (zipmap (map #(cond (int? %)     (str %)
                                           (keyword? %) (name %))
                                    card-ranks)
                               card-ranks))
        input   (cond
                  (keyword? string) (name string)
                  (nil? string) :blank
                  :else (clojure.string/lower-case string))]
    (get to-rank input :unknown)))

(defn normalize-suit [string]
  (let [to-suit
        (zipmap (map #(cond (keyword? %) (name %))
                     card-suits)
                card-suits)
        input   (cond
                  (keyword? string) (name string)
                  (nil? string) :blank
                  :else (clojure.string/lower-case string))]
    (get to-suit input :unknown)))

(defn normalize-card-info [map]
  (-> map
      (update :rank normalize-rank)
      (update :suit normalize-suit)))

(defn read-spreadsheet-data [url]
  (let [lines
        (->> (slurp url)
             (clojure.string/split-lines)
             (map #(clojure.string/split % #"\t")))
        header (first lines)
        rest   (rest lines)
        keys   (map keyword header)
        rows   (map #(zipmap keys %) rest)]
    (map normalize-card-info rows)))

(defn to-lookup-map [card-rows]
  (into {}
        (for [{:keys [rank suit]
               :as row} card-rows]
          [[suit rank] row])))


(comment
  ;; Use eval and replace to pull in a published sheet with tsv
  ;; Use :s/, \[/,\n\]/g
  ;; (read-spreadsheet-data url)
  (to-lookup-map (read-spreadsheet-data "https://docs.google.com/spreadsheets/d/e/2PACX-1vQBY3mq94cg_k3onDKmA1fa_L3AGbKVBfdxxeP04l73QVIXMkD4gEdG-e2ciex2jjTJjaKkdU1Vtaf1/pub?gid=0&single=true&output=tsv"))

  )

;; TODO: Make this data instead of a URL lookup

(def playing-cards (for [rank card-ranks
                         suit card-suits]
                     (hash-map :rank rank :suit suit)))

(def characters {:ace   {:title       "The child"
                         :description "full of hope"}
                 2      {:title       "The lover"
                         :description "of another character"}
                 3      {:title       "The teacher"
                         :description "building hope"}
                 4      {:title       "The doctor"
                         :description "helping others"}
                 5      {:title       "The soldier"
                         :description "armed and ready"}
                 6      {:title       "The scientist"
                         :description "who knows"}
                 7      {:title       "The celebrity"
                         :description "loved by all"}
                 8      {:title       "The pariah"
                         :description "hated or feared"}
                 9      {:title       "The leader"
                         :description "important to society"}
                 10     {:title       "The millionaire"
                         :description "powerful and rich"}
                 :jack  {:title       "The artist"
                         :description "who can tell the story"}
                 :queen {:title       "The average"
                         :description "an everyday person"}
                 :king  {:title       "The criminal"
                         :description "armed and anxious"}})

(def introduction [
                   "The Walking Deck is a story game played by reading and responding to prompts.\n\nRead these prompts to everyone and when you have added your own details to the story, press \"**Finish Turn**\""
                   "Each player will be introduced as a character of a group of survivors in a zombie wasteland."
                   "The exact nature of the disaster is up to the players."])

(def intro-cards (mapv #(hash-map :text % :type :intro) introduction))
(def padding-card {:text "Finish turn if you are ready to play" :type :intro})

(defn character-card [{:keys [rank] :as card} {:keys [user-name]}]
  (let [{:keys [title description]} (get characters rank)]
    (merge card
          {:text (str user-name " is...\n\n" title "... " description)
           :type :character})))

(defn start-game [world-atom room-id]
  (let [players           (get-in @world-atom [:rooms room-id :players])
        first-player      (first players)
        next-players      (rest players)
        player-count      (count players)
        intro-cards       (->> intro-cards
                          (partition player-count player-count (cycle [padding-card]))
                          (apply concat))
        deck              (shuffle playing-cards)
        [characters deck] (split-at player-count deck)
        character-cards   (map character-card characters players)
        ;; Update the players to assign characters
        players           (map #(assoc %1 :character %2) players characters)
        player-ranks      (zipmap (map :rank characters) players)

        deck              (concat intro-cards character-cards deck)

        new-game          {:players-by-id   (zipmap (map :id players) players)
                           :players-by-rank player-ranks
                           :game-type       :walking-deck
                           :act             0
                           :discard         []
                           :deck            (rest deck)
                           :active-player   (first players)
                           :next-players    (rest players)}
        new-game          (assoc new-game
                            :active-display   (build-active-card new-game (first deck))
                            :inactive-display (build-inactive-card new-game "yo"))]
    (doto world-atom
      (swap! update-in [:rooms room-id] assoc :game new-game))))

(defn finish-card [game]
  (let [{:keys [player-order
                active-player
                next-players
                discard
                deck
                act]} game
        active-card     (get-in game [:active-display :card])
        all-players     (conj (into [] next-players) active-player)
        next-up         (first all-players)
        ;; This lets us push first player back in the mix (only single player)
        next-players    (rest all-players)
        discard         (cons active-card discard)
        next-card       (first deck)
        deck            (into [] (rest deck))
        next-state      0
        next-game       (assoc game
                               :deck deck
                               :next-players next-players
                               :discard discard
                               :active-player next-up
                               )]
    (assoc next-game
           :active-display (build-active-card next-game next-card)
           :inactive-display (build-inactive-card next-game nil))))

(defn discard-card [game]
  (let [{:keys [player-order
                active-player
                next-players
                discard
                deck
                state]} game
        active-card     (get-in game [:active-display :card])
        discard         (cons active-card discard)
        next-card       (first deck)
        deck            (rest deck)
        next-state      (:state next-card)
        next-game       (-> game
                            (assoc-in [:inactive-display :x-card-active?] false)
                            (assoc :deck deck
                                   :discard discard))]
    (-> next-game
        (assoc
         :active-display (build-active-card next-game next-card)))))


(defn push-uniq [coll item]
  (if (some #(= % item) coll)
    coll
    (into [item] coll)))

(defn x-card [game]
  (let [{:keys []} game]
    (-> game
        (assoc-in [:active-display :x-card-active?] true)
        (update-in [:active-display :actions] push-uniq discard-action)
        (assoc-in [:inactive-display :x-card-active?] true))))

(defn end-game [game]
  nil)

(defn take-action [world-atom {:keys [uid room-id action]}]
  (let [{:keys [player-order
                active-player
                active-display
                state]
         :as   game} (get-in @world-atom [:rooms room-id :game])

        current-card   (:card active-display)
        active-player? (= (:id active-player) uid)
        valid?         (valid-action? active-player? action)
        next-state     (case action
                         :done           (finish-card game)
                         :x-card         (x-card game)
                         :discard        (discard-card game)
                         ;; TODO allow players to leave game without ending
                         ;;; change action text
                         :leave-game     (end-game game)
                         :end-game       (end-game game))]
    ;; (println next-state)
    ;; TODO FIXME: Swapping on update after computing next state lets you do multiple turns
    ;; Need to compute next state atomically in the swap
    (swap! world-atom update-in [:rooms room-id] assoc :game next-state)))

(comment
  (def fake-state {:rooms {1 {:playes [{:id "a"}]}}})

  (start-game (atom fake-state) 1)

  )