(ns chapter-by-chapter.4_core_functions_in_depth
  (:gen-class)


)

(defn -main

  ;"I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


;::::::::::Programming to Abstractions, examples:

;Treating Lists, Vectors, Sets, and Maps as Sequences
(print (+ 1 2))

(defn titleize
  [topic]
  (str topic " for the Brave and True"))



;map on vector
(map titleize ["Hamsters" "Ragnarok"])
; => ("Hamsters for the Brave and True" "Ragnarok for the Brave and True")

; map on sequence
(map titleize '("Empathy" "Decorating"))
; => ("Empathy for the Brave and True" "Decorating for the Brave and True")


;map on SEts
(map titleize #{"Elbows" "Soap Carving"})
; => ("Elbows for the Brave and True" "Soap Carving for the Brave and True")

;anonymous function’s argument before title-izing it because the argument is a map
(map #(titleize (second %)) {:uncomfortable-thing "Winking"})
; => ("Winking for the Brave and True")


; Seq siempre siempre retorna un valor que se comporta como 
;una lista, ver los siguientes cuatro ejemplo

(seq '(1 2 3))
; => (1 2 3)

(seq [1 2 3])
; => (1 2 3)

(seq #{1 2 3})
; => (1 2 3)


; the seq of a map consists of two-element key-value vectors. That’s why map treats your maps like lists of vectors!
; execute the snipped code an try by yourselft
(seq {:name "Bill Compton" :occupation "Dead mopey guy"})
; => ([:name "Bill Compton"] [:occupation "Dead mopey guy"])


;You can convert the seq back into a map by using into
(into {} (seq {:a 1 :b 2 :c 3}))
; => {:a 1, :c 3, :b 2}

;In general, programming to abstractions gives you power by letting you use libraries of functions on different data structure regardless of how those data structures are implemented.



;:::::::::::::
;Seq Function Examples::


; 1) map function

; On one Collectin
(map inc [1 2 3])
; => (2 3 4)

; oN TWO COLLECTIONS, the str function is the concatenatio op
; En este caso el primer elemento del primer vector se le pasa como argumento a la funcion str; el primer argumento del segundo vector se le pasa como segundo parametro a la funcion Str (o cualquier funcion aplicada)
; Solo debemos asegurar que la funcion de mapeo(en este caso str) soporta la 
;cantidad de elementos que le estamos pasando
;
(map str ["a" "b" "c" "d"] ["A" "B" "C" "D"])

; lo anterior es lo mismo que 
(list (str "a" "A") (str "b" "B") (str "c" "C") (str "d" "D"))


; O algo mas elaborado:

;Listado en 
;definicoin de atributos
(def human-consumption   [8.1 7.3 6.6 5.0])
(def critter-consumption [0.0 0.2 0.3 1.1])
(defn unify-diet-data
  "Recibe los dos parametros y los usa como value del map deseado"
  [human critter]
  {:human human
   :critter critter})

;aplica la funcion unify-diet-data a las listas human-consumption y critter-consumption
(map unify-diet-data human-consumption critter-consumption)
; => ({:human 8.1, :critter 0.0}
;      {:human 7.3, :critter 0.2}
;      {:human 6.6, :critter 0.3}
;      {:human 5.0, :critter 1.1}



; O tambien pasar varias operaciones a la lista no solo una como las que hemos hecho, antes dijimos como aplicar la operacion a multiples listas, ahora la cuestion es como aplicar multiple operaciones sobre una o varias listas
;reduce en este caso toma lo que se le pase, pejemplo una lista y lo resuleve a un solo valor que sería sumando todos los elementos y returnar la sumatoria
(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))
(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

(stats [3 4 10])
; => (17 3 17/3)

(stats [80 1 44 13 6])
; => (144 5 144/5)



; Ademas map tambien es usado para obtener una clave dentro de un Map 

(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spider-Man" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])

; Se le indica el Key que se desea obtener
(map :real identities)



;::::::::reduce Function
; other useful usages
;I)  Transform a Map's values

; en este caso reduce recibe 3  argumentos
;1):::: Funcion que recibe un map y una lista de de valores tipo key vla 
; luego a new-map le inserta el nuevo elemento deseado (asocia assoc), 
; dicho elemento debe ser de la forma key val(acá val se incrementa en 1)
; (fn [new-map [key val]]
;          (assoc new-map key (inc val))
;2) Un cinjunto vacío {} o map vacío 
;3) Un Map con dos elementos key:Val

; Finalmente, reduce aplica la funcion del Punto1 y se la aplica al listado de maps que le lleguen 
; en este caso el vació y el que contiene info 
(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 10})
; => {:max 31, :min 11}
 

;II)filter out keys from a map based on their value
(reduce (fn [new-map [key val]]
          (if (> val 4)
            (assoc new-map key val)
            new-map))
        {}
        {:human 4.1
         :critter 3.9})
; => {:human 4.1}


; Tarea: IMPLEMENTAR MAP, FILTER Y SOME en terminos de reduce
;::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.


;::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::


;;take, drop, take-while, and drop-while
(take 3 [1 2 3 4 5 6 7 8 9 10])
; => (1 2 3)

(drop 3 [1 2 3 4 5 6 7 8 9 10])
; => (4 5 6 7 8 9 10)


;take-while, and drop-while



(take-while (partial > 1000) (iterate inc 0))

(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :critter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}])

