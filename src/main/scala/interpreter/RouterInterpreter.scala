package interpreter

import algebra.RouterAlgebra

class RouterInterpreter[F[_], Req, Resp](f: Req => F[Resp]) extends RouterAlgebra[F, Req, Resp] {
  override def run(request: Req): F[Resp] = f(request)
}
