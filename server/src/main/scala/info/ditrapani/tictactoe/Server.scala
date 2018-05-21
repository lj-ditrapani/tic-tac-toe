package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.async.Ref
import org.http4s.{HttpService, Request, Response, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.http4s.{Cookie, headers}
import state.{Actor, Board, Entity, Player, Player1, Spectator}
import state.game
import game.Game
import state.cell

class Server(state: ServerState) extends Http4sDsl[IO] {
  private val p1Id = state.p1Id
  private val p2Id = state.p2Id
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var gameStateRef: Ref[IO, Game] = state.game
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var firstPlayerRef: Ref[IO, Player] = state.firstPlayer

  @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
  def static(file: String, request: Request[IO]): IO[Response[IO]] =
    StaticFile.fromResource("/" + file, Some(request)).getOrElseF(NotFound())

  val service: HttpService[IO] = HttpService[IO] {
    case request @ GET -> Root =>
      for {
        gameState <- gameStateRef.get
        firstPlayer <- firstPlayerRef.get
        responseWithFile <- static("index.html", request)
        fullResponse <- handleRoot(gameState, firstPlayer, responseWithFile)
      } yield fullResponse
    case request @ GET -> Root / "js" / file =>
      static(s"js/$file", request)
    case request @ GET -> Root / "img" / file =>
      static(s"img/$file", request)
    case request @ GET -> Root / "status" =>
      gameStateRef.get.flatMap(gameState => Ok(statusString(getEntity(request), gameState)))
    case request @ POST -> Root / "play" / IntVar(index) =>
      val entity = getEntity(request)
      for {
        gameState <- gameStateRef.get
        response <- handlePostPlay(index: Int, entity: Entity, gameState: Game)
      } yield response
    case request @ POST -> Root / "reset" =>
      val entity = getEntity(request)
      for {
        gameState <- gameStateRef.get
        response <- handlePostReset(entity: Entity, gameState: Game)
      } yield response
    case POST -> Root / "accept-reset" =>
      Ok("not implemented yet")
    case POST -> Root / "quit" =>
      // if GameOver: puts game in Quit (1 or 2) state (waiting for remaining player to acknowledge)
      // if Ready: puts game in Init state
      Ok("not implemented yet")
    case POST -> Root / "acknowledge-quit" =>
      Ok("not implemented yet")
  }

  private def handleRoot(
      gameState: Game,
      firstPlayer: Player,
      response: Response[IO]
  ): IO[Response[IO]] =
    gameState match {
      case game.Init =>
        gameStateRef
          .setSync(game.Ready(Player1))
          .map(_ => response.addCookie(Cookie("id", p1Id.toString)))
      case game.Ready(player) =>
        val id = if (player == Player1) { p2Id } else { p1Id }
        // get Option[Cookie ID] from Request Header
        // if present and valid; don't set it again
        gameStateRef
          .setSync(game.Turn(firstPlayer, Board.init))
          .map(_ => response.addCookie(Cookie("id", id.toString)))
      case _ => IO.pure(response)
    }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private def handlePostPlay(index: Int, entity: Entity, gameState: Game): IO[Response[IO]] =
    entity match {
      case Actor(player) =>
        getBoard(player, gameState) match {
          case _ if index < 0 => Ok(statusString(entity, gameState))
          case _ if index > 8 => Ok(statusString(entity, gameState))
          case None => Ok(statusString(entity, gameState) + s" not your turn! $index")
          case Some(board) =>
            if (board.cells(index) == cell.Empty) {
              val newBoard = Board(board.cells.updated(index, player.token))
              val temp: Game = gameState match {
                case game.Turn(player, _) => game.Turn(player.toggle, newBoard)
                case _ => throw new RuntimeException("should be unreachable...")
              }
              gameStateRef.setSync(temp).flatMap(_ => Ok(statusString(entity, gameState)))
            } else {
              Ok(statusString(entity, gameState) + s" can't play there! $index")
            }
        }
      case Spectator =>
        Ok(statusString(Spectator, gameState))
    }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private def handlePostReset(entity: Entity, gameState: Game): IO[Response[IO]] =
    gameState match {
      case game.GameOver(_, _) if entity != Spectator =>
        val temp: Game = entity match {
          case Actor(player) => game.Ready(player)
          case Spectator =>
            throw new IllegalStateException("Guard clause should prevent Spectator case")
        }
        for {
          _ <- firstPlayerRef.modify(_.toggle)
          _ <- gameStateRef.setSync(temp)
          response <- Ok(statusString(entity, gameState))
        } yield response
      case _ => Ok(statusString(entity, gameState))
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
      case game.Turn(turnPlayer, board) if player == turnPlayer => Some(board)
      case _ => None
    }
  }
}
