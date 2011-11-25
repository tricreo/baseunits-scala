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
import org.junit.Test
import jp.tricreo.baseunits.scala.time.{TimeRate, Duration}
import java.util.Currency

/**`MoneyTimeRate`のテストクラス。
 */
class MoneyTimeRateTest extends AssertionsForJUnit {
  /**{@link MoneyTimeRate#equals(Object)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Equals {
    val amount = Money.euros(11.00)
    val rate = amount.per(Duration.days(2))
    assert(rate.equals(rate) == true)
    assert(rate.equals(new MoneyTimeRate(Money.euros(11.00), Duration.days(2))) == true)
    assert(rate.equals(new MoneyTimeRate(Money.euros(11.01), Duration.days(2))) == false)
    assert(rate.equals(new MoneyTimeRate(Money.euros(11.00), Duration.days(1))) == false)
    assert(rate.equals(new MoneyTimeRate(Money.yens(11.00), Duration.days(2))) == false)
    assert(rate.equals(null) == false)
    //		assert(rate.equals(new MoneyTimeRate(Money.euros(11.00), Duration.days(2)) {
    //		})==false)

    assert(rate.hashCode == new MoneyTimeRate(Money.euros(11.00), Duration.days(2)).hashCode)
    assert(rate.hashCode != new MoneyTimeRate(Money.euros(11.01), Duration.days(2)).hashCode)
    assert(rate.hashCode != new MoneyTimeRate(Money.euros(11.00), Duration.days(1)).hashCode)
    assert(rate.hashCode != new MoneyTimeRate(Money.yens(11.00), Duration.days(2)).hashCode)
  }

  /**{@link MoneyTimeRate}のインスタンス生成テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_SimpleRate {
    val rate = new MoneyTimeRate(Money.dollars(20.00), Duration.hours(1))
    assert(rate.over(Duration.hours(2)) == (Money.dollars(40.00)))

    assert(rate.breachEncapsulationOfRate == (new TimeRate(BigDecimal("20.00"), Duration.hours(1))))
    assert(rate.breachEncapsulationOfCurrency == Currency.getInstance("USD"))
  }

  /**{@link MoneyTimeRate#over(Duration)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_Rounding {
    val rate = new MoneyTimeRate(Money.dollars(100.00), Duration.minutes(3))
    try {
      rate.over(Duration.minutes(1))
      fail("ArtithmeticException should have been thrown. This case requires rounding.")
    } catch {
      case _:ArithmeticException => // success
      case _ => fail
    }
  }

  /**{@link MoneyTimeRate#over(Duration, RoundingMode)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_RoundingRate {
    val rate = MoneyTimeRate(Money.euros(100.00), Duration.minutes(3))
    assert(rate.over(Duration.minutes(1), BigDecimal.RoundingMode.DOWN) == Money.euros(BigDecimal("33.33")))
  }

  /**{@link MoneyTimeRate#over(Duration, int, RoundingMode)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_RoundingScalingRate {
    val rate = MoneyTimeRate(Money.euros(BigDecimal("100.00")), Duration.minutes(3))
    assert(rate.over(Duration.minutes(1), 2, BigDecimal.RoundingMode.DOWN) == Money.euros(BigDecimal("33.33")))
  }

  /**{@link MoneyTimeRate#toString()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_toString {
    val amount = Money.euros(11.00)
    val rate = amount.per(Duration.days(2))
    assert(rate.toString == "EUR 11.00 per 2 days")
  }
}