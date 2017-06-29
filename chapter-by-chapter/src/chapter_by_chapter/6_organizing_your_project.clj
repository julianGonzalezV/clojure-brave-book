; Organizing your project:
; Imaginemos que clojure posee puros objetos de almacenamiento, SI LAS FUNCIONES Y ESTRUCTURAS SON OBJECTOS que finalmente almacenan cosas
;:::::::::::::::::

;namespaces :
; Namespaces mapea un identificador a un set de objetos que contiene , es de tipo clojure.lang.Namespace
;y podemos referirnos a estos de la misma forma como lo hacemos con otros objetos (funciones, estructuras* etc)
;eg
;con *ns* por ejemplo nos referimos a namespace actual que es user por defecto, o en el que estamos parados
(ns-name *ns*)
;user++




;en clojure siempre estamos en un namespace

;::::::::::::::::::::::::
;Symbols are data types within Clojure,: Por lo cual la función map , inc , dec son simbolos

inc
; representacion textual de la funcion a la que inc se refiere
; => #<core$inc clojure.core$inc@30132014>

'inc
; => inc   : Retorna inc que sería el símbolo sin ser evaluado inc 

'(inc 3)
;(inc 3); retorna el simbolo tratado com un dato y un número
;EXISTIRAN CASOS en el que se necesite que solo retorne el simbolo sin evaluar(más adelante)  

(inc 3); acá SI LO EVALUA
;4

(map inc [1 2])
; => (2 3)

'(map inc [1 2])
; => (map inc [1 2])




;::::::::::::::::::::::

;::::::::::::::::::::::
;Storing Objects with def
; def realmente es el primer tool que tenemos para almacenar objetos ,de hecho  defn se basa en éste.

(def great-books ["East of Eden" "The Glass Bead Game"])
; => #'user/great-books : esto es in interned var en donde queda el valor del vector

