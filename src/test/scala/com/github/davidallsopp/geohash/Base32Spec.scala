package com.github.davidallsopp.geohash

import org.scalacheck.Properties
import org.scalacheck.Prop._
import org.scalacheck.Arbitrary._
import org.scalacheck._
import Base32._

object Base32Spec extends Properties("Base32") {

  // Any list of 5 booleans (bits) results in a valid Base32 character
  // It's debatable whether this should require a non-empty list as input...for the moment, an empty list produces a result of zero

  val bits = Gen.listOfN(5, Gen.oneOf(true, false)) // scalastyle:ignore

  property("bits2base32") = forAll(bits) { bits =>
    BASE32.contains(toBase32(bits))
  }

  // Not tested: lists of lengths other than 5 still produce a result

  // Any base32 string produces a seq of boolean bits

  private def base32 = Gen.oneOf(BASE32)
  private def base32str: Gen[String] = (for (cs <- Gen.nonEmptyListOf(base32)) yield cs.mkString).suchThat(isValid(_))

  property("base32toBits") = forAll(base32str) { str =>
    toBits(str).length > 0
  }

  // Round-trip
  property("round-trip") = forAll(base32str) { str =>
    toBits(str).grouped(5).map(toBase32).mkString == str // scalastyle:ignore
  }

  // Specific examples
  property("examples") = {
    val (t,f) = (true, false)
    val examples = Seq(
        (Seq(f,f,f,f,f), '0'),
        (Seq(f,t,f,t,t), 'c'), // asymmetric to ensure LSB convention is correct
        (Seq(t,t,t,t,t), 'z'))
    examples.forall{ case (bits, char) => toBase32(bits) == char }
  }
}
