package info.ditrapani.tictactoe.state

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
}
