package app

import app.wiring.Wiring

object App extends scala.App {
  //Wiring.echoServer.run.unsafeRunSync()
  Wiring.interpretedServer.unsafeRunSync()
}
