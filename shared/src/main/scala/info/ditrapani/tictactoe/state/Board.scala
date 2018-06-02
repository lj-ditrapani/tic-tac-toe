package info.ditrapani.tictactoe.state

import cell.Cell
import game.{EndingLines, P1Wins, P2Wins, Tie}

final case class Board(cells: Vector[Cell]) {
  override def toString = s"""Board
   ${cells(0)} ${cells(1)} ${cells(2)}
   ${cells(3)} ${cells(4)} ${cells(5)}
   ${cells(6)} ${cells(7)} ${cells(8)}
   """

  def toResponse: String = cells.map(_.toResponse).mkString

  def getEnding(): Option[EndingLines] = {
    val p1Lines = Board.lines.filter(line => line.forall(isX(_)))
    val p2Lines = Board.lines.filter(line => line.forall(isO(_)))
    val hasEmpties = Board.lines.exists(line => line.exists(isEmpty(_)))
    (p1Lines.isEmpty, p2Lines.isEmpty, hasEmpties) match {
      case (false, true, _) => Some(EndingLines(P1Wins, p1Lines))
      case (true, false, _) => Some(EndingLines(P2Wins, p2Lines))
      case (_, _, false) => Some(EndingLines(Tie, List[List[Int]]()))
      case _ => None
    }
  }

  def isX(index: Int): Boolean = cells(index) == cell.X

  def isO(index: Int): Boolean = cells(index) == cell.O

  def isEmpty(index: Int): Boolean = cells(index) == cell.Empty
}

object Board {
  val init: Board = create(Vector.fill(9)(cell.Empty))

  def create(cells: Vector[Cell]): Board = {
    require(cells.size == 9)
    Board(cells)
  }

  def fromStatusString(status: String): Board = {
    require(status.size == 12)
    Board.create(status.substring(3, 12).map(Cell.fromChar).toVector)
  }

  val lines: List[List[Int]] = List(
    List(0, 1, 2),
    List(3, 4, 5),
    List(6, 7, 8),
    List(0, 3, 6),
    List(1, 4, 7),
    List(2, 5, 8),
    List(0, 4, 8),
    List(2, 4, 6),
  )
}
