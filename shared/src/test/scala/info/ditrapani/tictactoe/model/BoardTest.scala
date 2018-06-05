package info.ditrapani.tictactoe.model

import game.{EndingLines, P1Wins, P2Wins, Tie}
import info.ditrapani.tictactoe.Spec

class BoardTest extends Spec {
  "Board.init" - {
    "returns an empty board" in {
      Board.init shouldBe Board(Vector.fill(9)(cell.Empty))
    }
  }

  "create" - {
    "throws if cells.size < 9" in {
      an[IllegalArgumentException] should be thrownBy {
        Board.create(Vector.fill(8)(cell.Empty))
      }
    }

    "throws if cells.size > 9" in {
      an[IllegalArgumentException] should be thrownBy {
        Board.create(Vector.fill(10)(cell.Empty))
      }
    }

    "constructs board if cells.size = 9" in {
      Board.create(Vector.fill(9)(cell.Empty)) shouldBe Board.init
    }
  }

  "fromStatusString" - {
    "throws if status.size < 12" in {
      an[IllegalArgumentException] should be thrownBy {
        Board.fromStatusString("1R1OOOXXXEE")
      }
    }

    "throws if status.size > 12" in {
      an[IllegalArgumentException] should be thrownBy {
        Board.fromStatusString("1R1OOOXXXEEEO")
      }
    }

    "constructs board if status.size = 12" in {
      Board.fromStatusString("1R1OOOXXXEEE") shouldBe Board.create(
        Vector(
          cell.O,
          cell.O,
          cell.O,
          cell.X,
          cell.X,
          cell.X,
          cell.Empty,
          cell.Empty,
          cell.Empty
        )
      )
    }
  }

  "getEnding" - {
    val tests = List[(String, EndingLines)](
      ("---OOOXEEEEX", EndingLines(P2Wins, List(List(0, 1, 2)))),
      ("---EEEXEEOOO", EndingLines(P2Wins, List(List(6, 7, 8)))),
      ("---OOXEEXEEX", EndingLines(P1Wins, List(List(2, 5, 8)))),
      ("---OOXEXEXEE", EndingLines(P1Wins, List(List(2, 4, 6)))),
      ("---OOXXXOOOX", EndingLines(Tie, List[List[Int]]())),
      ("---OXOOOXXOX", EndingLines(Tie, List[List[Int]]())),
      ("---OXXOXOXXO", EndingLines(P1Wins, List(List(1, 4, 7), List(2, 4, 6)))),
      ("---XOXOOOXOX", EndingLines(P2Wins, List(List(3, 4, 5), List(1, 4, 7)))),
    )

    for ((boardString, endingLines) <- tests) {
      s"$boardString -> Some($endingLines)" in {
        Board.fromStatusString(boardString).getEnding() shouldBe Some(endingLines)
      }
    }

    "---EEEEEEEEE -> None" in {
      Board.fromStatusString("---EEEEEEEEE").getEnding() shouldBe None
    }

    "---XOXOEOXOX -> None" in {
      Board.fromStatusString("---XOXOEOXOX").getEnding() shouldBe None
    }
  }
}
