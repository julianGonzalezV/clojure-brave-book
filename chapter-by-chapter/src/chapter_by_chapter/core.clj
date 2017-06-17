(ns chapter-by-chapter.core
  (:gen-class)


)

(defn -main

  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(+ 1 2 (* 3 2)  4)

; => 10
(map inc [1 2 3 4])
; => (2 3 4 5)
(reduce + [5 6 100])
; => 111

(+ 1 7)

(if true
  (do (println "Success!")
      "By Zeus's hammer!")
  (do (println "Failure!")
      "By Aquaman's trident!"))

(get [ 4 5 6] 0)


;Definir variables
(def variable1 "julian")
(println variable1)

;ejercicio
(def flagJulian true)
(def pre-error-message "El error es :" )
(def error-message (if flagJulian
                     (str pre-error-message "verdadero")
                     (str pre-error-message "falso")))
(println error-message)

;Ahora hacer el mismo ejercicio pero en una funcion

(defn error-message1 
  [flag];parameters
  ;Body
  (str "El error es " 
       (if (= flag :tr) 
         "verdadero"
         "falso")))

(println  (error-message1 :tr))
;:tr en este caso es el parametero que se le envía

;Maps
;como function
({:a 1 :b 2} :b)

;Como KEYWORDS
(:b {:a 1 :b 3})

;y SI NO EXISTE ENTONCES DEFAULT 
(:c {:a 1 :b 3} "nanay")

;funciones

; Sobrecarga de una funcion
(defn x-chop
  "Describe the kind of chop you're inflicting on someone"
  ([name chop-type]
     (str "I will " chop-type " chop " name "! Take that!"))
  ([name]
     (x-chop name "karate")))

;Si se llama con sus 2 parametros todo ok 
(x-chop "lo que sea" "juli")
;R/"I will juli chop lo que sea! Take that!"

;y si se llama con 1 entonces pa especificacion para ello 
(x-chop "platanito")
;R/ "I will karate chop platanito! Take that!"
; Note que la sobrecarga lo que hace es llamar al la funcion x-chop de nuevo con name (aridad 1) y le agrega el parametro faltante "karate"

;funcion que hace algo especifico
(defn muestro-mensaje
  [entrada]
  (str "hola sr " entrada))

(defn aplico-func-por-entradas 
  [& muchas-entradas]
  (map muestro-mensaje muchas-entradas))

(aplico-func-por-entradas "Merly" "julito" "luisinio")
;R/ ("hola sr Merly" "hola sr julito" "hola sr luisinio")



;;Destructuring:Basicamente es ligar nombres a los valores de una collection


;; Return the first element of a collection
(defn my-first
  [[first-thing la-dos]] ; Notice that first-thing is within a vector
  first-thing)

(my-first ["oven" "bike" "war-axe"])
; => "oven"



;Se retorna the last line !
(defn illustrative-function
  []
  (+ 1 304)
  30
  "joe")

(illustrative-function)
;R/ joe

;Funciones anonimas 
;En este caso la funcion anonima es la fn tal que al recibir el parametro name lo que hace en concatenar al inicio con el "Hi"

(map (fn [name] (str "Hi, " name))
     ["Darth Vader" "Mr. Magoo" "juliano" "clarita"])
; => ("Hi, Darth Vader" "Hi, Mr. Magoo")

(map (fn [x] (* x 3)) [8 3 4])

((fn [x] (* x 3)) 8)
; => 24

;;Ahora algo de azucar sintactico en funciones anonimas
(map #(* % 3) [3 4 5])
; => 24
;Here’s an example of passing an anonymous function as an argument to map:

(map #(str "Hi, " %)
     ["Darth Vader" "Mr. Magoo"])

         
;------------------------------------
;Forma de decir que que de todos los parametros que lleguen coja el primero y el 2do y los concatene, además dice que la fucion es de dos parametros}
(#(str %1 " and " %2 " y " %&) "cornbread" "butter beans" "jose" "maria")
; => "cornbread and butter beans"
;You can also pass a rest parameter with %&:

(#(identity %&) 1 "blarg" :yip [1 2 3] 4 5 6)
; => (1 "blarg" :yip)

; EL estilo de #(.. es aplicable cuando sea una funcion anonima simple , sí requiere unas implementaciones grande estonces use fn

;Returning Functions
(defn inc-maker
  "Create a custom incrementor"
  [inc-by]
  #(+ % inc-by))
; La funcion que se retorna es un closure, lo que significa que 
;esta puede acceder a todas las variables creadas en el alcanse 
;note como inc-by es accedido en la funcion anonima que se retorna, SE PUEDE PREGUNTAR A EDWARD :) 

(def inc3 (inc-maker 3))

(inc3 7)
; => 10}

(defn inc-by-input [init-value] #(+ % init-value))
(def inc100 (inc-by-input 100))
(inc100 30)

;Pulling It All Together

;Creando Modelo hobbit:


;uso del into , VER COMO :a solo queda una sola vez en el vector resultante
(into [] 
      (set [:a :a]))


;Uso del let:
;relaciona el name x con el valor 3
(let [x 3]
  x)
; => 3


;o para listas
(def dalmatian-list
  ["Pongo" "Perdita" "Puppy 1" "Puppy 2"])
(let [dalmatians (take 2 dalmatian-list)]
  dalmatians)
; => ("Pongo" "Perdita")

(def x 0)
(let [x 1] x)
(let [x 2] x)
; => 1

(def x 4)
(println x)
;en este ejemplo Dentro de los limites del let x es 5 
;ver que let tiene la  forma (let [var new-asign] into-var)
;lo leo como let recibe un var y el valor a asignar new-assign(puede
;ser una funcion etc) 
(let [x (inc x)] x)
(println x);sigue siendo 4, solo lo cambia para el contexto/alcance 
;del let
; => 1

;La siguiente los que hace es que  xs1mero lo liga al primer valor de la lista dalmatian-list y el resto a dalmatians
;en la linea final lo que hace es retornar un vector en donde el primer elemento es un valor y el segundo es una lista 
(let [[xs1mero & dalmatians] dalmatian-list]
  [ xs1mero dalmatians])
; => ["Pongo" ("Perdita" "Puppy 1" "Puppy 2")]


;usando loop 
;el recur es propio de clojure que realiza el llamado recursivo a la 
;funcion 
(loop [counter 0]
      (println (str "voy en " counter))
      (if (> counter 3)
        (println " chao")
        (recur  (inc counter))))


;Tambien podemos crear nuetra version de recursividad, pero es mas verbose
;por ejemplo:
(defn mi-loop 
  ([]
   (mi-loop 0))
  ([counter]
   (println (str "mi loop " counter ))
   (if (> counter 3)
     (println "chao")
     (mi-loop (inc counter)))))

(mi-loop)



;Regular Expressions
;el re-find recibe el pattern y el string al cual le desea 
;;realizar la verificación, si cumple entonces retorna el 
;patron sino retorna nil
(re-find #"^left-" "left-eye")
; => "left-"

(re-find #"^left-" "cleft-chin")
; => nil

(re-find #"^left-" "wongleblart")
; => nil


(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})
(matching-part {:name "left-eye" :size 1})
;acá si se ve claramente el reemplzo por keyword
; => {:name "right-eye" :size 1}]

(matching-part {:name "head" :size 3})
;como no encuentra el patron entonces el resultado es el 
;;mismo que el de entrada => {:name "head" :size 3}



;;Ahora si el ejemplo 
(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])

;;Toda la parte derecha esta desaparecida 


(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
;En la sgte linea el [] del loop son los parametros que recibe 
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      ;Aqui dice que cree un nuevo scope en donde part va a tener el 
;primer elemento o cabeza o head de remaining-asym-parts 
;y obviamente remaining el resto
      (let [[part & remaining] remaining-asym-parts]
;se inicia a recorrer remaining 
        (recur remaining
;Las siguientes dos lineas se leen : use un set para crear un conjunto de 
;part y matching part y el SET resultante guardelo en final-body-parts
;set no permite duplicados
               (into final-body-parts
                     (set [part (matching-part part)])))))))


(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
;En la sgte linea el [] del loop son los parametros que recibe 
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      ;Aqui dice que cree un nuevo scope en donde part va a tener el 
;primer elemento o cabeza o head de remaining-asym-parts 
;y obviamente remaining el resto
      (let [[part & remaining] remaining-asym-parts]
;se inicia a recorrer remaining 
        (recur remaining
;Las siguientes dos lineas se leen : use un set para crear un conjunto de 
;part y matching part y el SET resultante guardelo en final-body-parts
;set no permite duplicados
               (into final-body-parts
                     (set [part (matching-part part)])))))))

