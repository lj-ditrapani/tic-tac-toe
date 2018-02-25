package info.ditrapani.tictactoe

sealed abstract class Game
object Init extends Game
object Player1Ready extends Game
object Player2Ready extends Game
final case class Player1Turn(board: Board) extends Game
final case class Player2Turn(board: Board) extends Game
final case class GameOver(winner: Player, board: Board) extends Game

sealed abstract class Player
object Player1 extends Player {
  override def toString = "Player1"
}
object Player2 extends Player {
  override def toString = "Player1"
}

final case class Board(cells: Vector[Cell])

object Board {
  def init(): Board = create(Vector.fill(9)(Empty))
  def create(cells: Vector[Cell]): Board = {
    require(cells.size == 9)
    Board(cells)
  }
}

sealed abstract class Cell
object Empty extends Cell {
  override def toString = "EmptyCell"
}
object X extends Cell {
  override def toString = "XCell"
}
object O extends Cell {
  override def toString = "OCell"
}
