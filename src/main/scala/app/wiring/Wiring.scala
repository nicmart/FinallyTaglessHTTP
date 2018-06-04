package app.wiring

import java.net.ServerSocket

import cats.effect.IO
import http.message.{HttpRequest, HttpResponse}
import module.{EchoServerModule, HttpServerModule, ServerModule}
import program.Server

trait Wiring {
  lazy val javaServerSocket = new ServerSocket(5000)

  def httpRouter(req: HttpRequest): HttpResponse = req match {
    case HttpRequest("GET", url) if url.startsWith("/gabba") =>
      HttpResponse(200, "Hi Gaby???")
    case _ =>
      HttpResponse(200, s"You requested ${req.url}")
  }

  lazy val httpServerModule: ServerModule = new HttpServerModule(javaServerSocket, httpRouter _ andThen IO.pure)
  lazy val httpServer: httpServerModule.F[Unit] = Server(httpServerModule.interpreter)

  def echoRouter(req: String): String = req
  lazy val echoServerModule: ServerModule = new EchoServerModule(javaServerSocket, echoRouter _ andThen IO.pure)
  lazy val echoServer: echoServerModule.F[Unit] = Server(echoServerModule.interpreter)
}

object Wiring extends Wiring
