(ns
  ^{:author xlo}
  control.BlogManagerLogic
  (:import [model.db BlogCollection MarkUserCollection MessageCollection UserCollection]
           [org.bson Document BsonArray BsonDocument BsonDateTime BsonString]
           [model.config LengthLimitConfig ConfigManager ReturnCodeConfig ConstConfig]
           [java.util Date LinkedList Comparator]
           [control ManagerLogic BlogManager]
           [java.text SimpleDateFormat]))

(defn document-compare [o1 o2]
  (let [date1 (. (. o1 object) get "time")
        date2 (. (. o2 object) get "time")]
    (if (. date1 after date2) -1 (if (. date2 after date1) 1 0))))

(defn sendAimList [aimList page manager event]
  (let [onePageSize (. (. ConstConfig getConfig) getConst "blog page size")
        left (* (- page 1) onePageSize)
        right (if (> (+ left onePageSize) (. aimList size)) (. aimList size) (+ left onePageSize))]
    (if (<= left right) (do (. manager addSuccessMessage event (. aimList subList left right)) true)
      false)))

(defn sendAimListSize [aimList onePageSize manager event]
  (let [pageSize (max 1 (+ (int (/ (count aimList) onePageSize)) (if (= 0 (rem (count aimList) onePageSize)) 0 1)))]
    (. manager addSuccessMessage event (str "{\"return\":" pageSize "}")) true))

(defn sendDocumentList [manager event message from to page]
  (let [aimList (sort document-compare (vec (. (new BlogCollection) findDocumentListData message from to)))
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
    (sendAimList ans page manager event)))

(defn sendDocumentListSize [manager event message from to]
  (let [onePageSize (. (. ConstConfig getConfig) getConst "blog page size")
        aimList (vec (. (new BlogCollection) findDocumentListData message from to))]
    (sendAimListSize aimList onePageSize manager event)))

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

(defn getReply[documentID page manager event returnCodeConfig]
  (documentNotFoundMessage manager event returnCodeConfig)
  (if (or (nil? documentID) (nil? page)) false
    (let [data (. (new BlogCollection) getDocumentData documentID)]
      (if (nil? data) false
        (let [object (. (. data object) get "reply")]
          (sendAimList object (. Integer valueOf page) manager event))))))

(defn getReplySize [documentID manager event returnCodeConfig]
  (documentNotFoundMessage manager event returnCodeConfig)
  (if (nil? documentID) false
    (let [data (. (new BlogCollection) getDocumentData documentID)]
      (if (nil? data) false
        (let [object (. data object)]
          (sendAimListSize (. object get "reply") manager event))))))

(defn getDocument [id manager event returnCodeConfig]
  (documentNotFoundMessage manager event returnCodeConfig)
  (if (nil? id) false
    (let [data (. (new BlogCollection) getDocumentData id)]
      (if (nil? data) false
        (let [object (. data object)
              message (do (. object toJson))]
          (. manager addSuccessMessage event message) true)))))

(defn addReader [id]
  (let [data (. (new BlogCollection) getDocument id)]
    (if (nil? data) false
      (let [object (. data object)
            val (+ (. object getInteger "reader" 0) 1)]
        (. object put "reader" (int val)) true))))

(defn getDocumentList [author type from to page manager event]
  (if (or (nil? author) (nil? type) (nil? from) (nil? to)) false
    (let [document (new Document)]
      (if (not= author "null") (. document append "author" author))
      (if (not= type "null") (. document append "type" type))
      (let [dateFormat (new SimpleDateFormat "yyyy-MM-dd HH:mm:ss")]
        (let [fromDate (if (not= from "null") (new Date (. Long valueOf from)) (new Date 0))
              toDate (if (not= from "null") (new Date (. Long valueOf to)) (new Date))]
          (sendDocumentList manager event document fromDate toDate (. Integer valueOf page)))))))

(defn getDocumentListSize [author type from to manager event]
  (if (or (nil? author) (nil? type) (nil? from) (nil? to)) false
    (let [document (new Document)]
      (if (not= author "null") (. document append "author" author))
      (if (not= type "null") (. document append "type" type))
      (let [dateFormat (new SimpleDateFormat "yyyy-MM-dd HH:mm:ss")]
        (let [fromDate (if (not= from "null") (new Date (. Long valueOf from)) (new Date 0))
              toDate (if (not= from "null") (new Date (. Long valueOf to)) (new Date))]
          (sendDocumentListSize manager event document fromDate toDate))))))


(. ManagerLogic put "control.BlogManager$addDocument" addDocument 6)
(. ManagerLogic put "control.BlogManager$addReply" addReply 4)
(. ManagerLogic put "control.BlogManager$getDocument" getDocument 4)
(. ManagerLogic put "control.BlogManager$addReader" addReader 1)
(. ManagerLogic put "control.BlogManager$getDocumentList" getDocumentList 7)
(. ManagerLogic put "control.BlogManager$getDocumentListSize" getDocumentListSize 6)
(. ManagerLogic put "control.BlogManager$getReply" getReply 5)
(. ManagerLogic put "control.BlogManager$getReplySize" getReplySize 4)
