package jp.tricreo.baseunits.scala.money

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test

class AllotmentTest extends AssertionsForJUnit {
  /**等価性検証。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Equals {
    val abc123dollars = new Allotment("ABC", Money.dollars(1.23))
    assert(abc123dollars == new Allotment("ABC", Money.dollars(1.23)))
    assert(abc123dollars != new Allotment("ABC", Money.euros(1.23)))
    assert(abc123dollars != new Allotment("XYZ", Money.dollars(1.23)))
    assert(abc123dollars != new Allotment("ABC", Money.dollars(1.24)))
  }

  /**正負転換検証。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_Negated {
    val abc123dollars = new Allotment("ABC", Money.dollars(1.23))
    assert(abc123dollars.negated == new Allotment("ABC", Money.dollars(-1.23)))
  }

}