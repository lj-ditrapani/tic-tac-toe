package info.ditrapani.tictactoe

import org.scalajs.jquery.jQuery

object App {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    setupUI()
  }

  def appendPar(text: String): Unit = {
    jQuery("body").append(s"<p>$text</p>")
    (): Unit
  }

  def addClickedMessage(): Unit = {
    appendPar("You clicked the button!")
  }

  def setupUI(): Unit = {
    jQuery("body").append("<p>Hello World</p>")
    jQuery("#click-me-button").click(() => addClickedMessage())
    (): Unit
  }
}
