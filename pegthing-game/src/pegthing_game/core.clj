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

;para entenderlo mejor esta es la parte clave 

;(take-while #(> 5  %) tri)
; al hacerle count da 2 y luego inc cuyo resultado es 3
;para el negocio esta propiedad se entiende que es la fila en el PEg-game

;(row-num 1) 
; => 1
;(row-num 5) 
; => 3

;(row-num 11)
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

(assoc-in {1 {:pegged true}} [1 :connection 1] 2)
;{1 {:pegged true, :connection {1 2}}}

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

(connect-right {4 {:connections {6 5}}} 15 1)
;No hace nada porque el resultado de 15 1 en connect-right es vacío {}

(connect-right {} 15 4)
;{4 {:connections {6 5}}, 6 {:connections {4 5}}}

(connect-down-right {} 15 3)
; => {3  {:connections {10 6}}, 10 {:connections {3 6}}}



;
;1)la fn anónima recibe un board para el caso de (add-pos {} 15 1)
;assoc-in de ({} [1 _pegged] true) resuelve a {1 {:pegged true}} y este es el 
;board inicial
;
;2)Luego fn anónima recibe un new-board (board inicial punto 1) y una funcion 
;de conexión de las antes mencionadas ejemplo connect-down-right y aplica dicha
;función(cada una con lo que se tenga en new-board, el valor que se le pasa a add-pos en max-pos y pos son siempre los mismos que se le pasen a las funciones) al new-board.
;3) En este ejemplo aplicaría 3 funciones consecutivas de connect sobre el 
;board con sus valore que este vaya acumulando por la aplicación de cada función
;Finalmente, cuando no hay más funciones el resultado es el Board 
(defn add-pos
  "Pegs the position and performs connections"
  [board max-pos pos]
  (let [pegged-board (assoc-in board [pos :pegged] true)]
    (reduce (fn [new-board connection-creation-fn]
              (connection-creation-fn new-board max-pos pos))
            pegged-board
            [connect-right connect-down-left connect-down-right])))


;::::::::::::::APLICANDO FUNCIONES::::::::::::::::
; por dentro haría algo así
;I) connect-right no hace nada y deja el board como llegó 
;II) connect-down-left hace 
(def connect-dl-part1 (assoc-in {1 {:pegged true}} [1 :connection 4] 2))

; connect-dl es:
;{1 {:pegged true, :connection {4 2}}})

; luego hace algo así como 
(def connect-dl-part-2 (assoc-in connect-dl-part1 [4 :connection 1] 2))

; connect-dl-part2 sería:
;{1 {:pegged true, :connection {4 2}}, 4 {:connection {1 2}}})


;III) connect-down-right:
; agarra el resultado de connect-dl-part-2

(def connect-dr-part1 (assoc-in connect-dl-part-2 [1 :connection 6] 3))
;{1 {:pegged true, :connection {1 2, 6 3}}, 4 {:connection {1 2}}})

;y luego 
(def connect-dr-part-2 (assoc-in connect-dr-part1 [6 :connection 1] 3))
;{1 {:pegged true, :connection {4 2, 6 3}}, 4 {:connection {1 2}}, 6 {:connection {1 3}}})


