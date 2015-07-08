(ns cicinnurus.svg
  (:require
   [analemma.svg :as svg]
   [analemma.xml :as xml]
   [cicinnurus.circle :as circle]))

(defn generate-circle
  [center-range radius-range]
  (svg/circle (center-range) (center-range) (radius-range) :fill (circle/random-color)))

(defn generate-circles
  [center-range radius-range n]
  (for [_ (range n)]
    (generate-circle center-range radius-range)))

(defn circle->svg
  [{:keys [center radius color]}]
  (svg/circle (first center) (last center) radius :fill color))

(defn emit
  [mass [tx ty]]
  (xml/emit (svg/svg (svg/translate (apply svg/group mass) tx ty))))

(defn emit-circles
  [circles translate]
  (emit (map circle->svg circles) translate))