;;prueba
(symmetrize-body-parts asym-hobbit-body-parts)

(defn symmetrize-body-parts2
  [asym-body-parts] 
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))


;EN lo único que me estaba confundiendo era en la última línea
;(set [part (matching-part part)]...
;lo que pasa allí es que en el primer si [part..
;es igual a (matching-part part) entonces por ser un set no se repite 
(symmetrize-body-parts2 asym-hobbit-body-parts)


[{:name "head", :size 3} {:name "left-eye", :size 1} {:name "right-eye", :size 1} {:name "left-ear", :size 1} {:name "right-ear", :size 1} {:name "mouth", :size 1} {:name "nose", :size 1} {:name "neck", :size 2} {:name "left-shoulder", :size 3} {:name "right-shoulder", :size 3} {:name "right-upper-arm", :size 3} {:name "left-upper-arm", :size 3} {:name "chest", :size 10} {:name "back", :size 10} {:name "left-forearm", :size 3} {:name "right-forearm", :size 3} {:name "abdomen", :size 6} {:name "left-kidney", :size 1} {:name "right-kidney", :size 1} {:name "left-hand", :size 2} {:name "right-hand", :size 2} {:name "right-knee", :size 2} {:name "left-knee", :size 2} {:name "right-thigh", :size 4} {:name "left-thigh", :size 4} {:name "right-lower-leg", :size 3} {:name "left-lower-leg", :size 3} {:name "right-achilles", :size 1} {:name "left-achilles", :size 1} {:name "right-foot", :size 2} {:name "left-foot", :size 2}]

;{:name "left-lower-leg", :size 3}

;Better Symmetrizer with reduce

; el patron de recorrer una estructura y construir un resultado se abstrae con la función reduce de clojure.Ejemplo

(reduce + [1 3 4 5])
;13

;OJO AUNQUE EN ESTE EJERCICIO EL RESULTADO ES UN SOLO NÚMERO EN LA PRÁCTICA MUCHAS VECES HASTA DEVUELTE 
;COLLECCIONES MAS GRANDES QUE LA QUE SE LE ENTREGÓ AL REDUCE INICIALMENTE.
(reduce + 15 [1 3  4] )
;23

(first ["a" 4 5])
;"a"
(rest  ["a" 4 5] )
;(4 5)



(defn better-symmetrize-body-parts-reduce
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (into final-body-parts (set [part (matching-part part)])))
          []
          asym-body-parts))


