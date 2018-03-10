package info.ditrapani.tictactoe.state

import info.ditrapani.tictactoe.Spec

class EntityTest extends Spec {
  "Spectator" - {
    "toString returns string representation" in {
      Spectator.toString shouldBe "a Spectator"
    }

    "toResponse returns response entity string" in {
      Spectator.toResponse shouldBe "S"
    }

    "toPlayer returns None" in {
      Spectator.toPlayer shouldBe None
    }
  }

  "Actor" - {
    "containing Player1" - {
      "toString returns string representation" in {
        Actor.player1.toString shouldBe "Player 1"
      }

      "toResponse returns response entity string" in {
        Actor.player1.toResponse shouldBe "1"
      }

      "toPlayer returns Some(Player1)" in {
        Actor.player1.toPlayer shouldBe Some(Player1)
      }
    }

    "containing Player2" - {
      "toString returns string representation" in {
        Actor.player2.toString shouldBe "Player 2"
      }

      "toResponse returns response entity string" in {
        Actor.player2.toResponse shouldBe "2"
      }

      "toPlayer returns Some(Player1)" in {
        Actor.player2.toPlayer shouldBe Some(Player2)
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
