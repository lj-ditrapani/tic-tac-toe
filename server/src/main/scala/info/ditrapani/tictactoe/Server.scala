package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.StreamApp
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

object Server extends StreamApp[IO] with Http4sDsl[IO] {
  private val p1Id = Random.nextInt()
  private val p2Id = Random.nextInt()
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var game: Game = Init

  @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
  def static(file: String, request: Request[IO]): IO[Response[IO]] =
    StaticFile.fromResource("/" + file, Some(request)).getOrElseF(NotFound())

  val service: HttpService[IO] = HttpService[IO] {
    case request @ GET -> Root =>
      static("index.html", request).map(
        response =>
          game match {
            case Init =>
              game = Player1Ready
              response.addCookie(Cookie("id", p1Id.toString))
            case Player1Ready =>
              game = Player1Turn(Board.init())
              response.addCookie(Cookie("id", p2Id.toString))
            case _ => response
        }
      )
    case request @ GET -> Root / "js" / file =>
      static(s"js/$file", request)
    case GET -> Root / "hello" / name =>
      Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}")))
    case GET -> Root / "status" =>
      Ok(s"current game status is $game")
    case PUT -> Root / "play" / IntVar(x) / IntVar(y) =>
      Ok(s"you played on position $x $y")
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}
