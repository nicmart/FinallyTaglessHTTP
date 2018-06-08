package app

import app.wiring.Wiring._

object App extends scala.App {
  httpServerModule.runProgram(httpServer)
  //echoServerModule.runProgram(echoServer)
}
