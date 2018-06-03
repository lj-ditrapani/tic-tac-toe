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
  val baseRequest: HttpRequest = HttpRequest().withHost(host).withPort(port)
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var entity: Entity = Spectator
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  var gameState: Game = game.Init

  def main(args: Array[String]): Unit = {
    jQuery(() => setupUI())
    statusUpdateLoop()
    (): Unit
  }

  def click(index: Int): Unit = {
    (entity -> gameState) match {
      case (Actor(self), game.Turn(turnPlayer, _)) if self == turnPlayer => postPlay(index)
      case _ => (): Unit
    }
  }

  def postPlay(index: Int): Unit = {
    baseRequest
      .withPath(s"/play/$index")
      .withMethod(POST)
      .send()
      .map(updateStatusWith)
    (): Unit
  }

  def postReset(): Unit = {
    baseRequest
      .withPath("/reset")
      .withMethod(POST)
      .send()
      .map(updateStatusWith)
    (): Unit
  }

  def postAcceptReset(): Unit = {
    baseRequest
      .withPath("/accept-reset")
      .withMethod(POST)
      .send()
      .map(updateStatusWith)
    (): Unit
  }

  def setupUI(): Unit = {
    import scalatags.JsDom.all._
    val bgImg = "img/bg.png"

    val d = div(
      h1("Tic-tac-toe"),
      p(id := "entity")("Player Unknown"),
      p(id := "message")("Loading..."),
      div(Styles.frame)(
        for (x <- 0.to(2))
          yield
            div(Styles.row)(
              for (y <- 0.to(2))
                yield {
                  val index = x * 3 + y
                  val boxId = s"box$index"
                  val imgId = s"img$index"
                  val clickAttr = onclick := { () =>
                    click(index)
                  }
                  val image = img(id := imgId, src := bgImg)
                  div(id := boxId, Styles.box, Styles.availableBox, clickAttr)(image)
                }
            )
      ),
      div(Styles.buttons)(
        button(id := "reset-button", disabled := true, onclick := { () =>
          postReset()
        })("Reset"),
        button(id := "accept-reset-button", disabled := true, onclick := { () =>
          postAcceptReset()
        })("Accept Reset")
      )
    )
    jQuery("head").append(s"<style>${Styles.styleSheetText}</style>")
    jQuery("body").append(d.render)
    (): Unit
  }

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def statusUpdateLoop(): Unit = {
    baseRequest
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
    entity = Entity.fromStatusString(status)
    gameState = Game.fromStatusString(status)
    jQuery("#entity").text(s"You are $entity")
    jQuery("#message").text(s"${gameState.toMessage(entity)}")
    renderBoard(gameState.board)
    (gameState -> entity) match {
      case (game.GameOver(_, _), Actor(_)) =>
        enableButtons(true, false)
      case (game.Reset(resetPlayer, _), Actor(self)) if resetPlayer != self =>
        enableButtons(false, true)
      case _ =>
        enableButtons(false, false)
    }
    (): Unit
  }

  def renderBoard(board: Board): Unit = {
    def getImage(c: Cell): String = c match {
      case cell.X => "img/ex.png"
      case cell.O => "img/oh.png"
      case cell.Empty => "img/bg.png"
    }
    for ((c, index) <- board.cells.zipWithIndex) {
      jQuery(s"#img$index").attr("src", getImage(c))
      jQuery(s"#box$index").removeClass(Styles.winBox.name)
      if (c != cell.Empty) {
        jQuery(s"#box$index").removeClass(Styles.availableBox.name)
      } else {
        jQuery(s"#box$index").addClass(Styles.availableBox.name)
      }
    }
    val lines = board.getEnding().map(_.lines).getOrElse(List[List[Int]]())
    for (line <- lines) {
      for (index <- line) {
        jQuery(s"#box$index").addClass(Styles.winBox.name)
      }
    }
  }

  def enableButtons(
      reset: Boolean,
      acceptReset: Boolean
  ): Unit = {
    jQuery("#reset-button").attr("disabled", !reset)
    jQuery("#accept-reset-button").attr("disabled", !acceptReset)
    (): Unit
  }
}

import scalatags.stylesheet.StyleSheet

@SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
object Styles extends StyleSheet {
  import scalatags.JsDom.{styles => s}
  import scalatags.JsDom.implicits._
  initStyleSheet()

  val box = cls(
    s.display := "inline-block",
    s.backgroundColor := "white",
    s.height := 128,
    s.width := 128,
    s.border := "2px solid white",
  )

  val availableBox = cls(
    &.hover(
      s.borderColor := "red"
    )
  )

  val winBox = cls(
    s.borderColor := "blue"
  )

  val row = cls(
    s.height := 132,
    s.width := 396
  )

  val frame = cls(
    s.width := 396,
    s.padding := 2
  )

  val buttons = cls(
    s.padding := 10
  )
}
