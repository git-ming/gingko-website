(ns
  ^{:author xlo}
  control.UserManagerLogic
  (:import [model.db UserCollection BlogCollection MarkUserCollection MessageCollection]
           [control ManagerLogic]
           [org.bson Document]
           [model.config LengthLimitConfig]))

(defn loginUser [username password]
  (if (or (nil? username) (nil? password)) false
    (let [data (. (new UserCollection) getUserData username)]
      (if (nil? data) (do (println "not found") false)
        (let [doucment (. data object)]
          (. (. doucment get "password") equals password))))))

(defn register [username password]
  (if (or (nil? username) (nil? password)) false
    (let [lengthLimitConfig (. LengthLimitConfig getConfig)]
      (if (or (> (count username) (. lengthLimitConfig getLimit "username"))
            (> (count password) (. lengthLimitConfig getLimit "password"))) false
        (let [userCollection (new UserCollection)]
          (. userCollection lockCollection)
          (if-not (nil? (. userCollection getUserData username)) false
            (do (. userCollection registerUser username password) true)))))))

(defn removeUser [username password]
  (let [userCollection (new UserCollection)]
    (. userCollection removeUser username))
  (let [blogCollection (new BlogCollection)]
    (let [blogs (vec (. blogCollection findDocumentListData (new Document "author" username)))]
      (dotimes [i (count blogs)] (. blogCollection removeDocument (. (. (. (nth blogs i) object) get "_id") toString)))))
  (let [markUserCollection (new MarkUserCollection)]
    (let [marks (vec (. markUserCollection find (new Document "from" username)))]
      (dotimes [i (count marks)]
        (. markUserCollection removeMark (. (. (nth marks i) object) getString "from") (. (. (nth marks i) object) getString "to"))))
    (let [marks (vec (. markUserCollection find (new Document "to" username)))]
      (dotimes [i (count marks)]
        (. markUserCollection removeMark (. (. (nth marks i) object) getString "from") (. (. (nth marks i) object) getString "to")))))
  (let [messageCollection (new MessageCollection)]
    (let [messages (vec (. messageCollection findMessageData (new Document "username" username)))]
      (dotimes [i (count messages)] (. messageCollection removeMessage (. (. (. (nth messages i) object) get "_id") toString)))))
  true)

(defn changeUserAccess [username password aimUser accessType val]
  (if (nil? aimUser) false
    (let [data (. (new UserCollection) getUser aimUser)]
      (if (nil? data) false
        (do (. (. data object) put accessType val) true)))))

(defn setMotto [username password motto]
  (if (nil? motto) false
    (let [lengthLimitConfig (. LengthLimitConfig getConfig)]
      (if (> (count motto) (. lengthLimitConfig getLimit "motto")) false
        (let [data (. (new UserCollection) getUser username)]
          (. (. data object) put "motto" motto) true)))))

(defn getMotto [username manager event]
  (if (nil? username) false
    (let [data (. (new UserCollection) getUser username)]
      (if (nil? data) false
        (do (. manager addSuccessMessage event (str "{\"return\":\"" (. (. data object) getString "motto") "\"}"))
          true)))))


(defn userExist [username manager event]
  (if (nil? username) false
    (let [resoult (not (nil? (. (new UserCollection) getUserData username)))]
      (do (. manager addSuccessMessage event (str "{\"return\":" resoult "}")) true))))

(. ManagerLogic put "control.UserManager$loginUser" loginUser 2)
(. ManagerLogic put "control.UserManager$register" register 2)
(. ManagerLogic put "control.UserManager$removeUser" removeUser 2)
(. ManagerLogic put "control.UserManager$changeUserAccess" changeUserAccess 5)
(. ManagerLogic put "control.UserManager$setMotto" setMotto 3)
(. ManagerLogic put "control.UserManager$getMotto" getMotto 3)
(. ManagerLogic put "control.UserManager$userExist" userExist 3)