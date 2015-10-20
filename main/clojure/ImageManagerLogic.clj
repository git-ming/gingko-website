(ns
  ^{:author xlo}
  control.ImageManagerLogic
  (:import [model.db ImageCollection]
           [control ManagerLogic]
           [model.cache ImageCacheManager]))

(defn uploadImage [type data manager event]
  (let [path (. (new ImageCollection) insert data type)]
    (do (. manager addSuccessMessage event (str "{\"return\":\"" path "\"}")) true)))

(defn getImage [path manager event]
  (let [fileObject (. (new ImageCollection) find path)]
    (do (. (. ImageCacheManager getImageCacheManager) cache fileObject) (. manager addSendFile event (. fileObject getPath)) true)))

(. ManagerLogic put "control.ImageManager$uploadImage" uploadImage 4)
(. ManagerLogic put "control.ImageManager$getImage" getImage 3)