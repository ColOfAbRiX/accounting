package com.colofabrix.scala.accounting.utils.validation

import fs2._

/**
 * Accounting Validation (AValidation) module
 */
package object streams {

  /** Validated Stream */
  type VStream[+F[_], +A] = Stream[F, AValidated[A]]

  /** Validated Pipe */
  type VPipe[F[_], -I, +O] = Pipe[F, AValidated[I], AValidated[O]]

}