;;Lo siguiente hace lo mismo del symmetrize-body-part arriba y mucho mas corto
 (better-symmetrize-body-parts-reduce   asym-hobbit-body-parts)



;;Definiendo la funcion hit que indica la parte del hobbit que se
;debe  golpear, selecciona

(defn hit
  [asym-body-parts]
; en sym-parts queda el resultado de la plicar la funcion bsbp a la 
;entrada asym-body-parts
  (let [sym-parts (better-symmetrize-body-parts-reduce asym-body-parts)
        body-part-size-sum (reduce + (map :size sym-parts))
        target (rand body-part-size-sum)]
    (loop [[part & remaining] sym-parts
           accumulated-size (:size part)]
      (if (> accumulated-size target)
        part
;recurdar que recur es el llamado recursivo a la misma funcion loop en ;; donde me encuentro 
        (recur remaining (+ accumulated-size (:size (first remaining))))))))


(hit asym-hobbit-body-parts){:name "left-upper-arm", :size 3}

;Exercises:
;1)Use the str, vector, list, hash-map, and hash-set functions.
;2).
;write a function that takes a number and adds 100 to it.
;Solution:

(defn inc-by-input [init-value] #(+ % init-value))
(def inc100 (inc-by-input 100))
(inc100 30)

(defn op-on-number
  [f number]
  (map f [number]))



(op-on-number (fn [x] (+ 100 x)) 3)
(op-on-number (fn [x] (* 100 x)) 3)


;3) Write de nex function (dec9 10) => 1 

(defn dec9 
  [number]
  (op-on-number (fn [x] (- x 9)) number)
   )

(dec9 10)


; 4) Write a function, mapset, that works like map except the return value is a set:
;(mapset inc [1 1 2 2])
; => #{2 3}}}


(defn mapset
  [f parameters]
  (set (map f parameters)))

(mapset inc [1 1 2 2])

;5) Create a function that’s similar to symmetrize-body-parts except that it has to work with weird space aliens with radial symmetry. Instead of two eyes, arms, legs, and so on, they have five.






;6) Create a function that generalizes symmetrize-body-parts and the function you created in Exercise 5. The new function should take a collection of body parts and the number of matching body parts to add. If you’re completely new to Lisp languages and functional programming, it probably won’t be obvious how to do this. If you get stuck, just move on to the next chapter and revisit the problem later.

