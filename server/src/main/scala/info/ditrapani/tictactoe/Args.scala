package info.ditrapani.tictactoe

import cats.effect.IO
import fs2.async.Ref
import scala.util.Random

final case class Args(p1Id: Int, p2Id: Int, state: Ref[IO, State])

object Args {
  def init(): IO[Args] =
    for {
      state <- Ref[IO, State](State.init)
      p1Id <- IO { Random.nextInt() }
      p2Id <- IO { Random.nextInt() }
    } yield Args(p1Id, p2Id, state)
}
