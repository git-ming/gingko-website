(ns
  ^{:author xlo}
  control.ZanManagerLogic
  (:import [model.db ZanCollection]
           [control ManagerLogic]))

(defn zan[username id]
  (if (or (nil? username) (nil? id)) false
    (let [zanCollection (new ZanCollection)
          data (. zanCollection getZan username id)]
      (if (nil? data) (do (. zanCollection addZanDocuemnt username id) true)
        false))))

(defn noZan[username id]
  (if (or (nil? username) (nil? id)) false
    (let [zanCollection (new ZanCollection)
          data (. zanCollection getZan username id)]
      (if (nil? data) false
        (do (. zanCollection removeZan username id) true)))))

(defn isZan[username id]
  (if (or (nil? username) (nil? id)) false
    (let [zanCollection (new ZanCollection)
          data (. zanCollection getZan username id)]
      (if (nil? data) false
        (do (. zanCollection removeZan username id) true)))))

(. ManagerLogic put "control.ZanManager$zan" zan 2)
(. ManagerLogic put "control.ZanManager$noZan" noZan 2)
(. ManagerLogic put "control.ZanManager$isZan" isZan 2)