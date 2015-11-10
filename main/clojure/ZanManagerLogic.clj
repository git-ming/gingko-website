(ns
  ^{:author xlo}
  control.ZanManagerLogic
  (:import [model.db ZanCollection BlogCollection MessageCollection]
           [java.util Date]
           [control ManagerLogic]))

(defn zan [username id]
  (if (or (nil? username) (nil? id)) false
    (let [zanCollection (new ZanCollection)
          data (. zanCollection getZan username id)]
      (if (nil? data) (do (. zanCollection addZanDocument username id)
                        (let [messageCollection (new MessageCollection)
                              document (. (. (new BlogCollection) getDocument id) object)]
                          (. document put "zan" (int (+ (. document get "zan") 1)))
                          (. messageCollection addMessage
                            (. document getString "author") username (str "user " username " support your document. id: " id) (new Date) "system" 100)) true)
        false))))

(defn noZan [username id]
  (if (or (nil? username) (nil? id)) false
    (let [zanCollection (new ZanCollection)
          data (. zanCollection getZan username id)]
      (if (nil? data) false
        (do (. zanCollection removeZan username id) true)))))

(defn isZan [username id manager event]
  (if (or (nil? username) (nil? id)) false
    (let [zanCollection (new ZanCollection)
          data (. zanCollection getZan username id)]
      (if (nil? data) (. manager addSuccessMessage event (str "{\"return\":false}"))
        (. manager addSuccessMessage event (str "{\"return\":true}")))
      true)))

(. ManagerLogic put "control.ZanManager$zan" zan 2)
(. ManagerLogic put "control.ZanManager$noZan" noZan 2)
(. ManagerLogic put "control.ZanManager$isZan" isZan 4)