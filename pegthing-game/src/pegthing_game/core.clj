;ns or namespace 
;(require [clojure.set :as set]) permite el uso de funciones en el
;clojure.set namespace 

;:gen-class : Para correr el programa desde la consola de compandos
(ns pegthing-game.core
  (require [clojure.set :as set])
  (:gen-class))

;Permite que en nuetro código escribamos funciones que se van a referir a estos nombres
;util para ando modelando mi sistema por ejemplo, o que otro equipo lo tiene
;realmente es un caso común en la vida real.
(declare successful-move prompt-move game-over query-rows)


; Creating the Board:::::::::::::::::::::::
;Los keys son numeros, que representan cada posicion del tablero (1- 15) y el value es un Map con informacion específica de cada posicion, ejemplo:
;{:pegged true, :connections {6 3, 4 2}}
;> El key pegged indica si existe un dardo, o pieza allí
;> El key :conenctions indica los movimientos que se pueden hacer estando el la posición Key, en eeste caso para el registro en cuestion el Key es 1, tiene piza allí y los movimientos validos From 1 son I) To 6 sobre 3 y To 4 sobre 2

; También hay un key :row con la ifnormacion de la cantidd de filas del tablero.

; Hasta acá representamos las posiciones con números y aún no con letras porque resulta más útil el uso de números en casos en que se cumplan algunas reglas matemáticas
;PERO AL USUARIO LE VAMOS A MOSTRAR LETRAS PARA MAYOR USABILIDAD
{1  {:pegged true, :connections {6 3, 4 2}},
 2  {:pegged true, :connections {9 5, 7 4}},
 3  {:pegged true, :connections {10 6, 8 5}},
 4  {:pegged true, :connections {13 8, 11 7, 6 5, 1 2}},
 5  {:pegged true, :connections {14 9, 12 8}},
 6  {:pegged true, :connections {15 10, 13 9, 4 5, 1 3}},
 7  {:pegged true, :connections {9 8, 2 4}},
 8  {:pegged true, :connections {10 9, 3 5}},
 9  {:pegged true, :connections {7 8, 2 5}},
 10 {:pegged true, :connections {8 9, 3 6}},
 11 {:pegged true, :connections {13 12, 4 7}},
 12 {:pegged true, :connections {14 13, 5 8}},
 13 {:pegged true, :connections {15 14, 11 12, 6 9, 4 8}},
 14 {:pegged true, :connections {12 13, 5 9}},
 15 {:pegged true, :connections {13 14, 6 10}},
 :rows 5}



;

;si se llama sin argumentos entonces genra la suma de los numeros consecutivos hasta infinito, solo que como es lazy 
;podemos solicitar lo que necesitemos
;let new-sum no es mas que una variable que inicia el la suma de sum y n OJO NO ES UNA FUNCION
(defn tri*
  "Generates lazy sequence of triangular numbers"
  ([] (tri* 0 1))
  ([sum n]
     (let [new-sum (+ sum n)]
       (cons new-sum (lazy-seq (tri* new-sum (inc n)))))))


; ejemplo de uso 
; se debe colocar entre parentesis el llamado a la funcion tri*, indicando que es tri* de nada y por ende llamará el segundo constructor 

(def tri (tri*))

(take 5 tri)
;acá ya tenemos la triangular derecha 
;(1 3 6 10 15)

(def tri-left (tri* 1 0))
(take 5 tri-left)

