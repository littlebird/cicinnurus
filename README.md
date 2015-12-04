# cicinnurus

Generating exotic nestings of svg

<p align="center">
<img src="https://github.com/littlebird/cicinnurus/blob/master/resources/public/img/cicinnurus.jpg">
</p>

## Usage

Cicinnurus was originally intended as a circle packing library, but grew to encompass color, math and svg functionality as well.  

To add Cicinnurus to your project.clj:

```clj
[littlebird/cicinnurus "0.0.14"]
```

A quick demo:

```clj
(require '[cicinnurus.core :as cicinnurus])
(cicinnurus/generate-svg-circles [-400 400] [10 50] [400 400] 100)
```

![CIRCLES](https://rawgit.com/littlebird/cicinnurus/master/resources/public/img/circles.svg)

There are four components to the Cicinnurus library: Circle, Color, Math, and SVG.

### cicinnurus.circle

Make a bunch of circles:

```clj
(require '[cicinnurus.circle :as circle])

(def circles
  (doseq [radius (range 1 121)]
    (circle/circle
      [0 0]   ;; center, origin for now
      radius  ;; radius
      [(/ (* 2 Math/PI) radius), 0.5, 0.7]))) ;; colors are in HSB, from [0..2π, 0..1, 0..1]
```

Then pack them into a plane using the twirl method we invented:

```clj
(def plane (circle/twirl circles))
```

This will set all of their centers so that they are touching but not overlapping with some reasonable degree of packedness.

### cicinnurus.color

There are a lot of useful color operations in here.  Colors can be represented in one of three ways:

* HSB
* RGB
* Hex string

and freely converted between all of these.  RGB and HSB are represented as three element vectors.

```clj
(require '[cicinnurus.color :as color])

(def chartreuse-hex "#66ff33")
(def chartreuse-rgb (color/hex->rgb chartreuse-hex)) ;; [102 255 51]
(def chartreuse-hsb (color/rgb->hsb chartreuse-rgb)) ;; [2.3561944901923444 0.8 1.0]
(= chartreuse-hsb (color/hex->hsb chartreuse-hex)) ;; true

;; Not always isomorphic due to mapping between different color spaces
;; (but close!)
(color/hsb->hex chartreuse-hsb) ;; "#66ff33"
```

Once you have an HSB color, you can perform rotations on its hue while holding the saturation and brightness constant.

```clj
(color/rotate-hue [6 0.5 0.8] 3) ;; [2.7168146928204138 0.5 0.8]
```

You can also generate random colors and color ranges:

```clj
(color/random-color) ;; pretty!
(def musty-reds (color/color-range [6 0.1] [0.6 0.8] [0.5 0.6])
(musty-reds) ;; intense!
(musty-reds) ;; !!
```

### cicinnurus.math

This provides a variety of basic vector and trigonometry math Cicinnurus needs to do its circle packing.

```clj
(require '[cicinnurus.math :as math])

(math/distance [1 1] [3 3]) ;; 2.8284271247461903
(math/add [1 1] [3 3]) ;; [4 4]
(math/subtract [1 1 1] [3 3 -3]) ;; [-2 -2 4]
(math/scale [3 4 5] 5) ;; [15 20 25]
(math/normalize [3 4 5]) ;; [0.4242640687119285 0.565685424949238 0.7071067811865475]
(math/rotate [3 4] (* Math/PI 0.5)) ;; [-4 3]
(math/area->radius 121) ;; 6.206085419025319
(math/sides->angle 3 4 5) ;; 1.5707963267948966
```

And the culmination of all of this, a function which, given three sides and two of the center points, returns the third center point:

```clj
(math/third-point 3 4 5 1 1 1 4) ;; [5 4]
```

### cicinnurus.svg

There are functions here to turn Clojure data structures into Hiccup formatted SVG.  The main entry point is `emit-circles`:

```clj
(require '[cicinnurus.circle :as circle])
(require '[cicinnurus.svg :as svg])

(def circles
  (doseq [radius (range 1 121)]
    (circle/circle
      [0 0]   ;; center, origin for now
      radius  ;; radius
      [(/ (* 2 Math/PI) radius), 0.5, 0.7]))) ;; colors are in HSB, from [0..2π, 0..1, 0..1]

(def plane (circle/twirl circles))
(def svg (svg/emit-circles plane))
```

Drop this into your markup and voila!  Circles everywhere.

## License

Copyright © 2015 Little Bird

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
