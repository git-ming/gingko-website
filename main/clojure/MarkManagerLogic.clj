(ns
  ^{:author xlo}
  control.MarkManagerLogic
  (:import [model.db MarkUserCollection]
           [control ManagerLogic]
           [org.bson Document]
           [java.util LinkedList]))

(defn markUser [username password aimUser]
  (if (or (nil? aimUser) (= username aimUser)) false
    (let [markUserCollection (new MarkUserCollection)
          data (. markUserCollection getMarkData username aimUser)]
      (if-not (nil? data) false
        (do (. markUserCollection markUser username aimUser) true)))))

(defn unMarkUser [username password aimUser]
  (if (or (nil? aimUser) (= username aimUser)) false
    (let [markUserCollection (new MarkUserCollection)
          data (. markUserCollection getMarkData username aimUser)]
      (if (nil? data) false
        (do (. markUserCollection removeMark username aimUser) true)))))

(defn isMarked [username passwrod aimUser manager event]
  (if (nil? aimUser) false
    (let [markUserCollection (new MarkUserCollection)
          ans (not (nil? (. markUserCollection getMarkData username aimUser)))]
      (. manager addSuccessMessage event (str "{\"return\":" ans "}")) true)))

(defn getMarkedList [key username manager event]
  (let [aimList (vec (. (new MarkUserCollection) find (new Document key username)))
        ans (new LinkedList)]
    (dotimes [i (count aimList)]
      (let [now (nth aimList i)
            object (. now object)
            nowMap {"to" (. object get "to")}]
        (. ans add nowMap)))
    (. manager addSuccessMessage event ans) true))

(. ManagerLogic put "control.MarkManager$markUser" markUser 3)
(. ManagerLogic put "control.MarkManager$unMarkUser" unMarkUser 3)
(. ManagerLogic put "control.MarkManager$isMarked" isMarked 5)
(. ManagerLogic put "control.MarkManager$getMarkedList" getMarkedList 4)
(. ManagerLogic put "control.MarkManager$getMarkedMeList" getMarkedList 4)
