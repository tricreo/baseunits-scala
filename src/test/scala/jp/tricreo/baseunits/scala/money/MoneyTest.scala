/*
 * Copyright 2011 Tricreo Inc and the Others.
 * lastModified : 2011/04/22
 *
 * This file is part of Tricreo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.tricreo.baseunits.scala.money

import org.scalatest.junit.AssertionsForJUnit
import org.junit.{Test, Before}
import java.util.{Locale, Currency}
import jp.tricreo.baseunits.scala.util.Ratio
import collection.mutable.ListBuffer

/**`Money`のテストクラス。
 */
class MoneyTest extends AssertionsForJUnit {
  val USD = Currency.getInstance("USD")

  val JPY = Currency.getInstance("JPY")

  val EUR = Currency.getInstance("EUR")

  var d15: Money = _

  var d2_51: Money = _

  var y50: Money = _

  var y100: Money = _

  var e2_51: Money = _

  var d100: Money = _

  var d0: Money = _

  var y0: Money = _

  var y100minus: Money = _


  /**テストを初期化する。
   *
   * @throws Exception 例外が発生した場合
   */
  @Before
  def setUp {
    d15 = Money.adjustBy(BigDecimal("15.0"), USD)
    d2_51 = Money.adjustBy(BigDecimal("2.51"), USD)
    e2_51 = Money.adjustBy(BigDecimal("2.51"), EUR)
    y50 = Money.adjustBy(BigDecimal("50"), JPY)
    y100 = Money.adjustBy(BigDecimal("100"), JPY)
    d100 = Money.adjustBy(BigDecimal("100.0"), USD)
    d0 = Money.adjustBy(BigDecimal(0), USD)
    y0 = Money.adjustBy(BigDecimal(0), JPY)
    y100minus = Money.adjustBy(BigDecimal("-100"), JPY)
  }

