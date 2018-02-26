package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.StreamApp
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.{Cookie, headers}
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
    case request @ GET -> Root / "status" =>
      Ok(s"you are ${getPlayer(request)}; current game status is $game")
    case request @ POST -> Root / "play" / IntVar(x) / IntVar(y) =>
      getBoard(game, request) match {
        case None => Ok(s"It is not your turn; you played on position $x $y")
        case Some(board) => Ok(s"It is your turn and you played on position $x $y with $board")
      }
  }

  def getPlayer(request: Request[IO]): Player = {
    val maybeCookie: Option[Cookie] = for {
      header <- headers.Cookie.from(request.headers)
      cookie <- header.values.find(_.name == "id")
    } yield cookie
    maybeCookie match {
      case Some(cookie) if cookie.content == p1Id.toString => Player1
      case Some(cookie) if cookie.content == p2Id.toString => Player2
      case _ => Spectator
    }
  }

  def getBoard(game: Game, request: Request[IO]): Option[Board] = {
    val player = getPlayer(request)
    game match {
      case Player1Turn(board) if player == Player1 => Some(board)
      case Player2Turn(board) if player == Player2 => Some(board)
      case _ => None
    }
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}
