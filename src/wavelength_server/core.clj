(ns wavelength-server.core
  (:require [org.httpkit.server :as server
             :refer [with-channel
                     websocket?
                     on-receive
                     on-close
                     run-server
                     send!
                     ]]
            
            [clojure.data.json :as json]
            [ring.util.response :as response]
            ;; [compojure.handler :only [site]]
            [compojure.core :refer [defroutes routes GET POST DELETE ANY context]
             :as compojure]
            [compojure.route :as route])
  
  (:gen-class))

(defn comm-handler [ring-request]
  ;; unified API for WebSocket and HTTP long polling/streaming
  (with-channel ring-request channel    ; get the channel
    (on-receive channel (fn [data] ; two way communication
                          (println "to data" data)
                          (send! channel data)))
    (on-close channel (fn [status]))))



(def my-routes
  (routes
   (GET "/ws" [] comm-handler)
   (GET "/index.html" [] (response/resource-response "index.html" {:root "public"}))
   (GET "/" [] (response/resource-response "index.html" {:root "public"}))
   (GET "/foo" [] "Hello Foo")
   (GET "/bar" [] "Hello Bar")
   (route/not-found "Not Found2"))
  )




(comment
  (def server (run-server #'my-routes {:port 8080}))
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))