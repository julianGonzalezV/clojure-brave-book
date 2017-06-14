; chapter 5 Functional Programming::::::::::::::::::::::::
;::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

;Pure Functions: What and Why

; Una funcion es pura si 
; 1) Transparecia referencial : siempre retorna lo mismo, con los mismo argumentos
; 2) No causa side effect: Eto es que no afecta componentes, objetos externos , ejemplo escribir en un archivo, bd etc , cambio del valor de una variable global.



;1) Example:If a function relies on an immutable value, it’s referentially transparent, El string ", Daniel-san" es inmutable, por lo tanto la funcion wisdow se basae en ello y por eso es Referentially Transparent
(defn wisdom
  [words]
  (str words ", Daniel-san"))

(wisdom "Always bathe on Fridays")
;por mas que se llame siempre retornará
; => "Always bathe on Fridays, Daniel-san"



; lo que no sucede con esta:
(defn year-end-evaluation
  []
  (if (> (rand) 0.5)
    "You get a raise!"
    "Better luck next year!"))
(year-end-evaluation)



;
; usa String que es inmutable y si se le pasa el mismo text, siempre retornará lo mismo
(defn analysis
  [text]
  (str "Character count: " (count text)))

; Esta función ya no es referentially transparent 
; porque lee un archivo y el archivo puede cambiar de 
; contenido, pero la fun alalysis SI LO ES 
; lo anterior demuestra como puedo usar funciones RT(refT)
; y que no necesariamente si lo uso estoy siendo RT
(defn analyze-file
  [filename]
  (analysis (slurp filename)))

; USO 
(def file1-test "file1.txt")
(analyze-file file1-test)


; 2 Pure Functions Have No Side Effects

;En resumen es cambiar la relacion entre el NAMED VALUE y so Value dentro de un contexto dado.(MUTAR)
; Estilo lo que hacemos en java hacer Sets

; Obvio existen varias circunstancias en la que requerimos de ese SIDE ENFFECT , como lo es escribir en un archivo, BD , llamar a un servicio, configuraciones de X cosa.

; Lo difícil de todo esto es que no podemos llevarla traza de que, como y cuando mis compoenentes, variables se van a ver afectadas en un contexto y es cuando viene el debug Y SUPER DEBUGS , la idea es que eso desaparezca, Obviamente es difícil cambiar el chip pero nos toca, CLOJURE NO deja trabajar con mutables elements por eso a cambiar el chip.


;Living with Immutable Data Structures
; Estas estructuras lo que busca es evitar el side Effect y nos obliga a vivir con ellas :)

;Entonces como aprender, aquí lo que debemos ir cambiando:

;::::::::::::::::::::::::::::::::
;Recursion Instead of for/while
;::::::::::::::::::::::::::::
;
;var allPatients = getArkhamPatients();
;var analyzedPatients = [];
;var l = allPatients.length;

; y un for acá :) 

;1) demostración de que clojure no deja cambiar los value a un NamedVal

(def great-baby-name "Rosanthony")
great-baby-name
; => "Rosanthony"

;le solo permite cambiar en el contexto de la funcion
(let [great-baby-name "Bloodthunder"]
  great-baby-name)
; => "Bloodthunder"

great-baby-name
;sigue siendo el mismo valor inicial
; => "Rosanthony"


; la funcion suma lo que hace es iniciar el llamado con el vector de 
; de números a sumar y en el primer llamado de primero está el llamado a la funcion con accmulator en cero 0
;sí no es empty se llama a sum de nuevo con el resto de los 
;valores en el vector y acc sería la suma del primero de la lista
;y lo que lleve el acc hasta esa iteracion.
;LO QUE SOLUCIONA LA RECURSIVIDAD ES el problema de no saber que hacer si nos nos dejan mutar variables en un for por ejemplo
(defn sum
"funcion con 2 construectors uno con un solo arg y el otro con 2"
    ([vals] (sum vals 0))
  ([vals accumulating-total]
      (if (empty? vals)  
       accumulating-total
       (sum (rest vals) (+ (first vals) accumulating-total)))))


(sum [1 2 3 4] )

; En llamados recursivos como el anterios, clojure recomienda el uso de recur por questiones de performance.Note como al final se usa recur  en lugar del llamado a la funcion

(defn sum
  ([vals]
     (sum vals 0))
  ([vals accumulating-total]
     (if (empty? vals)
       accumulating-total
       (recur (rest vals) (+ (first vals) accumulating-total)))))
; No se estará creando demasiadas variables nuevas en cada iteracion y vamos a reventar??
; see http://hypirion.com/musings/understanding-persistent-vector-pt-1.

;Function Composition Instead of Attribute Mutation

