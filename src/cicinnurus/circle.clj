(ns cicinnurus.circle
  (:require
   [cicinnurus.math :as math]
   [cicinnurus.color :as color]))

(defn circle
  [center radius color]
  {:center center :radius radius :force [0 0] :color color})

(defn intersect?
  [a b]
  (let [d (math/distance (:center a) (:center b))]
    (< (+ d 0.1) (+ (:radius a) (:radius b)))))

(defn distances
  [{a-center :center a-radius :radius} {b-center :center b-radius :radius} {c-radius :radius}]
  [(math/distance a-center b-center)
   (+ b-radius c-radius)
   (+ a-radius c-radius)])

(defn third-center
  ([a b c] (third-center a b c 1))
  ([a b c mirror]
   (let [[ab bc ac] (distances a b c)
         [ax ay] (:center a)
         [bx by] (:center b)
         point (math/third-point ab bc ac ax ay bx by mirror)]
     (assoc c :center point))))

(defn biggest-head
  [circles]
  (if (empty? circles)
    circles
    (let [decreasing (sort-by :radius > circles)
          head (first decreasing)
          tail (rest decreasing)]
      (cons head (shuffle tail)))))

(defn nan-center?
  [circle]
  (Double/isNaN (-> circle :center first)))

(defn step-tail
  [circles]
  (if (< (count circles) 2)
    circles
    (let [head (first circles)
          head (assoc head :center [0 0])
          neck (second circles)
          tail (drop 2 circles)
          down (+ (:radius head) (:radius neck))
          fall (math/add (:center head) [0 down])
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
                (recur anchor (inc here) (conj chain third) (rest tail) off sour)))))))))

(defn twirl
  [circles]
  (loop [iterations 0]
    (let [layout (step-tail (biggest-head circles))]
      (if (or (> iterations 10) (= (count circles) (count layout)))
        layout
        (recur (inc iterations))))))

(defn generate-circle
  ([center-range radius-range] (generate-circle center-range radius-range color/random-color))
  ([center-range radius-range random-color]
   (circle (center-range) (radius-range) (random-color))))

(defn generate-circles
  [center-range radius-range random-color n]
  (for [_ (range n)]
    (generate-circle center-range radius-range random-color)))
