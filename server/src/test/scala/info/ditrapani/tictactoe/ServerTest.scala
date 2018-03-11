package info.ditrapani.tictactoe

import cats.effect.IO
import org.http4s.{HttpService, Method, Request, Status, Uri}
import org.http4s.syntax.KleisliSyntax
import org.http4s.util.CaseInsensitiveString
import org.scalatest.OptionValues
import state.game
import state.Player1
import scala.io.Source

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class ServerTest extends Spec with KleisliSyntax with OptionValues {
  "get index" in {
    val state = ServerState(1, 2, game.Init, Player1)
    val service: HttpService[IO] = new Server(state).service
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
  }
}
