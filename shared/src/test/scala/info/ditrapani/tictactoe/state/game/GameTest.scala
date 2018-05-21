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
      ("1R1EEEEEEEEE", Ready(Player1)),
      ("1R2EEEEEEEEE", Ready(Player2)),
      (boardT1, Turn(Player1, Board.fromStatusString(boardT1))),
      (boardT2, Turn(Player2, Board.fromStatusString(boardT2))),
      (boardG1, GameOver(P1Wins, Board.fromStatusString(boardG1))),
      (boardG2, GameOver(P2Wins, Board.fromStatusString(boardG2)))
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
      (Ready(Player1), "R1EEEEEEEEE"),
      (Ready(Player2), "R2EEEEEEEEE"),
      (Turn(Player1, Board.fromStatusString(boardT1)), "T1XXXOOOXXX"),
      (Turn(Player2, Board.fromStatusString(boardT2)), "T2EEEXXXEEE"),
      (GameOver(P1Wins, Board.fromStatusString(boardG1)), "G1OOOEEEEEE"),
      (GameOver(P2Wins, Board.fromStatusString(boardG2)), "G2EEEEEEXXX")
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
      (Ready(Player1), Actor.player1, "Waiting for Player 2 to join"),
      (Ready(Player1), Spectator, "Player 1 has joined.  Waiting for Player 2 to join"),
      (Ready(Player2), Actor.player1, "Waiting for Player 1 to join"),
      (Turn(Player1, Board.fromStatusString(boardT1)), Actor.player1, "Your turn"),
      (Turn(Player2, Board.fromStatusString(boardT2)), Actor.player1, "Player 2's turn"),
      (GameOver(P1Wins, Board.fromStatusString(boardG1)), Spectator, "Player 1 won!"),
      (GameOver(P1Wins, Board.fromStatusString(boardG1)), Actor.player1, "You win!"),
      (GameOver(P2Wins, Board.fromStatusString(boardG2)), Actor.player1, "You loose :(")
    )
    for ((game, player, message) <- tests) {
      s"given player: $player and $game returns $message" in {
        game.toMessage(player) shouldBe message
      }
    }
  }
}
