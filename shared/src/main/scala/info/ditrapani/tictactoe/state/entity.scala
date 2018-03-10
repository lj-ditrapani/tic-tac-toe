package info.ditrapani.tictactoe.state

sealed abstract class Entity {
  def toResponse: String
  def toPlayer: Option[Player]
}
object Spectator extends Entity {
  override def toString = "a Spectator"
  def toResponse = "S"
  def toPlayer = None
}
final case class Actor(player: Player) extends Entity {
  override def toString = s"Actor $player"
  def toResponse = player.toResponse
  def toPlayer = Some(player)
}
object Actor {
  val player1 = Actor(Player1)
  val player2 = Actor(Player2)
}
object Entity {
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def fromStatusString(status: String): Entity =
    status(0) match {
      case '1' => Actor.player1
      case '2' => Actor.player2
      case 'S' => Spectator
      case char => throw new IllegalArgumentException(s"Uknown entity char $char")
    }
}
