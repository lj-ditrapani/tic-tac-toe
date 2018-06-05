package info.ditrapani.tictactoe

import model.{Player, Player1}
import model.game
import model.game.Game

final case class State(game: Game, firstPlayer: Player)

object State {
  val init: State = State(game.Init, Player1)
}
