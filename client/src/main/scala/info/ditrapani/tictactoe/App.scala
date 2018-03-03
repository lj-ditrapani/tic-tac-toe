package info.ditrapani.tictactoe

import org.scalajs.jquery.jQuery
import fr.hmil.roshttp.HttpRequest

object App {
  def main(args: Array[String]): Unit = {
    jQuery(() => setupUI())
    import monix.execution.Scheduler.Implicits.global
    HttpRequest()
      .withHost("localhost")
      .withPort(8080)
      .withPath("/status")
      .send()
      .map(r => {
        val status = r.body
        println(status)
        val player = Player.fromStatusString(status)
        val game = Game.fromStatusString(status)
        jQuery("#player").text(s"You are $player")
        jQuery("#message").text(s"${game.toMessage(player)}")
        println(s"$player  $game")
      })
    (): Unit
  }

  def click(id: String)(): Unit = {
    jQuery(s"#$id").attr("src", "img/ex.png")
    (): Unit
  }

  def setupUI(): Unit = {
    import scalatags.JsDom.all._
    val bgImg = "img/bg.png"

    val d = div(Styles.body)(
      h1("Tic-tac-toe"),
      p(id := "player")("Player Unknown"),
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
                    click(imgId)()
                  }
                  val image = img(id := imgId, src := bgImg)
                  div(id := boxId, Styles.availableBox, clickAttr)(image)
                }
            )
      )
    )
    jQuery(s"<style>${Styles.styleSheetText}</style>").appendTo("head")
    jQuery("body").append(d.render)
    (): Unit
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
