package http.server

import http.socket.Socket

trait ConnectionListener {
  def accept(): Socket
  def close(): Unit
}
