package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.utils.validation._
import org.scalatest._

class CsvInputProcessorUtilsSpecs extends FlatSpec with Matchers {

  def rawInput(data: List[RawRecord]): VRawInput[fs2.Pure] = fs2.Stream.emits(data.map(_.aValid))

  "dropHeader" should "remove the first row" in {
    val testData = List(
      List.fill(3)("header"),
      List.fill(0)("content"),
      List.fill(1)("content"),
      List.fill(2)("content"),
    )
    val expected = List(
      List.fill(0)("content").aValid,
      List.fill(1)("content").aValid,
      List.fill(2)("content").aValid,
    )
    val computed = rawInput(testData).through(dropHeader).toList
    computed should contain theSameElementsAs (expected)
  }

  "dropLength" should "remove rows of specific length" in {
    val testData       = List.tabulate(4)(i => List.fill(i)("sample"))
    val expected       = List(List.fill(2)("sample").aValid)
    val dropLengthNot2 = dropLength(_ != 2)
    val computed       = rawInput(testData).through(dropLengthNot2).toList
    computed should contain theSameElementsAs (expected)
  }

  "dropAnyMatch" should "remove rows with at least one field that matches" in {
    val testData = List(
      "",
      " ",
      "random",
      "This is some random sentence",
      "Another random sentence",
      "It's the last day of the year",
    ).map(_.split(" ").toList)

    val expected = List(
      "",
      " ",
      "It's the last day of the year",
    ).map(_.split(" ").toList.aValid)

    val dropRecordWithRandom = dropAnyMatch(_.contains("random"))

    val computed = rawInput(testData).through(dropRecordWithRandom).toList
    computed should contain theSameElementsAs (expected)
  }

  "dropAnyMatch" should "handle null fields" in {
    val testData = List(
      List.fill(3)(null),
      List("sample", null, "text"),
      List("random", null, "text"),
    )
    val expected = List(
      List.fill(3)(null).aValid,
      List("sample", null, "text").aValid,
    )
    val dropRecordWithRandom = dropAnyMatch(_.contains("random"))

    val computed = rawInput(testData).through(dropRecordWithRandom).toList
    computed should contain theSameElementsAs (expected)
  }

  "dropEmptyRows" should "remove empty or null records" in {
    val testData = List(
      List(),
      List.fill(3)(""),
      List.fill(3)(null),
    )
    val computed = rawInput(testData).through(dropEmptyRows).toList
    computed should have size (0)
  }

  "dropEmptyRows" should "keep rows with some empty or null strings" in {
    val testData = List(
      List("filled", null),
      List("filled", ""),
    )
    val expected = List(
      List("filled", null).aValid,
      List("filled", "").aValid,
    )
    val computed = rawInput(testData).through(dropEmptyRows).toList
    computed should contain theSameElementsAs (expected)
  }

  "fixNulls" should "replace nulls with empty strings anywhere in the record" in {
    val testData = List(
      List(),
      List.fill(3)(null),
      List("filled", null, "filled", null),
    )
    val expected = List(
      List().aValid,
      List.fill(3)("").aValid,
      List("filled", "", "filled", "").aValid,
    )
    val computed = rawInput(testData).through(fixNulls).toList
    computed should contain theSameElementsAs (expected)
  }

}
