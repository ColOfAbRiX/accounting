package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.{ Pure, Stream }
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

@SuppressWarnings(Array("org.wartremover.warts.Null", "org.wartremover.warts.Equals"))
class InputProcessorUtilsSpecs extends AnyFlatSpec with Matchers {

  def rawInput(data: List[RawRecord]): VRawInput[Pure] = Stream.emits(data.map(_.aValid))

  "dropHeader" should "remove the first row" in {
    val testData = List(
      List.fill(3)("header"),
      List.fill(0)("content"),
      List.fill(1)("content"),
      List.fill(2)("content"),
    )

    val computed = rawInput(testData).through(dropHeader).toList
    val expected = List(
      List.fill(0)("content").aValid,
      List.fill(1)("content").aValid,
      List.fill(2)("content").aValid,
    )

    computed should contain theSameElementsAs (expected)
  }

  "dropLength" should "remove rows of specific length" in {
    val dropLengthNot2 = dropLength(_ != 2)
    val testData       = List.tabulate(4)(i => List.fill(i)("sample"))

    val computed = rawInput(testData).through(dropLengthNot2).toList
    val expected = List(List.fill(2)("sample").aValid)

    computed should contain theSameElementsAs expected
  }

  "dropAnyMatch" should "remove rows with at least one field that matches" in {
    val dropRecordWithRandom = dropAnyMatch(_.contains("random"))
    val testData = List(
      "",
      " ",
      "random",
      "This is some random sentence",
      "Another random sentence",
      "It's the last day of the year",
    ).map(_.split(" ").toList)

    val computed = rawInput(testData).through(dropRecordWithRandom).toList
    val expected = List(
      "",
      " ",
      "It's the last day of the year",
    ).map(_.split(" ").toList.aValid)

    computed should contain theSameElementsAs expected
  }

  "dropAnyMatch" should "handle null fields" in {
    val dropRecordWithRandom = dropAnyMatch(_.contains("random"))
    val testData = List(
      List.fill(3)(null),
      List("sample", null, "text"),
      List("random", null, "text"),
    )

    val computed = rawInput(testData).through(dropRecordWithRandom).toList
    val expected = List(
      List.fill(3)(null).aValid,
      List("sample", null, "text").aValid,
    )

    computed should contain theSameElementsAs expected
  }

  "dropEmptyRows" should "remove empty or null records" in {
    val testData = List(
      List(),
      List.fill(3)(""),
      List.fill(3)(null),
    )

    val computed = rawInput(testData).through(dropEmptyRows).toList

    computed should have size 0
  }

  "dropEmptyRows" should "keep rows with some empty or null strings" in {
    val testData = List(
      List("filled", null),
      List("filled", ""),
    )

    val computed = rawInput(testData).through(dropEmptyRows).toList
    val expected = List(
      List("filled", null).aValid,
      List("filled", "").aValid,
    )

    computed should contain theSameElementsAs expected
  }

  "fixNulls" should "replace nulls with empty strings anywhere in the record" in {
    val testData = List(
      List(),
      List.fill(3)(null),
      List("filled", null, "filled", null),
    )

    val computed = rawInput(testData).through(fixNulls).toList
    val expected = List(
      List().aValid,
      List.fill(3)("").aValid,
      List("filled", "", "filled", "").aValid,
    )

    computed should contain theSameElementsAs expected
  }

}
