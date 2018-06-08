package program

import algebra.{ConnectionAlgebra, ServerAlgebra}
import cats.syntax.all._

object Server {
  def apply[F[_], Req, Resp, Socket](serverAlgebra: ServerAlgebra[F, Req, Resp, Socket]): F[Unit] = {
    import serverAlgebra.connection._, serverAlgebra.ioAlgebra._, serverAlgebra.Instances._

    val serveRequest = handleRequest(serverAlgebra)(_)

    def loop: F[Unit] =
      for {
        socket <- acceptConnection
        _ <- inBackground(serveRequest(socket))
        _ <- loop
      } yield ()

    loop
  }

  def handleRequest[F[_], Req, Resp, Socket](
    serverAlgebra: ServerAlgebra[F, Req, Resp, Socket]
  )(socket: Socket): F[Unit] = {
    import serverAlgebra.communication._, serverAlgebra.connection._, serverAlgebra.router._, serverAlgebra.Instances._
    for {
      request <- extractRequest(socket)
      response <- run(request)
      _ <- sendResponse(socket, response)
      _ <- closeSocket(socket)
    } yield ()
  }
}
