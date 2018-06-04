package interpreter

import java.io._
import java.net.Socket

import algebra.CommunicationAlgebra
import cats.effect.IO

class JavaEchoCommunicationInterpreter extends CommunicationAlgebra[IO, Socket, String, String] {
  override def extractRequest(socket: Socket): IO[String] = IO {
    val bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    bufferedReader.readLine()
  }

  override def sendResponse(socket: Socket, response: String): IO[Unit] = IO {
    val printWriter = new PrintWriter(socket.getOutputStream, true)
    printWriter.println(response)
  }
}
