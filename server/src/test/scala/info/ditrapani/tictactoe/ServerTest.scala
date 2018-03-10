package info.ditrapani.tictactoe

import cats.effect.IO
import org.http4s.{HttpService, Method, Request, Uri}
import org.http4s.syntax.KleisliSyntax

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class ServerTest extends Spec with KleisliSyntax {
  val service: HttpService[IO] = Server.service
  "get index" in {
    val getRoot = Request[IO](Method.GET, Uri.uri("/"))
    service.orNotFound.run(getRoot).unsafeRunSync shouldBe false
    // test that cookie is set with correct p1 value
  }
}