;confirmando lo anterior entonces 
(add-pos {} 15 1)
;{1 {:pegged true, :connections {4 2, 6 3}}, 4 {:connections {1 2}}, 6 {:connections {1 3}}

; NOTE LO INTERESANTE QUE ES COMPONER FUNCIONES APLICANDO REDUCE 

;Otro ejemplo de composición, igualmente usando reduce



;Función de creación de un nuevo tablero dado las cantidades de 
; filas que se le indique.
(defn new-board
  "Creates a new board with the given number of rows"
  [rows]
  (let [initial-board {:rows rows}
        max-pos (row-tri rows)]
    (reduce (fn [board pos] (add-pos board max-pos pos))
            initial-board
            (range 1 (inc max-pos)))))

 (row-tri 5)
;15

(range 1 (inc 15))
;(1 2 3 4 5 6 7 8 9 10 11 12 13 14 15)

(new-board 5)
;{7 {:connections {2 4, 9 8}, :pegged true}, 1 {:pegged true, :connections {4 2, 6 3}}, 4 {:connections {1 2, 6 5, 11 7, 13 8}, :pegged true}, 15 {:connections {6 10, 13 14}, :pegged true}, 13 {:connections {4 8, 6 9, 11 12, 15 14}, :pegged true}, :rows 5, 6 {:connections {1 3, 4 5, 13 9, 15 10}, :pegged true}, 3 {:pegged true, :connections {8 5, 10 6}}, 12 {:connections {5 8, 14 13}, :pegged true}, 2 {:pegged true, :connections {7 4, 9 5}}, 11 {:connections {4 7, 13 12}, :pegged true}, 9 {:connections {2 5, 7 8}, :pegged true}, 5 {:pegged true, :connections {12 8, 14 9}}, 14 {:connections {5 9, 12 13}, :pegged true}, 10 {:connections {3 6, 8 9}, :pegged true}, 8 {:connections {3 5, 10 9}, :pegged true}}



;:::::::::::::::::::::::::::::::
;Moving Pegs:
;Código con todas las funciones realizadas respecto al movimiento de fichas
;::::::::::::::::::::::::::::::
;La función pegged? hace uso de get-in para obtener el key
; pegged del Map, retoornando el valor que tenga, sí no existe retorna nil
(defn pegged?
  "Does the position have a peg in it?"
  [board pos]
  (get-in board [pos :pegged]))

;Ejemplos:

(def boar-test-1 {1 {:pegged true, :connections {4 2, 6 3}}, 4 {:pegged false, :connections {1 2}}, 6 {:connections {1 3}}})

(pegged? boar-test-1 1)
;true

(pegged? boar-test-1 4)
;false


(pegged? boar-test-1 6)
;nil : NO existe el key pegged


(defn remove-peg
  "Take the peg at given position out of the board"
  [board pos]
  (assoc-in board [pos :pegged] false))
(remove-peg boar-test-1 1)
;{1 {:pegged false, :connections {4 2, 6 3}}, 4 {:pegged false, :connections {1 2}}, 6 {:connections {1 3}}}


;igual que remove, pero esta vez para colocar true , indicando que la posición is pegged, y retorna el Map/board actualizado
(defn place-peg
  "Put a peg in the board at given position"
  [board pos]
  (assoc-in board [pos :pegged] true))

; Move no es más que colocar el pegged de la posición from en false y el de pa posición To en true y retorna el Map/board actualizado
(defn move-peg
  "Take peg out of p1 and place it in p2"
  [board p1 p2]
  (place-peg (remove-peg board p1) p2))

;Ejemplo
(move-peg boar-test-1 1 4)
;{1 {:pegged false, :connections {4 2, 6 3}}, 4 {:pegged true, :connections {1 2}}, 6 {:connections {1 3}}}

;---
;Ahora la función que valida si un moviiento es válido
;Coḿo se logra? :
;Se retorna un Map con los posibles movimientos desde la posición FROMa la que se desea quitar la ficha y poner en otra posición.  Into {}, indica que deseamos un Map.
;
;Que hace la función anónima allí adentro?:
;En sus argumentos indica qque el patrón de lo que recibe
; debe ser un destino y un Jumped(posión que se desea saltar) 
; con el filter se indica que la posición destino NO PUEDE TNER UNA ficha y que la POSICIÓN jumped, por donde se va saltar SI debe tener una Ficha
(defn valid-moves
  "Return a map of all valid moves for pos, where the key is the
  destination and the value is the jumped position"
  [board pos]
  (into {}
        (filter (fn [[destination jumped]]
                  (and (not (pegged? board destination))
                       (pegged? board jumped)))
                (get-in board [pos :connections]))))

(filter #(> % 5 ) [2 4 6 8])
;(6 8)
; 


;
(def my-board (assoc-in (new-board 5) [4 :pegged] false))
(println my-board)
; lo anterior es como tener un Peg-thing en donde la posición 4 sea el hueco

;Desglozando un poco, para pos 1 cómo INICIO:
(get-in my-board [1 :connections])
;{4 2, 6 3}--> DE estas posiciones {p1 p2, p1 p2}
; le pasa cada posición a la función anónima de valid-moves
; y por ejemplo para la pareja 4 2 valida que pa posición 4 no tenga un Ficho  y que 2 SI lo tenga, 2 en este caso indica la posición por la cual va a saltar.


(valid-moves my-board 1)  ; => {4 2}
; lo anterio indica que desde 1 solo se tiene 1 movimiento para poder mover una ficha al cajón 4, y es saltando por la posición 2.

(def my-board-2 (assoc-in (new-board 5) [5 :pegged] false))
(valid-moves my-board-2 14)
;{5 9}

(valid-moves my-board 6)  ; => {4 5}
(valid-moves my-board 11) ; => {4 7}
(valid-moves my-board 5)  ; => {}
(valid-moves my-board 8)  ; => {}
; LOS ejemplos anteriores siempre retornan un Map 
;por la facilidad de buscar en estos, así como en java acá se tiene bondades sobre los Maps en el momento de realizar busquedas.


;Valid-move por ejemplo aprovecha que valid-moves (con s) retorna un Map y hace get sombre Map 
(defn valid-move?
  "Return jumped position if the move from p1 to p2 is valid, nil
  otherwise"
  [board p1 p2]
  (get (valid-moves board p1) p2))
  
(valid-move? my-board 8 4) ; => nil
(valid-move? my-board 1 4) ; => 2
; el anterior es un movimiento válido, dado el caso en que 
;se desee validar antes.

;make-move lo que hace es validar con el if-let 
;Sí el movimiento es valido, sí no lo es entonces retorna nil
; y si lo es entonces guarda en en jumped la posición a saltar, dicha posición le aplica remove-peg, que no es más que coloca el false el Key :pegged, lo anterior retorna el Map actualizado. Finalmente con el map actualizado, realiza move-peg que coloca :pegged true del p2 y en false el de p1
(defn make-move
  "Move peg from p1 to p2, removing jumped peg"
  [board p1 p2]
  (if-let [jumped (valid-move? board p1 p2)]
    (move-peg (remove-peg board jumped) p1 p2)))

;

;El (second %) quiere decir que se obtiene el segundo Key del Map,
;en nuestro caso el key :pegged
(defn can-move?
  "Do any of the pegged positions have valid moves?"
  [board]
  (some (comp not-empty (partial valid-moves board))
        (map first (filter #(get (second %) :pegged) board))))

;Ojo para entender la funcion anónima, todo inicia con 
;filter , en donde dicha funcion agarra cada posición del Map board y las convierte en una secuencia de 2 elementos con el patrón ([e1 e2] ...[]), en donde e1 es la posición y e2 es la información detallada de la posicion.

; a lo anterior se le aplica second y el second de [e1 e2 ] es e2, ejemplo:
(def second-t1(second [ 1  {:pegged true, :connections {6 3, 4 2}}]))
;{:pegged true, :connections {6 3, 4 2}}

;luego con get obtiene el valor de pegged 
(get second-t1 :pegged)
;true

(second [ 3 4 5])
;4

;ejemplo de todo junto, lo que podemos notar es qye elimina los que tengan false en :pegged
(filter #(get (second %) :pegged) {1 {:connections {3 6, 8 9}, :pegged true}, 2 {:connections {3 5, 10 9}, :pegged false}})
;([1 {:connections {3 6, 8 9}, :pegged true}])




;my-board está definido arriba
(can-move? my-board)
;{4 2}

(def seq-of-pos1 [1 {:connections {6 3, 4 2}, :pegged true}]
 [2 {:connections {9 5, 7 4}, :pegged true}])


;:::::::::::::::::::::::::::::::::::::::
;Rendering and Printing the Board
;::::::::::::::::::::::::::::::::::::::
;letra 'a'(en caracter no string) del abcdario, sí se coloca ejemplo 94 saldrán otros caracteres
(def alpha-start 97)
;letra 'z'(en caracter no string) del abcdario
(def alpha-end 123)
;map aplica una funcion a un Seq de registros, en este caso al aplicar char obtiene el caracter y al aplicar string lo castea a Str 
(def letters (map (comp str char) (range alpha-start alpha-end)))
; El resultado es:

;("a" "b" "c" "d" "e" "f" "g" "h" "i" "j" "k" "l" "m" "n" "o" "p" "q" "r" "s" "t" "u" "v" "w" "x" "y" "z")

;esta se usa en la funcion row-padding para indicar cuantos especios mover
(def pos-chars 3)

(def ansi-styles{:red "[31m":green "[32m":blue "[34m":reset "[0m"})

(defn ansi
"Produce a string which will apply an ansi style"
  [style]
   (str \u001b (style ansi-styles)))
;ejemplo de llamado
(ansi :red)


(defn colorize
 "Apply ansi color to text"
 [text color]
  (str (ansi color) text (ansi :reset)))


;Las funciones render-pos, row-positions, row-padding y render-row 
;crean String para representar el tablero
; I) obtiene con nth la letra que representa la posición 
; II)Obtiene un 0 o un - de acuerdo a si el key :pegged es true 
; Concatena I y II quedando por ejemplo a0 dado que pegged en la posicion de entrada (pos) de la funcion tenga true en pegged
(defn render-pos
  [board pos]
  (str (nth letters (dec pos))
       (if (get-in board [pos :pegged])
         (colorize "0" :blue)
         (colorize "-" :red))))

;Dec lo que hace es quitarle uno al número que se le pasa, es lo contrario de inc
(dec 15)

(render-pos my-board 3)

;Retorna un Seq con todas las posiciones de una fila 
(defn row-positions
  "Return all positions in the given row"
  [row-num]
  (range (inc (or (row-tri (dec row-num)) 0))
         (inc (row-tri row-num))))

;Ejemplo de uso para row-num = 5 , al decrementar es 4 en en range
;por lo cual 
(row-tri 4)
;10, que sería la última posición de la fila 4 y se incrementa en uno para arrancar desde el inicio de la fila 5 y se va hasta el último elemento de dicha fila lo que genera:
(row-positions 5)
;(11 12 13 14 15)

;recordar que or retorna el primer truly encontrado, ejemplo:
(or nil 16 7)
;16

;recorda que tri es un Seq (1 3 5 7 9 ...inf)
;y row-tri obtiene la fila N del pegthing   y retorna el último de esa fila
; ejemplo de la fila 6 tiene el 21 como última posición (dado el caso que el peg-thing sea de tmaño 6 )
(row-tri 6)
;21

(range 0 6)
;(0 1 2 3 4 5)

(defn row-padding
  "String of spaces to add to the beginning of a row to center it"
  [row-num rows]
  (let [pad-length (/ (* (- rows row-num) pos-chars) 2)]
    (apply str (take pad-length (repeat " ")))))

(row-padding 0 0)
;""
(row-padding 8 6)
;"        "

; un take negativo devuelve vacío
(apply str (take 3 (repeat " ")))

(defn render-row
  [board row-num]
  (str (row-padding row-num (:rows board))
       (clojure.string/join " " (map (partial render-pos board) 
                                     (row-positions row-num)))))

;Con el print se ven los colores :) :) 
(println (render-row my-board 5))

;

; hace un range de 1 -> 6 y lo guarda eb row-num 
; Luego hace un doseq que se usa para cuando se realizan side-effect operations, que en este caso  es el println (recordar que puede ser un ingreso a BD , escribir en un archivo etc)
(defn print-board
  [board]
  (doseq [row-num (range 1 (inc (:rows board)))]
    (println (render-row board row-num))))

;

(print-board my-board)
(:rows my-board)
;5


;:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
;Player Interaction
;::::::::::::::::::::::::::::::::::::::::::::::::::::::::

;I) Función que recibe una letra y obtiene su número int que la representa
;II) Luego le resta el  alpha-start que es 97 
;III) Al resultado del paso II le aumenta 1 y esa es la posicion en el tablero :) 
(defn letter->pos
  "Converts a letter string to the corresponding position number"
  [letter]
  (inc (- (int (first letter)) alpha-start)))

