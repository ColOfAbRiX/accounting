package com.colofabrix.scala.accounting.example


import shapeless.labelled.FieldType
import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, LabelledGeneric, Lazy, Witness}


/**
  * Provides functions to encode a class to CSV format.
  * Follows formatting standards outlined in https://www.ietf.org/rfc/rfc4180.txt
  *
  * @tparam A The class to encode.
  */
trait CSVEncoder[-A] {

  /**
    * Encode the values of a class to a list of strings.
    *
    * @param value The class to encode.
    * @return a list of string representations of the values of a class.
    */
  def encode(value: A): List[String]

  /**
    * Encode the values of a class to a comma-separated string with a line break.
    *
    * @param value The class to encode.
    * @return a comma-separated string of the values of a class.
    */
  def encodeString(value: A): String = encode(value).mkString(",") + "\n"

}


/**
  * Provides implicit instances of CSVEncoder.
  */
object CSVEncoder {

  def apply[A](implicit enc: CSVEncoder[A]): CSVEncoder[A] = enc

  /**
    * Creates a new instance of CSVEncoder using the provided function.
    *
    * @param func A function to encode the values of type A to a list of strings.
    * @tparam A the type of the class to encode
    * @return an instance of CSVEncoder for type A
    */
  def pure[A](func: A => List[String]): CSVEncoder[A] =
    new CSVEncoder[A] {
      def encode(value: A): List[String] = func(value)
    }

  def nullHandler[A](elem: A)(fn: A => List[String]): List[String] = Option(elem).fold(List(""))(fn)


  implicit val stringEncoder: CSVEncoder[String] = pure(nullHandler(_)(str => List(s""""${str.replace("\"","\"\"")}"""")))

  implicit val intEncoder: CSVEncoder[Int] = pure(nullHandler(_)(int => List(int.toString)))

  implicit val doubleEncoder: CSVEncoder[Double] = pure(nullHandler(_)(dbl => List(dbl.toString)))

  implicit val floatEncoder: CSVEncoder[Float] = pure(nullHandler(_)(flt => List(flt.toString)))

  implicit val booleanEncoder: CSVEncoder[Boolean] = pure(nullHandler(_)(bool => List(if (bool) "true" else "false")))

  implicit val longEncoder: CSVEncoder[Long] = pure(nullHandler(_)(lng => List(lng.toString)))

  implicit val shortEncoder: CSVEncoder[Short] = pure(nullHandler(_)(shrt => List(shrt.toString)))

  implicit val byteEncoder: CSVEncoder[Byte] = pure(nullHandler(_)(byte => List(byte.toString)))

  implicit val charEncoder: CSVEncoder[Char] = pure(nullHandler(_)(char => List(char.toString)))

  implicit val utilDateEncoder: CSVEncoder[java.util.Date] = pure(nullHandler(_)(date => List(date.toString)))

  implicit val dateTimeEncoder: CSVEncoder[java.time.LocalDate] = pure(nullHandler(_)(date => List(date.toString)))

  implicit val timestampEncoder: CSVEncoder[java.sql.Timestamp] = pure(nullHandler(_)(timestamp => List(timestamp.toString)))

  implicit def optionEncoder[A](implicit aEncoder: CSVEncoder[A]): CSVEncoder[Option[A]] = pure(opt => opt.fold(List(""))(aEncoder.encode))


  implicit val hnilEncoder: CSVEncoder[HNil] = pure(_ => Nil)

  implicit val cnilEncoder: CSVEncoder[CNil] = pure(_ => Nil)

  implicit def hlistEncoder[H, T <: HList](implicit hEncoder: Lazy[CSVEncoder[H]], tEncoder: CSVEncoder[T]): CSVEncoder[H :: T] = pure {
    case h :: t => hEncoder.value.encode(h) ++ tEncoder.encode(t)
  }

  implicit def coproductEncoder[H, T <: Coproduct](implicit hEncoder: Lazy[CSVEncoder[H]], tEncoder: CSVEncoder[T]): CSVEncoder[H :+: T] = pure {
    case Inl(h) => hEncoder.value.encode(h)
    case Inr(t) => tEncoder.encode(t)
  }

  implicit def genericEncoder[A, R](implicit gen: Generic.Aux[A, R], env: Lazy[CSVEncoder[R]]): CSVEncoder[A] = pure(a => env.value.encode(gen.to(a)))

}


/**
  * Provides functions to list the field names of a class.
  *
  * @tparam A the type of class to extract from.
  */
trait HeaderExtractor[A] {

  /**
    * Extracts the fields of the class into a list of strings.
    *
    * @return the fields of the class as a list of strings.
    */
  def headers: List[String]

  /**
    * Extracts the fields of the class into a comma-separated string with a line break.
    *
    * @return the fields of the class as a comma-separated string.
    */
  def headersString: String = headers.mkString(",") + "\n"

}


/**
  * Provides low-priority implicit HeaderExtractor instances.
  */
trait HeaderExtractorLowPriority {

  def apply[A](implicit enc: HeaderExtractor[A]): HeaderExtractor[A] = enc

  /**
    * Creates a new instance of HeaderExtractor using the provided function
    *
    * @param headerFn the list of fields.
    * @tparam A the type of the class to extract
    * @return an instance of HeaderExtractor for type A
    */
  def pure[A](headerFn: List[String]): HeaderExtractor[A] =
    new HeaderExtractor[A] {
      def headers: List[String] = headerFn
    }

  implicit def primitiveExtractor[K <: Symbol, H, T <: HList](implicit witness: Witness.Aux[K], tExtractor: HeaderExtractor[T]): HeaderExtractor[FieldType[K, H] :: T] =
    pure(witness.value.name :: tExtractor.headers)
}


/**
  * Provides implicit HeaderExtractor instances.
  */
object HeaderExtractor extends HeaderExtractorLowPriority {

  implicit val hnilExtractor: HeaderExtractor[HNil] = pure(Nil)

  implicit def hlistExtractor[K, H, T <: HList](implicit hExtractor: Lazy[HeaderExtractor[H]], tExtractor: HeaderExtractor[T]): HeaderExtractor[FieldType[K, H] :: T] =
    pure(hExtractor.value.headers ++ tExtractor.headers)

  implicit def genericExtractor[A, R](implicit gen: LabelledGeneric.Aux[A, R], extractor: Lazy[HeaderExtractor[R]]): HeaderExtractor[A] = pure(extractor.value.headers)

}