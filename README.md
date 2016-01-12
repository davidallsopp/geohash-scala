geohash-scala
=============

A Scala implementation of the geohashing algorithm as described at 
http://en.wikipedia.org/wiki/Geohash and originally made available at 
http://geohash.org/

This implementation is an exercise in writing idiomatic Scala and using the 
Scala toolset (SBT, ScalaCheck and ScalaStyle).

##Usage:

```scala
import com.github.davidallsopp.geohash.GeoHash._

encode(12.345, 123.456)  // latitude, longitude

    // res0: String = wdpy1r3fv6c9  // using default precision of 12 chars

encode(lat=12.345, lon=123.456)  // named arguments to avoid mixing up latitude and longitude

    // res1: String = wdpy1r3fv6c9  // using default precision of 12 chars

encode(12.345, 123.456, 6)  // specify precision of 6 chars

    // res2: String = wdpy1r

decode("wdpy1r3fv6c9")

    // res2: (Double, Double) = (12.345000011846423,123.45599988475442)
```
