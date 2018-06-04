package http.message

import java.io.BufferedReader

class HttpRequestParser(bufferedReader: BufferedReader) {
  def parseRequest(): Either[Throwable, HttpRequest] =
    for {
      line <- parseRequestLine()
      (method, url) = line
      headers <- parseHeaders()
    } yield HttpRequest(method, url)

  private def parseHeaders(): Either[Throwable, Map[String, String]] =
    Right(Map.empty)

  private def parseCRLF(): Either[Throwable, Unit] =
    parseNonEmptyLine().flatMap { line =>
      Either.cond(line == "", (), new MalformedHttpRequest("Expected CRLF"))
    }

  private def parseRequestLine(): Either[Throwable, (String, String)] =
    parseNonEmptyLine().flatMap { line =>
      line.split(" ").toList match {
        case method :: url :: _ => Right((method, url))
        case _                  => Left(new MalformedHttpRequest("Failed to parse request line"))
      }
    }

  private def parseNonEmptyLine(): Either[Throwable, String] = {
    val line = bufferedReader.readLine()
    Either.cond(line != null, line, new MalformedHttpRequest("Non-empty line required"))
  }
}

class MalformedHttpRequest(str: String) extends RuntimeException
