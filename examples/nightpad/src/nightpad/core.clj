(ns nightpad.core
  (:require [nightcode.editors :as editors]
            [nightcode.shortcuts :as shortcuts]
            [nightcode.utils :as utils]
            [nightcode.ui :as ui]
            [nightcode.window :as window]
            [seesaw.core :as s])
  (:gen-class))

(defn init-completer!
  [text-area extension]
  (some->> (editors/create-completer text-area extension)
           (editors/install-completer! text-area)))

(defn create-window-content
  [extension]
  (doto (editors/create-text-area)
    (.setSyntaxEditingStyle (get utils/styles extension))
    (.setTabSize 2)
    (init-completer! extension)
    (editors/init-paredit! true true)
    (.setText "(println \"Hello, world!\")")))

(defn create-window
  []
  (doto (s/frame :title "Nightpad"
                 :content (create-window-content "clj")
                 :on-close :exit
                 :size [800 :by 600])
    ; listen for keys while modifier is down
    (shortcuts/listen-for-shortcuts!
      (fn [key-code]
        (case key-code
          ; Q
          81 (window/confirm-exit-app!)
          ; else
          false)))
    ; set various window properties
    window/enable-full-screen!
    window/add-listener!))

(defn -main [& args]
  ; this will give us a nice dark theme by default, or allow a lighter theme
  ; by adding "-s light" to the command line invocation
  (window/set-theme! args)
  ; create and display the window
  ; it's important to save the window in the ui/root atom
  (s/invoke-later
    (s/show! (reset! ui/root (create-window)))))
