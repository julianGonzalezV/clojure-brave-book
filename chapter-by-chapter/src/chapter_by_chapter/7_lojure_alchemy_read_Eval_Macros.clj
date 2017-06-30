;Clojure Alchemy: Reading, Evaluation, and Macros

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
