

;::::::::::::::::::Macros Are Essential:::::::::::::::::::::::::


(macroexpand '(when boolean-expression
                expression-1
                expression-2
                expression-3))
;(if boolean-expression (do expression-1 expression-2 expression-3))


;::::::::::::Anatomy of a Macro:::::::::::::::

; Muy similar a la de una funcion, con su body , comentario o documentacion
; recordar que una de las diferencias es que no evalua los argumentos de entrada 



(defmacro infix
  "Use this macro when you pine for the notation of your childhood"
  [infixed]
  (list (second infixed) (first infixed) (last infixed)))

(infix (1 + 1))
; => 2


;Verifica la estructura generada por un macro antes de enviarla al Eval 
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


;::::::::::::::::::::Building Lists for Evaluation:::::::::::
