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

final case class Ready(player: Player) extends Game {
  def toResponse = "R" + player.toResponse + Game.emptyBoard
  override def toString = s"game.Ready ${player}"
  def toMessage(entity: Entity) = Game.readyMessage(entity, player.toInt, player.toggle.toInt)
}

final case class Turn(player: Player, board: Board) extends Game {
  def toResponse = "T" + player.toResponse + board.toResponse
  override def toString = s"game.Turn $player $board"
  def toMessage(entity: Entity) = Game.turnMessage(entity, player, player.toInt)
}

final case class GameOver(ending: Ending, board: Board) extends Game {
  def toResponse = "G" + ending.toResponse + board.toResponse
  override def toString = s"game.GameOver $ending $board"
  def toMessage(entity: Entity) =
    entity match {
      case Spectator => ending.toString
      case Actor(player) =>
        (ending -> player) match {
          case (Tie, _) => Tie.toString()
          case (P1Wins, Player1) | (P2Wins, Player2) => "You win!"
          case _ => "You loose :("
        }
    }
}

final case class Reset(player: Player, board: Board) extends Game {
  def toResponse = "X" + player.toResponse + board.toResponse
  override def toString = s"game.Reset $player $board"
  def toMessage(entity: Entity) =
    entity match {
      case Spectator => s"$player wants a rematch"
      case Actor(self) =>
        if (self == player) {
          s"Waiting for ${player.toggle} to accept the rematch."
        } else {
          s"$player wants a rematch.  Do you accept?"
        }
    }
}

final case class Quit(player: Player, board: Board) extends Game {
  def toResponse = "Q" + player.toResponse + board.toResponse
  override def toString = s"game.Quit $player $board"
  def toMessage(entity: Entity) =
    entity match {
      case Spectator => s"$player quit"
      case Actor(self) =>
        if (self == player) {
          "You quit...quitter"
        } else {
          s"$player quit.  Acknowlege by pressing the Acknowlege Quit button."
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
      case "R1" => Ready(Player1)
      case "R2" => Ready(Player2)
      case "T1" => Turn(Player1, Board.fromStatusString(status))
      case "T2" => Turn(Player2, Board.fromStatusString(status))
      case "G1" => GameOver(P1Wins, Board.fromStatusString(status))
      case "G2" => GameOver(P2Wins, Board.fromStatusString(status))
      case "GT" => GameOver(Tie, Board.fromStatusString(status))
      case "X1" => Reset(Player1, Board.fromStatusString(status))
      case "X2" => Reset(Player2, Board.fromStatusString(status))
      case "Q1" => Quit(Player1, Board.fromStatusString(status))
      case "Q2" => Quit(Player2, Board.fromStatusString(status))
      case _ => throw new IllegalArgumentException(s"Unknown game status in $status")
    }
  }

  def readyMessage(entity: Entity, ready: Int, waiting: Int): String =
    entity match {
      case Spectator => s"Player $ready has joined.  " + waitingMessage(waiting)
      case _ => waitingMessage(waiting)
    }

  def turnMessage(entity: Entity, turnPlayer: Player, turn: Int): String =
    entity.toPlayer.map(_ == turnPlayer).getOrElse(false) match {
      case true => "Your turn"
      case false => s"Player $turn's turn"
    }

  private def waitingMessage(waiting: Int): String = s"Waiting for Player $waiting to join"
}