(println food-journal)


; Averiguar como funcional el takewhile y obtener solo los del mes de enero
; pimer intento(take-while (partial {:month < 3  food-journal}))

;segundo intento :) algo más cerca (take-while :month<3 food-journal)
;Tercero y vencido :) funcion anonima 

(def enero-food (take-while #(= (:month %) 1) food-journal))
(println enero-food)

; No me funciona preguntar :(
(def febrero-food (take-while #(= (:month %) 2) food-journal))
(println febrero-food)

; Por que no funcionaba la linea anterior? de febrero: R/
; Debido a que take-while and drop-while procesan hasta que encuentran el primer false, en ese instante
; se para el computo, por lo cual una idea de obtener febrero seria algo así como eliminar los de enero, para que 
;no intervengan el proceso y hacer take-while de febrero, go ahead!

(def febrero-food2 (take-while #(< (:month %) 3 )  (drop-while #(< (:month %) 2) food-journal)))
(println febrero-food2)



;filter and some::::::::::::::::::
;1) filter
;En el ejemplo de take-while lo que hacíamos era elimianar y take para obtener los registros de febrero
;por lo cual filter hace esto de una vez :

(def febrero-filter (filter #(= (:month %) 2) food-journal))
(println febrero-filter)

; Entonces por que no usar filter de una vez? quien da más , quien tiene la respuesta?
;.
;.
;.
;R/ Debido a que filter hace un full scan de toda la estructura, mientra que take-while solo lo hace hasta que falla 
; por lo cual en algunos casos sería de notable mejora usarlo, sobretodo si ya sabemos que la cleccion está ordenada.



;2) some::::::::::

; En algunos casos necesitamos saber si una coleccción contiene algun valor de verdad (o que evalue a true), por lo cual
; some resulta útil para ello

(some #(= (:month %) 4) food-journal)

; Si no existe entonces nil
(some #(= (:month %) 5) food-journal)

; Recordar, relacionar valores de la siguiente manera siempre retornará el ultimo valor de la secuencia 
(and true "jose" 1 5 6 "siete")
;"siete"

; siempre y cuando no haya en el flujo un false 
(and true "jose" 1 false  5 6 "siete")
;false

; Por lo cual si del ejemplo some de arriba deseo obtener el registro que es true:

(some #(and (= (:month %) 4) %) food-journal)



;sort and sort-by::::::::::::::::::::::::::

; ascending order 
(sort [ 3 2 1])
;(1 2 3)


; si necesitamos sort mas elaborados entonces está:
(sort-by count ["a" "abc" "xy" "12345"])
;("a" "xy" "abc" "12345"), por lo que count de "a" es 1 , de ab es 2 ....
; count en este caso sería la funcion que yo mismo elaboré para ello, iobviamente puedo crear mis propias funciones 


;CONCAT::::::::::::::::::::::::::
(str "ju" "lian"); para string
(concat "ju" "lian")
;(\j \u \l \i \a \n), algo asi como una secuencia.
; comprobemos
(nth (concat "ju" "lian") 3)
; \i, es correcto una secuencia

; une los miembros de una secuencia al final de la anterior
(concat [1 2 3] [4 5 6] [6 7 8])
 ;(1 2 3 4 5 6 6 7 8)


; Lazy Seqs::::::::::::::::::::::::::..
; son Seq cuyos miembron no son computados hasta 
;que tratamos de accederlos, dicha acción se llama "realizing" the Seq, posponer las computaciones hace que podamos implementar
;infinitos Seq :) 

;::::::::::::::::::::::::::::::::::::
; Demostración:Demonstrating Lazy Seq Efficiency:::::::::::::::::::::::::::
; buscando un vampiro
; Map que contiene el indice y el registro(otro Map key value)
(def vampire-database
  {0 {:makes-blood-puns? false, :has-pulse? true  :name "McFishwich"}
   1 {:makes-blood-puns? false, :has-pulse? true  :name "McMackson"}
   2 {:makes-blood-puns? true,  :has-pulse? false :name "Damon Salvatore"}
   3 {:makes-blood-puns? true,  :has-pulse? true  :name "Mickey Mouse"}})

 ; Cómo información nos dan un millon de numeros (a lo que pudo reducir-filtrar dentro de muchos mas millones) 
; 

; forma de obtener con indice 
(get vampire-database 3)
;{:makes-blood-puns? true, :has-pulse? true, :name "Mickey Mouse"}

; Recordar que 
(first ["x"  2 3 4])
; "x"

;vampire-related-details es Una funcion que dado un numero(no una lista cmo lo es mas adelante) o Id
;retorna el vampiro que le corresponde a dicho Id (social-security-number)
(defn vampire-related-details
  [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

; Note el time de 1 segundo

(time (vampire-related-details 3))


;funcion para evaluar un registro de entrada "record" y saber si es un vampiro jeje
; de ser un vampiro retorna el registro record de lo contrario false
(defn vampire?
  [record]
  (and (:makes-blood-puns? record)
       (not (:has-pulse? record))
       record))

; tomandolo por partes entonces: 
(def record-test {:makes-blood-puns? true, :has-pulse? false  :name "Juliano"})
; Retorna que juliano es un vampiro :) , por ser true en el primer key y
; false en el segundo
(and (:makes-blood-puns? record-test)
     (not (:has-pulse? record-test))
       record-test)


; identify-vampire es una funcion que dada una lista SSM"social-security-numbers" (con 1 millon de resgistros para este problema) 
;1- hace map de los vampiros con dishos Ids de number SSM para obtener un primer listado
;2- el listado del punto 1 lo filtra solo por aquellos que son vampiros
;3- el listado del punto 2 obtiene solo el primero , diría que es porque el 
;problema me dice que en mi ciudad solo hay uno 99% seguro que es por eso
; finalmente retorna el registro que ando buscando
(defn identify-vampire
  [social-security-numbers]
  (first (filter vampire?
                 (map vampire-related-details social-security-numbers))))




; Sacando el map de la función de arriba
; colocandole range es para crear la secuencia dinamica
; Noten como el time se muestra instantáneamente al compilar
; sin embargo el listado resultante si tarde conforme al tamanio del range, si lo colocamos hasta 100000, el computo se para (duriaría 12 días:))
; ::::prueba interesante 
; Computo 1
(time (map vampire-related-details (range 0 10)))
; Haga que al ejecutarse el computo 1, inmediamente vaya 
; y ejecute el computo 3 y notará que se tardará hasta que el 1 no se ejecute


; Peeero note lo poderoso de esta parte  como al hacer 
; una asignación del computo a una constante listado-vampire, el computo de las siguentes funciones en adelante no se ve interrumpido porque map el Lazy, compruebe inmediatemente ejecutando el 
;computo 3, incluso mire como en range acá es mas grande
;Computo 2
(time (def listado-vampire (map vampire-related-details (range 0 1000000))))
; Que pasa si despues del computo2 usted ejecuta el computo 2,1 R/ Ahí si como usted solicito algo de la variable(named value), entonces el computo se ejecutó, pero tampoco se demeró los 12 días sino como 30 segundos  ya uq aunque se solicita solo el primero, clojure procesa los primeros 31(recurde que le dimos al cimputo un delay de 1seg) elementos, y cuando usted solicite el resto, entonces se hará el calculo de los siguientes 32, UFF IMPRESIONANTE
; Eso aplica en muchas cosas en donde esperar por todo el computo es imposible , usted necesita ir pasando PEDAZOS DE registros procesados para que X proceso, persona etc no se desespere. o pasarle solo lo que vaya solicitando

;computo 2.1:
(nth  listado-vampire 3)

; lo anterior es la demostracion de que map 

;Computo 3
;Ahora llamando la funcion identify-vampire
(time (identify-vampire (range 0 1000000)) )
; 32 segundos para encontrar el registro dentro de 1 millon de numeros telefonicos Wow!!!

; Note que: Lo interesante es que al buscar los vampiros relacionados a los numeros SSM en la funcion vampire-related-details, se coloca un delay de 1 segundo lo que simula el caso en que sean demasiados registros.




;::::::::::::::::::::::::::::::::::::::::::
;Infinite Sequences
;::::::::::::::::::::::::::::::::::::::::::

; repeat crea un Seq infinito, en estos ejemplos se
; muestra la concatenacion y el take
(take 8 (repeat "test"))
(concat (take 128 (repeat "na")) ["Batman!"]) 

  
;repeatedly lo que hace es aplicar una funcion 
; para la genenacin de secuencias infinifas 

(take 8 (repeatedly (fn [] (rand-int 5))))


; Otra forma de secuencia infinita es el lazy-seq
; a diferencia de repeat no hay que decirle cuando terminar

(cons "a" '("b" "c" "d"))
;("a" "b" "c" "d")
(cons "a" ["b" "c" "d"])
;("a" "b" "c" "d")

; funcion que devuelve una secuencia de numeros pares
;La funcion tiene 2 constructores, un para cuando no se le envia algo al invocarlo, se iniciaria desde cero, haciendo un llamado recursivo
; otro para cuando si se le envía el numero de inicio y le sumaria 2 al anterior
; realmente tan numero par no es porque si lo inicio en 1 ya no me generaría numeros pares
(defn even-numbers
  ([] (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2)))))) 

(take 8 (even-numbers))
(take 8 (even-numbers 1))


;.::::::::::::::::::::::::::::::::::::::
;The Collection Abstraction
;::::::::::::::::::::::::::::::

; Vale aclarar que sequence abstraction trata todo que tiene que ver 
; con operar sobre sus miembros de manera individual
; 

;The Collection Abstraction por su parte trata de las operaciones sobre la estructura como un todo, ejemplo count, 
; empty?, every?

; Las siguiente operaciones no se hacen sobre cada elemento
; sino que es es sobre toda la colección.
(empty? [])
;true

(empty? ["a"]);
false

(count ["x", "y", "z"])



; :::::::into:::::::::::
;1) Recordar que identity retorna sus argumentos 
(identity [1 2 3 nil 4 false true 1234])
;[1 2 3 nil 4 false true 1234]
(filter #(= % 2) [1 2 false 3])

;Interesante como identity pasado como funcion de filter
;devuelve solo valores de verdad.
(filter identity [1 2 3 nil 4 false true 1234])
;(1 2 3 4 true 1234)

(map identity {:sunlight-reaction "Glitter!"})
; retorna el siguiente Seq 
; => ([:sunlight-reaction "Glitter!"])

(into {} (map identity {:sunlight-reaction "Glitter!"}))
; lo convierte en la estructura que deseamos
; => {:sunlight-reaction "Glitter!"}

(map identity [:garlic :sesame-oil :fried-eggs])
; => (:garlic :sesame-oil :fried-eggs)

(into [] (map identity [:garlic :sesame-oil :fried-eggs]))
; => [:garlic :sesame-oil :fried-eggs]


; map a un vector con entradas iguales (duplicidad)
(map identity [:garlic-clove :garlic-clove :julian-G])
; retorna una lista de los elementos repetidos
; => (:garlic-clove :garlic-clove)

;Acá vamos a almacenar los valores anteriores dentro de un 
;set, recordar Sets en el chapter3
; al asociarlos a un Set por defecto elimina repetidos 
;por su previa existencia en el conjunto
(into #{} (map identity [:garlic-clove :garlic-clove :julian-G ]))
;#{:garlic-clove :julian-G}

; => #{:garlic-clove}


;Note que por ser la estructrua un Map, tampoco se duplican las claves valor, pues ya existían, lo que si se modifica son sus Values (Keys intactas), a manera de indicar que se 
;está actualizando
(into {:favorite-emotion "gloomy"} [[:sunlight-reaction "Glitter!"] [:favorite-emotion "gloomy"] [:favorite-food "bandeja paisa"]])
;{:favorite-emotion "gloomy", :sunlight-reaction "Glitter!", :favorite-food "bandeja paisa"}
; => {:favorite-emotion "gloomy" :sunlight-reaction "Glitter!"}

;note como cherry se repite en la repuesta, pues vector no 
; tiene restricciones de valores repetidos
(into ["cherry"] '("pine" "spruce" "cherry"))
; => ["cherry" "pine" "spruce"]


;Pegando al otra estructura del mismo tipo, las anteriores era de diferentes tipos
(into {:favorite-animal "kitty"} 
      {:least-favorite-smell "dog"
       :relationship-with-teenager "creepy"})
;{:favorite-animal "kitty", :least-favorite-smell "dog", :relationship-with-teenager "creepy"}

; if you were asked for Into then the answer ISS: “I’m great at taking two collections and adding all the elements from the second to the first.”


; Recordar que lo que se pega o une es la Segunda estructura y la receptora,
; de los datos en la primera :) 

; conj :::::::::::
; Se basa casi en lo mismo de INTO pero con algunas deferencias

(conj [0] [1])
;[0 [1]] ---------ups pedo hasta ela estructrua dentron de la primera 

; Si quieres algo así
(into [0] [1])
; => [0 1]

;entonces:
; Se entiende entonces que conj no es para estructuras del mismo tipo???
(conj [0] 1)
; => [0 1]


; al parecer la respuesta a la pregunta de arriba, es que exactamente no se puede tratar de hacer conj sobre 2 estructuras del mismo tipo

(conj [1] 2 3 4)
(conj {:name "julian"} [:last-name "gonzalez"])




; conj en terminos de into
(defn my-conj
  [target & additions]
  (into target additions))

(my-conj [0] 1 2 3)
; => [0 1 2 3]



;:::::::::::::::::::::::::::::::::::::
;Function Functions
;::::::::::::::::::::::::::::
; Aprender a recibir funciones como parametros y/o retornar funciones
; es algo muy util y que deberiamos acostumbrarnos.

; Iniciemos con apply and partial functions, ambas RECIBEN y RETORNAN
;funciones.

(max 0 1 2)
; => 2

(max [0 1 2])
; => [0 1 2]  ups! acá devuelve el mismo, ya que max entiente al vector 
; como un unico elemento que le pasa,como si en el de arriba solo le
;pasaramos 2 

; por lo cual una manera en donde apply encaja es en un ejemplo como este

;Firma en clojure doc:
;(apply f args)(apply f x args)(apply f x y args)(apply f x y z args)(apply f a b c d & args)

; En la teoria apply digamos que se encarga de deshacerse de la estructrura que rodea el dato(s) para pasarlos a funcion que estabezcamos en el 

(apply max [0 1 2])
; => 2



;; Note the equivalence of the following two forms
(apply str ["str1" "str2" "str3"])  ;;=> "str1str2str3"
(str "str1" "str2" "str3")

; lo veo como un mecanismo de sacar los datos dentro de la estructura que  me genera ruido PREGUNTAR????????????'



; INTO en terminos de applu y conj

(defn my-into
  [target additions]
  (apply conj target additions))

(my-into [0] [1 2 3])
; => [0 1 2 3]

(my-into {:name "julian"} [{:last-name "gonzalez"}])
;{:name "julian", :last-name "gonzalez"}

;lo mismo que 
(my-into {:name "julian"} {:last-name "gonzalez"})
;{:name "julian", :last-name "gonzalez"}

;:::::::::::::::::::::::::::
;partial: Toma una funcion y cualquier cantidad de argumentos 
; y retorna una nueva funcion, si nosotros guardamos la funcion en otra
;variable(named value) entonces lo que pasa es que al llamar la variable/named value (que en si ya es una funcion) y ademas le indicamos más argumentos 
; entonces el resultado será la aplicacion de la funcion original, con los argumentos originales mas los nuevos que le paso
;:::::::::::::::::::::::



;partial toma la funcion + y la cantidad deseada de parametros
; se guarda en add10 
(def add10 (partial + 10 ))
;al llamar add10 lo que va a pasar es que se va a ejecutar la funcion original y además de adiciona el 3 y le aplica la misma funcion
(add10 3) 
; => 15

;Es como la manera de saber que a la funcion add10 (que ya es un NAMED VALUE en donde el value es una funcion) ya viene con un argumento adicional que es el 10

; podría uno irse mas adelante y decir que una conexion a base de datos ya venga en nuestras funciones o para que es útil??????
(add10 5 3 6) 
; => 17

; aca se indica que el partial add-missing-elements siempre se tendrá
;los valores por defectos dados
(def add-missing-elements
  (partial conj ["water" "earth" "air"]))

(add-missing-elements "unobtainium" "adamantium")
; => ["water" "earth" "air" "unobtainium" "adamantium"]



; Entendiendo que hace partial::::::::::::
;  my-partial lo que hace es:
; 1) recibe una funcion en [partialized-fn
; 2) con & args] indica que puede recibir 1 o mas parametros, como el ... en java
;3) Retorna una funcion anonima (recordar funciones anonimas chapter3) que hace:
    ;> recibe uno o mas argumentos el el aparametro de entrada & more-args
    ;> primero unifica more-args dentro de args usando INTO
    ;> luego a ese set de resultados le aplica partialized-fn, que es la funcion que le llego de entrada en my-partia
(defn my-partial 
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into args more-args))))

; en este caso la funcion es suma + y los paramertros (para este ejemlo uno) es 20, esto retorna la siguiente funcioin anonima:

;y esta funcion queda ligada(bind)  al NAMED-VALUE add20 
(fn [& more-args]
  (apply + (into [20] more-args)))


(def add20 (my-partial + 20))

;sabiendo que add20 es una funcion anonima entonces la podemos llamar con mas argumentos "more-args"
(add20 3) 
; => 23

(add20 3 4 6)

; solo por entender mejor, lo anterior es:

(def add20Vii  (fn [& more-args]
                 (apply + (into [20] more-args))))


(add20Vii 3 6 4)
;33


; Para que es UTIL?? : Para los casos que que detectemos que en nuestro codigo siempre estamos escribiendo la misma
; secuencia de funcionnes para hacer el algo , ejemplo:

(defn lousy-logger
  [log-level message]
  (condp = log-level
    :warn (clojure.string/lower-case message)
    :emergency (clojure.string/upper-case message)))

; Podriamos llamarlo cada vez que necesitemos un logger asi:
(lousy-logger :warn "es una alerta")
; EN OTRA LINEA DE mi codigo 
(lousy-logger :warn "segunda alerta")
;"es una alerta"
(lousy-logger :emergency "es un error")
;"ES UN ERROR"
;.... y así sucesivamente por toodo nuestro codigo


;Por lo anterior detectamos que podriamos aplicar PARTIAL ALLÍ

(def warn (partial lousy-logger :warn))
(def error (partial lousy-logger :emergency))

(warn "Red light ahead")
; => "red light ahead"
(error "ojo es un error")
;OJO ES UN ERROR"

; mas entendible  no!!!



;:::::::::::::::::::::::::::::::::::
;complement: The Negation 
; Es la negacion de algo para que no se haga uso del not, 
; útil sobretodo cuando lo que valida el valor de verdad 
; es una función
;::::::::::::::::::::::::::::::::::

; ejemplo de 
(defn identify-humans
  [social-security-numbers]
  (filter #(not (vampire? %))
          (map vampire-related-details social-security-numbers)))

; PODEMOS PASAR A 
; definiendo antes el complemento
;en este caso vampire es una funcion 
(def not-vampire? (complement vampire?))

; Y después su uso: Eso puede resultar poco importante, 
; pero para algunos es una mejor legibilidad del código
(defn identify-humans-using-complement
  [social-security-numbers]
  (filter not-vampire?
          (map vampire-related-details social-security-numbers)))


; una forma de como implementar complement podría ser
;& : para definir aridad variable.
(defn my-complement
  [fun]
  (fn [& args]
    (not (apply fun args))))

;neg? : Returns true if num is less than zero, else false
(neg? -1)
;true
(neg? 0)
;false
(neg? 1)
;false

(def test-compl (fn [args]
                 (not (apply neg? args))))
; acá me perdi por lo del la aridad variable que no funciona sino es con &, queda pendiente.


(not true )
(not false)

(def my-pos? (my-complement neg?))
(my-pos? 12 2)   
; => true

(my-pos? -1) 
; => false

;:::::::::::::::::::::::::::::::::::::::::::::::
;A Vampire Data Analysis Program for the FWPD
; Es un programa que el libro indica como hacer, igualmente
; al final de chapter4 recomienda algunos cambios sobre el 
; código ...go ahead!


; pasos:

;1) Cree el proyecto lein, for instace:
;lein new app chapter4-fwpd 

;2) Under the new fwpd(chapter4-fwpd) directory, create a file named suspects.csv
;3) Abrir una nueva sesion de Emacs
;4) Abrir el archivo core.clj
;5) Borrar lo de este arechivo(ojo solo es cuando se crea por primera vez el proyecto)
;6) ctrl-c ctrl-k compila el archivo :) 
;7) o pruebo solo esa parte
;:::
:::::::::::::::::::::::::::::::::::::::::
