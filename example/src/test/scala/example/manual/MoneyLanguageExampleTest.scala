/*
 * Copyright 2011 Sisioh Project and the Others.
 * lastModified : 2011/04/22
 *
 * This file is part of Tricreo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package example.manual

import java.util.Currency

import org.junit.Test
import org.scalatest.{ Assertions, ShouldMatchers }
import org.sisioh.baseunits.scala.money.{ Money, MoneyTimeRate, Proration }
import org.sisioh.baseunits.scala.time.Duration

class MoneyLanguageExampleTest extends Assertions with ShouldMatchers {

  val CAD = Currency.getInstance("CAD")

  /** 物品税率 */
  val GST_RATE = 0.06

  /**
   * 税額の計算例。
   *
   * 税率は{@code 0.06%}で、丸めモードは四捨五入（{@link RoundingMode#HALF_UP}）とするのが
   * カナダ物品税の仕様（らしい）。そこで、{@code 3.00カナダドル}の税額を計算すると、{@code 0.18カナダドル}
   * となる。
   */
  @Test
  def testMoneyBigDecimalExample {
    val moneyResult = calculateGST(Money.adjustBy(3.00, CAD))
    val bigDecimalAmount = calculateGST(BigDecimal("3.00"))

    moneyResult shouldEqual Money.adjustBy(0.18, CAD)
    bigDecimalAmount shouldEqual BigDecimal("0.18")
  }

  /**
   * 金銭の四則演算例。
   *
   * {@code 10.03 USドル}について、2倍, {@code 1.14 USドル}との差, {@code 2.08 USドル}との和を求める。
   * それぞれの結果の合計を求め、{@code 41.06 USドル}であることを確認している。
   */
  @Test
  def testMoneyExample {
    val baseAmount = Money.dollars(10.03)
    val calc1 = baseAmount.times(2)
    val calc2 = baseAmount.minus(Money.dollars(1.14))
    val calc3 = baseAmount.plus(Money.dollars(2.08))

    calc1 shouldEqual Money.dollars(20.06)
    calc2 shouldEqual Money.dollars(8.89)
    calc3 shouldEqual Money.dollars(12.11)

    val amounts = Array(calc1, calc2, calc3)

    Money.sum(amounts) shouldEqual Money.dollars(41.06)
  }

  /**
   * 単位時間当たりの金額 {@link MoneyTimeRate} の使用例。
   *
   * 「1日あたり{@code 400.00 USドル}」を定義し、3日間では{@code 1200.00 USドル}であることを確認。
   */
  @Test
  def testMoneyRateExample_1 {
    // My consulting rate is $400 per day
    val rate = Money.dollars(400.00).per(Duration.days(1))

    // For three days work I will earn $1200
    rate.over(Duration.days(3)) shouldEqual Money.dollars(1200.00)
  }

  /**
   * 単位時間当たりの金額 {@link MoneyTimeRate} の使用例2。
   *
   * 丸め処理が入る場合の例。「3分あたり{@code 100.00ユーロ}」を定義し、1分（切り捨て）では{@code 33.33ユーロ}
   * となることを確認。
   */
  @Test
  def testMoneyRateExample_2 {
    // Rate calculation with rounding
    val rate = new MoneyTimeRate(Money.euros(100.00), Duration.minutes(3))
    rate.over(Duration.minutes(1), BigDecimal.RoundingMode.DOWN) shouldEqual Money.euros(33.33)
  }

  /**
   * Example.
   */
  @Test
  def testProration {
    // My salary is defined as $80,000 per year. I'm paid weekly.
    // If I work 36 weeks of a year, how much will I earn?

    // Salary rounds to 1538.46 per week.
    val salaryPayments = Proration.dividedEvenlyIntoParts(Money.dollars(80000.00), 52)
    salaryPayments(0) shouldEqual Money.dollars(1538.47)
    salaryPayments(2) shouldEqual Money.dollars(1538.47)
    salaryPayments(4) shouldEqual Money.dollars(1538.47)
    salaryPayments(6) shouldEqual Money.dollars(1538.47)
    salaryPayments(8) shouldEqual Money.dollars(1538.46)
    salaryPayments(10) shouldEqual Money.dollars(1538.46)
    salaryPayments(12) shouldEqual Money.dollars(1538.46)

    // TODO: Following currently fails. May be a problem in proration.
    //		Money totalSalaryEarned = Proration.partOfWhole(Money.dollars(80000.00), 36, 52)
    //		assertThat(totalSalaryEarned, is(Money.dollars(1538.46).times(36)/*55384.56*/)))
  }

  private def calculateGST(amount: BigDecimal) = {
    val rawResult = amount * BigDecimal(GST_RATE.toString)
    rawResult.setScale(2, BigDecimal.RoundingMode.HALF_UP)
  }

  private def calculateGST(amount: Money) =
    amount.times(GST_RATE, BigDecimal.RoundingMode.HALF_UP)

}