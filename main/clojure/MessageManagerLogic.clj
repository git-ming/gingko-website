(ns
  ^{:author xlo}
  control.MessageManagerLogic
  (:import [model.db MessageCollection UserCollection]
           [java.util Date LinkedList]
           [model.config LengthLimitConfig ConstConfig]
           [control ManagerLogic]
           [org.bson Document]))

(defn addMessage [username password message aimUser preview]
  (if (or (nil? message) (nil? aimUser)) false
    (let [lengthLimitConfig (. LengthLimitConfig getConfig)]
      (if (or (> (count message) (. lengthLimitConfig getLimit "message"))
            (> (. Integer valueOf preview) (. lengthLimitConfig getLimit "preview"))) false
        (let [aimUserData (. (new UserCollection) getUserData aimUser)]
          (if (nil? aimUserData) false
            (do (. (new MessageCollection) addMessage aimUser username message (new Date) "message" (. Integer valueOf preview)) true)))))))

(defn getMessage [username passwrod id manager event]
  (if (nil? id) false
    (let [data (. (new MessageCollection) getMessageData id)]
      (if (or (nil? data) (not= username (. (. data object) getString "username"))) false
        (do (. manager addSuccessMessage event (. (. data object) toJson)) true)))))

(defn readMessage[username password id]
  (if (nil? id) false
    (let [data (. (new MessageCollection) getMessage id)]
      (if (or (nil? data) (not= username (. (. data object) getString "username"))) false
        (do (. (. data object) put "read" true) true)))))

(defn getAllMessage[username password aimUser manager event]
  (let [aimList (vec (. (new MessageCollection) findMessageData (new Document "username" aimUser)))
        ans (new LinkedList)]
    (dotimes [i (count aimList)]
      (let [now (nth aimList i)
            object (. now object)]
        (. object put "id" (str (. object get "_id")))
        (. object remove "_id")
        (let [body (. object get "message")
              previewDefault (. (. ConstConfig getConfig) getConst "preview default")
              preview (if (> (count body) (. object getInteger "preview" previewDefault)) (subs body 0 (. object getInteger "preview" previewDefault)) body)]
          (. object remove "message")
          (. object put "preview" preview))
        (. ans add object)))
    (. manager addSuccessMessage event ans) true))

(defn removeMessage[username password id]
  (if (nil? id) false
    (let [data (. (new MessageCollection) getMessage id)]
      (if (or (nil? data) (not= username (. (. data object) getString "username"))) false
        (do (. (new MessageCollection) removeMessage id) true)))))

(. ManagerLogic put "control.MessageManager$addMessage" addMessage 5)
(. ManagerLogic put "control.MessageManager$getMessage" getMessage 5)
(. ManagerLogic put "control.MessageManager$readMessage" readMessage 3)
(. ManagerLogic put "control.MessageManager$getAllMessage" getAllMessage 5)
(. ManagerLogic put "control.MessageManager$getUserAllMessage" getAllMessage 5)
(. ManagerLogic put "control.MessageManager$removeMessage" removeMessage 3)
(. ManagerLogic put "control.MessageManager$readAllMessage" readMessage 3)
(. ManagerLogic put "control.MessageManager$removeAllMessage" removeMessage 3)