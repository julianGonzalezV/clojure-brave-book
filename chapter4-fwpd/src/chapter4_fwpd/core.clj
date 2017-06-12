;establishe the namespace, tartar de cambiar a ver si falla :)
(ns chapter4-fwpd.core)
;named value para referirnos al archivo, el el caso se ser una ruta larga es mejor :)
(def filename "suspects.csv")
(slurp filename)

; Named value para la secuencia de maps tipo k-v que se leera del archivo
(def vamp-keys [:name :glitter-index])


(defn str->int
"return an Integer giver a String value"
  [str]
  (Integer. str))

; Named value para definir el map de conversion 
; a ;name le aplica la funcion identity lo que significa que devolverá lo mismo
; a :glitter-index le aplica la funcion definica arriba que transforma de str a Int
(def conversions {:name identity
                  :glitter-index str->int})


(defn convert
"Given two arguments vamp-key and value return the converted value"
  [vamp-key value]
  ((get conversions vamp-key) value))

;para evitar lo siguiente entonces por eso creamos la funcion conver 
((get conversions :glitter-index) "1")

; y simplemente llamarlo así:
(convert :glitter-index "3")
; => 3




;:::::::::Leer y explicar
;parse recibe un string y :
;1) le hace split por ENTER, EL RESULTADO ES UNA LISTA o Seq en donde cada valor es una linea del archivo
;2) Le hace un split por coma y el resultado es un vector de 2 posiciones el nombre y el value

; se lee en ese orden  porque la funcion retorna el map de split de COMA del resultado de hace split POR ENTER
(defn parse
  "Convert a CSV into rows of columns"
  [string]
  (map #(clojure.string/split % #",")
       (clojure.string/split string #"\n")))
; example:
(parse "Edward Cullen,10\nBella Swan,0\nCharlie Swan,0\nJacob Black,3\nCarlisle Cullen,6\n")
;(["Edward Cullen" "10"] ["Bella Swan" "0"] ["Charlie Swan" "0"] ["Jacob Black" "3"] ["Carlisle Cullen" "6"])

;slurp lee el archivo y retorna un string con el contenido del archivo 
(slurp filename)
;"Edward Cullen,10\nBella Swan,0\nCharlie Swan,0\nJacob Black,3\nCarlisle Cullen,6\n"

; example?
(parse (slurp filename))
; => (["Edward Cullen" "10"] ["Bella Swan" "0"] ["Charlie Swan" "0"] ["Jacob Black" "3"] ["Carlisle Cullen" "6"])


(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-index 10}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [vamp-key value]]
                   (assoc row-map vamp-key (convert vamp-key value)))
                 {}
                 (map vector vamp-keys unmapped-row)))
       rows))



(first (mapify (parse (slurp filename))))
; => {:glitter-index 10, :name "Edward Cullen"}


(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) records))

(glitter-filter 3 (mapify (parse (slurp filename))))
({:name "Edward Cullen", :glitter-index 10}
 {:name "Jacob Black", :glitter-index 3}
 {:name "Carlisle Cullen", :glitter-index 6})
