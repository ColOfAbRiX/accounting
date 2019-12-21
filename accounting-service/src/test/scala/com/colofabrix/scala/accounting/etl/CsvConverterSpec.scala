// package com.colofabrix.scala.accounting.csv

// import cats.data._
// import cats.implicits._
// import org.scalatest.{ FlatSpec, Matchers }
// import com.colofabrix.scala.accounting.utils.validation.AValidated
// import com.colofabrix.scala.accounting.utils.validation._
// import com.colofabrix.scala.accounting.etl._
// import com.colofabrix.scala.accounting.etl.definitions._
// import com.colofabrix.scala.accounting.etl.FieldConverter._
// import shapeless._
// import com.colofabrix.scala.accounting.model.InputTransaction

// class CsvConverterSpec extends FlatSpec with Matchers {

//   case class Person(name: String, age: Int)

//   val personConverter = new RecordConverter[Person] {
//     def testConvert(input: List[String]): AValidated[Person] = convert(input) {
//       parse[String](r => r(0) + " " + r(1)) ::
//       parse[Int](r => r(2)) ::
//       HNil
//     }
//   }

//   "A CSV row" should "be converted to case class" in {
//     val testRow  = List("fabrizio", "colonna", "34")
//     val computed = personConverter.testConvert(testRow)
//     val expected = Person("fabrizio colonna", 34).aValid
//     computed should equal(expected)
//   }

//   "A badly formatted CSV row" should "be invalid" in {
//     val testRow  = List(null, "colonna", "abcd")
//     val computed = personConverter.testConvert(testRow)
//     val expected = Validated.Invalid(
//       NonEmptyChain(
//         "Exception on parsing CSV cell 'abcd': java.lang.NumberFormatException: For input string: \"abcd\"",
//         "Exception on parsing CSV cell 'null': java.lang.NumberFormatException: For input string: \"abcd\"",
//       ),
//     )

//     computed should equal(expected)
//   }
// }
