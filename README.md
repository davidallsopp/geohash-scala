geohash-scala
=============

A scala implementation of the geohashing algorithm as described at 
http://en.wikipedia.org/wiki/Geohash and originally made available at 
http://geohash.org/

This implementation is an exercise in writing idiomatic Scala and using the 
Scala toolset (SBT, ScalaCheck and ScalaStyle). Style and correctness were the
goals, not performance (though gratuitous inefficiency should be avoided), and 
no benchmarking has been done.

Usage:

```scala
import com.github.davidallsopp.geohash.GeoHash._

encode(12.345, 123.456)

    // res0: String = wdpy1r3fv6c9

encode(12.345, 123.456, 6)

    // res1: String = wdpy1r

decode("wdpy1r3fv6c9")

    // res2: (Double, Double) = (12.345000011846423,123.45599988475442)
```
