(ns cicinnurus.color
  (:require
   [clojure.string :as string]
   [cicinnurus.math :as math]))

(defn hex->rgb
  [hex]
  (let [[_ match] (re-find #"#?([0-9a-f]+)" hex)
        parts (map string/join (partition 2 match))]
    (mapv #(Integer/parseInt % 16) parts)))

(defn rgb->hex
  [rgb]
  (str "#" (string/join (map (partial format "%02x") rgb))))

(defn rgb->hsb
  [rgb]
  (let [[r g b] (map #(/ % 255.0) rgb)
        brightness (max r g b)
        darkness (min r g b)
        delta (- brightness darkness)
        saturation (if (zero? brightness) 0.0 (/ delta brightness))
        hue (if (zero? delta)
              0.0
              (* Math/PI
                 (/ (cond
                      (= r brightness) (- g b)
                      (= g brightness) (+ (- b r) 2)
                      :else (+ (- r g) 4))
                    (* 3 delta))))]
    [(mod hue math/TAU)
     saturation
     brightness]))

(defn hsb->rgb
  [[hue saturation brightness]]
  (let [unit-hue (/ hue math/TAU)
        six-hue (* 6 unit-hue)
        f (- six-hue (Math/floor six-hue))
        p (* brightness (- 1 saturation))
        q (* brightness (- 1 (* f saturation)))
        t (* brightness (- 1 (* (- 1 f) saturation)))]
    (mapv
     (comp int (partial * 255))
     (cond
       (< six-hue 1) [brightness t p]
       (< six-hue 2) [q brightness p]
       (< six-hue 3) [p brightness t]
       (< six-hue 4) [p q brightness]
       (< six-hue 5) [t p brightness]
       :else [brightness p q]))))

(def hex->hsb (comp rgb->hsb hex->rgb))
(def hsb->hex (comp rgb->hex hsb->rgb))

(defn random-color
  []
  [(* (rand) 2 Math/PI) (rand) (rand)])

(defn random-hex
  []
  (str "#" (format "%06x" (rand-int 16rFFFFFF))))

(defn color-range
  [[h-low h-high] [s-low s-high] [b-low b-high]]
  (let [h-range (comp #(mod % math/TAU) (math/random-range h-low h-high))
        s-range (math/random-range s-low s-high)
        b-range (math/random-range b-low b-high)]
    (fn []
      [(h-range) (s-range) (b-range)])))
