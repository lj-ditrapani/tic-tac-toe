package info.ditrapani.tictactoe

import cats.effect.{Effect, IO}
import fs2.StreamApp
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext

object Main extends StreamApp[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]) = {
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(new Server(ServerState.init()).service, "/")
      .serve(Effect[IO], ExecutionContext.global)
  }
}
