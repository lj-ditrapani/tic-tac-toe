package info.ditrapani.tictactoe

import org.scalatest.{AsyncFreeSpec, FreeSpec, Matchers}

abstract class Spec extends FreeSpec with Matchers

abstract class AsyncSpec extends AsyncFreeSpec with Matchers
