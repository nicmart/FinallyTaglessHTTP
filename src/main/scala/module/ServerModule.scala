package module

import java.net.Socket

import algebra.ServerAlgebra

trait ServerModule {
  type F[A]
  type Req
  type Resp
  type Socket
  def interpreter: ServerAlgebra[F, Req, Resp, Socket]
  def runProgram(program: F[Unit]): Unit
}
