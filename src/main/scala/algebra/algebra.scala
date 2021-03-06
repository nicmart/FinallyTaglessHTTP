package algebra

import cats.Monad

trait ConnectionAlgebra[F[_], Socket] {
  def acceptConnection: F[Socket]
  def shutDown: F[Unit]
  def closeSocket(socket: Socket): F[Unit]
}

trait CommunicationAlgebra[F[_], Socket, Req, Resp] {
  def extractRequest(socket: Socket): F[Req]
  def sendResponse(socket: Socket, response: Resp): F[Unit]
}

trait RouterAlgebra[F[_], Req, Resp] {
  def run(request: Req): F[Resp]
}

trait ConsoleAlgebra[F[_]] {
  def read: F[String]
  def write(message: String): F[Unit]
}

trait InfoAlgebra[Req, Resp, Socket] {
  def reqInfo(req: Req): String
  def respInfo(resp: Resp): String
  def socketInfo(socket: Socket): String
}

trait IOAlgebra[F[_]] {
  val monad: Monad[F]
  def inBackground(fa: F[Unit]): F[Unit]
}

trait ServerAlgebra[F[_], Req, Resp, Socket] {
  val ioAlgebra: IOAlgebra[F]
  val connection: ConnectionAlgebra[F, Socket]
  val communication: CommunicationAlgebra[F, Socket, Req, Resp]
  val router: RouterAlgebra[F, Req, Resp]
  object Instances {
    implicit val monadInstance: Monad[F] = ioAlgebra.monad
  }
}
