package info.ditrapani.tictactoe.state

import info.ditrapani.tictactoe.Spec

class PlayerTest extends Spec {
  "Player1" - {
    "toString returns string representation" in {
      Player1.toString shouldBe "Player 1"
    }

    "toResponse returns response string" in {
      Player1.toResponse shouldBe "1"
    }

    "token returns cell X" in {
      Player1.token shouldBe cell.X
    }

    "toggle returns Player2" in {
      Player1.toggle shouldBe Player2
    }
  }

  "Player2" - {
    "toString returns string representation" in {
      Player2.toString shouldBe "Player 2"
    }

    "toResponse returns response string" in {
      Player2.toResponse shouldBe "2"
    }

    "token returns cell O" in {
      Player2.token shouldBe cell.O
    }

    "toggle returns Player2" in {
      Player2.toggle shouldBe Player1
    }
  }


  "fromStatusString" - {
    val tests = List(
      ("1", Player1),
      ("2", Player2)
    )

    for ((string, player) <- tests) {
      s"given $string returns $player" in {
        Player.fromStatusString(string) shouldBe player
      }
    }

    "given unknown character, throws an IllegalArgumentException" in {
      an[IllegalArgumentException] should be thrownBy {
        Player.fromStatusString("0")
      }
    }
  }
}
