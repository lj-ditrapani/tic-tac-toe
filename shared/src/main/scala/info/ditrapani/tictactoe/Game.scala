package info.ditrapani.tictactoe

sealed abstract class Game {
  val emptyBoard = "EEEEEEEEE"
  def toResponse: String
  def toMessage(player: Player): String
}
object Init extends Game {
  def toResponse = "IN" + emptyBoard
  def toMessage(player: Player) = "No players have joined yet..."
}
object Player1Ready extends Game {
  def toResponse = "R1" + emptyBoard
  def toMessage(player: Player) = Game.playerReadyMessage(player, 1, 2)
}
object Player2Ready extends Game {
  def toResponse = "R2" + emptyBoard
  def toMessage(player: Player) = Game.playerReadyMessage(player, 2, 1)
}
final case class Player1Turn(board: Board) extends Game {
  def toResponse = "T1" + board.toResponse
  def toMessage(player: Player) = Game.playerTurnMessage(player, Player1, 1)
}
final case class Player2Turn(board: Board) extends Game {
  def toResponse = "T2" + board.toResponse
  def toMessage(player: Player) = Game.playerTurnMessage(player, Player2, 2)
}
final case class GameOver(winner: Player, board: Board) extends Game {
  def toResponse = "G" + winner.toResponse + board.toResponse
  def toMessage(player: Player) =
    player == Spectator match {
      case true => s"$winner won"
      case false =>
        winner == player match {
          case true => "You win!"
          case false => "You loose :("
        }
    }
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

  def playerReadyMessage(player: Player, ready: Int, waiting: Int): String =
    player match {
      case Spectator => s"Player $ready has joined.  " + waitingMessage(waiting)
      case _ => waitingMessage(waiting)
    }

  def playerTurnMessage(player: Player, turnPlayer: Player, turn: Int): String =
    player == turnPlayer match {
      case true => "Your turn"
      case false => s"Player $turn's turn"
    }

  private def waitingMessage(waiting: Int): String = s"Waiting for Player $waiting to join"
}

sealed abstract class Player {
  def toResponse: String
  def token: Cell
  def toggle: Player
}
object Player1 extends Player {
  override def toString = "Player 1"
  def toResponse = "1"
  def token = X
  def toggle = Player2
}
object Player2 extends Player {
  override def toString = "Player 2"
  def toResponse = "2"
  def token = O
  def toggle = Player1
}
object Spectator extends Player {
  override def toString = "a Spectator"
  def toResponse = "S"
  def token = Empty
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def toggle: Player =
    throw new RuntimeException("toggle should only be called on Player1 or Player2")
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
