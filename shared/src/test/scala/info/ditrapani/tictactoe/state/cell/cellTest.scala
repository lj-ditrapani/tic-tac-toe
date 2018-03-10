package info.ditrapani.tictactoe.state.cell

import info.ditrapani.tictactoe.Spec

class CellTest extends Spec {
  private val tests = List(
    (Empty, "ECell", "E"),
    (X, "XCell", "X"),
    (O, "OCell", "O")
  )
  
  for ((cell, string, response) <- tests) {
    s"$cell.toString returns $string" in {
      cell.toString shouldBe string
    }
    s"$cell.toResponse returns $response" in {
      cell.toResponse shouldBe response
    }
  }

  "fromChar" - {
    val tests = List(
      ('E', Empty),
      ('X', X),
      ('O', O)
      )

    for ((char, cell) <- tests) {
      s"given $char returns $cell" in {
        Cell.fromChar(char) shouldBe cell
      }
    }

    s"given an unknown char, throws an IllegalArgumentException" in {
      an[IllegalArgumentException] should be thrownBy {
        Cell.fromChar('A')
      }
    }
  }
}
