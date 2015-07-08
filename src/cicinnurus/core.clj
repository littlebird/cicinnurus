(ns cicinnurus.core
  (:require
   [cicinnurus.circle :as circle]
   [cicinnurus.svg :as svg]))

(defn generate-svg-circles
  [[center-low center-high] [radius-low radius-high] translate n]
  (let [circles (circle/generate-circles
                 (circle/random-range center-low center-high)
                 (circle/random-range radius-low radius-high)
                 n)
        twirling (circle/twirl circles)]
    (svg/emit-circles twirling translate)))
