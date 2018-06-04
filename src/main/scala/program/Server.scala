package program

import algebra.{ConnectionAlgebra, ServerAlgebra}
import cats.Monad
import cats.syntax.all._

object Server {
  def apply[F[_]: Monad, Req, Resp, Socket](serverAlgebra: ServerAlgebra[F, Req, Resp, Socket]): F[Unit] = {
    import serverAlgebra.communication._, serverAlgebra.connection._, serverAlgebra.console._, serverAlgebra.router._
    def loop: F[Unit] =
      for {
        socket <- acceptConnection
        request <- extractRequest(socket)
        response <- run(request)
        _ <- sendResponse(socket, response)
        _ <- closeSocket(socket)
        _ <- loop
      } yield ()

    loop
  }
}
