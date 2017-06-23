;ns se conoce como un MACRO que incorpora varias funcionalidades/librerías al namespacr 
(ns the-divine-cheese-code.core
  (:gen-class)
;si colocamos esta línea entonces al hacer lesin run desde la carpeta raiz del proyecto va a decir que no reconoce println o habría que colocar en todas parte donde se use el clojure.core/println
;(:refer-clojure :exclude [println])
)

;por lo cual es uso de ns es mas adecuado, lo anterior es como escribir :
;(in-ns 'the-divine-cheese-code.core)
;(refer 'clojure.core :exclude ['println]) se ahorra cierto :) 

;; Ensure that the SVG code is evaluated
(require 'the-divine-cheese-code.visualization.svg)
;; Refer the namespace so that you don't have to use the 
;; fully qualified name to reference svg functions
;el namespace está en ../src/the_divine_cheese_code/visualization
(refer 'the-divine-cheese-code.visualization.svg)

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

;Note como se llama a point sin necesidad de indicarle el namespace completo + /function sino que se llamara como si fuera del namespace actual (como analogía a los imports de java)
(defn -main
  [& args]
  (println (points heists)))
