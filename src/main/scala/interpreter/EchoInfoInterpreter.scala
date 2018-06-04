package interpreter

import java.net.Socket

import algebra.InfoAlgebra

class EchoInfoInterpreter extends InfoAlgebra[String, String, Socket] {
  override def reqInfo(req: String): String = req
  override def respInfo(resp: String): String = resp
  override def socketInfo(socket: Socket): String =
    s"${socket.getInetAddress}"
}
