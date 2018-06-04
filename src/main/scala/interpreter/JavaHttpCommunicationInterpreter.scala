package interpreter

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket

import algebra.CommunicationAlgebra
import cats.effect.IO
import http.message.{HttpRequest, HttpRequestParser, HttpResponse}

class JavaHttpCommunicationInterpreter extends CommunicationAlgebra[IO, Socket, HttpRequest, HttpResponse] {
  override def extractRequest(socket: Socket): IO[HttpRequest] = {
    val bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val parser = new HttpRequestParser(bufferedReader)
    IO(parser.parseRequest().getOrElse(HttpRequest("GET", "/parse-error")))
  }
  override def sendResponse(socket: Socket, response: HttpResponse): IO[Unit] = {
    val printWriter = new PrintWriter(socket.getOutputStream, true)
    IO {
      printWriter.println(statusLine(response))
      printWriter.println("")
      printWriter.println(response.body)
    }
  }

  private def statusLine(response: HttpResponse): String =
    s"HTTP/1.1 ${response.status} OK"
}
