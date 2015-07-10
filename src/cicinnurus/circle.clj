(ns cicinnurus.circle)

(defn distance
  [a b]
  (Math/sqrt (reduce + (map (fn [a b] (let [d (- a b)] (* d d))) a b))))

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

(defn intersect?
  [a b]
  (let [d (distance (:center a) (:center b))]
    (< (+ d 0.1) (+ (:radius a) (:radius b)))))

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
         dx (- (* cx (Math/cos b-angle)) (* cy (Math/sin b-angle)))
         dy (+ (* cx (Math/sin b-angle)) (* cy (Math/cos b-angle)))]
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

(defn random-range
  [low high]
  (let [width (- high low)]
    (fn []
      (+ low (* (rand) width)))))

(defn random-color
  []
  (str "#" (format "%06x" (rand-int 16rFFFFFF))))

(defn circle
  [center radius color]
  {:center center :radius radius :force [0 0] :color color})

(defn distances
  [{a-center :center a-radius :radius} {b-center :center b-radius :radius} {c-radius :radius}]
  [(distance a-center b-center)
   (+ b-radius c-radius)
   (+ a-radius c-radius)])

(defn third-center
  ([a b c] (third-center a b c 1))
  ([a b c mirror]
   (let [[ab bc ac] (distances a b c)
         [ax ay] (:center a)
         [bx by] (:center b)
         point (third-point ab bc ac ax ay bx by mirror)]
     (assoc c :center point))))

(defn biggest-head
  [circles]
  (let [decreasing (sort-by :radius > circles)
        head (first decreasing)
        tail (rest decreasing)]
    (cons head (shuffle tail))))

(defn nan-center?
  [circle]
  (Double/isNaN (-> circle :center first)))

(defn step-tail
  [circles]
  (let [head (first circles)
        head (assoc head :center [0 0])
        neck (second circles)
        tail (drop 2 circles)
        down (+ (:radius head) (:radius neck))
        fall (add (:center head) [0 down])
        neck (assoc neck :center fall)]
    (loop [anchor 0
           here 1
           chain [head neck]
           tail tail
           off []
           sour 0]
      (if (empty? tail)
        (if (or (empty? off) (= sour (count off)))
          chain
          (recur anchor here chain off [] (count off)))
        (if (= anchor here)
          (recur 0 (dec (count chain)) chain (rest tail) (conj off (first tail)) sour)
          (let [toward (first tail)
                head (nth chain anchor)
                neck (nth chain here)
                third (third-center head neck toward)
                mirror (third-center head neck toward -1)]
            (if (or (nan-center? third) (some (partial intersect? third) chain))
              (if (or (nan-center? mirror) (some (partial intersect? mirror) chain))
                (let [shift (inc anchor)
                      length (count chain)]
                  (if (>= shift length)
                    (recur 0 (dec length) chain (rest tail) (conj off toward) sour)
                    (recur shift here chain tail off sour)))
                (recur anchor (inc here) (conj chain mirror) (rest tail) off sour))
              (recur anchor (inc here) (conj chain third) (rest tail) off sour))))))))

(defn twirl
  [circles]
  (loop [iterations 0]
    (let [layout (step-tail (biggest-head circles))]
      (if (or (> iterations 10) (= (count circles) (count layout)))
        layout
        (recur (inc iterations))))))

(defn generate-circle
  [center-range radius-range]
  (circle [(center-range) (center-range)] (radius-range) (random-color)))

(defn generate-circles
  [center-range radius-range n]
  (for [_ (range n)]
    (generate-circle center-range radius-range)))

