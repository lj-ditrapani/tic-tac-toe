package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.async.Ref
import org.http4s.{HttpService, Method, Request, Status, Uri}
import org.http4s.syntax.KleisliSyntax
import org.http4s.util.CaseInsensitiveString
import org.scalatest.OptionValues
import state.game
import game.Game
import state.{Board, Player, Player1}
import scala.io.Source

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class ServerTest extends Spec with KleisliSyntax with OptionValues {
  def makeServerState(gameState: game.Game, player: Player): ServerState = {
    val io = for {
      gameStateRef <- Ref[IO, Game](gameState)
      playerRef <- Ref[IO, Player](player)
    } yield ServerState(1, 2, gameStateRef, playerRef)
    io.unsafeRunSync
  }

  "get index" - {
    "when game.Init" in {
      val state = makeServerState(game.Init, Player1)
      val server = new Server(state)
      val service: HttpService[IO] = server.service
      val getRoot = Request[IO](Method.GET, Uri.uri("/"))
      val response = service.orNotFound.run(getRoot).unsafeRunSync
      response.status shouldBe Status.Ok
      val headers = response.headers
      val cookie = headers.get(CaseInsensitiveString("Set-Cookie")).value.value
      cookie shouldBe "id=1"
      val contentType = headers.get(CaseInsensitiveString("Content-Type")).value.value
      contentType shouldBe "text/html"
      val body = response.body.map(_.toChar).compile.toVector.unsafeRunSync.mkString("")
      val index: String = Source.fromResource("index.html").mkString
      body shouldBe index
      server.gameStateRef.get.unsafeRunSync shouldBe game.Player1Ready
    }

    "when game.Player1Ready" - {
      "and player's cookie id != player1 id; sets cookie to Player2 & advances state" in {
        val state = makeServerState(game.Player1Ready, Player1)
        val server = new Server(state)
        val service: HttpService[IO] = server.service
        val getRoot = Request[IO](Method.GET, Uri.uri("/"))
        val response = service.orNotFound.run(getRoot).unsafeRunSync
        response.status shouldBe Status.Ok
        val headers = response.headers
        val cookie = headers.get(CaseInsensitiveString("Set-Cookie")).value.value
        cookie shouldBe "id=2"
        val contentType = headers.get(CaseInsensitiveString("Content-Type")).value.value
        contentType shouldBe "text/html"
        val body = response.body.map(_.toChar).compile.toVector.unsafeRunSync.mkString("")
        val index: String = Source.fromResource("index.html").mkString
        body shouldBe index
        server.gameStateRef.get.unsafeRunSync shouldBe game.Player1Turn(Board.init)
      }

      "and player's cookie id == player1 id; does not set cookie again or advance state" ignore {
        val state = makeServerState(game.Player1Ready, Player1)
        val server = new Server(state)
        val service: HttpService[IO] = server.service
        val getRoot = Request[IO](Method.GET, Uri.uri("/")).addCookie("id", "1")
        val response = service.orNotFound.run(getRoot).unsafeRunSync
        response.status shouldBe Status.Ok
        val headers = response.headers
        val cookie = headers.get(CaseInsensitiveString("Set-Cookie"))
        cookie shouldBe None
        val contentType = headers.get(CaseInsensitiveString("Content-Type")).value.value
        contentType shouldBe "text/html"
        val body = response.body.map(_.toChar).compile.toVector.unsafeRunSync.mkString("")
        val index: String = Source.fromResource("index.html").mkString
        body shouldBe index
        server.gameStateRef.get.unsafeRunSync shouldBe game.Player1Ready
      }
    }

    "when any other game state" in {
      val state = makeServerState(game.Player2Ready, Player1)
      val server = new Server(state)
      val service: HttpService[IO] = server.service
      val getRoot = Request[IO](Method.GET, Uri.uri("/"))
      val response = service.orNotFound.run(getRoot).unsafeRunSync
      response.status shouldBe Status.Ok
      val headers = response.headers
      val cookie = headers.get(CaseInsensitiveString("Set-Cookie"))
      cookie shouldBe None
      val contentType = headers.get(CaseInsensitiveString("Content-Type")).value.value
      contentType shouldBe "text/html"
      val body = response.body.map(_.toChar).compile.toVector.unsafeRunSync.mkString("")
      val index: String = Source.fromResource("index.html").mkString
      body shouldBe index
      server.gameStateRef.get.unsafeRunSync shouldBe game.Player2Ready
    }
  }
}
