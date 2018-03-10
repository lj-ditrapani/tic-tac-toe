package info.ditrapani.tictactoe.state

import cell.Cell

final case class Board(cells: Vector[Cell]) {
  override def toString = s"""Board
   ${cells(0)} ${cells(1)} ${cells(2)}
   ${cells(3)} ${cells(4)} ${cells(5)}
   ${cells(6)} ${cells(7)} ${cells(8)}
   """
  def toResponse: String = cells.map(_.toResponse).mkString
}

object Board {
  def init(): Board = create(Vector.fill(9)(cell.Empty))

  def create(cells: Vector[Cell]): Board = {
    require(cells.size == 9)
    Board(cells)
  }

  def fromStatusString(status: String): Board =
    Board.create(status.substring(3, 12).map(Cell.fromChar).toVector)
}
