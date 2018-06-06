package info.ditrapani.tictactoe

class ArgsTest extends AsyncSpec {
  "init - constructs the default Args" in {
    val io = for {
      args <- Args.init()
      state <- args.state.get
    } yield state shouldBe State.init
    io.unsafeToFuture
  }
}
