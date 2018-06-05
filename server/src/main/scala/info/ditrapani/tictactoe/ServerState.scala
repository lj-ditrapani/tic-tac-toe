package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.async.Ref
import state.{Player, Player1}
import state.{game => gamePackage}
import state.game.Game
import scala.util.Random

final case class Args(
    p1Id: Int,
    p2Id: Int,
    game: Ref[IO, Game],
    firstPlayer: Ref[IO, Player]
)

object Args {
  def init(): IO[Args] =
    for {
      state <- Ref[IO, Game](gamePackage.Init)
      firstPlayer <- Ref[IO, Player](Player1)
      p1Id <- IO { Random.nextInt() }
      p2Id <- IO { Random.nextInt() }
    } yield Args(p1Id, p2Id, state, firstPlayer)
}
