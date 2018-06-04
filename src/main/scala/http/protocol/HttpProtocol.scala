package http.protocol

import java.io._
import cats.effect.IO
import http.message.{HttpRequest, HttpRequestParser, HttpResponse}

class HttpProtocol extends Protocol[HttpRequest, HttpResponse, Throwable] {
  override def consumeRequest(input: InputStream): IO[Either[Throwable, HttpRequest]] = IO {
    val bufferedReader = new BufferedReader(new InputStreamReader(input))
    val parser = new HttpRequestParser(bufferedReader)
    parser.parseRequest()
  }

  override def sendResponse(response: HttpResponse, output: OutputStream): IO[Unit] = IO {
    val printWriter = new PrintWriter(output, true)
    printWriter.println(statusLine(response))
    printWriter.println("")
    printWriter.println(response.body)
  }

  private def statusLine(response: HttpResponse): String =
    s"HTTP/1.1 ${response.status} OK"
}

class MalformedHttpRequest(override val getMessage: String) extends RuntimeException
