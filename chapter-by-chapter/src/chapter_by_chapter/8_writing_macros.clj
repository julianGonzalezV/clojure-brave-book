

;::::::::::::::::::Macros Are Essential:::::::::::::::::::::::::


(macroexpand '(when boolean-expression
                expression-1
                expression-2
                expression-3))
; al compilarlo vemos que when est[a escritos en terminnos de if!!
; macro expand permite verificar la funcion en terminos de lo que va a hacer vs su estructura, lo que nos permite analizar si eso que deseamos va a funcionar
;(if boolean-expression (do expression-1 expression-2 expression-3))


;::::::::::::Anatomy of a Macro:::::::::::::::

; Muy similar a la de una funcion, con su body , comentario o documentacion
; recordar que una de las diferencias es que no evalua los argumentos de entrada 
; Ademas permite extender el lenguaje como tal, podemos crear nuestras propias estructuras!!


(defmacro infix
  "Use this macro when you pine for the notation of your childhood"
  [infixed]
  (list (second infixed) (first infixed) (last infixed)))

(infix (1 + 1))
; => 2


;Verifica (con macroexpand) la estructura generada por un macro antes de enviarla al Eval 
;ademas esta es una prueba de que macros no evaluan los argumentos de entrada, por lo cual lo que hizo fue trabajar con cada elemento que se le pas[o
(macroexpand '(infix (1 + 1)))
;(+ 1 1)


;tambien se puede usar desturcturing arguments , que ayuda a escribir el codigo de una manera mas elegante, ejemplo :
(defmacro infix-2
  [[operand1 op operand2]]
  (list op operand1 operand2))

(infix-2 (3 + 4))
;7


; tambien se pueden escribir Macros de Multiple aridad:
; Ejemplo de 3 aridades , empty, one and more than one 

; And y Or est[an definidos como marcos!!! en el core del clojure!!

; Macros also can use rest args using "&" 
(defmacro and
  "Evaluates exprs one at a time, from left to right. If a form
  returns logical false (nil or false), and returns that value and
  doesn't evaluate any of the other expressions, otherwise it returns
  the value of the last expr. (and) returns true."
  {:added "1.0"}
  ([] true)
  ([x] x)
  ([x & next]
   `(let [and# ~x]
      (if and# (and ~@next) and#))))

(and )
;true




;::::::::::::::::::::Building Lists for Evaluation:::::::::::
; quote expressions para que no se evaluen las estructuras de datos que se pasen


;Distinguishing Symbols and Values
 
;el siguiente genera error porque est[a tranatndo de obtener el value al que el sybol let hace referencia
 (defmacro my-print-whoopsie
  [expression]
  (list let [result expression]
        (list println result)
        result))

; la version con las comillas permiten es retornar el simbolo y no el value , si se quitan entonces clojure intentar[a resolver el let que liga result con expression
; esta funcion imprime y retorna el value al final
(defmacro my-print
  [expression]
  (list 'let ['result expression]
        (list 'println 'result)
        'result))

(my-print (+ 1 2))


; simple quote 

; se puede usar 

'(+ 1 2)
;(+ 1 2)

;o tambien

(quote (+ 1 2))
;(+ 1 2)

; MAS EJEMPLO DE USO DE LAS COMILLAS SIMPLES

(defmacro when
  "Evaluates test. If logical true, evaluates body in an implicit do."
  {:added "1.0"}
  [test & body]
  (list 'if test (cons 'do body)))

;Cual es el motivo entonces de la comilla simple ac[a
;R/ Es porque deseo que al final se retorne una lista
;Unable to resolve symbol: if in this context

;Uso
(macroexpand '(when (the-cows-come :home)
                (call me :pappy)
                (slap me :silly)))
;salida 
;(if (the-cows-come :home) (do (call me :pappy) (slap me :silly)))

; recordar que cons es de la forma (cons x seq) donde x es el primer elemento de la ;ista y seq es el resto

(cons 2 '(4 5 6))
;(2 4 5 6)


(defmacro unless
  "Inverted 'if'"
  [test & branches]
  (conj (reverse branches) test 'if))

(macroexpand '(unless (done-been slapped? me)
                      (slap me :silly)
                      (say "I reckon that'll learn me")))
;(if (done-been slapped? me) (say "I reckon that'll learn me") (slap me :silly))




;:::::::::::::::::::Syntax Quoting:::::::::::::::::::::::::::
; es la comilla inclinada `
; note la diferencia como esta comilla muestra el namespace

'+
; => 

'clojure.core/+
; => clojure.core/+

`+
; => clojure.core/+

'(+ 1 2)
; => (+ 1 2)

`(+ 1 2)
; => (clojure.core/+ 1 2)

;importante para evitar el name collisions,en donde si no le deciamos el namespace generaba error al momento de tener que se tuvieran dos funciones con el mismo nombre

;Hacer uso de syntax quoting introduce la funcion de unquoting, eje


`(+ 1 ~(inc 1))
; => (clojure.core/+ 1 2)



`(+ 1 (inc 1))
; => (clojure.core/+ 1 (clojure.core/inc 1))

; Es como una especie de String intrpolation, que nos permiten crear pantillas!! 

(comment 

name = "Jebediah"
"Churn your butter, " + name + "!"


;or through interpolation:

"Churn your butter, #{name}!"
)


;note el vs de quoting and sintax quoting 

(list '+ 1 (inc 1))
; => (+ 1 2)

; note como este es mas conciso
`(+ 1 ~(inc 1))
; => (clojure.core/+ 1 2)


;Using Syntax Quoting in a Macro

; SIN 

(defmacro code-critic
  "Phrases are courtesy Hermes Conrad from Futurama"
  [bad good]
  (list 'do
        (list println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))

;como bad and good no sosn symbols entonces se deben de anteponer con el la funcion quote

(code-critic (1 + 1) (+ 1 1))
;note en los mensajes que no se resuelve la suma
; => Great squid of Madrid, this is bad code: (1 + 1)
; => Sweet gorilla of Manila, this is good code: (+ 1 1)



; CON SINTAX QUOTING
;note que la suma no se resuelve
;Se evita las repeticiones de List , sintactic sugar !
(defmacro code-critic2
  "Phrases are courtesy Hermes Conrad from Futurama"
  [bad good]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))

(code-critic2 (1 + 1) (+ 1 1))
; => Great squid of Madrid, this is bad code: (1 + 1)
; => Sweet gorilla of Manila, this is good code: (+ 1 1)


;Refactoring a Macro and Unquote Splicing


;vamos a refactorizar code-critic

;1) note como se repiten los ptinln en el code-critic2 (y 1 tambien)
;entonces esas repeticiones se pueden sacar en una funcion aparte criticize-code
;recibe la critica y el codigo y reotrna un sintax quoting que representa una lista, donde el primer elemento es la funcion println el segundo en criticism y el tercero el quote de code
(defn criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))


; el resultado es un codigo mas clareo :)
(defmacro code-critic3
  [bad good]
  `(do ~(criticize-code "Cursed bacteria of Liberia, this is bad code:" bad)
       ~(criticize-code "Sweet sacred boa of Western and Eastern Samoa, this is good code:" good)))


(code-critic3 (1 + 1) (+ 1 1))

; pero aun se puede mejorar mas!
; podemos ver que de igual forma se sigue llamando una funcion(criticize-code) varias veces (2)

;llamandolo con el map

(map  #(apply criticize-code %) [["Cursed bacteria of Liberia, this is bad code:" 1]
                                 ["Sweet gorilla of Manila, this is good code:" 1]])
;((clojure.core/println "Cursed bacteria of Liberia, this is bad code:" (quote 1)) (clojure.core/println "Sweet gorilla of Manila, this is good code:" (quote 1)))

;note como ya se llama una sola vez
(defmacro code-critic4
  [bad good]
  `(do ~(map #(apply criticize-code %)
             [["Great squid of Madrid, this is bad code:" bad]
              ["Sweet gorilla of Manila, this is good code:" good]])))

;sin embargo esto causa un NullPointer cual es el motivo ?
; si vemos arriba aplicando el map el resultado es un list de prints y eso cuando se evalua es un nil
(println "hola")
;imprime en consola hola pero evalua a nil
;volviendo al ejercicio entonces Do sobre nil genera Nu;; pointer
(code-critic4 (1 + 1) (+ 1 1))


;si solamente se aplica unquoting entonces:
`(+ ~(list 1 2 3))
; => (clojure.core/+ (1 2 3))



; y si se aplica unquoting splicing, entonces elimina el sequence structure o wraper
;y saca los datos dejandolos en el quoting que inicia el de antes de mas +
`(+ ~@(list 1 2 3))
; => (clojure.core/+ 1 2 3)

;recordar que sin unquoting, lo que hace es no evaluar nada y retornar el namespace
`(+ (list 1 2 3))
;(clojure.core/+ (clojure.core/list 1 2 3))

(defmacro code-critic5
  [bad good]
  `(do @~(map #(apply criticize-code %)
              [["Great squid of Madrid, this is bad code:" bad]
               ["Sweet gorilla of Manila, this is good code:" good]])))
