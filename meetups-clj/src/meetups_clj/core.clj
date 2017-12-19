(ns meetups-clj.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(foo "juliano")

;Ejemplo de Map 
(println "oeeeeee")

(def people [
{:id 1 :name "Edd" :age 27} 
{:id 2 :name "oredon" :age 30}
{:id 3 :name "Meta" :age 35}] )


(first people);;{:id 1, :name "Edd", :age 27}

(get (first people) :id);1

(:id (first people));1

;un map con multiples colecciones debe tener en la firma la cantidad de colecciones 

(map (fn  [p] (get p :id)) people)

(map (fn [p] (update p :age inc)) people)

(map (fn [person] (update person :age (fn [age]  (+ age 1)))) people)

;filter;;;;;;;;
;personas mayores a 28
(def viejos (filter (fn [person] (> (:age person) 28)) people))

(filter (fn [person] (< (:age person) 35)) people)

;Reduce 
;a las persnas  


(map (fn [joven] (assoc joven :child {:id 1 :name "el hijo"})) jovenes)



(reduce  (fn [acc-person person]
           (if (< 35 (:age person))
             (conj acc-person
                   (assoc person :child {:id 1 :name "otro name"}))
             acc-person)) 
         [] people)



(take 2 (range))

(defn factorial1 )

(reduce * 1 (range 1 5))

(defn mifactorial [num] (reduce * 1 (range 1 (inc num))))
(mifactorial 4)


(mod 4 2)


(range 1 9)

;filter; determinar si un numero es primo o no 
;usar el modulo, filter
()


(defn es-primo [num] () )




;dojo:
;una coleccion de pares de puntos1 y otra de puntos2
;Map calcular la distancia entre 2 puntos
;P1(x1 y1)
; (x1 -x2)2 + (y1 -y2)2



;Ejercicio
;contar Strings
;eje dado AAABBBCCAAAA   el resultado será A3B3C2A4
;partition-by 
(def listadoStr (partition-by identity "AAABBBCCAAAA"))
listadoStr

(first listadoStr)

(map count listadoStr )

(count listadoStr)

(map (fn [lista] ))

(defn ejercicioStr [my-string]
  (map count (partition-by identity my-string)))

(ejercicioStr  "AAABBBCCAAAA")


