package info.ditrapani.tictactoe.model.cell

sealed abstract class Cell {
  def toResponse: String
}
object Empty extends Cell {
  override def toString = "ECell"
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
