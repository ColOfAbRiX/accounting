package com.colofabrix.scala.accounting.etl

import org.scalatest._
import FieldConverter._
import com.colofabrix.scala.accounting.utils.validation._
import java.time.LocalDate

class FieldConverterSpecs extends WordSpec with Matchers {
  // parse[Int]
  "Running parse[Int]" when {
    "providing a String" should { "return a valid Int" in {
      val computed = parse[Int](x => x(0)).run(List("123"))
      val expected = 123.aValid
      computed should equal (expected)
    }}
    "providing an a badly formatted String" should { "complain with a NumberFormatException" in {
      val computed = parse[Int](x => x(0)).run(List("ab12cd"))
      val expected = "Exception on converting field '': java.lang.NumberFormatException: For input string: \"\"".aInvalid
      computed should equal (expected)
    }}
    "providing an empty String" should { "complain with a NumberFormatException" in {
      val computed = parse[Int](x => x(0)).run(List(""))
      val expected = "Exception on converting field '': java.lang.NumberFormatException: For input string: \"\"".aInvalid
      computed should equal (expected)
    }}
    "providing null value" should { "complain with a NullPointerException" in {
      val computed = parse[Int](x => x(0)).run(List(null))
      val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
      computed should equal (expected)
    }}
    "providing an empty list" should { "compain with IndexOutOfBoundsException" in {
      val computed = parse[Int](x => x(0)).run(List())
      val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
      computed should equal (expected)
    }}
  }

  // parse[String]
  "Running parse[String]" when {
    "providing a String" should { "return a valid String" in {
      val computed = parse[String](x => x(0)).run(List("123"))
      val expected = "123".aValid
      computed should equal (expected)
    }}
    "providing an empty String" should { "return a valid String" in {
      val computed = parse[String](x => x(0)).run(List(""))
      val expected = "".aValid
      computed should equal (expected)
    }}
    "providing null value" should { "complain with a NullPointerException" in {
      val computed = parse[String](x => x(0)).run(List(null))
      val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
      computed should equal (expected)
    }}
    "providing an empty list" should { "compain with IndexOutOfBoundsException" in {
      val computed = parse[String](x => x(0)).run(List())
      val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
      computed should equal (expected)
    }}
  }

  // parse[Double]
  "Running parse[Double]" when {
    "providing a String" should { "return a valid Double" in {
      val computed = parse[Double](x => x(0)).run(List("123.45"))
      val expected = 123.45.aValid
      computed should equal (expected)
    }}
    "providing an empty String" should { "complain with a NumberFormatException" in {
      val computed = parse[Double](x => x(0)).run(List(""))
      val expected = "Exception on converting field '': java.lang.NumberFormatException: empty String".aInvalid
      computed should equal (expected)
    }}
    "providing null value" should { "complain with a NullPointerException" in {
      val computed = parse[Double](x => x(0)).run(List(null))
      val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
      computed should equal (expected)
    }}
    "providing an empty list" should { "compain with IndexOutOfBoundsException" in {
      val computed = parse[Double](x => x(0)).run(List())
      val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
      computed should equal (expected)
    }}
  }

  // parse[BigDecimal]
  "Running parse[BigDecimal]" when {
    "providing a String" should { "return a valid BigDecimal" in {
      val computed = parse[BigDecimal](x => x(0)).run(List("123.45"))
      val expected = BigDecimal("123.45").aValid
      computed should equal (expected)
    }}
    "providing an a badly formatted String" should { "complain with a NumberFormatException" in {
      val computed = parse[BigDecimal](x => x(0)).run(List("ab12cd"))
      val expected = "Exception on converting field '': java.lang.NumberFormatException".aInvalid
      computed should equal (expected)
    }}
    "providing an empty String" should { "complain with a NumberFormatException" in {
      val computed = parse[BigDecimal](x => x(0)).run(List(""))
      val expected = "Exception on converting field '': java.lang.NumberFormatException".aInvalid
      computed should equal (expected)
    }}
    "providing null value" should { "complain with a NullPointerException" in {
      val computed = parse[BigDecimal](x => x(0)).run(List(null))
      val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
      computed should equal (expected)
    }}
    "providing an empty list" should { "compain with IndexOutOfBoundsException" in {
      val computed = parse[BigDecimal](x => x(0)).run(List())
      val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
      computed should equal (expected)
    }}
  }

  // parse[LocalDate]
  "Running parse[LocalDate]" when {
    "providing a String" should { "return a valid LocalDate" in {
      val computed = parse[LocalDate](x => x(0))("dd/MM/yyyy").run(List("123.45"))
      val expected = BigDecimal("123.45").aValid
      computed should equal (expected)
    }}
    "providing an empty String" should { "complain with a NumberFormatException" in {
      val computed = parse[LocalDate](x => x(0))("dd/MM/yyyy").run(List(""))
      val expected = "Exception on converting field '': java.lang.NumberFormatException".aInvalid
      computed should equal (expected)
    }}
    "providing null value" should { "complain with a NullPointerException" in {
      val computed = parse[LocalDate](x => x(0))("dd/MM/yyyy").run(List(null))
      val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
      computed should equal (expected)
    }}
    "providing an empty list" should { "compain with IndexOutOfBoundsException" in {
      val computed = parse[LocalDate](x => x(0))("dd/MM/yyyy").run(List())
      val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
      computed should equal (expected)
    }}
  }

  // parse[Option[A]]
  "Running parse[Option[Int]]" when {
    "providing a String" should { "return a valid Option[A]" in {
      val computed = parse[Option[Int]](x => x(0)).run(List("123"))
      val expected = Some("123").aValid
      computed should equal (expected)
    }}
    "providing an empty String" should { "complain with a NumberFormatException" in {
      val computed = parse[Option[Int]](x => x(0)).run(List(""))
      val expected = "Exception on converting field '': java.lang.NumberFormatException".aInvalid
      computed should equal (expected)
    }}
    "providing null value" should { "complain with a NullPointerException" in {
      val computed = parse[Option[Int]](x => x(0)).run(List(null))
      val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
      computed should equal (expected)
    }}
    "providing an empty list" should { "compain with IndexOutOfBoundsException" in {
      val computed = parse[Option[Int]](x => x(0)).run(List())
      val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
      computed should equal (expected)
    }}
  }
}
