package info.ditrapani.tictactoe.state.game

import info.ditrapani.tictactoe.state
import state.{Board, Player, Player1, Player2, Spectator}

sealed abstract class Game {
  val emptyBoard = "EEEEEEEEE"
  def toResponse: String
  def toMessage(player: Player): String
}
object Init extends Game {
  def toResponse = "IN" + emptyBoard
  def toMessage(player: Player) = "No players have joined yet..."
}
object Player1Ready extends Game {
  def toResponse = "R1" + emptyBoard
  def toMessage(player: Player) = Game.playerReadyMessage(player, 1, 2)
}
object Player2Ready extends Game {
  def toResponse = "R2" + emptyBoard
  def toMessage(player: Player) = Game.playerReadyMessage(player, 2, 1)
}
final case class Player1Turn(board: Board) extends Game {
  def toResponse = "T1" + board.toResponse
  def toMessage(player: Player) = Game.playerTurnMessage(player, Player1, 1)
}
final case class Player2Turn(board: Board) extends Game {
  def toResponse = "T2" + board.toResponse
  def toMessage(player: Player) = Game.playerTurnMessage(player, Player2, 2)
}
final case class GameOver(winner: Player, board: Board) extends Game {
  def toResponse = "G" + winner.toResponse + board.toResponse
  def toMessage(player: Player) =
    player == Spectator match {
      case true => s"$winner won"
      case false =>
        winner == player match {
          case true => "You win!"
          case false => "You loose :("
        }
    }
}

object Game {
  def fromStatusString(status: String): Game =
    status.substring(1, 3) match {
      case "IN" => Init
      case "R1" => Player1Ready
      case "R2" => Player2Ready
      case "T1" => Player1Turn(Board.fromStatusString(status))
      case "T2" => Player2Turn(Board.fromStatusString(status))
      case "G1" => GameOver(Player1, Board.fromStatusString(status))
      case "G2" => GameOver(Player2, Board.fromStatusString(status))
    }

  def playerReadyMessage(player: Player, ready: Int, waiting: Int): String =
    player match {
      case Spectator => s"Player $ready has joined.  " + waitingMessage(waiting)
      case _ => waitingMessage(waiting)
    }

  def playerTurnMessage(player: Player, turnPlayer: Player, turn: Int): String =
    player == turnPlayer match {
      case true => "Your turn"
      case false => s"Player $turn's turn"
    }

  private def waitingMessage(waiting: Int): String = s"Waiting for Player $waiting to join"
}
