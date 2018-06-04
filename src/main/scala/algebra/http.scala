package algebra

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{ServerSocket, Socket}
import cats.effect.IO
import http.message.{HttpRequest, HttpRequestParser, HttpResponse}
import http.protocol.{HttpProtocol, Protocol}

trait HttpModule {
  final type F[A] = IO[A]
  final type Socket = java.net.Socket
  final type Req = HttpRequest
  final type Resp = HttpResponse

  def interpreter(
    serverSocket: ServerSocket,
    api: HttpRequest => IO[HttpResponse]
  ): ServerAlgebra[IO, HttpRequest, HttpResponse, java.net.Socket] =
    new ServerAlgebra[IO, HttpRequest, HttpResponse, java.net.Socket] {
      override val console: ConsoleAlgebra[IO] = new Console
      override val connection: ConnectionAlgebra[IO, java.net.Socket] = new Connection(serverSocket)
      override val communication: CommunicationAlgebra[IO, Socket, Req, Resp] =
        new ConsoleCommunicatonInterpreter(new Communication, new Info, console)
      override val router: RouterAlgebra[IO, Req, Resp] = new Router(api)
    }

  class Connection(serverSocket: ServerSocket) extends ConnectionAlgebra[IO, java.net.Socket] {
    override def acceptConnection: IO[java.net.Socket] = IO(serverSocket.accept())
    override def shutDown: IO[Unit] = IO(serverSocket.close())
    override def closeSocket(socket: java.net.Socket): IO[Unit] = IO(socket.close())
  }

  class Communication extends CommunicationAlgebra[IO, java.net.Socket, HttpRequest, HttpResponse] {
    override def extractRequest(socket: Socket): IO[Req] = {
      val bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val parser = new HttpRequestParser(bufferedReader)
      IO(parser.parseRequest().getOrElse(HttpRequest("GET", "/parse-error")))
    }
    override def sendResponse(socket: Socket, response: Resp): IO[Unit] = {
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

  class Router(f: HttpRequest => IO[HttpResponse]) extends RouterAlgebra[F, HttpRequest, HttpResponse] {
    override def run(request: Req): F[Resp] = f(request)
  }

  class Console extends ConsoleAlgebra[IO] {
    override def read: IO[String] = IO(scala.io.StdIn.readLine())
    override def write(message: String): IO[Unit] = IO(println(message))
  }

  class Info extends InfoAlgebra[HttpRequest, HttpResponse, java.net.Socket] {
    override def reqInfo(req: Req): String = req.toString
    override def respInfo(resp: Resp): String = resp.toString
    override def socketInfo(socket: Socket): String =
      s"${socket.getInetAddress}"
  }
}

object HttpModule extends HttpModule
