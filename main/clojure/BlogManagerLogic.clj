(ns
  ^{:author xlo}
  control.BlogManagerLogic
  (:import [model.db BlogCollection MarkUserCollection MessageCollection UserCollection]
           [org.bson Document BsonArray BsonDocument BsonDateTime BsonString]
           [model.config LengthLimitConfig ConfigManager ReturnCodeConfig ConstConfig]
           [java.util Date LinkedList Comparator]
           [control ManagerLogic BlogManager]))

(defn document-compare [o1 o2]
  (let [date1 (. (. o1 object) get "time")
        date2 (. (. o2 object) get "time")]
    (if (. date1 after date2) -1 (if (. date2 after date1) 1 0))))

(defn sendDocumentList [manager event message page]
  (let [aimList (sort document-compare (vec (. (new BlogCollection) findDocumentListData message)))
        ans (new LinkedList)]
    (dotimes [i (count aimList)]
      (let [now (nth aimList i)
            object (. now object)
            body (. object get "body")
            previewDefault (. (. ConstConfig getConfig) getConst "preview default")
            preview (if (> (count body) (. object getInteger "preview" previewDefault)) (subs body 0 (. object getInteger "preview" previewDefault)) body)
            nowMap {"id" (str (. object get "_id")),
                    "title" (. object get "title"),
                    "author" (. object get "author"),
                    "time" (. object get "time"),
                    "reader" (. object getInteger "reader" 0)
                    "preview" preview}]
        (. ans add nowMap)))
    (let [onePageSize (. (. ConstConfig getConfig) getConst "blog page size")
          left (* (- page 1) onePageSize)
          right (if (> (+ left onePageSize) (. ans size)) (. ans size) (+ left onePageSize))]
      (if (<= left right) (do (. manager addSuccessMessage event (. ans subList left right)) true)
        false))))

(defn sendDocumentListSize [manager event message]
  (let [onePageSize (. (. ConstConfig getConfig) getConst "blog page size")
        aimList (vec (. (new BlogCollection) findDocumentListData message))
        pageSize (max 1 (+ (int (/ (count aimList) onePageSize)) (if (= 0 (rem (count aimList) onePageSize)) 0 1)))]
    (do (. manager addSuccessMessage event (str "{\"return\":" pageSize "}"))) true))

(defn documentNotFoundMessage [manager event returnCodeConfig]
  (let [object {"return" (. returnCodeConfig getCode "not found")}]
    (. manager addFailMessage event object)))

(defn addDocument [username password title body type preview]
  (if (or (nil? title) (nil? body) (nil? type)) false
    (let [lengthLimitConfig (. LengthLimitConfig getConfig)]
      (if (or (> (count title) (. lengthLimitConfig getLimit "documentTitle"))
            (> (count body) (. lengthLimitConfig getLimit "documentBody"))
            (> (. Integer valueOf preview) (. lengthLimitConfig getLimit "preview"))) false
        (do (let [blogCollection (new BlogCollection)]
              (. blogCollection addDocument username title body (new Date) type (. Integer valueOf preview)))
          (let [markUserCollection (new MarkUserCollection)
                messageCollection (new MessageCollection)
                marks (vec (. markUserCollection find (new Document "to" username)))]
            (dotimes [i (count marks)]
              (. messageCollection addMessage (. (. (nth marks i) object) getString "from") username title (new Date) "system" 100)))
          true)))))

(defn addReply [username password documentID reply]
  (if (or (nil? documentID) (nil? reply)) false
    (let [lengthLimitConfig (. LengthLimitConfig getConfig)]
      (if (> (count reply) (. lengthLimitConfig getLimit "documentBody")) false
        (let [blogCollection (new BlogCollection)
              document (. blogCollection getDocument documentID)]
          (if (nil? document) false
            (let [replyList (new BsonArray)
                  pastList (vec (. (. document object) get "reply"))
                  nowReply (new BsonDocument)]
              (. nowReply put "author" (new BsonString username))
              (. nowReply put "data" (new BsonDateTime (. (new Date) getTime)))
              (. nowReply put "reply" (new BsonString reply))
              (dotimes [i (count pastList)] (. replyList add (. BsonDocument parse (. (nth pastList i) toJson))))
              (. replyList add nowReply)
              (. (. document object) put "reply" replyList)
              (let [messageCollection (new MessageCollection)]
                (. messageCollection addMessage
                  (. (. document object) getString "author") username (str "reply: " reply) (new Date) "system" 100))
              true)))))))

(defn getDocument [id manager event returnCodeConfig]
  (documentNotFoundMessage manager event returnCodeConfig)
  (if (nil? id) false
    (let [data (. (new BlogCollection) getDocumentData id)]
      (if (nil? data) false
        (let [object (. (. data object) toJson)]
          (. manager addSuccessMessage event object) true)))))

(defn addReader [id]
  (let [data (. (new BlogCollection) getDocument id)]
    (if (nil? data) false
      (let [object (. data object)
            val (+ (. object getInteger "reader" 0) 1)]
        (. object put "reader" (int val)) true))))

(defn getAuthorTypeDocumentList [author typeMessage page manager event returnCodeConfig]
  (documentNotFoundMessage manager event returnCodeConfig)
  (if (or (nil? author) (nil? typeMessage)) false
    (let [userData (. (new UserCollection) getUserData author)]
      (let [document (new Document)]
        (sendDocumentList manager event (. (. document append "author" author) append "type" typeMessage) (. Integer valueOf page))))))

(defn getAuthorTypeDocumentListSize [author typeMessage manager event returnCodeConfig]
  (documentNotFoundMessage manager event returnCodeConfig)
  (if (or (nil? author) (nil? typeMessage)) false
    (let [userData (. (new UserCollection) getUserData author)]
      (let [document (new Document)]
        (sendDocumentListSize manager event (. (. document append "author" author) append "type" typeMessage))))))

(defn getTypeDocumentList [typeKey typeMessage page manager event returnCodeConfig]
  (documentNotFoundMessage manager event returnCodeConfig)
  (if (nil? typeMessage) false
    (let [document (new Document)]
      (sendDocumentList manager event (. document append typeKey typeMessage) (. Integer valueOf page)))))

(defn getTypeDocumentListSize [typeKey typeMessage manager event returnCodeConfig]
  (documentNotFoundMessage manager event returnCodeConfig)
  (if (nil? typeMessage) false
    (let [document (new Document)]
      (sendDocumentListSize manager event (. document append typeKey typeMessage)))))

(. ManagerLogic put "control.BlogManager$addDocument" addDocument 6)
(. ManagerLogic put "control.BlogManager$addReply" addReply 4)
(. ManagerLogic put "control.BlogManager$getDocument" getDocument 4)
(. ManagerLogic put "control.BlogManager$addReader" addReader 1)
(. ManagerLogic put "control.BlogManager$getAuthorTypeDocumentList" getAuthorTypeDocumentList 6)
(. ManagerLogic put "control.BlogManager$getTypeDocumentList" getTypeDocumentList 6)
(. ManagerLogic put "control.BlogManager$getAuthorDocumentList" getTypeDocumentList 6)
(. ManagerLogic put "control.BlogManager$getAuthorTypeDocumentListSize" getAuthorTypeDocumentListSize 5)
(. ManagerLogic put "control.BlogManager$getTypeDocumentListSize" getTypeDocumentListSize 5)
(. ManagerLogic put "control.BlogManager$getAuthorDocumentListSize" getTypeDocumentListSize 5)
