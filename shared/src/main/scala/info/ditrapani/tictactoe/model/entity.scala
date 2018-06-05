package info.ditrapani.tictactoe.model

import game.{Ending, P1Wins, P2Wins, Tie}

sealed abstract class Entity {
  def toResponse: String
  def readyMessage(): String
  def turnMessage(turnPlayer: Player): String
  def gameOverMessage(ending: Ending): String
  def resetMessage(resetter: Player): String
  def quitMessage(quitter: Player): String
}

object Spectator extends Entity {
  override def toString = "a Spectator"

  def toResponse = "S"

  def readyMessage =
    s"Player 1 has joined.  " + Entity.waitingMessage

  def turnMessage(turnPlayer: Player) = s"$turnPlayer's turn"

  def gameOverMessage(ending: Ending) = ending.toMessage()

  def resetMessage(resetter: Player) = s"$resetter wants a rematch"

  def quitMessage(quitter: Player) = s"$quitter quit"
}

final case class Actor(player: Player) extends Entity {
  override def toString = player.toString

  def toResponse = player.toResponse

  def readyMessage() = Entity.waitingMessage

  def turnMessage(turnPlayer: Player) =
    if (player == turnPlayer) {
      "Your turn"
    } else {
      s"$turnPlayer's turn"
    }

  def gameOverMessage(ending: Ending) =
    (ending -> player) match {
      case (Tie, _) => Tie.toMessage()
      case (P1Wins, Player1) | (P2Wins, Player2) => "You win!"
      case _ => "You loose :("
    }

  def resetMessage(resetter: Player) =
    if (player == resetter) {
      s"Waiting for ${resetter.toggle} to accept the rematch."
    } else {
      s"$resetter wants a rematch.  Do you accept?"
    }

  def quitMessage(quitter: Player) =
    if (player == quitter) {
      "You quit...quitter"
    } else {
      s"$quitter quit.  Acknowlege by pressing the Acknowlege Quit button."
    }
}

object Actor {
  val player1: Actor = Actor(Player1)
  val player2: Actor = Actor(Player2)
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

  val waitingMessage: String = s"Waiting for Player 2 to join."
}
