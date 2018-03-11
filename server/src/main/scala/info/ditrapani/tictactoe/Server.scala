package info.ditrapani.tictactoe

import cats.effect.IO
import org.http4s.{HttpService, Request, Response, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.http4s.{Cookie, headers}
import state.{Actor, Board, Entity, Player, Player1, Player2, Spectator}
import state.game
import game.Game
import state.cell

class Server(state: ServerState) extends Http4sDsl[IO] {
  private val p1Id = state.p1Id
  private val p2Id = state.p2Id
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var gameState: Game = state.game
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var firstPlayer: Player = state.firstPlayer

  @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
  def static(file: String, request: Request[IO]): IO[Response[IO]] =
    StaticFile.fromResource("/" + file, Some(request)).getOrElseF(NotFound())

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  val service: HttpService[IO] = HttpService[IO] {
    case request @ GET -> Root =>
      static("index.html", request).map(
        response =>
          gameState match {
            case game.Init =>
              gameState = game.Player1Ready
              response.addCookie(Cookie("id", p1Id.toString))
            case game.Player1Ready =>
              gameState = game.Player1Turn(Board.init)
              response.addCookie(Cookie("id", p2Id.toString))
            case _ => response
        }
      )
    case request @ GET -> Root / "js" / file =>
      static(s"js/$file", request)
    case request @ GET -> Root / "img" / file =>
      static(s"img/$file", request)
    case request @ GET -> Root / "status" =>
      Ok(statusString(getEntity(request), gameState))
    case request @ POST -> Root / "play" / IntVar(index) =>
      val entity = getEntity(request)
      entity match {
        case Actor(player) =>
          getBoard(player, gameState) match {
            case _ if index < 0 => Ok(statusString(entity, gameState))
            case _ if index > 8 => Ok(statusString(entity, gameState))
            case None => Ok(statusString(entity, gameState) + s" not your turn! $index")
            case Some(board) =>
              if (board.cells(index) == cell.Empty) {
                val newBoard = Board(board.cells.updated(index, player.token))
                gameState = gameState match {
                  case game.Player1Turn(_) => game.Player2Turn(newBoard)
                  case game.Player2Turn(_) => game.Player1Turn(newBoard)
                  case _ => throw new RuntimeException("should be unreachable...")
                }
                Ok(statusString(entity, gameState))
              } else {
                Ok(statusString(entity, gameState) + s" can't play there! $index")
              }
          }
        case Spectator =>
          Ok(statusString(Spectator, gameState))
      }
    case request @ GET -> Root / "reset" => {
      val entity = getEntity(request)
      gameState match {
        case game.GameOver(_, _) if entity != Spectator =>
          firstPlayer = firstPlayer.toggle
          gameState = entity match {
            case Actor(Player1) => game.Player1Ready
            case Actor(Player2) => game.Player2Ready
            case Spectator =>
              throw new IllegalStateException("Guard clause should prevent Spectator case")
          }
          Ok(statusString(entity, gameState))
        case _ => Ok(statusString(entity, gameState))
      }
    }
  }

  def statusString(entity: Entity, gameState: Game): String =
    entity.toResponse + gameState.toResponse

  def getEntity(request: Request[IO]): Entity = {
    val maybeCookie: Option[Cookie] = for {
      header <- headers.Cookie.from(request.headers)
      cookie <- header.values.find(_.name == "id")
    } yield cookie
    maybeCookie match {
      case Some(cookie) if cookie.content == p1Id.toString => Actor.player1
      case Some(cookie) if cookie.content == p2Id.toString => Actor.player2
      case _ => Spectator
    }
  }

  def getBoard(player: Player, gameState: Game): Option[Board] = {
    gameState match {
      case game.Player1Turn(board) if player == Player1 => Some(board)
      case game.Player2Turn(board) if player == Player2 => Some(board)
      case _ => None
    }
  }
}
