package info.ditrapani.tictactoe.model.game

import info.ditrapani.tictactoe.Spec
import info.ditrapani.tictactoe.model.{Actor, Board, Player1, Player2, Spectator}

class GameTest extends Spec {
  private val boardT1 = "2T1XXXOOOXXX"
  private val boardT2 = "2T2EEEXXXEEE"
  private val boardG1 = "2G1OOOEEEEEE"
  private val boardG2 = "1G2EEEEEEXXX"
  private val boardGT = "2GTXXOOOXXXO"
  private val boardS1 = "2S1XXOOOXXXO"
  private val boardS2 = "2S2XXOOOXXXO"

  "fromStatusString" - {
    val tests = List(
      ("1INEEEEEEEEE", Init),
      ("1R1EEEEEEEEE", ReadyPlayer1),
      (boardT1, Turn(Player1, Board.fromStatusString(boardT1))),
      (boardT2, Turn(Player2, Board.fromStatusString(boardT2))),
      (boardG1, GameOver(P1Wins, Board.fromStatusString(boardG1))),
      (boardG2, GameOver(P2Wins, Board.fromStatusString(boardG2))),
      (boardGT, GameOver(Tie, Board.fromStatusString(boardGT))),
      (boardS1, Reset(Player1, Board.fromStatusString(boardS1))),
      (boardS2, Reset(Player2, Board.fromStatusString(boardS2))),
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
      an[IllegalArgumentException] should be thrownBy { Game.fromStatusString("1SXEEEAAAEEE") }
    }
  }

  "toResponse" - {
    val tests = List(
      (Init, "INEEEEEEEEE"),
      (ReadyPlayer1, "R1EEEEEEEEE"),
      (Turn(Player1, Board.fromStatusString(boardT1)), "T1XXXOOOXXX"),
      (Turn(Player2, Board.fromStatusString(boardT2)), "T2EEEXXXEEE"),
      (GameOver(P1Wins, Board.fromStatusString(boardG1)), "G1OOOEEEEEE"),
      (GameOver(P2Wins, Board.fromStatusString(boardG2)), "G2EEEEEEXXX"),
      (Reset(Player1, Board.fromStatusString(boardS1)), "S1XXOOOXXXO"),
      (Reset(Player2, Board.fromStatusString(boardS1)), "S2XXOOOXXXO"),
    )

    for ((game, status) <- tests) {
      s"given $game returns $status" in {
        game.toResponse shouldBe status
      }
    }
  }

  "toMessage" - {
    val rematchMesssage = "Player 1 wants a rematch.  Do you accept?"

    val tests = List(
      (Init, Actor.player1, "No players have joined yet..."),
      (ReadyPlayer1, Actor.player1, "Waiting for Player 2 to join."),
      (ReadyPlayer1, Spectator, "Player 1 has joined.  Waiting for Player 2 to join."),
      (Turn(Player1, Board.fromStatusString(boardT1)), Actor.player1, "Your turn"),
      (Turn(Player2, Board.fromStatusString(boardT2)), Actor.player1, "Player 2's turn"),
      (GameOver(P1Wins, Board.fromStatusString(boardG1)), Spectator, "Player 1 wins!"),
      (GameOver(P1Wins, Board.fromStatusString(boardG1)), Actor.player1, "You win!"),
      (GameOver(P2Wins, Board.fromStatusString(boardG2)), Actor.player1, "You loose :("),
      (Reset(Player1, Board.fromStatusString(boardS1)), Actor.player2, rematchMesssage),
    )

    for ((game, player, message) <- tests) {
      s"given player: $player and $game returns $message" in {
        game.toMessage(player) shouldBe message
      }
    }
  }
}
