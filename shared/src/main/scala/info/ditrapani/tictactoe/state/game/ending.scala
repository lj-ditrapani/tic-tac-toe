package info.ditrapani.tictactoe.state.game

sealed abstract class Ending {
  def toResponse(): String
}

object P1Wins extends Ending {
  def toResponse() = "1"
  override def toString() = "Player 1 won!"
}

object P2Wins extends Ending {
  def toResponse() = "2"
  override def toString() = "Player 2 won!"
}

object Tie extends Ending {
  def toResponse() = "T"
  override def toString() = "It was a tie!"
}
