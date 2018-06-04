package httpimpl.socket

import java.io.{InputStream, OutputStream}
import java.net

import http.socket.Socket

class JavaSocket(socket: net.Socket) extends Socket {
  override val input: InputStream = socket.getInputStream
  override val output: OutputStream = socket.getOutputStream
  override def close(): Unit = socket.close()
}
