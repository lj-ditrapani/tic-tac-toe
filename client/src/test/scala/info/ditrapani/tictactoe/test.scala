package info.ditrapani.tictactoe

import org.scalatest.{FunSpec, Matchers}
import org.scalajs.jquery.jQuery

class MainSpec extends FunSpec with Matchers {
  describe("UI") {
    it("says Hello World") {
      App.setupUI()
      jQuery("body p:contains('Hello World')").length shouldBe 1
    }
  }
}
