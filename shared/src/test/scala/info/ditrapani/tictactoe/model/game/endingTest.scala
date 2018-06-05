package info.ditrapani.tictactoe.model.game

import info.ditrapani.tictactoe.Spec

class EndingTest extends Spec {
  "P1Wins" - {
    "toResponse returns response string char" in {
      P1Wins.toResponse() shouldBe "1"
    }

    "toMessage returns the game message" in {
      P1Wins.toMessage() shouldBe "Player 1 wins!"
    }

    "toString returns a string representation" in {
      P1Wins.toString() shouldBe "P1Wins"
    }
  }

  "P2Wins" - {
    "toResponse returns response string char" in {
      P2Wins.toResponse() shouldBe "2"
    }

    "toMessage returns the game message" in {
      P2Wins.toMessage() shouldBe "Player 2 wins!"
    }

    "toString returns a string representation" in {
      P2Wins.toString() shouldBe "P2Wins"
    }
  }

  "Tie" - {
    "toResponse returns response string char" in {
      Tie.toResponse() shouldBe "T"
    }

    "toMessage returns the game message" in {
      Tie.toMessage() shouldBe "It's a tie!"
    }

    "toString returns a string representation" in {
      Tie.toString() shouldBe "Tie"
    }
  }
}
