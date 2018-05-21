package info.ditrapani.tictactoe

import org.scalajs.dom.document
import org.scalajs.jquery.jQuery
import fr.hmil.roshttp
import monix.execution.Scheduler.Implicits.global
import roshttp.HttpRequest
import roshttp.Method.POST
import roshttp.response.SimpleHttpResponse
import scala.scalajs.js.timers
import state.Board
import state.cell
import cell.Cell
import state.game
import game.Game
import state.{Actor, Entity, Spectator}

object App {
  val host: String = document.location.host.split(":")(0)
  val port: Int = document.location.host.split(":")(1).toInt
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var entity: Entity = Spectator
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var gameState: Game = game.Init

  def main(args: Array[String]): Unit = {
    println("before ui setup")
    jQuery(() => setupUI())
    println("after ui setup")
    println("before update loop")
    statusUpdateLoop()
    println("after update loop")
    (): Unit
  }

  def click(index: Int): Unit = {
    (entity, gameState) match {
      case (Actor(self), game.Turn(turnPlayer, _)) if self == turnPlayer => postPlay(index)
      case _ => (): Unit
    }
  }

  def postPlay(index: Int): Unit = {
    HttpRequest()
      .withHost(host)
      .withPort(port)
      .withPath(s"/play/$index")
      .withMethod(POST)
      .send()
      .map(updateStatusWith)
    (): Unit
  }

  def setupUI(): Unit = {
    import scalatags.JsDom.all._
    val bgImg = "img/bg.png"

    val d = div(Styles.body)(
      h1("Tic-tac-toe"),
      p(id := "entity")("Player Unknown"),
      p(id := "message")("Loading..."),
      div(Styles.frame)(
        for (x <- 1.to(3))
          yield
            div(Styles.row)(
              for (y <- 1.to(3))
                yield {
                  val index = (x - 1) * 3 + y
                  val boxId = s"box$index"
                  val imgId = s"img$index"
                  val clickAttr = onclick := { () =>
                    click(index - 1)
                  }
                  val image = img(id := imgId, src := bgImg)
                  div(id := boxId, Styles.availableBox, clickAttr)(image)
                }
            )
      )
    )
    jQuery("head").append(s"<style>${Styles.styleSheetText}</style>")
    jQuery("body").append(d.render)
    (): Unit
  }

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def statusUpdateLoop(): Unit = {
    HttpRequest()
      .withHost(host)
      .withPort(port)
      .withPath("/status")
      .send()
      .map(updateStatusWith)
      .map(
        _ =>
          timers.setTimeout(500) {
            statusUpdateLoop()
        }
      )
    (): Unit
  }

  def updateStatusWith(response: SimpleHttpResponse): Unit = {
    val status = response.body
    println(status)
    entity = Entity.fromStatusString(status)
    gameState = Game.fromStatusString(status)
    jQuery("#entity").text(s"You are $entity")
    jQuery("#message").text(s"${gameState.toMessage(entity)}")
    renderBoard(gameState.board)
    println(s"$entity  $gameState")
  }

  def renderBoard(board: Board): Unit = {
    def getImage(c: Cell): String = c match {
      case cell.X => "img/ex.png"
      case cell.O => "img/oh.png"
      case cell.Empty => "img/bg.png"
    }
    for ((c, index) <- board.cells.zipWithIndex) {
      jQuery(s"#img${index + 1}").attr("src", getImage(c))
    }
  }
}

import scalatags.stylesheet.StyleSheet

@SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
object Styles extends StyleSheet {
  import scalatags.JsDom.{styles => s}
  import scalatags.JsDom.implicits._
  initStyleSheet()

  val unavailableBox = cls(
    s.display := "inline-block",
    s.backgroundColor := "white",
    s.height := 128,
    s.width := 128,
    s.border := "2px solid white",
  )

  val availableBox = cls(
    unavailableBox.splice,
    &.hover(
      s.borderColor := "red"
    )
  )

  val row = cls(
    s.height := 132,
    s.width := 396
  )

  val frame = cls(
    s.width := 396,
    s.padding := 2
  )

  val body = cls()
}
