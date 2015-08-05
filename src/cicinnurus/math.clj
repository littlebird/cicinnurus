(ns cicinnurus.math)

(def TAU (* Math/PI 2))

(defn distance
  [a b]
  (Math/sqrt
   (reduce
    +
    (map
     (fn [a b]
       (let [d (- a b)]
         (* d d)))
     a b))))

(defn add
  [a b]
  (map + a b))

(defn subtract
  [a b]
  (map - a b))

(defn scale
  [a x]
  (map (partial * x) a))

(defn normalize
  [a]
  (let [d (distance a (repeat 0))]
    (scale a (/ 1.0 d))))

(defn rotate
  [[x y] angle]
  (let [dx (- (* x (Math/cos angle)) (* y (Math/sin angle)))
        dy (+ (* x (Math/sin angle)) (* y (Math/cos angle)))]
    [dx dy]))

(defn area->radius
  [a]
  (Math/sqrt (/ a Math/PI)))

(defn sides->angle
  "gives the angle opposite side c"
  [a b c]
  (let [num (+ (* a a) (* b b) (* c c -1))
        den (* 2 a b)]
    (Math/acos (/ num den))))

(defn third-point-wrong-somehow
  ([ab bc ac ax ay bx by] (third-point-wrong-somehow ab bc ac ax ay bx by -1))
  ([ab bc ac ax ay bx by mirror]
   (let [a-angle (sides->angle ab ac bc)
         b-angle (* -1 mirror (sides->angle ab bc ac))
         tan-a (Math/tan a-angle)
         tan-b (Math/tan b-angle)
         offset (- ay (* tan-a ax))
         cx (/
             (+ (* tan-a ax) (* -1 tan-b bx) by (* -1 ay))
             (- tan-a tan-b))
         cy (+ (* tan-a cx) offset)]
     [cx cy])))

(defn third-point
  ([ab bc ac ax ay bx by] (third-point ab bc ac ax ay bx by -1))
  ([ab bc ac ax ay bx by mirror]
   (let [[cx cy] (normalize (subtract [ax ay] [bx by]))
         b-angle (* -1 mirror (sides->angle ab bc ac))
         [dx dy] (rotate [cx cy] b-angle)]
     (add [bx by] (scale [dx dy] bc)))))

(defn all-pairs
  [f coll]
  (loop [[x & xs] coll
         result []]
    (if (nil? xs)
      result
      (recur xs (concat result (map (partial vector x) xs))))))

(defn iterate-pairs
  [f coll]
  (loop [[x & xs] coll
         result []]
    (if (nil? xs)
      (conj result x)
      (let [[x ys] (reduce
                    (fn [[x ys] y]
                      (let [[xn yn] (f x y)]
                        [xn (conj ys yn)]))
                    [x []] xs)]
        (recur ys (conj result x))))))

(defn random-angle
  []
  (* TAU (rand)))

(defn random-range
  [low high]
  (let [width (- high low)]
    (fn []
      (+ low (* (rand) width)))))

(defn random-power
  [low high power]
  (let [width (- high low)]
    (fn []
      (+ low (* (Math/pow (rand) power) width)))))

(defn random-point
  [[x-low x-high] [y-low y-high] power]
  (let [width (- x-high x-low)
        height (- y-high y-low)]
    (fn []
      (let [magnitude (Math/pow (rand) power)
            angle (random-angle)
            point (rotate [magnitude 0.0] angle)
            [x y] (add (scale point 0.5) [0.5 0.5])]
        [(+ (* x width) x-low) (+ (* y height) y-low)]))))

(defn border-range
  [low high power]
  (let [width (- high low)]
    (fn []
      (let [sign (if (<= (rand) 0.5) -0.5 0.5)
            border (+ 0.5 (* sign (Math/pow (rand) power)))]
        (+ low (* border width))))))

