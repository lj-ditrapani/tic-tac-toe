package info.ditrapani.tictactoe

import cats.effect.{Effect, IO}
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext
import StreamApp.ExitCode

object Main extends StreamApp[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    Stream.eval(Args.init()).flatMap(serverStream)

  private def serverStream(args: Args): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(new Server(args).service, "/")
      .serve(Effect[IO], ExecutionContext.global)
}
