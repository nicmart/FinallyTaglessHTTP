package http.protocol

import java.io._

import cats.effect.IO

class EchoProtocol extends Protocol[String, String, Throwable] {
  override def consumeRequest(input: InputStream): IO[Either[Throwable, String]] = IO {
    val bufferedReader = new BufferedReader(new InputStreamReader(input))
    Right(bufferedReader.readLine())
  }

  override def sendResponse(response: String, output: OutputStream): IO[Unit] = IO {
    val printWriter = new PrintWriter(output, true)
    printWriter.println(response)
  }
}
