package httpimpl.server

import java.net.ServerSocket

import http.server.ConnectionListener
import http.socket.Socket
import httpimpl.socket.JavaSocket

class JavaConnectionListener(serverSocket: ServerSocket) extends ConnectionListener {
  override def accept(): Socket = new JavaSocket(serverSocket.accept())
  override def close(): Unit = serverSocket.close()
}
