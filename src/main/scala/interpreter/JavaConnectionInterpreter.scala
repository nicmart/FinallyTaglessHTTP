package interpreter

import java.net.ServerSocket

import algebra.ConnectionAlgebra
import cats.effect.IO

class JavaConnectionInterpreter(serverSocket: ServerSocket) extends ConnectionAlgebra[IO, java.net.Socket] {
  override def acceptConnection: IO[java.net.Socket] = IO(serverSocket.accept())
  override def shutDown: IO[Unit] = IO(serverSocket.close())
  override def closeSocket(socket: java.net.Socket): IO[Unit] = IO(socket.close())
}
