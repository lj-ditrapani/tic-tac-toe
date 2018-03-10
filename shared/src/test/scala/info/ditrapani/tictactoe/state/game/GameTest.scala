package info.ditrapani.tictactoe.state.game

import info.ditrapani.tictactoe.Spec
import info.ditrapani.tictactoe.state.{Actor, Board, Player1, Player2, Spectator}

class GameTest extends Spec {
  private val boardT1 = "2T1XXXOOOXXX"
  private val boardT2 = "2T2EEEXXXEEE"
  private val boardG1 = "2G1OOOEEEEEE"
  private val boardG2 = "1G2EEEEEEXXX"

  "fromStatusString" - {
    val tests = List(
      ("1INEEEEEEEEE", Init),
      ("1R1EEEEEEEEE", Player1Ready),
      ("1R2EEEEEEEEE", Player2Ready),
      (boardT1, Player1Turn(Board.fromStatusString(boardT1))),
      (boardT2, Player2Turn(Board.fromStatusString(boardT2))),
      (boardG1, GameOver(Player1, Board.fromStatusString(boardG1))),
      (boardG2, GameOver(Player2, Board.fromStatusString(boardG2)))
    )
    for ((status, game) <- tests) {
      s"given $status returns $game" in {
        Game.fromStatusString(status) shouldBe game
      }
    }
    "throws an IllegalArgumentException if status string is too short" in {
      an[IllegalArgumentException] should be thrownBy { Game.fromStatusString("1R2EEEAAAEE") }
    }

    "throws an IllegalArgumentException if status string is too long" in {
      an[IllegalArgumentException] should be thrownBy { Game.fromStatusString("1R2EEEAAAEEEA") }
    }

    "throws an IllegalArgumentException if status string has unknow game status" in {
      an[IllegalArgumentException] should be thrownBy { Game.fromStatusString("1XXEEEAAAEEE") }
    }
  }

  "toResponse" - {
    val tests = List(
      (Init, "INEEEEEEEEE"),
      (Player1Ready, "R1EEEEEEEEE"),
      (Player2Ready, "R2EEEEEEEEE"),
      (Player1Turn(Board.fromStatusString(boardT1)), "T1XXXOOOXXX"),
      (Player2Turn(Board.fromStatusString(boardT2)), "T2EEEXXXEEE"),
      (GameOver(Player1, Board.fromStatusString(boardG1)), "G1OOOEEEEEE"),
      (GameOver(Player2, Board.fromStatusString(boardG2)), "G2EEEEEEXXX")
    )
    for ((game, status) <- tests) {
      s"given $game returns $status" in {
        game.toResponse shouldBe status
      }
    }
  }

  "toMessage" - {
    val tests = List(
      (Init, Actor.player1, "No players have joined yet..."),
      (Player1Ready, Actor.player1, "Waiting for Player 2 to join"),
      (Player1Ready, Spectator, "Player 1 has joined.  Waiting for Player 2 to join"),
      (Player2Ready, Actor.player1, "Waiting for Player 1 to join"),
      (Player1Turn(Board.fromStatusString(boardT1)), Actor.player1, "Your turn"),
      (Player2Turn(Board.fromStatusString(boardT2)), Actor.player1, "Player 2's turn"),
      (GameOver(Player1, Board.fromStatusString(boardG1)), Spectator, "Player 1 won"),
      (GameOver(Player1, Board.fromStatusString(boardG1)), Actor.player1, "You win!"),
      (GameOver(Player2, Board.fromStatusString(boardG2)), Actor.player1, "You loose :(")
    )
    for ((game, player, message) <- tests) {
      s"given player: $player and $game returns $message" in {
        game.toMessage(player) shouldBe message
      }
    }
  }
}