  /**{@link Money}のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Serialization {
    //SerializationTester.assertCanBeSerialized(d15)
  }

  /**{@link Money(double, Currency)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_CreationFromDouble {
    assert(Money.adjustBy(15.0, USD) == d15)
    assert(Money.adjustBy(2.51, USD) == d2_51)
    assert(Money.adjustBy(50.1, JPY) == y50)
    assert(Money.adjustBy(100, JPY) == y100)
  }

  /**円単位 {@link Money} のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_Yen {
    assert(y50.toString == "￥ 50")
    val y80 = Money.adjustBy(BigDecimal("80"), JPY)
    val y30 = Money.adjustBy(30, JPY)
    assert(y50.plus(y30) == y80)
    assert(y50.times(1.6) == y80, "mult")
  }

  /**{@link Money#Money(BigDecimal, Currency)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_Constructor {
    val d69_99 = new Money(BigDecimal("69.99"), USD)
    assert(d69_99.breachEncapsulationOfAmount == BigDecimal("69.99"))
    assert(d69_99.breachEncapsulationOfCurrency == USD)
    try {
      new Money(BigDecimal("69.999"), USD)
      fail("Money constructor shall never round, and shall not accept a value whose scale doesn't fit the Currency.")
    } catch {
      case _: IllegalArgumentException =>
    }
  }

  /**{@link Money#dividedBy(double)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_Divide() {
    assert(d100.dividedBy(3) == Money.dollars(33.33))
    assert(d100.dividedBy(6) == Money.dollars(16.67))
    assert(y100.dividedBy(6) == Money.yens(BigDecimal(17)))
    assert(y50.dividedBy(5) == Money.yens(10))
  }

  /**{@link Money#times(double)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_Multiply() {
    assert(d15.times(10) == Money.dollars(150))
    assert(d15.times(0.1) == Money.dollars(1.5))
    assert(d100.times(0.7) == Money.dollars(BigDecimal("70")))
  }

  /**{@link Money#times(double, RoundingMode)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_MultiplyRounding() {
    assert(d100.times(0.66666667) == Money.dollars(66.67))
    assert(d100.times(0.66666667, BigDecimal.RoundingMode.DOWN) == Money.dollars(66.66))
  }

  /**{@link Money#times(BigDecimal, RoundingMode)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_MultiplicationWithExplicitRounding {
    assert(d100.times(BigDecimal("0.666666"), BigDecimal.RoundingMode.HALF_EVEN) == Money.dollars(66.67))
    assert(d100.times(BigDecimal("0.666666"), BigDecimal.RoundingMode.DOWN) == Money.dollars(66.66))
    assert(d100.negated.times(BigDecimal("0.666666"), BigDecimal.RoundingMode.DOWN) == Money.dollars(-66.66))
  }

  /**{@link Money#minimumIncrement()}のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_MinimumIncrement {
    assert(d100.minimumIncrement == Money(0.01, USD))
    assert(y50.minimumIncrement == Money(1, JPY))
  }

  /**{@link Money#plus(Money)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_AdditionOfDifferentCurrencies() {
    try {
      d15.plus(e2_51)
      fail("added different currencies")
    } catch {
      case _: ClassCastException => // success
    }
  }

  /**{@link Money#dividedBy(Money)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_DivisionByMoney() {
    assert(Money.dollars(5.00).dividedBy(Money.dollars(2.00)).decimalValue(1, BigDecimal.RoundingMode.UNNECESSARY) ==
      BigDecimal(2.50))
    assert(Money.dollars(5.00).dividedBy(Money.dollars(4.00)).decimalValue(2, BigDecimal.RoundingMode.UNNECESSARY) ==
      BigDecimal(1.25))
    assert(Money.dollars(5.00).dividedBy(Money.dollars(1.00)).decimalValue(0, BigDecimal.RoundingMode.UNNECESSARY) ==
      BigDecimal(5))
    try {
      Money.dollars(5.00).dividedBy(Money.dollars(2.00)).decimalValue(0, BigDecimal.RoundingMode.UNNECESSARY)
      fail("dividedBy(Money) does not allow rounding.")
    } catch {
      case _: ArithmeticException => // success
    }
    try {
      Money.dollars(10.00).dividedBy(Money.dollars(3.00)).decimalValue(5, BigDecimal.RoundingMode.UNNECESSARY)
      fail("dividedBy(Money) does not allow rounding.")
    } catch {
      case _: ArithmeticException => // success
    }
  }

  /**{@link Money#equals(Object)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_CloseNumbersNotEqual() {
    val d2_51a = Money.dollars(2.515)
    val d2_51b = Money.dollars(2.5149)
    assert(d2_51a.equals(d2_51b) == false)
  }

  /**{@link Money#compareTo(Money)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_Compare() {
    var monies = Array(
      Money.dollars(0),
      Money.dollars(10.1),
      Money.dollars(0),
      Money.dollars(9.99),
      Money.dollars(-9.00),
      Money.dollars(-10),
      Money.dollars(10)
    )
    monies = monies.sorted
    //Arrays.sort(monies)

    assert(monies(0) == Money.dollars(-10))
    assert(monies(1) == Money.dollars(-9.00))
    assert(monies(2) == Money.dollars(0))
    assert(monies(3) == Money.dollars(0))
    assert(monies(4) == Money.dollars(9.99))
    assert(monies(5) == Money.dollars(10))
    assert(monies(6) == Money.dollars(10.1))

    monies(3) = Money.euros(3)

    try {
      monies.sorted
      fail()
    } catch {
      case _: IllegalArgumentException => // success
    }

    try {
      y100.compareTo(null)
      fail()
    } catch {
      case _: NullPointerException => // success
    }
  }

  /**{@link Money#isGreaterThan(Money)}, {@link Money#isLessThan(Money)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test14_Compare_2() {
    assert(d15.isGreaterThan(d2_51) == true)
    assert(d2_51.isLessThan(d15) == true)
    assert(d15.isGreaterThan(d15) == false)
    assert(d15.isLessThan(d15) == false)
    try {
      d15.isGreaterThan(e2_51)
      fail
    } catch {
      case _: IllegalArgumentException => // success
    }
  }

  /**{@link Money#equals(Object)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test15_DifferentCurrencyNotEqual() {
    assert(d2_51.equals(e2_51) == false)
  }

  /**{@link Money#equals(Object)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test16_Equals() {
    val d2_51a = Money.dollars(2.51)
    assert(d2_51.equals(d2_51a) == true)
    assert(d2_51.equals(d2_51) == true)
    assert(d2_51.equals(2.51) == false)
  }

  /**{@link Money#equals(Object)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test17_EqualsNull() {
    assert(d2_51.equals(null) == false)
  }

  /**{@link Money#hashCode()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test18_Hash {
    val d2_51a = Money.dollars(2.51)
    assert(d2_51.hashCode == d2_51a.hashCode)
    assert(d2_51.hashCode != d15.hashCode)
  }

  /**{@link Money#negated()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test19_Negation {
    assert(d15.negated == (Money.dollars(-15)))
    assert(e2_51.negated.negated == e2_51)
  }

  /**{@link Money#isPositive()}, {@link Money#isNegative()}, {@link Money#isZero()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test20_PositiveNegative() {
    assert(d15.isPositive == true)
    assert(Money.dollars(-10).isNegative == true)
    assert(d15.isNegative == false)
    assert(Money.dollars(-10).isPositive == false)
    assert(Money.dollars(0).isPositive == false)
    assert(Money.dollars(0).isNegative == false)
    assert(Money.dollars(0).isZero == true)
    assert(d15.isZero == false)
  }

  /**{@link Money#toString(Locale)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test21_Print {
    assert(d15.toString(Some(Locale.US)) == "$ 15.00")
    assert(d15.toString(Some(Locale.UK)) == "USD 15.00")
    assert(d15.toString(Some(Locale.JAPAN)) == "USD 15.00")
    assert(d15.toString(Some(Locale.CANADA)) == "US$ 15.00")

    val backup = Locale.getDefault
    try {
      Locale.setDefault(Locale.CANADA)
      assert(d15.toString(None) == "US$ 15.00")
    } finally {
      Locale.setDefault(backup)
    }
  }

  /**{@link Money}インスタンス生成時の丸めテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test22_Round {
    val dRounded = Money.dollars(1.2350)
    assert(dRounded == Money.dollars(1.24))
  }

  /**{@link Money#minus(Money)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test23_Subtraction {
    assert(d15.minus(d2_51) == Money.dollars(12.49))
    assert(y100.minus(y100minus) == Money.yens(200))
  }

  /**
   * {@link Money#applying(Ratio, int, RoundingMode)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test24_ApplyRatio {
    val oneThird = Ratio(1, 3)
    assert(Money.dollars(100).applying(oneThird, 1, BigDecimal.RoundingMode.UP) == Money.dollars(33.40))
  }

  /**
   * {@link Money#incremented}のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test25_Incremented {
    assert(d2_51.incremented == Money.dollars(2.52))
    assert(y50.incremented == Money(51, JPY))
  }

  /**
   * 最小単位以下の金額（例えば日本で言う「銭」）に関するテスト。現在この仕様は存在しない。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test26_FractionalPennies {
    //        CurrencyPolicy(USD, 0.0025)
    //        Smallest unit.unit Any Money based on this CurrencyPolicy must be some multiple of the
    //        smallest unit. "Scale" is insufficient, because the limit is not always a number of demial places.
    //        Money someFee = Money.dollars(0.0025)
    //        Money wholeMoney = someFee.times(4)
    //        assert(wholeMoney==(Money.dollars(0.01)))
  }

  /**
   * {@link Money#abs}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test27_abs {
    assert(y100minus.abs == (y100))
    assert(y100.abs == (y100))
  }

  //	/**
  //	 * TODO: Formatted printing of Money
  //	 *
  //	 * @throws Exception 例外が発生した場合
  //	 */
  //	@Test
  //	def test27_LocalPrinting {
  //		assert(d15.localString==("$15.00"))
  //		assert(m2_51.localString==("2,51 DM"))
  //	}

  /**
   * {@link Money#plus(Money)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test28_plus {
    assert(d15.plus(d100) == Money.dollars(115))
    assert(y100.plus(y100minus) == Money.yens(0))
    assert(d15.plus(d0) == (d15))
    assert(d15.plus(y0) == (d15))
    assert(d0.plus(y0) == (d0))
    assert(y0.plus(d0) == (y0))
  }

  /**
   * {@link Money#sum(Collection)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test29_sum {
    val backup = Locale.getDefault
    try {
      Locale.setDefault(Locale.JAPAN)

      val monies = ListBuffer.empty[Money]
      assert(Money.sum(monies) == Money.yens(0))
      monies += Money.yens(1)
      monies += Money.yens(2)
      monies += Money.yens(4)
      monies += Money.yens(8)
      monies += Money.yens(16)
      monies += Money.yens(32)
      assert(Money.sum(monies) == Money.yens(63))

      monies += Money.dollars(64)
      try {
        Money.sum(monies)
        fail
      } catch {
        case _: IllegalArgumentException => // success
      }
    } finally {
      Locale.setDefault(backup)
    }
  }

}