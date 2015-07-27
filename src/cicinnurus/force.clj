(ns cicinnurus.force
  (:require
   [cicinnurus.math :as math]
   [cicinnurus.circle :as circle]))

(def whirl-force -1.0)
(def repel-force 1000)

(defn whirl
  [a]
  (update-in
   a [:force]
   (fn [force]
     (math/add force (math/scale (math/normalize (:center a)) whirl-force)))))

(defn repel
  [a b]
  (update-in
   a [:force]
   (fn [force]
     (let [d (math/distance (:center a) (:center b))
           direction (math/normalize (math/subtract (:center a) (:center b)))]
       (math/add force (math/scale direction (* repel-force (/ 1.0 (* d d)))))))))

(defn relate
  [a b]
  [(repel a b) (repel b a)])

(defn apply-force
  [a]
  (-> a
      (update-in [:center] (partial math/add (:force a)))
      (assoc :force [0 0])))

(defn mass-relate
  [circles]
  (math/iterate-pairs relate circles))

(defn iterate-mass
  [circles]
  (let [related (mass-relate circles)]
    (map (comp apply-force whirl) related)))

