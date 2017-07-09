;Clojure Alchemy: Reading, Evaluation, and Macros++
;http://www.braveclojure.com/read-and-eval/

; Macros nos permite extender el lenguaje, covirtiendo a codigo valido en clojure expresiones que personalicemos para nuestra conveniencia sintactica
;Lo anterior se permite dado que Lisp es un lenguaje  homoiconic ( el codigo escrito es codificado a estructuras de datos que el mismo lemguaje puede manipular mediante herramientas
;de este / programaticamente) es decir que se tratar el codigo como dato 
; mientras una funcion trasforma un dato en otro dato un Macro transforma codigo en otro codigo 
;Note como simplemente se le dice a la funcion que lo que se le ingrese solo le aplique reverse 
; con esta carateristica se extiende el lenguaje, aca ya podemos crear nuestra propia sintaxis!!
(defmacro backwards
  [form]
  (reverse form))

(backwards (" backwards" " am" "I" str))
; => "I am backwards"

;this blow up!!
;(backwards (str " backwards" " am" "I"))

;Elementos de la evaluaci [on en clojure :]

;the reader, the evaluator, and the macro expander. 

;:::::::::::::::::::::::::::::::::::::::::An Overview of Clojure’s Evaluation Model
; clojure lee el codigo y produce estructuras de datos que representan dicho codigo >> luego la estructura es la que se evalua 

; para (+ 1 2) clojure lo representa en una lista , en donde el primer elemento es el + >> la lista entera se le pasa al evaluador de clojure
; quien detecta la funcion correspondiente a + y aplica dicha funcion a 1 y 2

;:::::::::::::::::::::::::;;COMPILATION::::::::::::::::::::::::::::::::::::
; El compilador tambien conocido como interpreter transforma el codigo que escribimos (caracteres unicode) a otro codigo o a instrucciones de maquina 
; Durante el proceso el compilador construye el árbol de sintaxis abstracta  AST, que es la estructura de datos que representa nuestro codigo 

; En la mayoria de los lenguajes el AST structure no se puede tocar es inaccesible desde el lenguaje / programaticaly , ya que se encuentran en diferentes spacios
; See Figure 7-1 

; CLOJURE aca si es DIFERENTE debido a que en lugar de evaluar un AST que no se puede accedder  entonces evalua su pripia estructura  nativa,  que son representados  usando clojure list (mediante el Clojure’s reader), en donde los nodos son los valores
; Las lista son ideales para representar arboles , en donde el primer elemendo de la lista es la raiz y el resto spn branches , para crear un arbol anidado seria con una lista 
; anidada :)  see Figure 7-2. ejemplo : (+ 1 (* 6 7)) 1- Clojure’s reader lo convierte a  listas anidada ; 2-Clojure’s evaluator  recibe como entrada la lista del punto 1 , lo procesa y retorna, igualmente compila JVM bitecodes 

;see Figure 7-3: Evaluation in Clojure

; En la fugura 7-3 pareciera que fuera obligatorio que el datastructure deba proceder del Reader, a continuacion se muestra que al Evaluator no le interesa de donde provenga, solo que se lo envien  
(def addition-list (list + 1 2))
(eval addition-list)
; => 3


; por lo anterior Clojure is homoiconic, representa el AST usando listas, ya que el ejemplo de anterio muesta que el evaluador eval no restrige de donde deba venir su input entonces eso nos sirve para intervenir all[i y programaticamente manipular a nuestras necesidades, dicha manipulacion se logra facilmente haciendo uso de MACROS 


;::::::::::::The Reader , para entender mejor como trabaja los Macros

; El reader/reader Macro convierte el codigo que escribimo a estructura de datos, ademas permite escribir codigo mas elegante
(str "To understand what recursion is," " you must first understand recursion.")

; El data Structure reprsentation se conoce  como reader form, en el ejemplo la forma es una lista de estructura de datos que contiene 3 formas mas, el simbolo  str y 2 strings mas
; reader y eval son dos pasos independientes es como si lo de arriba fuera lo siguiente:
(read-string  "(str \"To understand what recursion is,\" \"you must first understand recursion.\")")
;(str "To understand what recursion is," "you must first understand recursion.")
; Una lista en donde la raiz es la funcion y sus branches son los 2 Strings

(list? (read-string  "(str \"To understand what recursion is,\" \"you must first understand recursion.\")" ))
; => true; SE COMPRUEBA QUE SI ES UNA LISTA :)

(conj (read-string  "(str \"To understand what recursion is,\" \"you must first understand recursion.\")" ) :zagglewag)
; => (:zagglewag + 1 2) 

; note qcomo hasta aca no se ha EVALUADO


(eval (read-string  "(str \"To understand what recursion is,\" \"you must first understand recursion.\")"))
;"To understand what recursion is,you must first understand recursion."

; Mas reader form representation:
;() A list reader form
;str A symbol reader form
;[1 2] A vector reader form containing two number reader forms
;{:sound "hoot"} A map reader form with a keyword reader form and string reader form

; Los ejemplos arriba son uno a uno , en donde lo que escribo es similar a la representacion estrcutural , AHORA un ejemplo mas complejo

