package com.colofabrix.scala.accounting.etl

import cats.scalatest._
import com.colofabrix.scala.accounting.utils.validation._
import FieldConverter._
import java.time.LocalDate
import org.scalatest._

class FieldConverterSpecs extends WordSpec with Matchers with ValidatedMatchers {

  // parse[String]
  "Running parse[String]" when {
    val parser = parse[String](x => x(0))

    "providing a String" should {
      "return a valid String" in {
        val computed = parser.run(List("123"))
        val expected = "123".aValid
        computed should equal(expected)
      }
    }

    "providing an empty String" should {
      "return a valid String" in {
        val computed = parser.run(List(""))
        val expected = "".aValid
        computed should equal(expected)
      }
    }

    "providing null value" should {
      "complain with a NullPointerException" in {
        val computed = parser.run(List(null))
        val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
        computed should equal(expected)
      }
    }

    "providing an empty list" should {
      "compain with IndexOutOfBoundsException" in {
        val computed = parser.run(List())
        val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
        computed should equal(expected)
      }
    }
  }

  // parse[Int]
  "Running parse[Int]" when {
    val parser = parse[Int](x => x(0))

    "providing a String" should {
      "return a valid Int" in {
        val computed = parser.run(List("123"))
        val expected = 123.aValid
        computed shouldBe valid
      }
    }

    "providing an a badly formatted String" should {
      "complain with a NumberFormatException" in {
        val computed = parser.run(List("ab12cd"))
        val expected =
          "Exception on converting field 'ab12cd': java.lang.NumberFormatException: For input string: \"ab12cd\"".aInvalid
        computed should equal(expected)
      }
    }

    "providing an empty String" should {
      "complain with a NumberFormatException" in {
        val computed = parser.run(List(""))
        val expected =
          "Exception on converting field '': java.lang.NumberFormatException: For input string: \"\"".aInvalid
        computed should equal(expected)
      }
    }

    "providing null value" should {
      "complain with a NullPointerException" in {
        val computed = parser.run(List(null))
        val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
        computed should equal(expected)
      }
    }

    "providing an empty list" should {
      "compain with IndexOutOfBoundsException" in {
        val computed = parser.run(List())
        val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
        computed should equal(expected)
      }
    }
  }

  // parse[Double]
  "Running parse[Double]" when {
    val parser = parse[Double](x => x(0))

    "providing a String" should {
      "return a valid Double" in {
        val computed = parser.run(List("123.45"))
        val expected = 123.45.aValid
        computed should equal(expected)
      }
    }

    "providing an empty String" should {
      "complain with a NumberFormatException" in {
        val computed = parser.run(List(""))
        val expected = "Exception on converting field '': java.lang.NumberFormatException: empty String".aInvalid
        computed should equal(expected)
      }
    }

    "providing null value" should {
      "complain with a NullPointerException" in {
        val computed = parser.run(List(null))
        val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
        computed should equal(expected)
      }
    }

    "providing an empty list" should {
      "compain with IndexOutOfBoundsException" in {
        val computed = parser.run(List())
        val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
        computed should equal(expected)
      }
    }
  }

  // parse[BigDecimal]
  "Running parse[BigDecimal]" when {
    val parser = parse[BigDecimal](x => x(0))

    "providing a String" should {
      "return a valid BigDecimal" in {
        val computed = parser.run(List("123.45"))
        val expected = BigDecimal("123.45").aValid
        computed should equal(expected)
      }
    }

    "providing an a badly formatted String" should {
      "complain with a NumberFormatException" in {
        val computed = parser.run(List("ab12cd"))
        val expected = "Exception on converting field 'ab12cd': java.lang.NumberFormatException".aInvalid
        computed should equal(expected)
      }
    }

    "providing an empty String" should {
      "complain with a NumberFormatException" in {
        val computed = parser.run(List(""))
        val expected = "Exception on converting field '': java.lang.NumberFormatException".aInvalid
        computed should equal(expected)
      }
    }

    "providing null value" should {
      "complain with a NullPointerException" in {
        val computed = parser.run(List(null))
        val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
        computed should equal(expected)
      }
    }

    "providing an empty list" should {
      "compain with IndexOutOfBoundsException" in {
        val computed = parser.run(List())
        val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
        computed should equal(expected)
      }
    }
  }

  // parse[LocalDate]
  "Running parse[LocalDate]" when {
    val parser = parse[LocalDate](x => x(0))("dd/MM/yyyy")

    "providing a String" should {
      "return a valid LocalDate" in {
        val computed = parser.run(List("30/12/2019"))
        val expected = LocalDate.of(2019, 12, 30).aValid
        computed should equal(expected)
      }
    }

    "providing an a badly formatted String" should {
      "complain with a NumberFormatException" in {
        val computed = parser.run(List("ab12cd"))
        val expected =
          """Exception on converting field 'ab12cd': java.time.format.DateTimeParseException: Text 'ab12cd' could not be parsed at index 0""".aInvalid
        println(expected)
        computed should equal(expected)
      }
    }

    "providing an empty String" should {
      "complain with a NumberFormatException" in {
        val computed = parser.run(List(""))
        val expected =
          "Exception on converting field '': java.time.format.DateTimeParseException: Text '' could not be parsed at index 0".aInvalid
        computed should equal(expected)
      }
    }

    "providing null value" should {
      "complain with a NullPointerException" in {
        val computed = parser.run(List(null))
        val expected = "Exception on converting field 'null': java.lang.NullPointerException".aInvalid
        computed should equal(expected)
      }
    }

    "providing an empty list" should {
      "compain with IndexOutOfBoundsException" in {
        val computed = parser.run(List())
        val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
        computed should equal(expected)
      }
    }
  }

  // parse[Option[A]]
  "Running parse[Option[A]]" when {
    val parser = parse[Option[Int]](x => x(0))

    "providing a String" should {
      "return a valid Some[A]" in {
        val computed = parser.run(List("123"))
        val expected = Some(123).aValid
        computed should equal(expected)
      }
    }

    "providing an empty String" should {
      "return a valid None" in {
        val computed = parser.run(List(""))
        val expected = None.aValid
        computed should equal(expected)
      }
    }

    "providing null value" should {
      "return a valid None" in {
        val computed = parser.run(List(null))
        val expected = None.aValid
        computed should equal(expected)
      }
    }

    "providing an empty list" should {
      "compain with IndexOutOfBoundsException" in {
        val computed = parser.run(List())
        val expected = "Exception on converting record List(): java.lang.IndexOutOfBoundsException: 0".aInvalid
        computed should equal(expected)
      }
    }
  }
}
