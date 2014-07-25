package com.github.davidallsopp.lists

import org.scalacheck.Properties
import org.scalacheck.Prop._

object CalateSpec extends Properties("Intercalation/Extracalation") {

  import Calate._

  // Intercalate

  // (Implied by the alternates tests, but simple and quick)
  property("intercalateLengthsConserved") = forAll { (a: List[Int], b: List[Int]) =>
    intercalate(a, b).length == (a.length + b.length)
  }

  // Output contains all input values. True for any input list lengths
  property("intercalateContainsAll") = forAll { (a: List[Int], b: List[Int]) =>
    intercalate(a, b).toSet == (a ++ b).toSet
  }

  // Odd outputs come from first list; even outputs from the second
  property("alternatesForEqualInputLengths") = forAll { (a: List[Int], b: List[Int]) =>
    // Only true for input lists of equal lengths, or when the first list is 1 longer than the 2nd
    val aa = if (a.length - b.length > 1) a.take(b.length + 1) else a
    val bb = if (b.length - aa.length > 0) b.take(aa.length) else b
    // TODO better way to generate matching lists - with a for-comprehension?
    assert(List(0, 1).contains(aa.length - bb.length))
    val zipped = intercalate(aa, bb).zipWithIndex
    val odds = zipped.filter(_._2 % 2 == 0).map(_._1)
    val evens = zipped.filter(_._2 % 2 == 1).map(_._1)
    odds == aa && evens == bb
  }

  // if one input list is longer, the tail of the output list is the 'overhang'
  // e.g. intercalate([0,0],[1234]) is [0, 1, 0, 2, 3, 4]
  property("overhang") = forAll { (a: List[Int], b: List[Int]) =>
    (b.length - a.length > 0) ==> (intercalate(a, b).drop(2 * a.length) == b.drop(a.length))
  }

  // Extracalate

  // (Implied by the extractsAlternates tests, but simple and quick)
  property("extracalateLengthsConserved") = forAll { (a: List[Int]) =>
    val (b, c) = extracalate(a)
    b.length + c.length == a.length
  }

  // Outputs contains all input values.
  property("extracalateContainsAll") = forAll { (a: List[Int]) =>
    val (b, c) = extracalate(a)
    b.toSet ++ c.toSet == a.toSet
  }

  // Puts odd elements into one list, evens into another
  property("extractsAlternates") = forAll { (a: List[Int]) =>
    val (b, c) = extracalate(a)
    val zipped = a.zipWithIndex
    val odds = zipped.filter(_._2 % 2 == 0).map(_._1)
    val evens = zipped.filter(_._2 % 2 == 1).map(_._1)
    odds == b && evens == c
  }
}