(#(+ 1 %) 3 )
; => 4

;y si ejecutamos el reader 
(read-string "#(+ 1 %)")
; => (fn* [p1__423#] (+ 1 p1__423#))
; retorna una lista en donde el primer elemento es un simbolo fn* el segundo es un vector form con un simbolo p1__423#  y el tercero es una lista con 3 elementos

; Por lo anterior el Reader Macros::::

;Reader macros  : Son reglas para transformar el texto/codigo que escribimos a Estructura de datos

; Existe tambien el macro characters, like ' (the single quote), #, and @.

; Clojure al ver un ' lo expande a una lista cuyo primer elememento es el quote symbol y el segundo es la estructura de datos que sigue a ' en este caso es una lista pero puede ser otra estructura 
(read-string "'(a b c)")
; => (quote (a b c))


(read-string "'{:ke1 1 :key2 2}")
;(quote {:ke1 1, :key2 2})--aca el ejemplo de otra estructura


; Este es parecido al anterior solo que reemplaza @ por una lista cuyo primer elementos es la funcion clojure.core/deref y el segundo lo que se le paso 
(read-string "@var")
; => (clojure.core/deref var)

(read-string "; ignore!\n (+ 1 2)")
; => (+ 1 2)


; En resumen reader transforma lo que escribimos en estructuras que luego se pueder pasar al EVALUADOR

;;;;;;;;;The Evaluator::::::::::::::
; fUNCION QUE recibe la una Estructura de Datos, que bien puede ser producida por el reader o no y la procesa aplicando reglas y validaciones a la estructura con el fin de generar un resultado.

; Cuando el Eval en cuentra un symbol entonces busca la funcion a la que se refiero 
; Cuando encuentra una lista, obtiene el primer elemento y valida que tipo es , si es un special form, o es una funcion, o es otro macro

; ejemplo 
(+ 1 2); el reader lo transforna al DS que luego es pasado al Eval y este encuentra una lista: luego procede a buscar la funcion que represente al + que es la funcion addition: luego va por el segundo y detecta que no es funcion ni macro , por ende se lo pasa a la funcion, igual con el 3cer elemento; finalmente, retorna el resultado

; el proceso es diciente en la definicion de REPL first reads your text to get a data structure, then sends that data structure to the evaluator, and then prints the result as text.


; Estructuras que se evaluan a ellas mismas:

true
; => true

false
; => false

{}
; => {}

:huzzah
; => :huzzah

;:::::::::::::Symbols

;Looking up whether the symbol names a special form. If it doesn’t . . .
;Looking up whether the symbol corresponds to a local binding. If it doesn’t . . .
;Trying to find a namespace mapping introduced by def. If it doesn’t . . .
;Throwing an exception


; Ejemplos de special form
(if true :a :b)
; => :a
; Es especial porqie unsa el symbol if como primer elemento de la lista y es como el operador, pero no lo es

; si fuera operator al tratar de hacer lo sgte como el + no falaria
+
;#object[clojure.core$_PLUS_ 0x3e05eb94 "clojure.core$_PLUS_@3e05eb94"]

;pero si falla con if
;if

(let [x 5]
  (+ x 3))
; => 8

(def x 15)
(+ x 3)
; => 18


(def x 15)
; con let es un local binding, por lo que el x de def = 15 no se tomar[a en cuenta 
(let [x 5]
  (+ x 3))
x; sin preguntamos por el valor de x sigue siendo 15!!

;o aun si se anida mas tomara el del contexto anidado 
(let [x 5]
  (let [x 6]
    (+ x 3)))
; => 9


; ejemplo de manipulacion del Data Structure:
(read-string "+")
; => +

(type (read-string "+"))
; => clojure.lang.Symbol

(list (read-string "+") 1 2)
; => (+ 1 2)

; hasta ac[a no he hecho mas de maniplular la estructira como tal y no se manipula aun la funcion que represente el +
; pero ac[a si ya se va por la funcion y se aplica
(eval (list (read-string "+") 1 2))
; => 3


(map inc [1 2 3])
;(2 3 4)

; aca map es un symbol y no la funcion map
(read-string "map")
; => map

;se confirma lo anterior
(type (read-string "map"))
; => clojure.lang.Symbol


(eval (list (read-string "map inc") [1 2]))

; eval de lista vacia da la misma lista vacia
(eval (read-string "()"))
; => ()

(eval (read-string "(+ 1 2 (* 2 3))"))
;cuando clojure encuentra una funcion en como primer elemento de la lista entonces esto hace que se evaluen todos los demas elementos , hay elementos que se evaluan a esllos mismo como en este caso el 1 2 se evaluan a 1 2 pron son pasados a la funcion +Sin embargo hay una lista anidada que no se evalua a ella misma sino que de nuevo se repite el ciclo , al ser una lista el primer elemento es una funcion * que>> se evaluan los demas qelementos de la lista y se retorna 6 


;::::::::::::;Special Forms
(if true 1 2)
; especial porque implementan comportamientos que no se pueden implementar con funciones 
; ademas no siguen las reglas dadas para evaluar funciones , ya que por ejemplo el if no requiere evaluar toodos sus elementos 
(quote (a b c))

(quote (+ 1 2))
;(+ 1 2)  ' o quote lo que dice es que en ligar de evaluar retrone el data structure 

;def, let, loop, fn, do, and recur son special forms ya que no se evaluan de la misma forma que funciones 


;::::::::Macros:::::::::::::::::::::::
; Permite manipular el Data Structure que Clojure Evalua!!!

(read-string "(1 + 1)")
; => (1 + 1)

(let [infix (read-string "(1 + 1)")]
  (list (second infix) (first infix) (last infix)))
; => (+ 1 1)

(eval
 (let [infix (read-string "(1 + 1)")]
   (list (second infix) (first infix) (last infix))))
; => 2


; Macros se ejecutan entre el reader y Eval process, eso lo hace llamativo ya que no hay que evaluar para manipular

(defmacro ignore-last-operand
  [function-call]
  (butlast function-call))

(butlast [1 2 3])
;(1 2)

(ignore-last-operand (+ 1 2 10))
; => 3

;; This will not print anything
(ignore-last-operand (+ 1 2 (println "look at me!!!")))
; => 3
