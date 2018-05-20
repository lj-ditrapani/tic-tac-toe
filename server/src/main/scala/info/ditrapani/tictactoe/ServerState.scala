package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.async.Ref
import state.{Player, Player1}
import state.{game => gamePackage}
import state.game.Game
import scala.util.Random

final case class ServerState(
    p1Id: Int,
    p2Id: Int,
    game: Ref[IO, Game],
    firstPlayer: Ref[IO, Player]
)

object ServerState {
  def init(): IO[ServerState] =
    for {
      state <- Ref[IO, Game](gamePackage.Init)
      firstPlayer <- Ref[IO, Player](Player1)
      p1Id <- IO { Random.nextInt() }
      p2Id <- IO { Random.nextInt() }
    } yield ServerState(p1Id, p2Id, state, firstPlayer)
}