(int (first "b"))
;98


;
(defn get-input
  "Waits for user to enter text and hit enter, then cleans the input"
  ([] (get-input nil))
  ([default]
     (let [input (clojure.string/trim (read-line))]
       (if (empty? input)
         default
         (clojure.string/lower-case input)))))

; Al ejecutarlo entonces se activa la consola en espera de que se ingrese un valor, si es mayuscula le jace lower-case 
; si es un número retorna el miso número
; Sí no se le envía nada sino que se da enter de una retorna nil que es el valor por defecto cuando se llama sin parámetros
(get-input)

(str (seq "aaa"))


;con el pattern dado ([a-zA-Z]) descarta todo lo que no sea numerico o no sea alfabeto  ; retorna una secuenia de las letras que se le pasen
(defn characters-as-strings
  "Given a string, return a collection consisting of each individual
  character"
  [string]
   (re-seq #"[a-zA-Z]" string))


; ejemplo de uso
(characters-as-strings "a   b C D 4 5 ")
; => ("a" "b")

(characters-as-strings "a   cb45u")
; => ("a" "c" "b")


;

;cuando se llama a user-entered-invalid-move
;simplemente se muestra el mensaje al usuario y se le pasa el mismo board a 
; la función prompt-move(acá también es el mismo board que le ingresaron, es la forma de decir que dado un movimienoto erróneo el board NO CAMBIA)
(defn user-entered-invalid-move
  "Handles the next step after a user has entered an invalid move"
  [board]
  (println "\n!!! That was an invalid move :(\n")
  (prompt-move board))


;Cuando se llama esta función es poque se puede realizar el movimeinto por eso se llama a prompt-move de nuevo pero esta vez con un new-board, es la FORMA DE REPRESENTAR QUE EL BOARD CAMBIÓ DADO UN movimiento válido
; eso si, siempre y cuando existan más movimientos
(defn user-entered-valid-move
  "Handles the next step after a user has entered a valid move"
  [board]
  (if (can-move? board)
    (prompt-move board)
    (game-over board)))


;Con el first y second de input asegura que así se le pase 3 letras solo tomará las
;primeras 2, DEMOSTRAR QUE ES CIERTO+
;en new-board se guarda nil sí no hay forma de realizar el movimiento o el Map/board actualizado que lo retorna make-move

; si es nil se considera invalido el movimiento y se llama a  la función user-entered-invalid-move
; 
(defn prompt-move
  [board]
  (println "\nHere's your board:")
  (print-board board)
  (println "Move from where to where? Enter two letters:")
  (let [input (map letter->pos (characters-as-strings (get-input)))]
    (if-let [new-board (make-move board (first input) (second input))]
      (user-entered-valid-move new-board)
      (user-entered-invalid-move board))))


(prompt-move my-board)


; las dos siguientes funciones prompt-empty-peg y  prompt-rows
; se ejecutan solamente cuando se ejecuta game-over y sugiere (prompt) 
; que se le ingrese una nueva configuración del tablero Peg tanto en cantidad de filas COMO en la selección de la posición que va a quedar pegged en False
(defn prompt-empty-peg
  [board]
  (println "Here's your board:")
  (print-board board)
  (println "Remove which peg? [e]")
  (prompt-move (remove-peg board (letter->pos (get-input "e")))))

(defn prompt-rows
  []
  (println "How many rows? [5]")
  (let [rows (Integer. (get-input 5))
        board (new-board rows)]
    (prompt-empty-peg board)))

(defn game-over
  "Announce the game is over and prompt to play again"
  [board]
  (let [remaining-pegs (count (filter :pegged (vals board)))]
    (println "Game over! You had" remaining-pegs "pegs left:")
    (print-board board)
    (println "Play again? y/n [y]")
    (let [input (get-input "y")]
      (if (= "y" input)
        (prompt-rows)
        (do
          (println "Bye!")
          (System/exit 0))))))

;Que hace vals? .
;R/ Retorna una secuencia de los valores que los keys de una map
(vals {:a "foo", :b "bar"})
;("foo" "bar")

(vals {1 {:connections {3 6, 8 9}, :pegged true}, 2 {:connections {3 5, 10 9}, :pegged false}})
;({:connections {3 6, 8 9}, :pegged true} {:connections {3 5, 10 9}, :pegged true})


(filter :pegged ({:connections {3 6, 8 9}, :pegged true} {:connections {3 5, 10 9}, :pegged true}) )
