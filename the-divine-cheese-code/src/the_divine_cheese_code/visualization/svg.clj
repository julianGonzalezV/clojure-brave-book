(ns the-divine-cheese-code.visualization.svg
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [min max]))
; el  :exclude [min max] , se ignora del core de clojure porque vamos a tener los nuestros, MEJOR AUN LO VAMOS A SOBREESCRIBIR, usando la funcion del core pero en nuestra propia fincion que escribamos de min max (ver line 28 y 29 )




;esta prueba no es correcta porque la funcion no recibe un vector sino un solo elementos tipo Map 
;(latlng->point [{:lat 80 :lng 70}  {:lat 60 :lng 50}])

;Esta si funciona :)
; en donde la funcion recibe el Map y retorna el string sesparado por comas

(defn points
  [locations]
  (clojure.string/join " " (map latlng->point locations)))
(points [{:lat 80 :lng 70}  {:lat 60 :lng 50}])


 (defn comparator-over-maps
  [comparison-fn ks]
  (fn [maps]
    (zipmap ks
             (map (fn [k] (apply comparison-fn (map k maps)))
                 ks))))

;desglosando la funcion 
(map clojure.core/min [{:a 1 :b 3} {:a 5 :b 0}])

(map (fn [k] (apply clojure.core/min  (map k {:a 1 :b 3} ))) [:lat :lng])

(def min (comparator-over-maps clojure.core/min [:lat :lng]))
(def max (comparator-over-maps clojure.core/max [:lat :lng]))

(clojure.core/min 1 2 3)

(max [{:a 1 :b 3} {:a 5 :b 0}])

(min [{:a 1 :b 3} {:a 5 :b 0}])
; => {:a 1 :b 0}

;Los elementos del primer vector son los Keys del segundo
(clojure.core/zipmap [:a :b ] [1 2])
; => {:a 1 :b 2}


 (defn translate-to-00
  [locations]
  (let [mincoords (min locations)]
    (map #(merge-with - % mincoords) locations)))

 (defn scale
  [width height locations]
  (let [maxcoords (max locations)
        ratio {:lat (/ height (:lat maxcoords))
               :lng (/ width (:lng maxcoords))}]
    (map #(merge-with * % ratio) locations)))

(merge-with - {:lat 50 :lng 10} {:lat 5 :lng 5})
; => {:lat 45 :lng 5}



(defn latlng->point
  "Convert lat/lng map to comma-separated string" 
  [latlng]
  (str (:lat latlng) "," (:lng latlng)))

(defn points
  "Given a seq of lat/lng maps, return string of points joined by space"
  [locations]
  (s/join " " (map latlng->point locations)))

(latlng->point {:lat 60 :lng 50})
;"80,70"


(defn line
  [points]
  (str "<polyline points=\"" points "\" />"))

(defn transform
  "Just chains other functions"
  [width height locations]
  (->> locations
       translate-to-00
       (scale width height)))

(defn xml
  "svg 'template', which also flips the coordinate system"
  [width height locations]
  (str "<svg height=\"" height "\" width=\"" width "\">"
       ;; These two <g> tags change the coordinate system so that
       ;; 0,0 is in the lower-left corner, instead of SVG's default
       ;; upper-left corner
       "<g transform=\"translate(0," height ")\">"
       "<g transform=\"rotate(-90)\">"
       (-> (transform width height locations)
           points
           line)
       "</g></g>"
       "</svg>"))
