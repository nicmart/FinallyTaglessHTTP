package http.protocol

import java.io.{InputStream, OutputStream}

import cats.effect.IO

trait Protocol[Req, Resp, E] {
  def consumeRequest(input: InputStream): IO[Either[E, Req]]
  def sendResponse(response: Resp, output: OutputStream): IO[Unit]
}
