package module

import java.net.ServerSocket

import algebra._
import cats.effect.IO
import interpreter._

final class EchoServerModule(serverSocket: ServerSocket, api: String => IO[String]) extends ServerModule {
  override type F[A] = IO[A]
  override type Req = String
  override type Resp = String
  override type Socket = java.net.Socket

  override def interpreter: ServerAlgebra[IO, String, String, java.net.Socket] =
    new ServerAlgebra[IO, String, String, java.net.Socket] {
      override val ioAlgebra: IOAlgebra[IO] = new CatsIOInterpreter
      override val connection: ConnectionAlgebra[IO, Socket] = new JavaConnectionInterpreter(serverSocket)
      override val communication: CommunicationAlgebra[IO, Socket, String, String] =
        new ConsoleCommunicationInterpreter(
          new JavaEchoCommunicationInterpreter,
          new EchoInfoInterpreter,
          new StdIOConsoleInterpreter
        )
      override val router: RouterAlgebra[IO, String, String] =
        new RouterInterpreter(api)
    }
  override def runProgram(program: IO[Unit]): Unit = program.unsafeRunSync()
}
