package info.ditrapani.tictactoe.state.game

sealed abstract class Ending {
  def toResponse(): String
  def toMessage(): String
}

object P1Wins extends Ending {
  def toResponse() = "1"
  def toMessage() = "Player 1 wins!"
  override def toString() = "P1Wins"
}

object P2Wins extends Ending {
  def toResponse() = "2"
  def toMessage() = "Player 2 wins!"
  override def toString() = "P2Wins"
}

object Tie extends Ending {
  def toResponse() = "T"
  def toMessage() = "It's a tie!"
  override def toString() = "Tie"
}
