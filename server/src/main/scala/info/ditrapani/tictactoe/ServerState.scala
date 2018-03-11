package info.ditrapani.tictactoe

import state.{Player, Player1}
import state.{game => gamePackage}
import state.game.Game
import scala.util.Random

final case class ServerState(p1Id: Int, p2Id: Int, game: Game, firstPlayer: Player)

object ServerState {
  def init(): ServerState =
    ServerState(Random.nextInt(), Random.nextInt(), gamePackage.Init, Player1)
}
