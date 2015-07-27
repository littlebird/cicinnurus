(ns cicinnurus.core
  (:require
   [cicinnurus.math :as math]
   [cicinnurus.color :as color]
   [cicinnurus.circle :as circle]
   [cicinnurus.svg :as svg]))

(defn generate-svg-circles
  [center [radius-low radius-high] translate n]
  (let [circles (circle/generate-circles
                 (math/random-point center center 1)
                 (math/random-range radius-low radius-high)
                 color/random-color
                 n)
        twirling (circle/twirl circles)]
    (svg/position-mass twirling)))

(defn generate-circle-border
  [x-range y-range [radius-low radius-high] random-color center-power radius-power n]
  (let [circles (circle/generate-circles
                 (math/random-point x-range y-range center-power)
                 (math/random-power radius-low radius-high radius-power)
                 random-color
                 n)]
    (svg/position-mass (sort-by :radius > circles))))
