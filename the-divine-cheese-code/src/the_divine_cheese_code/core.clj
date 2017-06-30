;ns se conoce como un MACRO que incorpora varias funcionalidades/librerías al namespacr 
(ns the-divine-cheese-code.core
  (:require [clojure.java.browse :as browse]
            [the-divine-cheese-code.visualization.svg :refer [xml]])
  (:gen-class))

;si colocamos esta línea entonces al hacer lesin run desde la carpeta raiz del proyecto va a decir que no reconoce println o habría que colocar en todas parte donde se use el clojure.core/println
;(:refer-clojure :exclude [println])


;por lo cual es uso de ns es mas adecuado, lo anterior es como escribir :
;(in-ns 'the-divine-cheese-code.core)
;(refer 'clojure.core :exclude ['println]) se ahorra cierto :) 

;; Ensure that the SVG code is evaluated
;(require 'the-divine-cheese-code.visualization.svg)
;; Refer the namespace so that you don't have to use the 
;; fully qualified name to reference svg functions
;el namespace está en ../src/the_divine_cheese_code/visualization
;(refer 'the-divine-cheese-code.visualization.svg)

(def heists [{:location "Cologne, Germany"
              :cheese-name "Archbishop Hildebold's Cheese Pretzel"
              :lat 50.95
              :lng 6.97}
             {:location "Zurich, Switzerland"
              :cheese-name "The Standard Emmental"
              :lat 47.37
              :lng 8.55}
             {:location "Marseille, France"
              :cheese-name "Le Fromage de Cosquer"
              :lat 43.30
              :lng 5.37}
             {:location "Zurich, Switzerland"
              :cheese-name "The Lesser Emmental"
              :lat 47.37
              :lng 8.55}
             {:location "Vatican City"
              :cheese-name "The Cheese of Turin"
              :lat 41.90
              :lng 12.45}])


(defn url
  [filename]
  (str "file:///"
       (System/getProperty "user.dir")
       "/"
       filename))

(defn template
  [contents]
  (str "<style>polyline { fill:none; stroke:#5881d8; stroke-width:3}</style>"
       contents))


;Note como se llama a point sin necesidad de indicarle el namespace completo + /function sino que se llamara como si fuera del namespace actual (como analogía a los imports de java)
; este es el entry point del aplicativo, vaya a la raiz y ejecute lein run y el programa estra aca

(defn -main
  [& args]
  (let [filename "map.html"]
    (->> heists
         (xml 50 100)
         template
         (spit filename))
    (browse/browse-url (url filename))))
;Contrario a slurp , spit escribe un archivo con la informacion que se le pase 

; url lo que hace es escribir la ruta completa hasta la raiz del proyecto 
(url "hla")
;"file:////home/julian/Documents/study/clojure/clojure-brave-book/the-divine-cheese-code/hla"

; finalmemte con browse/browse-url se despliega o se lanza al navegador el contenido
