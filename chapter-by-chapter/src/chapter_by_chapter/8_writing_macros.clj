

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



