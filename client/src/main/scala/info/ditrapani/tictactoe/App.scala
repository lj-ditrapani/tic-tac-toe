package info.ditrapani.tictactoe

import org.scalajs.jquery.jQuery

object App {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    jQuery(() => setupUI())
    (): Unit
  }

  def setupUI(): Unit = {
    import scalatags.JsDom.tags._
    import scalatags.JsDom.{attrs => a}
    import scalatags.JsDom.implicits._
    val bgImg = "img/bg.png"

    val d = div(Styles.body)(
      h1("Tic-tac-toe"),
      div(Styles.frame)(
        for (x <- 1.to(3))
          yield div(Styles.row)(
            for (y <- 1.to(3))
              yield div(Styles.availableBox)(img(a.src := bgImg))
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
