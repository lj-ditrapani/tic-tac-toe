package info.ditrapani.tictactoe.model

import game.{P1Wins, P2Wins, Tie}
import info.ditrapani.tictactoe.Spec

class EntityTest extends Spec {
  "Spectator" - {
    "toString returns string representation" in {
      Spectator.toString shouldBe "a Spectator"
    }

    "toResponse returns response entity string" in {
      Spectator.toResponse shouldBe "S"
    }

    "readyMessage" in {
      Spectator.readyMessage() shouldBe "Player 1 has joined.  Waiting for Player 2 to join."
    }

    "turnMessage" in {
      Spectator.turnMessage(Player2) shouldBe "Player 2's turn"
    }

    "gameOverMessage" in {
      Spectator.gameOverMessage(Tie) shouldBe "It's a tie!"
    }

    "resetMessage" in {
      Spectator.resetMessage(Player1) shouldBe "Player 1 wants a rematch"
    }

    "quitMessage" in {
      Spectator.quitMessage(Player2) shouldBe "Player 2 quit"
    }
  }

  "Actor" - {
    "readyMessage" in {
      Actor.player1.readyMessage() shouldBe "Waiting for Player 2 to join."
    }

    "turnMessage" - {
      "when the players match, returns `your turn`" in {
        Actor.player2.turnMessage(Player2) shouldBe "Your turn"
      }

      "when the players don't match, returns `Player X's turn`" in {
        Actor.player2.turnMessage(Player1) shouldBe "Player 1's turn"
      }
    }

    "gameOverMessage" - {
      "when it is a Tie, returns tie message" in {
        Actor.player1.gameOverMessage(Tie) shouldBe "It's a tie!"
      }

      "when the Actor contains Player1 and the ending is P1Wins, returns `You win!`" in {
        Actor.player1.gameOverMessage(P1Wins) shouldBe "You win!"
      }

      "when the Actor contains Player2 and the ending is P2Wins, returns `You win!`" in {
        Actor.player2.gameOverMessage(P2Wins) shouldBe "You win!"
      }

      "when the player lost, returns `You loose :(`" in {
        Actor.player2.gameOverMessage(P1Wins) shouldBe "You loose :("
      }
    }

    "resetMessage" - {
      "when the players match, returns a waiting message" in {
        Actor.player1.resetMessage(Player1) shouldBe "Waiting for Player 2 to accept the rematch."
      }

      "when the players don't match, returns a rematch request message" in {
        Actor.player2.resetMessage(Player1) shouldBe "Player 1 wants a rematch.  Do you accept?"
      }
    }

    "quitMessage" - {
      "when the players match, returns a shameing message" in {
        Actor.player2.quitMessage(Player2) shouldBe "You quit...quitter"
      }

      "when the players don't match, returns a quit notification" in {
        Actor.player1
          .quitMessage(Player2)
          .shouldBe("Player 2 quit.  Acknowlege by pressing the Acknowlege Quit button.")
      }
    }

    "containing Player1" - {
      "toString returns string representation" in {
        Actor.player1.toString shouldBe "Player 1"
      }

      "toResponse returns response entity string" in {
        Actor.player1.toResponse shouldBe "1"
      }
    }

    "containing Player2" - {
      "toString returns string representation" in {
        Actor.player2.toString shouldBe "Player 2"
      }

      "toResponse returns response entity string" in {
        Actor.player2.toResponse shouldBe "2"
      }
    }
  }

  "fromStatusString" - {
    val tests = List(
      ("1", Actor.player1),
      ("2", Actor.player2),
      ("S", Spectator)
    )

    for ((string, entity) <- tests) {
      s"given $string returns $entity" in {
        Entity.fromStatusString(string) shouldBe entity
      }
    }

    "given unknown character, throws an IllegalArgumentException" in {
      an[IllegalArgumentException] should be thrownBy {
        Entity.fromStatusString("0")
      }
    }
  }
}
