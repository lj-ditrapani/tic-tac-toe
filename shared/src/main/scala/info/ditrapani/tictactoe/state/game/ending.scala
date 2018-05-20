package info.ditrapani.tictactoe.state.game

sealed abstract class Ending
object P1wins extends Ending
object P2wins extends Ending
object Tie extends Ending
