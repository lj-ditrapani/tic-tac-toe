package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.async.Ref
import org.http4s.{Method, Request, Status, Uri}
import org.http4s.syntax.KleisliSyntax
import org.http4s.util.CaseInsensitiveString
import org.scalatest.OptionValues
import state.game
import game.Game
import state.{Board, Player, Player1}
import scala.io.Source

final case class Result(
    statusCode: Status,
    setCookie: Option[String],
    contentType: String,
    body: String,
    gameState: Game
)

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
class Test(gameState: Game, player: Player, method: Method, path: Uri, id: Option[String])
    extends KleisliSyntax {
  def run(): IO[Result] = {
    val request = {
      val temp = Request[IO](method, path)
      id.map(value => temp.addCookie("id", value)).getOrElse(temp)
    }
    for {
      serverState <- makeServerState(gameState, player)
      server = new Server(serverState)
      response <- server.service.orNotFound.run(request)
      body <- response.body.map(_.toChar).compile.toVector
      newGameState <- server.gameStateRef.get
      headers = response.headers
      setCookie = headers.get(CaseInsensitiveString("Set-Cookie")).map(_.value)
      contentType = headers.get(CaseInsensitiveString("Content-Type")).map(_.value).get
    } yield Result(response.status, setCookie, contentType, body.mkString(""), newGameState)
  }

  def makeServerState(gameState: game.Game, player: Player): IO[ServerState] =
    for {
      gameStateRef <- Ref[IO, Game](gameState)
      playerRef <- Ref[IO, Player](player)
    } yield ServerState(1, 2, gameStateRef, playerRef)
}

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class ServerTest extends AsyncSpec with KleisliSyntax with OptionValues {
  "get index" - {
    "when game.Init" in {
      new Test(game.Init, Player1, Method.GET, Uri.uri("/"), None)
        .run()
        .map(result => {
          result.statusCode shouldBe Status.Ok
          result.setCookie.value shouldBe "id=1"
          result.contentType shouldBe "text/html"
          result.body shouldBe Source.fromResource("index.html").mkString
          result.gameState shouldBe game.Player1Ready
        })
        .unsafeToFuture
    }

    "when game.Player1Ready" - {
      "and player's cookie id != player1 id; sets cookie to Player2 & advances state" in {
        new Test(game.Player1Ready, Player1, Method.GET, Uri.uri("/"), None)
          .run()
          .map(result => {
            result.statusCode shouldBe Status.Ok
            result.setCookie.value shouldBe "id=2"
            result.contentType shouldBe "text/html"
            result.body shouldBe Source.fromResource("index.html").mkString
            result.gameState shouldBe game.Player1Turn(Board.init)
          })
          .unsafeToFuture
      }

      "and player's cookie id == player1 id; does not set cookie again or advance state" ignore {
        new Test(game.Player1Ready, Player1, Method.GET, Uri.uri("/"), Some("1"))
          .run()
          .map(result => {
            result.statusCode shouldBe Status.Ok
            result.setCookie shouldBe None
            result.contentType shouldBe "text/html"
            result.body shouldBe Source.fromResource("index.html").mkString
            result.gameState shouldBe game.Player1Ready
          })
          .unsafeToFuture
      }
    }

    "when any other game state" in {
      new Test(game.Player1Turn(Board.init), Player1, Method.GET, Uri.uri("/"), Some("1"))
        .run()
        .map(result => {
          result.statusCode shouldBe Status.Ok
          result.setCookie shouldBe None
          result.contentType shouldBe "text/html"
          result.body shouldBe Source.fromResource("index.html").mkString
          result.gameState shouldBe game.Player1Turn(Board.init)
        })
        .unsafeToFuture
    }
  }
}
