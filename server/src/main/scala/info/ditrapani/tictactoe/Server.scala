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
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var firstPlayer: Player = Player1

  @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
  def static(file: String, request: Request[IO]): IO[Response[IO]] =
    StaticFile.fromResource("/" + file, Some(request)).getOrElseF(NotFound())

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
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
    case request @ GET -> Root / "img" / file =>
      static(s"img/$file", request)
    case request @ GET -> Root / "status" =>
      Ok(statusString(getPlayer(request), game))
    case request @ POST -> Root / "play" / IntVar(index) =>
      val player = getPlayer(request)
      getBoard(player, game) match {
        case _ if index < 0 => Ok(statusString(player, game))
        case _ if index > 8 => Ok(statusString(player, game))
        case None => Ok(statusString(player, game) + s" not your turn! $index")
        case Some(board) =>
          if (board.cells(index) == Empty) {
            val newBoard = Board(board.cells.updated(index, player.token))
            game = game match {
              case Player1Turn(_) => Player2Turn(newBoard)
              case Player2Turn(_) => Player1Turn(newBoard)
              case _ => throw new RuntimeException("should be unreachable...")
            }
            Ok(statusString(player, game))
          } else {
            Ok(statusString(player, game) + s" can't play there! $index")
          }
      }
    case request @ GET -> Root / "reset" => {
      val player = getPlayer(request)
      game match {
        case GameOver(_, _) if player != Spectator =>
          firstPlayer = firstPlayer.toggle
          game = player match {
            case Player1 => Player1Ready
            case Player2 => Player2Ready
            case Spectator =>
              throw new IllegalStateException("Guard clause should prevent Spectator case")
          }
          Ok(statusString(player, game))
        case _ => Ok(statusString(player, game))
      }
    }
  }

  def statusString(player: Player, game: Game): String = player.toResponse + game.toResponse

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

  def getBoard(player: Player, game: Game): Option[Board] = {
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
