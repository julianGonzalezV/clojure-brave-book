(ns the-divine-cheese-code.visualization.svg)

(defn latlng->point
  "Convert lat/lng map to comma-separated string" 
  [latlng]
  (str (:lat latlng) "," (:lng latlng)))

(latlng->point {:lat 80 :lng 70})
;"80,70"

(defn points
  [locations]
  (clojure.string/join " " (map latlng->point locations)))