;para ver las variables internas que tengamos entonces:
(ns-interns *ns*)
;{my-first #'chapter-by-chapter.core/my-first, error-message #'chapter-by-chapter.core/error-message, x #'chapter-by-chapter.core/x, matching-part #'chapter-by-chapter.core/matching-part, symmetrize-body-parts #'chapter-by-chapter.core/symmetrize-body-parts, inc-by-input #'chapter-by-chapter.core/inc-by-input, variable1 #'chapter-by-chapter.core/variable1, dalmatian-list #'chapter

; o directamente la función que obtiene los var internos, ejemplo:
(get (ns-interns *ns*) 'great-books)
;#'chapter-by-chapter.core/great-books : este resultado se conoce como  reader form de un var

great-books
; => ["East of Eden" "The Glass Bead Game"]


;con #'chapter-by-chapter.core/great-books   se obtiene el var ASOCIADO CON EL SÍMBOLO great-books
; Y con deref se obtiene el objeto a que este apunta o almacena
(deref #'chapter-by-chapter.core/great-books )
;["East of Eden" "The Glass Bead Game"]


;pero en la mayoría de casos preferimos solo usar el SÍMBOLO 
great-books

; => ["East of Eden" "The Glass Bead Game"]


;::::::::y SI VOLVEMOS A REASIGNAR EL SIMBOLO great-books

(def great-books ["The Power of Bees" "Journey to Upstairs"])
;#'chapter-by-chapter.core/great-books
great-books
;["The Power of Bees" "Journey to Upstairs"]

;le asignó otro Vector!! y ya se pierde la relacioón del vector anterior , a esto se le conoce como name collision. Chaos! Anarchy!
; 

; es lo mismo como en java OO en donde por error o porque queremos reasignamos valores a variables scopes 
; y sin querer aveces generamos el caos
;::::::::::::::::::::::


;::::::::::::::::::::::::::::::::::::::::::::
;Creating and Switching to Namespaces
;3 formas de crear namespaces 

; 1) con create-ns: recibe un symbol y crea un namespace con dicho nombre 
;y finalmente, retorna el nombre del namespace
(create-ns 'cheese.taxonomy)
; => #<Namespace cheese.taxonomy>

;También podemos pasar el nombre que retorna y componer con otra funcion que reciba dicho nombre,
;por ejemplo ns-name retorna el nombre del namespace 
(ns-name (create-ns 'cheese.taxonomy))
; => cheese-taxonomy

; si se llama ns-name con un nombre que no exista se genera error porque no existe.


;::2) con in-ns, crear el namespace y hace switching a este

(in-ns 'cheese.analysis)
;#object[clojure.lang.Namespace 0x1dd4dd5a "cheese.analysis"]
; note como en el pie de página de buffer dide clojure cide nombre del ns en ql eue estoy

cheese.taxonomy=> (def cheddars ["mild" "medium" "strong" "sharp" "extra sharp"])
;#'cheese.analysis/cheddars
cheddars
;["mild" "medium" "strong" "sharp" "extra sharp"]

;Sí ejecuto la siguiente línea se genera error
;great-books

; pero si le especifico el namespace al que pertenece + el symbol a cargar ahí si funciona 
;esta sería la forma de usar funciones de otros ns u archivos
;
chapter-by-chapter.core/great-books
;["The Power of Bees" "Journey to Upstairs"]

;:::::::::::::::::::::::
;::::::::::::refer:::::::::
;:::::::::::::::::::::::

; Note como deste el namespace cheese.analysi se hace ref a cheese.taxonomy

user=> (in-ns 'cheese.taxonomy)
cheese.taxonomy=> (def cheddars ["mild" "medium" "strong" "sharp" "extra sharp"])
cheese.taxonomy=> (def bries ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"])
cheese.taxonomy=> (in-ns 'cheese.analysis)
cheese.analysis=> (clojure.core/refer 'cheese.taxonomy)
cheese.analysis=> bries
; => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]

cheese.analysis=> cheddars
;o estando en cheese.analisis simplemente llamamos el objeto del otro namespace 
cheddars
; => ["mild" "medium" "strong" "sharp" "extra sharp"]
; no va a funcionar porque no lo he incluido en el namespace
(+ 1 2)

;TAMBIEN  SE puede usar refer con filter como :only, :exclude, and :rename
cheese.taxonomy=> (def bries2 ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"])
cheese.taxonomy=> (def bries3 ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"])
cheese.analysis=> (clojure.core/refer 'cheese.taxonomy :exclude  ['bries2])
cheese.analysis=> bries2
; => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
cheese.analysis=> cheddars 
; => RuntimeException: Unable to resolve symbol: cheddars

;ejemplo de rename 
cheese.analysis=> (clojure.core/refer 'cheese.taxonomy :rename {'bries 'yummy-bries})
cheese.analysis=> bries
; => RuntimeException: Unable to resolve symbol: bries
cheese.analysis=> yummy-bries
; => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]


; Si se desean funciones que solo se puedan usar en un namespace en especial, entonces creamos
;funciones privadas con def-

;ejemplo;
;chapter-by-chapter.core/great-books
(in-ns 'cheese.analysis
)
(refer 'clojure.core :exclude ['println])
;; Notice the dash after "defn"
(defn- private-function
  "Just an example function that does nothing"
  [])


;::::::::::::::::ALIAS::::::::::::


cheese.analysis=> (clojure.core/alias 'taxonomy 'cheese.taxonomy)
cheese.analysis=> taxonomy/bries
; => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
;::::::::::::::::::::::::::::::::::::::::::::


;:::::::::::::::::Real Project Organization:::::::::::::::::::::::::

;::require and use Ver ejemplo de uso en proyecto /home/ubuntu/Documents/study/clojure/clojure-for-the-brave-and-true/clojure-brave-book/the-divine-cheese-code/src/the_divine_cheese_code/visualization


;The ns Macro:::::::::::::::::::::::

;Una llamada a ns incluye a require, use, in-ns, alias, and refer, incluso ya viene con el clojure.core, que permire usar el print, + - * etc 


;There are six possible kinds of references within ns:

   ; (:refer-clojure)
    ;(:require)
   ; (:use)
   ; (:import)
   ; (:load)
    ;(:gen-class)

 ; 1) Require*********
; hay dos formas de usar require I) como referencia ':require' o como funcion (require xx)

; usuando referencia(los dos puntos), por ejemplo podemos usar refer sobre los nombre de los simbolso
;(ns the-divine-cheese-code.core
;(:require [the-divine-cheese-code.visualization.svg :refer [points]]))

; Con require como funcion necesitariamos mas cod (ver libro)


; 2) Use 
; El libro recomienda NO USARLO 

(ns the-divine-cheese-code.core
  (:use [clojure.java browse io]))

; ES equivalente a esto

(in-ns 'the-divine-cheese-code.core)
(use 'clojure.java.browse)
(use 'clojure.java.io)
; Note como con use como ref usando : no requiere un use por cada library a usar 

; HASTA ACA EL USO DE NS

;volver al proyecto  the-divine-cheese para continuar el capitulo 6

