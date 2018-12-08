package info.ditrapani.tictactoe

import scalatags.stylesheet.StyleSheet

@SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
object Styles extends StyleSheet {
  import scalatags.JsDom.{styles => s}
  import scalatags.JsDom.implicits._
  initStyleSheet()

  val boxSide = 128
  val boxBorder = 8
  val boxWithBorder = 128 + 2 * 8

  val box = cls(
    s.display := "inline-block",
    s.backgroundColor := "white",
    s.height := boxSide,
    s.width := boxSide,
    s.border := "8px solid white",
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
    s.height := boxWithBorder,
    s.width := boxWithBorder * 3
  )

  val frame = cls(
    s.width := boxWithBorder
  )

  val body = cls(
    s.fontFamily := "'Press Start 2P'",
    s.fontSize := "16px"
  )

  val h1 = cls(
    s.fontSize := "16px",
    s.fontWeight := "normal"
  )

  val buttonBox = cls(
    s.padding := 8
  )

  val button = cls(
    s.fontFamily := "'Press Start 2P'",
    s.fontSize := "16px",
    s.border := "none",
    s.textAlign := "center",
    s.padding := "16px 32px",
    s.marginRight := 16,
    s.cursor := "pointer",
    s.backgroundColor := "#000000",
    s.color := "#FFFFFF",
    &.hover(
      s.backgroundColor := "#555555",
      s.color := "#FFFFFF"
    ),
    &.disabled(
      s.backgroundColor := "#AAAAAA",
      s.color := "#555555",
      s.cursor := "default"
    )
  )
}
