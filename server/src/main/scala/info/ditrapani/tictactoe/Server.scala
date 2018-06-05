package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.async.Ref
import org.http4s.{HttpService, Request, Response, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.http4s.{Cookie, headers}
import model.{Actor, Board, Entity, Player, Player1, Spectator}
import model.game
import game.Game
import model.cell

class Server(args: Args) extends Http4sDsl[IO] {
  private val p1Id = args.p1Id
  private val p2Id = args.p2Id
  val stateRef: Ref[IO, State] = args.state

  @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
  def static(file: String, request: Request[IO]): IO[Response[IO]] =
    StaticFile.fromResource("/" + file, Some(request)).getOrElseF(NotFound())

  val service: HttpService[IO] = HttpService[IO] {
    case request @ GET -> Root =>
      for {
        responseWithFile <- static("index.html", request)
        fullResponse <- handleRoot(getEntity(request), responseWithFile)
      } yield fullResponse
    case request @ GET -> Root / "js" / file =>
      static(s"js/$file", request)
    case request @ GET -> Root / "img" / file =>
      static(s"img/$file", request)
    case request @ GET -> Root / "status" =>
      stateRef.get.flatMap {
        case State(gameState, _) => Ok(statusString(getEntity(request), gameState))
      }
    case request @ POST -> Root / "play" / IntVar(index) =>
      val entity = getEntity(request)
      for {
        state <- stateRef.get
        response <- handlePlay(index, entity, state.game)
      } yield response
    case request @ POST -> Root / "reset" =>
      handleReset(getEntity(request))
    case request @ POST -> Root / "accept-reset" =>
      handleAcceptReset(getEntity(request))
  }

  private def handleRoot(entity: Entity, response: Response[IO]): IO[Response[IO]] =
    stateRef
      .modify2[Option[Int]] {
        case State(gameState, player) =>
          gameState match {
            case game.Init =>
              (State(game.ReadyPlayer1, player), Some(p1Id))
            case game.ReadyPlayer1 =>
              entity match {
                case Actor(Player1) => (State(gameState, player), None)
                case _ => (State(game.Turn(Player1, Board.init), player), Some(p2Id))
              }
            case _ =>
              (State(gameState, player), None)
          }
      }
      .map {
        case (_, maybe) =>
          maybe match {
            case None => response
            case Some(id) => response.addCookie(Cookie("id", id.toString))
          }
      }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private def handlePlay(index: Int, entity: Entity, gameState: Game): IO[Response[IO]] =
    entity match {
      case Actor(player) =>
        getBoard(player, gameState) match {
          case _ if index < 0 => Ok(statusString(entity, gameState))
          case _ if index > 8 => Ok(statusString(entity, gameState))
          case None => Ok(statusString(entity, gameState) + s" not your turn! $index")
          case Some(board) =>
            if (board.cells(index) == cell.Empty) {
              val newBoard = Board(board.cells.updated(index, player.token))
              val temp: Game = isGameOver(newBoard) match {
                case None => game.Turn(player.toggle, newBoard)
                case Some(ending) => game.GameOver(ending, newBoard)
              }
              stateRef.modify { _.copy(game = temp) }.flatMap(_ => Ok(statusString(entity, temp)))
            } else {
              Ok(statusString(entity, gameState) + s" can't play there! $index")
            }
        }
      case Spectator =>
        Ok(statusString(Spectator, gameState))
    }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private def handleReset(entity: Entity): IO[Response[IO]] =
    stateRef
      .modify(state => {
        val State(gameState, player) = state
        (gameState -> entity) match {
          case (game.GameOver(_, board), Actor(self)) =>
            State(game.Reset(self, board), player.toggle)
          case _ =>
            state
        }
      })
      .flatMap(change => Ok(statusString(entity, change.now.game)))

  private def handleAcceptReset(entity: Entity): IO[Response[IO]] =
    stateRef
      .modify(state => {
        val State(gameState, firstPlayer) = state
        (gameState -> entity) match {
          case (game.Reset(resetPlayer, _), Actor(self)) if resetPlayer != self =>
            State(game.Turn(firstPlayer, Board.init), firstPlayer)
          case _ => state
        }
      })
      .flatMap(change => Ok(statusString(entity, change.now.game)))

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
      case game.Turn(turnPlayer, board) if player == turnPlayer => Some(board)
      case _ => None
    }
  }

  def isGameOver(board: Board): Option[game.Ending] = board.getEnding.map(_.ending)
}
