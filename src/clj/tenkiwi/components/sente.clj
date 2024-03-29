(ns tenkiwi.components.sente
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [routes GET POST]]
            [taoensso.sente :as sente]
            [ring.util.response :as ring]
            [clojure.tools.logging :as log]))

;; Sente supports both CLJ and CLJS as a server
(defrecord ChannelSocketServer [web-server-adapter handler options]
  component/Lifecycle
  (start [component]
    (let [handler (get-in component [:sente-handler :handler] handler)
          {:keys [ch-recv ajax-post-fn ajax-get-or-ws-handshake-fn send-fn connected-uids]} (sente/make-channel-socket-server! web-server-adapter options)
          base (assoc component
                      :ch-chsk ch-recv
                      :ring-ajax-post ajax-post-fn
                      :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
                      :chsk-send! send-fn
                      :connected-uids connected-uids
                      )]
       (assoc base
             :router (sente/start-chsk-router! ch-recv (if (:wrap-component? options)
                                                         (handler base)
                                                         handler)))))
  (stop [component]
    (when-let [stop-f (:router component)]
      (stop-f))
    component))

(defn new-channel-socket-server
  ([web-server-adapter]
   (new-channel-socket-server nil web-server-adapter {}))
  ([event-msg-handler web-server-adapter]
   (new-channel-socket-server event-msg-handler web-server-adapter {}))
  ([event-msg-handler web-server-adapter options]
   (map->ChannelSocketServer {:web-server-adapter web-server-adapter
                              :handler event-msg-handler
                              :options options})))
