package info.ditrapani.tictactoe.state.game

import info.ditrapani.tictactoe.state
import state.{Board, Entity, Actor, Player, Player1, Player2, Spectator}

sealed abstract class Game {
  def toResponse: String
  def toString: String
  def toMessage(entity: Entity): String
}
object Init extends Game {
  def toResponse = "IN" + Game.emptyBoard
  override def toString = "game.Init"
  def toMessage(entity: Entity) = "No players have joined yet..."
}
object Player1Ready extends Game {
  def toResponse = "R1" + Game.emptyBoard
  override def toString = "game.Player1Ready"
  def toMessage(entity: Entity) = Game.playerReadyMessage(entity, 1, 2)
}
object Player2Ready extends Game {
  def toResponse = "R2" + Game.emptyBoard
  override def toString = "game.Player2Ready"
  def toMessage(entity: Entity) = Game.playerReadyMessage(entity, 2, 1)
}
final case class Player1Turn(board: Board) extends Game {
  def toResponse = "T1" + board.toResponse
  override def toString = s"game.Player1Turn $board"
  def toMessage(entity: Entity) = Game.playerTurnMessage(entity, Player1, 1)
}
final case class Player2Turn(board: Board) extends Game {
  def toResponse = "T2" + board.toResponse
  override def toString = s"game.Player2Turn $board"
  def toMessage(entity: Entity) = Game.playerTurnMessage(entity, Player2, 2)
}
final case class GameOver(winner: Player, board: Board) extends Game {
  def toResponse = "G" + winner.toResponse + board.toResponse
  override def toString = s"game.GameOver $winner $board"
  def toMessage(entity: Entity) =
    entity match {
      case Spectator => s"$winner won"
      case Actor(player) =>
        winner == player match {
          case true => "You win!"
          case false => "You loose :("
        }
    }
}

object Game {
  val emptyBoard = "EEEEEEEEE"

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def fromStatusString(status: String): Game = {
    require(status.length == 12)
    status.substring(1, 3) match {
      case "IN" => Init
      case "R1" => Player1Ready
      case "R2" => Player2Ready
      case "T1" => Player1Turn(Board.fromStatusString(status))
      case "T2" => Player2Turn(Board.fromStatusString(status))
      case "G1" => GameOver(Player1, Board.fromStatusString(status))
      case "G2" => GameOver(Player2, Board.fromStatusString(status))
      case _ => throw new IllegalArgumentException(s"Unknown game status in $status")
    }
  }

  def playerReadyMessage(entity: Entity, ready: Int, waiting: Int): String =
    entity match {
      case Spectator => s"Player $ready has joined.  " + waitingMessage(waiting)
      case _ => waitingMessage(waiting)
    }

  def playerTurnMessage(entity: Entity, turnPlayer: Player, turn: Int): String =
    entity.toPlayer.map(_ == turnPlayer).getOrElse(false) match {
      case true => "Your turn"
      case false => s"Player $turn's turn"
    }

  private def waitingMessage(waiting: Int): String = s"Waiting for Player $waiting to join"
}
