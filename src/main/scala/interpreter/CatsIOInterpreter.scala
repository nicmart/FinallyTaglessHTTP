package interpreter

import algebra.IOAlgebra
import cats.{Id, Monad}
import cats.effect._
import scala.concurrent.ExecutionContext.Implicits.global

class CatsIOInterpreter extends IOAlgebra[IO] {
  override val monad: Monad[IO] = Monad[IO]
  override def inBackground(fa: IO[Unit]): IO[Unit] = fa.start.map(_ => ())
}
