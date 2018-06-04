package interpreter

import algebra.ConsoleAlgebra
import cats.effect.IO

class StdIOConsoleInterpreter extends ConsoleAlgebra[IO] {
  override def read: IO[String] = IO(scala.io.StdIn.readLine())
  override def write(message: String): IO[Unit] = IO(println(message))
}
