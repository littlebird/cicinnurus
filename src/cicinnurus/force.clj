(ns cicinnurus.force
  (:require
   [cicinnurus.circle :as c]))

(def whirl-force -1.0)
(def repel-force 1000)

(defn whirl
  [a]
  (update-in
   a [:force]
   (fn [force]
     (c/add force (c/scale (c/normalize (:center a)) whirl-force)))))

(defn repel
  [a b]
  (update-in
   a [:force]
   (fn [force]
     (let [d (c/distance (:center a) (:center b))
           direction (c/normalize (c/subtract (:center a) (:center b)))]
       (c/add force (c/scale direction (* repel-force (/ 1.0 (* d d)))))))))

(defn relate
  [a b]
  [(repel a b) (repel b a)])

(defn apply-force
  [a]
  (-> a
      (update-in [:center] (partial c/add (:force a)))
      (assoc :force [0 0])))

(defn mass-relate
  [circles]
  (c/iterate-pairs relate circles))

(defn iterate-mass
  [circles]
  (let [related (mass-relate circles)]
    (map (comp apply-force whirl) related)))