;require to access the string function library
(require '[clojure.string :as s])
; funcion que primero (más interno) elimina el espacio final con trim ; Luego a ese resultado cabia lol por LOL 
; s/trim es una funcion pura que devuelve un inmmutable value
;s/replace tambien es una función pura e igual devuelve el inmmutable value.
(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))

(clean "My boa constrictor is so sassy lol!  ")
; => "My boa constrictor is so sassy LOL!"

;functional programming encourages you to build more complex functions by combining simpler functions.
; LO ANTERIOR de (f(x)) (g(x)) y que la salida de s/trim sea la entrada de s/replace se le conoce como composicion.

;programming to a small set of abstractions, you end up with more reusable, composable code. You gain power and lose nothing.

; Para resumir, lo anterior nos brinda beneficios como no tener que 
;preocuparnos en que niestro código se verá sucio etc


;::::::::::::::::::::::::::::::::::::::
;Cool Things to Do with Pure Functions
;:::::::::::::::::::::::::::::::::::::
; Derivar funciones de otras funciones.
; Derivar datos de otros datos.

; Por ejemplo el partial (visto en el chapter4), para la creación de nuevas funciones.
;Pero además existen otras comp and memoize que se basan en referencia transperente o inmutabilidad o ambas.

; :::::::::::::::COMP:::::::::
; Lo interesante de la composición es que solo nos tenemos que preocupar por las salidas de los computos de nuestras funciones, ya son las entradas de otras.

;Componer es innato en clojure que por eso tiene la función comp, 
;que crea una función a partir de varias.
;cabe notar que la primera función que se aplica es la * y recibe varias entrada pero de allí en adelante solo permite 1 , tener en cuenta que la salida de una sea una correcta entrada para la otra.
((comp inc *) 2 3 4)
;7


;

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :strength 4
                :dexterity 5}})

; se considera una forma elegante de hacer este tipo de busquedas :)
(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength :attributes))
(def c-dex (comp :dexterity :attributes))

; el int es de intellgence
(c-int character)
; => 10

(c-str character)
; => 4

(c-dex character)
; => 5

; o la manera larga de hacer la funcion c-str por ende com comp es mas elegante.
(def strength2 (fn [character] (:strength (:attributes character))))
(other-way character)
;4

; LA siguiente funcion toma c-int : Intelligence del Char que le 
;envíe y retorna 10 ; luego lo divide entre 2, como resultado retorna el 6, no obstante antes lo castea a int;
 (int 23.6)
;23
(defn spell-slots
  [char]
  (int (inc (/ (c-int char) 2))))

(spell-slots character)
;6

;Mire lo elegante que resulta con comp, simplemente use una funcion
;anónima y pásele el parametro que requiere es este caso c-int retorna 10 y se lo pasa a la función anónima (en el %) , que es la qe divide por 2; luego lo aumenta con la función inc y finalmente con la funcion int obtiene la parte Entera, ESTO ES COMPONER FUNCIONES Y CREAR FUNCIONES COMPLEJAS A PARTIR DE OTRAS MÁS PRQUEÑAS.
(def spell-slots-comp (comp int inc #(/ % 2) c-int))
(spell-slots-comp character)
;6

;Una idea de como esta implementado podría ser este ejemplo que compone dos funciones
(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))

(def comp1 (two-comp inc #(* % 5) ))
(comp1 2)

; TRATAR DE IMPLEMENTAR PARA QUE APLIQUE A VARIAS 


;::::::::::::::::::::::::::::::::::::::::::::
;memoize:::::::::::::::::
;Almacena los argumentos pasados a una funcion y retorna el valor que corresponde a dicho computo, sí no se ha hecho lo hace y almacena

;::::::::::::::::::::::::::::::::::::::::::::

(defn sleepy-identity
  "Returns the given value after 1 second"
  [x]
  (Thread/sleep 2000)
  x)
(sleepy-identity "Mr. Fantastico")
; => "Mr. Fantastico" after 1 second

(sleepy-identity "Mr. Fantastico")
; => "Mr. Fantastico" after 1 second


(def memo-sleepy-identity (memoize sleepy-identity))
(memo-sleepy-identity "Mr. Fantastico")
; => "Mr. Fantastico" after 1 second

; al llamarse la segunda vez es instantáneo por ser la misma entrada
;aprovecha el hecho de inmutabilidad y sobretoso transparecncia 
;referencial
(memo-sleepy-identity "Mr. Fantastico")
; => "Mr. Fantastico" immediately

;Obviamente al cambiar el argumento, pues tarda el computo
(memo-sleepy-identity "Mr. Fantastico2");
"Mr. Fantastico2"


;para operaciones complejas o que siempre se van a estar realizando esto es una ventaja!!.



