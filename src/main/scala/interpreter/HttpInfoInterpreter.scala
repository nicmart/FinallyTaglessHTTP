package interpreter

import java.net

import algebra.InfoAlgebra
import http.message.{HttpRequest, HttpResponse}

class HttpInfoInterpreter extends InfoAlgebra[HttpRequest, HttpResponse, net.Socket] {
  override def reqInfo(req: HttpRequest): String = req.toString
  override def respInfo(resp: HttpResponse): String = resp.toString
  override def socketInfo(socket: net.Socket): String =
    s"${socket.getInetAddress}"
}
