package info.ditrapani.tictactoe.state

import cell.Cell

sealed abstract class Player {
  def toResponse: String
  def token: Cell
  def toggle: Player
}
object Player1 extends Player {
  override def toString = "Player 1"
  def toResponse = "1"
  def token = cell.X
  def toggle = Player2
}
object Player2 extends Player {
  override def toString = "Player 2"
  def toResponse = "2"
  def token = cell.O
  def toggle = Player1
}
object Spectator extends Player {
  override def toString = "a Spectator"
  def toResponse = "S"
  def token = cell.Empty
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def toggle: Player =
    throw new RuntimeException("toggle should only be called on Player1 or Player2")
}
object Player {
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def fromStatusString(status: String): Player =
    status(0) match {
      case '1' => Player1
      case '2' => Player2
      case 'S' => Spectator
      case char => throw new IllegalArgumentException(s"Uknown player char $char")
    }
}
