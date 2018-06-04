package app.wiring

import java.net.ServerSocket

import http.message.{HttpRequest, HttpResponse}
import http.protocol.{EchoProtocol, HttpProtocol}
import http.server.{ConnectionListener, Server}
import httpimpl.server.JavaConnectionListener

trait Wiring {
  lazy val server =
    new Server[HttpRequest, HttpResponse, Throwable](listener, protocol, router)

  lazy val echoServer =
    new Server(listener, echoProtocol, echoRouter)

  lazy val listener: ConnectionListener = new JavaConnectionListener(javaServerSocket)
  lazy val javaServerSocket = new ServerSocket(5000)

  lazy val protocol = new HttpProtocol()
  lazy val echoProtocol = new EchoProtocol

  def router(req: HttpRequest): HttpResponse = req match {
    case HttpRequest("GET", url) if url.startsWith("/gabba") =>
      HttpResponse(200, "Hi Gaby!!!!")
    case _ =>
      HttpResponse(200, s"You requested ${req.url}")
  }

  def echoRouter(req: String): String = req
}

object Wiring extends Wiring