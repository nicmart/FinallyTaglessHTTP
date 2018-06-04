package module

import java.net.ServerSocket

import algebra._
import cats.Monad
import cats.effect.IO
import http.message.{HttpRequest, HttpResponse}
import interpreter._

final class HttpServerModule(serverSocket: ServerSocket, api: HttpRequest => IO[HttpResponse]) extends ServerModule {
  override type F[A] = IO[A]
  override type Req = HttpRequest
  override type Resp = HttpResponse
  override type Socket = java.net.Socket

  override def interpreter: ServerAlgebra[IO, HttpRequest, HttpResponse, java.net.Socket] =
    new ServerAlgebra[IO, HttpRequest, HttpResponse, java.net.Socket] {
      override val monad: Monad[IO] = Monad[IO]
      override val connection: ConnectionAlgebra[IO, Socket] = new JavaConnectionInterpreter(serverSocket)
      override val communication: CommunicationAlgebra[IO, Socket, HttpRequest, HttpResponse] =
        new ConsoleCommunicationInterpreter(
          new JavaHttpCommunicationInterpreter,
          new HttpInfoInterpreter,
          new StdIOConsoleInterpreter
        )
      override val router: RouterAlgebra[IO, HttpRequest, HttpResponse] =
        new RouterInterpreter(api)
    }
  override def runProgram(program: IO[Unit]): Unit = program.unsafeRunSync()
}
