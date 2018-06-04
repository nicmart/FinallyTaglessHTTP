package http.socket

import java.io.{InputStream, OutputStream}

trait Socket {
  def input: InputStream
  def output: OutputStream
  def close(): Unit
}
