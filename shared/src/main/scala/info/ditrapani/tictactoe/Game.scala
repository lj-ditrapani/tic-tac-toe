package info.ditrapani.tictactoe

sealed abstract class Game {
  val emptyBoard = "EEEEEEEEE"
  def toResponse: String
}
object Init extends Game {
  def toResponse = "IN" + emptyBoard
}
object Player1Ready extends Game {
  def toResponse = "R1" + emptyBoard
}
object Player2Ready extends Game {
  def toResponse = "R2" + emptyBoard
}
final case class Player1Turn(board: Board) extends Game {
  def toResponse = "T1" + board.toResponse
}
final case class Player2Turn(board: Board) extends Game {
  def toResponse = "T2" + board.toResponse
}
final case class GameOver(winner: Player, board: Board) extends Game {
  def toResponse = "G" + winner.toResponse + board.toResponse
}

object Game {
  def fromStatusString(status: String): Game =
    status.substring(1, 3) match {
      case "IN" => Init
      case "R1" => Player1Ready
      case "R2" => Player2Ready
      case "T1" => Player1Turn(Board.fromStatusString(status))
      case "T2" => Player2Turn(Board.fromStatusString(status))
      case "G1" => GameOver(Player1, Board.fromStatusString(status))
      case "G2" => GameOver(Player2, Board.fromStatusString(status))
    }
}

sealed abstract class Player {
  def toResponse: String
  def token: Cell
}
object Player1 extends Player {
  override def toString = "Player1"
  def toResponse = "1"
  def token = X
}
object Player2 extends Player {
  override def toString = "Player2"
  def toResponse = "2"
  def token = O
}
object Spectator extends Player {
  override def toString = "Spectator"
  def toResponse = "S"
  def token = Empty
}
object Player {
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def fromStatusString(status: String): Player =
    status(0) match {
      case '1' => Player1
      case '2' => Player2
      case 'S' => Spectator
      case char => throw new IllegalArgumentException(s"Uknown player char $char")
    }
}

final case class Board(cells: Vector[Cell]) {
  override def toString = s"""Board
   ${cells(0)} ${cells(1)} ${cells(2)}
   ${cells(3)} ${cells(4)} ${cells(5)}
   ${cells(6)} ${cells(7)} ${cells(8)}
   """
  def toResponse: String = cells.map(_.toResponse).mkString
}

object Board {
  def init(): Board = create(Vector.fill(9)(Empty))

  def create(cells: Vector[Cell]): Board = {
    require(cells.size == 9)
    Board(cells)
  }

  def fromStatusString(status: String): Board =
    Board.create(status.substring(3, 12).map(Cell.fromChar).toVector)
}

sealed abstract class Cell {
  def toResponse: String
}
object Empty extends Cell {
  override def toString = "EmptyCell"
  def toResponse = "E"
}
object X extends Cell {
  override def toString = "XCell"
  def toResponse = "X"
}
object O extends Cell {
  override def toString = "OCell"
  def toResponse = "O"
}
object Cell {
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def fromChar(c: Char): Cell =
    c match {
      case 'E' => Empty
      case 'X' => X
      case 'O' => O
      case x => throw new IllegalArgumentException(s"Unknown Cell char $x")
    }
}
