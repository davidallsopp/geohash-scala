package com.github.davidallsopp.geohash

import org.scalacheck.Properties
import org.scalacheck.Prop._
import org.scalacheck.Arbitrary._
import org.scalacheck._

object GeoHashSpec extends Properties("Geohash") {

  import GeoHash._

  // Generators
  // scalastyle:off
  val precision = Gen.choose(1, 15) // More than 15 should work but is pointlessly high resolution (~sub-millimetre)
  val badprecision = Gen.choose(Integer.MIN_VALUE, 0)
  val highprecision = Gen.choose(11, 20) // select a high level of precision
  val latitude = Gen.choose(-90.0, 90.0)
  val longitude = Gen.choose(-180.0, 180.0)

  val input: Gen[(Int, Double, Double)] = for {
    p <- precision
    lat <- latitude
    lon <- longitude
  } yield (p, lat, lon)

  val badinput: Gen[(Int, Double, Double)] = for {
    p <- precision
    lat <- arbitrary[Double]
    lon <- arbitrary[Double]
    // At least one of the numbers must be invalid
    if (lat > 90.0 || lat < -90.0 || lon > 180.0 || lon < -180.0)
  } yield (p, lat, lon)

  // Encoding properties

  // For all valid inputs, the hash length is as requested
  property("outputPrecision") = forAll(input) {
    case (p, lat, lon) => encode(lat, lon, p).length == p
  }

  // For all valid inputs, we get a hash (containing valid base32)
  property("outputInBase32") = forAll(input) {
    case (p, lat, lon) => Base32.isValid(encode(lat, lon, p))
  }

  val IAE = classOf[IllegalArgumentException]

  // For all invalid lat/longs we get an exception
  property("badLatLon") = forAll(badinput) {
    case (p, lat, lon) => throws(IAE)(encode(lat, lon, p))
  }

  // For invalid precision (< 1) we get an exception
  property("badPrecision") = forAll(input, badprecision) {
    case ((_, lat, lon), badp) => throws(IAE)(encode(lat, lon, badp))
  }

  // Decoding properties

  // For all valid base-32 hashes we get an output (valid lat/longs)
  // Caution - this assumes that the isValid function is correct
  import Base32._
  def base32 = Gen.oneOf(BASE32)
  def hashes: Gen[String] = (for (cs <- Gen.nonEmptyListOf(base32)) yield cs.mkString).suchThat(isValid(_))

  property("anyBase32Decoded") = forAll(hashes) { hash =>
    val (lat, lon) = decode(hash.mkString)
    (lat in LAT_RANGE) && (lon in LON_RANGE)
  }

  // Caution - this assumes that the isValid function is correct
  def badhashes: Gen[String] = (for (cs <- Gen.listOf(Gen.alphaNumChar)) yield cs.mkString).suchThat(!isValid(_))

  // For invalid (non-base32) inputs we get an exception
  property("badHashesNotDecoded") = forAll(badhashes) { hash =>
    throws(IAE)(decode(hash.mkString))
  }

  // Round-trip properties

  // for all inputs we can round-trip to reconstruct the original (to an acceptable accuracy)
  property("outputInBase32") = forAllNoShrink(input, highprecision) { // NoShrink: don't want the precision level to get reduced
    case ((_, lat, lon), p) => {
      val (newlat, newlon) = decode(encode(lat, lon, p))
      Math.abs(newlat - lat) < 1E-6 && Math.abs(newlon - lon) < 1E-6
    }
  }

  // Specific reference examples

  property("examples") = {

    // Also includes example from wikipedia entry: 57.64911,10.40744 -> u4pruydqqvj
    val examples = Map((0.0, 0.0) -> "s000", (90.0, 180.0) -> "zzzzzzzz", (-90.0, 180.0) -> "pbpbpbpbp",
      (90.0, -180.0) -> "bpbpbpbpb", (-90.0, -180.0) -> "000000000", (0.0, 180.0) -> "xbpbpbpbp",
      (0.0, -180.0) -> "800000000", (90.0, 0.0) -> "upbp", (-90.0, 0.0) -> "h000", (45.0, 90.0) -> "y000",
      (-45.0, -90.0) -> "6000", (45.0, -90.0) -> "f000", (-45.0, 90.0) -> "q000", (57.64911, 10.40744) -> "u4pruydqqvj")

    // Just check that our codes start with the same chars as the geohash.org
    // results (though our codes are allowed to be longer; geohash.org does some truncation/rounding)
    examples.forall { case (key, value) => encode(key._1, key._2).startsWith(value) }

  }
}
