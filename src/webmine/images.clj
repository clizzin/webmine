(ns webmine.images
  (:require [clj-http.client :as cl])
  (:use infer.core
        webmine.urls
        webmine.readability
        webmine.parser)
  (:import javax.imageio.ImageIO)
  (:import java.awt.image.BufferedImage)
  (:import java.awt.image.ConvolveOp)
  (:import java.awt.image.Kernel)
  (:import java.awt.RenderingHints))

;;http://www.mkyong.com/regular-expressions/10-java-regular-expression-examples-you-should-know/
;; (defn imgs [t]
;;   (re-seq #"([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)" t))
;;"([^\s]+(\.(?i)(jpg|png|gif|bmp))$)" t))

(defn ints [xs]
  (map #(Integer/parseInt %) xs))

(defn hw? [h w]
  (and h w
       (not (= "" h))
       (not (= "" w))))

(defn to-hw [h w] 
    (if (hw? h w)
      (ints [h w])
      nil))

(defn hw-from-str
  [s]
  (let [[h w] (re-seq #"[0-9]+" s)]
    (to-hw h w)))


(defn hw-from-style
  "in case hight and width are burried inside inline style tag."
  [st]
  (hw-from-str (.getValue st)))


(defn hw-from-tags [h w]
  (to-hw (.getValue h)
	 (.getValue w)))

(defn img? [u]
  (or (.contains u ".jpg")
      (.contains u ".png")
      (.contains u ".gif")
      (.contains u ".bmp")))

(defn img-urls [us] (filter img? us))

;;http://www.w3schools.com/tags/tag_IMG.asp
(defn size [n]
  (if-let [attrs (.getAttributes n)]
    (let [w (.getNamedItem attrs "width")
	  h (.getNamedItem attrs "height")
	  st (.getNamedItem attrs "style")]
      (cond
       (and w h) (hw-from-tags h w)
       st (hw-from-style st)
       :else nil))))

(defn imgs [d] (map (fn [n]
		      {:url (src n)
		       :size (size n)})
		    (elements d "img")))

(defn img-area [i]
  (apply * (:size i)))

(defn big-img [images]
  ;;discard images with no size information.
  (let [is (filter :size images)]
	(max-by img-area is)))

;;example usage
;;(big-img (imgs (dom (:body (cl/get "http://gigaom.com/2010/10/22/whos-driving-mobile-payments-hint-some-are-barely-old-enough-to-drive/")))))

(defn read-img [u]
  (ImageIO/read u))

(defn fetch-img [u]
  (if-let [ur (url u)]
    (read-img ur)))

(defn img-size [u]
  (if-let [i (fetch-img u)]
    [(.getHeight i)
     (.getWidth i)]))

(defn fetch-sizes [imgs]
  (map 
   (fn [i]
     (if-let [s (:size i)]
       i
       (let [s (img-size (:url i))]
	 (assoc i :size s))))     
   imgs))

;;example usage
;;(big-img (fetch-sizes (imgs (dom (:body (cl/get "http://gigaom.com/2010/10/22/whos-driving-mobile-payments-hint-some-are-barely-old-enough-to-drive/"))))))

(defn extract-all [d]
  (flatten
   (map
    #(flatten (map (fn [t] (do-children t identity))
		   (do-children % identity)))
    (divs d))))

(defn big-div [d]
  (max-by (comp count :textContent bean) (extract-all d)))

(defn best-img
  [u content]
  (let [d (dom content)
	;;first try to get the images out of the core body div.
	core-imgs (imgs (readability-div d))
	;;if that we have core images, use those, if not, get all the images in the dom
	target-imgs (if (not (empty? core-imgs))
		      core-imgs
		      (imgs d))
	eis (expand-relative-urls u target-imgs)
	;;ensure we have sizees for all images.
	sizes (fetch-sizes eis)]
    (if (empty? sizes) nil
	;;take the first image we find that has no follow image that is larger than twice it's size.
	(reduce (fn [best next]
		  (if (> (img-area next)
			 (* (img-area best) 2))
		    next
		    best))
		sizes))))

(defn best-img-at
  [u]
  (best-img u (:body (cl/get u))))

(defn with-best-img [m url-key content-key]
  (let [img (try
	         (best-img (url-key m)
	       (content-key m))
		 (catch java.lang.Exception _ nil))]
  (assoc m :img img)))

;;gets a crane:
;;http://measuringmeasures.com/blog/2010/10/11/deploying-clojure-services-with-crane.html

;;gets me:
;;http://measuringmeasures.com/blog/2010/10/21/clojure-key-value-stores-voldemort-and-s3.html

;;image with no size tags, also has later image in core body that is slightly larger, we should get the top image.
;;http://techcrunch.com/2010/10/22/stripon/

;;rolling back to all images when there are none in the body.  image is also relative path to host.
;;http://daringfireball.net/2010/10/apple_no_longer_bundling_flash_with_mac_os_x

;;trick outer div with bigger image for promotion.
;;http://gigaom.com/2010/10/22/whos-driving-mobile-payments-hint-some-are-barely-old-enough-to-drive/
;;http://gigaom.com/2010/10/23/latest-smartphones-reviewed-t-mobile-g2-nokia-n8/

;;RESIZING & CROPPING
;;http://www.componenthouse.com/article-20
;;http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
;;http://www.dreamincode.net/forums/topic/162774-java-image-manipulation-part-2-resizing/
;;http://www.java-tips.org/java-se-tips/javax.imageio/java-2d-api-enhancements-in-j2se-5.0.html

(def hints
     {:bilinear RenderingHints/VALUE_INTERPOLATION_BILINEAR
      :bicubic RenderingHints/VALUE_INTERPOLATION_BICUBIC})

(defn resize [i h w & [hint]]
  (let [hint ((or hint :bilinear)
	      hints)
	old-w (.getWidth i nil)
	old-h (.getHeight i nil)
	bi (BufferedImage. w h BufferedImage/TYPE_INT_RGB)
	bg (.createGraphics bi)]
    (.setRenderingHint bg
		       RenderingHints/KEY_INTERPOLATION
		       hint)
    (.scale bg (/ w old-w) (/ h old-h))
    (.drawImage bg i 0 0 nil)
    (.dispose bg)
    bi))

(defn scale [i h-percent w-percent & [hint]]
  (let [old-w (.getWidth i nil)
	old-h (.getHeight i nil)]
  (resize i (* old-h h-percent) (* old-w w-percent) hint)))

(defn scale-dim [dim new-dim old-dim]
  (* dim (/ new-dim old-dim)))

(defn scale-to
"takes an image and either hight or width.
returns the scaled image, retaining aspect ratio."
[i {h :height w :width} & [hint]]
  (let [old-w (.getWidth i nil)
	old-h (.getHeight i nil)
	height (or h (scale-dim old-h w old-w))
	width (or w (scale-dim old-w h old-h))]
  (resize i height width hint)))

;;TODO: get into a stream for the server API.
(defn save-img [image filename ext]
  (let [f (java.io.File. (str filename "." ext))]
    (try
     (ImageIO/write image ext f)
     (catch java.lang.Exception e 
       (println (.printStackTrace e))))))

;;http://www.exampledepot.com/egs/java.awt.image/Sharpen.html



    ;; private static BufferedImage toBufferedImage(Image src) {
    ;;     int w = src.getWidth(null);
    ;;     int h = src.getHeight(null);
    ;;     int type = BufferedImage.TYPE_INT_RGB;  // other options
    ;;     BufferedImage dest = new BufferedImage(w, h, type);
    ;;     Graphics2D g2 = dest.createGraphics();
    ;;     g2.drawImage(src, 0, 0, null);
    ;;     g2.dispose();
    ;;     return dest;
    ;; }


;;Filters
;;http://www.jhlabs.com/ip/filters/index.html

;;sharpen
;;contrast (might not work well for screenshots)
;;white balance
;;white color balance
(defn kernel [image]
  (let [kernel (ConvolveOp. (Kernel. 3, 3,
			(float-array [-1, -1, -1, -1, 9, -1, -1, -1, -1])))]
    (.filter kernel image nil)))