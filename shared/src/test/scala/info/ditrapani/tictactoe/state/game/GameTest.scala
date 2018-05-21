package info.ditrapani.tictactoe.state.game

import info.ditrapani.tictactoe.Spec
import info.ditrapani.tictactoe.state.{Actor, Board, Player1, Player2, Spectator}

class GameTest extends Spec {
  private val boardT1 = "2T1XXXOOOXXX"
  private val boardT2 = "2T2EEEXXXEEE"
  private val boardG1 = "2G1OOOEEEEEE"
  private val boardG2 = "1G2EEEEEEXXX"
  private val boardGT = "2GTXXOOOXXXO"
  private val boardX1 = "2X1XXOOOXXXO"
  private val boardX2 = "2X2XXOOOXXXO"
  private val boardQ1 = "2Q1XXOOOXXXO"
  private val boardQ2 = "2Q2XXOOOXXXO"

  "fromStatusString" - {
    val tests = List(
      ("1INEEEEEEEEE", Init),
      ("1R1EEEEEEEEE", Ready(Player1)),
      ("1R2EEEEEEEEE", Ready(Player2)),
      (boardT1, Turn(Player1, Board.fromStatusString(boardT1))),
      (boardT2, Turn(Player2, Board.fromStatusString(boardT2))),
      (boardG1, GameOver(P1Wins, Board.fromStatusString(boardG1))),
      (boardG2, GameOver(P2Wins, Board.fromStatusString(boardG2))),
      (boardGT, GameOver(Tie, Board.fromStatusString(boardGT))),
      (boardX1, Reset(Player1, Board.fromStatusString(boardX1))),
      (boardX2, Reset(Player2, Board.fromStatusString(boardX2))),
      (boardQ1, Quit(Player1, Board.fromStatusString(boardQ1))),
      (boardQ2, Quit(Player2, Board.fromStatusString(boardQ2))),
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
      (GameOver(P2Wins, Board.fromStatusString(boardG2)), "G2EEEEEEXXX"),
      (Reset(Player1, Board.fromStatusString(boardX1)), "X1XXOOOXXXO"),
      (Reset(Player2, Board.fromStatusString(boardX1)), "X2XXOOOXXXO"),
      (Quit(Player1, Board.fromStatusString(boardQ2)), "Q1XXOOOXXXO"),
      (Quit(Player2, Board.fromStatusString(boardQ2)), "Q2XXOOOXXXO"),
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
      (Ready(Player1), Actor.player1, "Waiting for Player 2 to join."),
      (Ready(Player1), Spectator, "Player 1 has joined.  Waiting for Player 2 to join."),
      (Ready(Player2), Actor.player1, "Waiting for Player 1 to join."),
      (Turn(Player1, Board.fromStatusString(boardT1)), Actor.player1, "Your turn"),
      (Turn(Player2, Board.fromStatusString(boardT2)), Actor.player1, "Player 2's turn"),
      (GameOver(P1Wins, Board.fromStatusString(boardG1)), Spectator, "Player 1 wins!"),
      (GameOver(P1Wins, Board.fromStatusString(boardG1)), Actor.player1, "You win!"),
      (GameOver(P2Wins, Board.fromStatusString(boardG2)), Actor.player1, "You loose :("),
      (Reset(Player1, Board.fromStatusString(boardX1)), Actor.player2, rematchMesssage),
      (Quit(Player2, Board.fromStatusString(boardQ2)), Actor.player2, "You quit...quitter"),
    )

    for ((game, player, message) <- tests) {
      s"given player: $player and $game returns $message" in {
        game.toMessage(player) shouldBe message
      }
    }
  }
}
