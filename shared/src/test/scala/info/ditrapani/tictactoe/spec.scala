package info.ditrapani.tictactoe

import org.scalatest.{AsyncFunSpec, FreeSpec, Matchers}

abstract class Spec extends FreeSpec with Matchers

abstract class AsyncSpec extends AsyncFunSpec with Matchers
