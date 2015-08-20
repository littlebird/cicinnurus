(ns cicinnurus.svg
  (:require
   [cicinnurus.color :as color]
   [cicinnurus.circle :as circle]))

(defn circle
  ([[x y] r color] (circle [x y] r color {}))
  ([[x y] r color render]
   [:circle
    (merge
     {:cx x :cy y :r r
      :fill (color/hsb->hex color)}
     render)]))

(defn text
  ([content [x y] font size color] (text [x y] font size color {}))
  ([content [x y] font size color render]
   [:text
    (merge
     {:x x :y y
      :fill (color/hsb->hex color)
      :font-size size :font-family font
      :text-anchor "middle"
      :dominant-baseline "middle"}
     render)
    content]))

(defn group
  [things]
  (into [:g {}] things))

(defn transform-str
  [transform x y]
  (str (name transform) "(" x "," y ")"))

(defn transform
  [trans el [x y]]
  (let [s (transform-str trans x y)]
    (update-in
     el [1 :transform]
     (fn [transform]
       (if transform
         (str s " " transform)
         s)))))

(def translate (partial transform :translate))
(def scale (partial transform :scale))

(defn svg
  [mass width height]
  (let [outer
        [:svg
         {:xmlns "http://www.w3.org/2000/svg"
          :xmlns:svg "http://www.w3.org/2000/svg"
          :xmlns:xlink "http://www.w3.org/1999/xlink"
          :version "1.0"
          :width width
          :height height}]]
    (if (empty? mass)
      outer
      (conj outer mass))))

(defn fit-in
  [svg fit]
  (let [width (get-in svg [1 :width])
        height (get-in svg [1 :height])
        extreme (max width height)
        ratio (/ fit extreme)
        fit-width (* width ratio)
        fit-height (* height ratio)]
    (-> svg
        (update-in [2] scale [ratio ratio])
        (assoc-in [1 :width] fit)
        (assoc-in [1 :height] fit))))

(defn circle->svg
  [{:keys [center radius color render] :as attributes}]
  (if radius
    (circle center radius color render)
    attributes))

(defn generate-circle
  [center-range radius-range]
  (circle [(center-range) (center-range)] (radius-range) (color/random-color)))

(defn generate-circles
  [center-range radius-range n]
  (for [_ (range n)]
    (generate-circle center-range radius-range)))

(defn find-extreme
  [mass axis extend seek]
  (apply
   seek
   (map
    (fn [circle]
      (extend (-> circle :center axis) (:radius circle)))
    (filter :radius mass))))

(defn position-mass
  ([mass] (position-mass mass nil))
  ([mass fit]
   (if (empty? mass)
     (svg mass 1 1)
     (let [min-x (find-extreme mass first - min)
           max-x (find-extreme mass first + max)
           min-y (find-extreme mass last - min)
           max-y (find-extreme mass last + max)
           width (Math/ceil (- max-x min-x))
           height (Math/ceil (- max-y min-y))
           circles (map circle->svg mass)
           group (translate (group circles) (map #(Math/ceil (* -1 %)) [min-x min-y]))
           svg (svg group width height)]
       (if fit
         (fit-in svg fit)
         svg)))))

(defn emit
  [mass to]
  (svg (translate (group mass) to)))

(defn emit-circles
  ([circles] (emit-circles circles [0 0]))
  ([circles translate]
    (emit (map circle->svg circles) translate)))
