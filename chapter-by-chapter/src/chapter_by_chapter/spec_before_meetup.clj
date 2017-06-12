(ns chapter-by-chapter.spec_before_meetup 
  (:gen-class)
  (:require [clojure.spec :as s]))


(+ 1 4)
(s/conform even? 1000)

;Lo siguiente retorna un clojure.spec/invalid  
(s/conform even? 1001)

(s/valid? even? 1000)

; si nos parece tedioso un clojure.spec/ivalid entonces 
;;usamos un valid
(s/valid? even? 1001)

(s/valid? string? "abc")

(s/valid? string? 123)

;Registry, forma de crear specs en el namespace para luego poder reusarlos, hasta este momento habiamos creado specs puros y nos tocaba reescribirlos en otro lugar.

(s/def ::date inst? js/Date )
                                        ; en este ejemplo el spec se llama date

                                        ; Es este el spec se llama fruitSuit y se define el rango permitido


(s/def ::fruit-suit #{:orange :banana :apple})

                                        ;How to call them?R/ The same as defautl spec written above 
                                        ;,with conform or vali

(s/valid? ::date (js/Date.))


(s/conform ::fruit-suit :banana)
(s/valid? ::fruit-suit :orang)

;Componiendo predicados, con and u or 

(s/def ::gran-entero-par (s/and int?  even? #(> % 1000)))
;En and en este caso pregunta relaciona las 3 funciones que sea int, even y que sea mayor que 1000, si alguno no cumple entonces false


(s/valid? ::gran-entero-par 2001)

(s/def ::name-or-id (s/or :name string? :id int?))

(s/valid? ::name-or-id "julian")
(s/valid? ::name-or-id 1116237362)

(s/valid? string? nil);false
(s/valid? (s/nilable string?) nil)
;true, debe usarse la funcion nilable para hacer el spec 

(s/explain ::fruit-suit 42)
;; val: 42 fails spec: ::suit predicate: #{:spade :heart :diamond :club}
(s/explain ::big-even 5)
;; val: 5 fails spec: ::big-even predicate: even?
(s/explain ::name-or-id :foo)
;; val: :foo fails spec: ::name-or-id at: [:name] predicate: string?
;; val: :foo fails spec: ::name-or-id at: [:id] predicate: int?


; During meetup;


;Entity Map:
(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::acctid int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type)

(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))

(s/valid? ::person {::first-name "julian"
                    ::last-name "gonzalez"
                    ::email "j.and@gmail.com"} )

(s/explain ::person {::first-name "julian"
                    ::last-name "gonzalez"
                    ::email "j.andgmail.com"} )

(s/explain ::person {::first-name "julian"            
                    ::email "j.and@gmail.com"} )



;unqualified keys on map 
(s/def :unq/person
  (s/keys :req-un [::first-name ::last-name ::email]
          :opt-un [::phone]))

(s/valid? :unq/person
  {:first-name "Elon"
   :last-name "Musk"
   :email "elon@example.com"})


;tambien se pueden validar los atrubutos de un registro

(defrecord Persona [first-name last-name email phone])
(s/valid? :unq/person (->Persona "julian" "gonzalez" "j@gmail.com" nil))



;Para el uso de s/merge , que se trata de combinar varios s/key

(s/def :animal/kind string?)
(s/def :animal/says string?)
(s/def :animal/common (s/keys :req [:animal/kind :animal/says]))
(s/def :dog/tail? boolean?)
(s/def :dog/breed string?)

;Vamos a componer el animal dog
;util cuando tengo varios spec 
(s/def :animal/dog (s/merge :animal/common
                            (s/keys :req [:dog/tail? :dog/breed])))
(s/explain :animal/dog
  {:animal/kind "dog"
   :animal/says "woof"
   :gog/tal  true 
   :dog/breed "retriever"})
;;=> true


;----------------------------------------
;During meetup 

;ins significaba instante 						

; defmulti
;multimethod 
;forma util de aplicar polimosfirmsoooo, el ejemplo que da de eventos dice que hay varios tipos de eventos

;collections
;se puede definir que tengan un tamanio, ver documentacion

;puede formalizar mapas , que estructura debe tener el mapa