;Triangular lo que hace es armar una lista a partir del del 
; tri ES EL Named Value arriba 
; hasta que el elemento en la Secuencia tri sea mayor que el n que le paso a la funcion, y con last le obtiene el último elemento de la lista resultante para saber si la posicipon o el n existe en la secuencia.
; y se hace last porque se parte bajo la seguridad que tri es un Seq ordenado.
;halando de traingular 5 la secuencia sería  1 3 6 ; el last sería 6 que no es
;igual a 5 ; por lo tanto false
(defn triangular?
  "Is the number triangular? e.g. 1, 3, 6, 10, 15, etc"
  [n]
  (= n (last (take-while #(>= n %) tri))))
(triangular? 5) 
; => false

(triangular? 6)
;true



;
;Obtiene los n elementos de tri y le aplica last para que devuelva el valor de esa última posición , como está ordenado me devuelve el valor correct
(defn row-tri
  "The triangular number at the end of row n"
  [n]
  (last (take n tri)))


(row-tri 1) 
; => 1

(row-tri 2) 
; => 3

(row-tri 3) 
; => 6

(row-tri 5)
;15



;Dado un valor retorna la fila del triangulo (ver imagen del juego) a la 
;que pertenece dicha posición, ejemplo la pos 15-última del tablero, es la fila 5 igual que la 11
(defn row-num
  "Returns row number the position belongs to: pos 1 in row 1,
  positions 2 and 3 in row 2, etc"
  [pos]
  (inc (count (take-while #(> pos %) tri))))

;para entenderlo mejo esta es la parte clave 

(take-while #(> 5  %) tri)
;(1 3) al hacerle count da 2 y luego inc cuyo resultado es 3
;para el negocio esta propiedad se entiende que es la fila en el PEg-game

(row-num 1) 
; => 1
(row-num 5) 
; => 3

(row-num 11)
;5

(row-num 15)
;5


(if (false) "logical true" "logical false")

;Connect forma una conexión mutua entre 2 posiciones 
;recordar que el reduce es de la forma 
;(reduce f coll)  ó (reduce f val coll)
; en este caso es la seguda, en donde val es el board que se le pase en la 
;la función connect y coll son las posiciones 

;para que sirve la validacion ?:
;R/ para evitar estas conecciones : (connect {} 15 7 16 22)
(defn connect
  "Form a mutual connection between two positions"
  [board max-pos pos neighbor destination]
  (if (<= destination max-pos)
    (reduce (fn [new-board [p1 p2]]
              (assoc-in new-board [p1 :connections p2] neighbor))
            board
            [[pos destination] [destination pos]])
    board))

; en el código anterior assoc-in haría por cada pareja [x y] que le pasemos:
; en este caso solo le hemos pasado [pos destination] [destination pos]
;cuyos valore son [[1 4] [4 1]]; Osea que en este caso la funcion en reduce se llamaría dos veces 
; La primera para [1 4] , en donde p1=1 ; p2=4 ; neighbor=2
(assoc-in {} [1 :connection 4] 2)
;{1 {:connection {4 2}}}

; La segunda  para [4 1] , en donde p1=4 ; p2=1; neighbor=2
(assoc-in {} [4 :connection 1] 2)
;{4 {:connection {1 2}}}



(connect {} 15 1 2 4)
; => {1 {:connections {4 2}}, 4 {:connections {1 2}}}

; Este NO es el mismo assoc que habiamos visto previamente sino 
; que es assoc-in y trabaja con maps anidados
;El get-in tambien trabaja con maps anidados, 

; Get es para sacar informacion que requiramos y assoc-in unifica todo dentro de un nuevo map acomodando los Keys (que inician por :) en dicho Map.
;este es de 3 entradas
(assoc-in {:epa 3} [:cookie :monster :vocals] "Finntroll")
; => {:cookie {:monster {:vocals "Finntroll"}}}

(get-in {:cookie {:monster {:vocals "Finntroll"}}} [:cookie])
;{:monster {:vocals "Finntroll"}}


(get-in {:cookie {:monster {:vocals "Finntroll"}}} [:cookie :monster])
;{:vocals "Finntroll"}

(get-in {:cookie {:monster {:vocals "Finntroll"}}} [:cookie :monster :vocals] )
;"Finntroll"

(assoc-in {} [:key1 1 :connections 4 :key2] 2)
;    {:key1 {1 {:connections {4 {:key2 2}}}}}
; => {:key1 {1 {:connections {4 {:key2 2}}}}}



;

; la idea de las siguientes 3 funciones es calcular los argumentos de entrada a la funcion connect; 
; si miramos el triangulo del juego lo que la función hace es sumas de row, pos y neighbor,Destination según sea el caso, y con esto connect retorna 
; las coneccoines que se tiene para ir de una posicion INICIAL  a DESTINO 
;HACIENDO USO DE triangle math, O FORMULAS QUE APLICAN AL Contexto de negocio Peg-Game
(defn connect-right
  [board max-pos pos]
  (let [neighbor (inc pos)
        destination (inc neighbor)]
    (if-not (or (triangular? neighbor) (triangular? pos))
      (connect board max-pos pos neighbor destination)
      board)))

(defn connect-down-left
  [board max-pos pos]
  (let [row (row-num pos)
        neighbor (+ row pos)
        destination (+ 1 row neighbor)]
    (connect board max-pos pos neighbor destination)))

(defn connect-down-right
  [board max-pos pos]
  (let [row (row-num pos)
        neighbor (+ 1 row pos)
        destination (+ 2 row neighbor)]
    (connect board max-pos pos neighbor destination)))


; Ejemplos de uso:

(connect-down-left {} 15 1)
; => {1 {:connections {4 2}, 4 {:connections {1 2}}}}

(connect-down-right {} 15 1)
;{1 {:connections {6 3}}, 6 {:connections {1 3}}}

(connect-right {} 15 1)
;{}

(connect-right {} 15 4)
;{4 {:connections {6 5}}, 6 {:connections {4 5}}}

(connect-down-right {} 15 3)
; => {3  {:connections {10 6}}, 10 {:connections {3 6}}}



;

(defn add-pos
  "Pegs the position and performs connections"
  [board max-pos pos]
  (let [pegged-board (assoc-in board [pos :pegged] true)]
    (reduce (fn [new-board connection-creation-fn]
              (connection-creation-fn new-board max-pos pos))
            pegged-board
            [connect-right connect-down-left connect-down-right])))


;Ejemplo de uso
(add-pos {} 15 1)
;{1 {:pegged true, :connections {4 2, 6 3}}, 
; 4 {:connections {1 2}}, 
; 6 {:connections {1 3}}}
