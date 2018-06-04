package algebra

import cats.Monad
import cats.syntax.all._

class ConsoleCommunicatonInterpreter[F[_]: Monad, Socket, Req, Resp](
  communication: CommunicationAlgebra[F, Socket, Req, Resp],
  info: InfoAlgebra[Req, Resp, Socket],
  console: ConsoleAlgebra[F]
) extends CommunicationAlgebra[F, Socket, Req, Resp] {

  override def extractRequest(socket: Socket): F[Req] =
    for {
      _ <- console.write(s"Extracting Request from socket ${info.socketInfo(socket)}")
      req <- communication.extractRequest(socket)
      _ <- console.write(s"Extracted Request ${info.reqInfo(req)}")
    } yield req

  override def sendResponse(socket: Socket, response: Resp): F[Unit] =
    for {
      _ <- console.write(s"Sending Response ${info.respInfo(response)} to Socket ${info.socketInfo(socket)}")
      _ <- communication.sendResponse(socket, response)
      _ <- console.write(s"Response ${info.respInfo(response)} sent to Socket ${info.socketInfo(socket)}")
    } yield ()
}
