(ns the-divine-cheese-code.visualization.svg
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [min max]))
; el  :exclude [min max] , se ignora del core de clojure porque vamos a tener los nuestros, MEJOR AUN LO VAMOS A SOBREESCRIBIR, usando la funcion del core pero en nuestra propia fincion que escribamos de min max (ver line 28 y 29 )





(defn latlng->point
  "Convert lat/lng map to comma-separated string" 
  [latlng]
  (str (:lat latlng) "," (:lng latlng)))


;esta prueba no es correcta porque la funcion no recibe un vector sino un solo elementos tipo Map 
;(latlng->point [{:lat 80 :lng 70}  {:lat 60 :lng 50}])

;Esta si funciona :)
; en donde la funcion recibe el Map y retorna el string sesparado por comas


(defn points
  [locations]
  (clojure.string/join " " (map latlng->point locations)))
(points [{:lat 80 :lng 70}  {:lat 60 :lng 50}])


;Entendiendo la funcion comparator-over-maps:
;1) recibe dos argumentos I una funcion que compara y II) un la lista de keys , para el ejemplo [:lat :lng]
;2) finalmente la funcion RETORNA OTRRA FUNCION fn que recibe un maps/listado (SE LE PASA EN EL MOMENTO DE DECIR (min locations)) de Maps de locations ejemplo el heists de  the-divine-cheese-code.core
; la funcion retornada en el punto 2 hace un map sobre ks en donde cada key k a su vez 
; se usa en (map k maps) algo asi como hacer (map :lng  [{:lat 1 :lng 3} {:lat 5 :lng 0}] ) ln es el key
; lo anterior retorna un seq de todas las key existesnte  en este ejemplo retorna (3 0); luegoal resultado anterior le aplica comparison-fn (clojure.core/min o clojure.core/mmax), si por rejemplo es min el resultad es 0 
; para lng y 1 para lat 
;Como resultado cuando se llega a zip se tiene algo as[i como (clojure.core/zipmap [:lat :lng ] [1 0])
;cuyo resultado es {:lat 1 :lng 0} para min y {:lat 5 :lng 3} para max , para el caso en que la entrada fuera
; [{:lat 1 :lng 3} {:lat 5 :lng 0}]
 (defn comparator-over-maps
  [comparison-fn ks]
  (fn [maps]
    (zipmap ks
             (map (fn [k] (apply comparison-fn (map k maps)))
                 ks))))


(map :lng  [{:lat 1 :lng 3} {:lat 5 :lng 0}] )
;(3 0)

(map :lat  [{:lat 1 :lng 3} {:lat 5 :lng 0}] )
;(1 5)
(map #( * 2 %) [1 2 3])
;(2 4 6)

;desglosando la funcion 
(map clojure.core/min [{:a 1 :b 3} {:a 5 :b 0}])
(min [{:lat 1 :lng 3} {:lat 5 :lng 0}])
;{:lat 1, :lng 0}

;Otro desgloce
(map (fn [k] (apply clojure.core/min  (map k [{:lat 1 :lng 3} {:lat 5 :lng 0}] ))) [:lat :lng])
;(1 0)


;tanto min como max son Named Values que son igual lo que retorne la funcion comparator-over-maps
;para lo cual sus entradas [comparison-fn ks] ser[ian el [clojure.core/min   [:lat :lng]] y el map que se le pase
;en donde el primero es la funcion que se va a aplicae y el segundo es la lista de keys sobre las qiue se
;va a iterar
(def min (comparator-over-maps clojure.core/min [:lat :lng]))
(def max (comparator-over-maps clojure.core/max [:lat :lng]))



(clojure.core/min 1 2 3)



;Los elementos del primer vector son los Keys del segundo
(clojure.core/zipmap [:a :b ] [1 2])
; => {:a 1 :b 2}

;Importantisimo entender como en ese momento en que llamamos a  (min locations) realmente le estamos pasando
; a comparator-over-maps el maps en el argumento de entrada de la funcion fn[maps]
; por lo cual a cada posicion de locations le resta lo que esta en mincoords
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

(/ 20 5)
:4

;merge-wit es una funcion propia del core de clojure que recibe una funcion en este caso el + o el - 
; y lo opera con los Key-values de una secuencya de maps y retorna un solo Map
(merge-with - {:lat 50 :lng 10} {:lat 5 :lng 5})
; => {:lat 45 :lng 5}

(merge-with + {:lat 50 :lng 10} {:lat 5 :lng 5})
;{:lat 55, :lng 15}



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
