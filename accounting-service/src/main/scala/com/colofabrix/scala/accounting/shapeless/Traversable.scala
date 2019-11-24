package com.colofabrix.scala.accounting.shapeless

import cats.Applicative
import cats.implicits._
import shapeless.{::, DepFn1, HList, HNil, Poly1}
import shapeless.syntax.std.tuple._


/*
From: https://blog.free2move.com/shapeless-hlists-and-how-to-traverse-them/
 */

// define a typeclass for our operation
// DepFn1 is a helper from shapeless, just accept it ;)
trait SequenceA[G[_], L <: HList] extends DepFn1[L]

object SequenceA extends Poly1 {
  // the Aux pattern allows to work around limitations of type parameters in Scala
  // see http://www.vlachjosef.com/aux-pattern-evolution/ for more
  type Aux[G[_], L <: HList, Out0] = SequenceA[G, L] { type Out = Out0 }

  // case for empty HLists (HNil), we create an instance of SequenceA which just wraps
  // the HNil in G using Applicative's pure
  implicit def hnil[G[_] : Applicative]: Aux[G, HNil, G[HNil]] = new SequenceA[G, HNil] {
    override type Out = G[HNil]
    // pure wraps HNil in G[_], widen makes sure we get the type right (G[HNil] instead of G[HNil.type])
    // needed as G[_] is invariant and shapeless has object HNil extends HNil
    override def apply(t: HNil): Out = HNil.pure[G].widen
  }

  // inductively define cases for HLists which are not empty. For that,
  // the current head needs to be a G[_] and we need an instance of SequenceA for list's tail
  implicit def hcons[G[_] : Applicative, T <: HList, H, TO <: HList]
  (implicit recurse: SequenceA.Aux[G, T, G[TO]]): Aux[G, G[H] :: T, G[H :: TO]] =
    new SequenceA[G, G[H] :: T] {
      override type Out = G[H :: TO]
      // take the input, deconstruct into head (h) and tail (t) and then
      // call use map2 to combine the contents (the insides of G[_]) of both
      // by simply constructing a new list which gets returned inside one "bigger" G[_]
      override def apply(l: G[H] :: T): Out = {
        val h :: t = l
        h.map2(recurse(t))((hv, t) => hv :: t)
      }
    }

  // some syntactic sugar to make usage more easy
  implicit class SequenceAOps[L <: HList](l : L) {
    def sequence[G[_], R <: HList](implicit sm: SequenceA.Aux[G, L, G[R]]): sm.Out = sm(l)
  }
}

// same as above, just one type parameter to identify the function to be called
trait TraverseA[G[_], F <: Poly1, L <: HList] extends DepFn1[L]

object TraverseA extends Poly1 {
  type Aux[G[_], F <: Poly1, L <: HList, Out0] = TraverseA[G, F, L] { type Out = Out0 }

  // all the same as in sequence, just passing through the F additionally
  implicit def hnil[G[_] : Applicative, F <: Poly1]: Aux[G, F, HNil, G[HNil]] = new TraverseA[G, F, HNil] {
    override type Out = G[HNil]
    override def apply(t: HNil): Out = HNil.pure[G].widen
  }

  // here are the important changes - we require "fc", a case of the polymorphic
  // function which is defined for the head of the HList so that we're sure we can call it
  // except for this call, everything works as we saw on sequence
  implicit def hcons[G[_] : Applicative, F <: Poly1, T <: HList, H, HO, TO <: HList]
  (implicit fc: shapeless.poly.Case1.Aux[F, H, HO], recurse: TraverseA.Aux[G, F, T, G[TO]]): Aux[G, F, G[H] :: T, G[HO :: TO]] =
    new TraverseA[G, F, G[H] :: T] {
      override type Out = G[HO :: TO]
      override def apply(l: G[H] :: T): Out = {
        val h :: t = l
        h.map2(recurse(t))((hv, tv) => fc(hv) :: tv)
      }
    }

  // make it usable
  implicit class TraverseAOps[L <: HList](l : L) {
    def traverse[G[_], R <: HList](f: Poly1)(implicit tm: TraverseA.Aux[G, f.type, L, G[R]]): tm.Out = tm(l)
  }
}
