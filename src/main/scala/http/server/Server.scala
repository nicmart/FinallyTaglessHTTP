package http.server

import cats.effect.IO
import cats.syntax.all._
import cats.data._
import http.protocol.Protocol
import http.socket.Socket

import scala.concurrent.ExecutionContext.Implicits.global

class Server[Req, Resp, E](
  listener: ConnectionListener,
  protocol: Protocol[Req, Resp, E],
  router: Req => Resp
) {
  final def run: IO[Unit] =
    runRequestAndConsoleLoopInParallel

  private def runRequestAndConsoleLoopInParallel: IO[Unit] =
    NonEmptyList.of(requestLoop, consoleLoop).parSequence.map(_ => ())

  private def requestLoop: IO[Unit] =
    for {
      socket <- IO(listener.accept())
      _ <- IO.shift
      _ <- handleRequest(socket)
      _ <- requestLoop
    } yield ()

  private def handleRequest(socket: Socket): IO[Unit] =
    for {
      request <- protocol.consumeRequest(socket.input)
      _ <- IO(println(s"Received request $request"))
      response = request.map(router)
      _ <- IO(println(s"Sending response $response"))
      _ <- handleResponse(socket, response)
      _ <- IO(socket.close())
    } yield ()

  private def handleResponse(socket: Socket, response: Either[E, Resp]): IO[Unit] =
    response match {
      case Left(e) => IO(println(e.toString))
      case Right(resp) =>
        protocol.sendResponse(resp, socket.output)
    }

  private def consoleLoop: IO[Unit] = {
    for {
      input <- IO(scala.io.StdIn.readLine())
      emptyLineEntered <- IO(input == "")
      _ <- if (emptyLineEntered) IO(listener.close()) else consoleLoop
    } yield ()
  }
}
