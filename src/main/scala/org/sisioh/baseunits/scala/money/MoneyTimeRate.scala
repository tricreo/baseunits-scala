/*
 * Copyright 2011 Sisioh Project and the Others.
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
package org.sisioh.baseunits.scala.money

import java.util.Currency
import org.sisioh.baseunits.scala.time.{Duration, TimeRate}

/**単位時間あたりの金額（時間量に対する金額の割合）をあらわすクラス。
 *
 * 例えば時給、人月単価など。
 *
 * @author j5ik2o
 * @param rate 単位時間あたりの数量
 * @param currency 通貨単位
 */
class MoneyTimeRate
(private val rate: TimeRate,
 private val currency: Currency) {

  /**インスタンスを生成する。
   *
   * @param money 金額
   * @param duration 時間量
   */
  def this(money: Money, duration: Duration) =
    this (TimeRate(money.breachEncapsulationOfAmount, duration),
      money.breachEncapsulationOfCurrency)

  /**
   * このオブジェクトの`currency`フィールド（通貨単位）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 通貨単位
   */
  def breachEncapsulationOfCurrency = currency

  /**このオブジェクトの`rate`フィールド（単位時間当たりの数量）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 単位時間あたりの数量
   */
  def breachEncapsulationOfRate = rate

  override def equals(obj: Any): Boolean = obj match {
    case that: MoneyTimeRate => currency == that.currency && rate == that.rate
    case _ => false
  }

  override def hashCode = currency.hashCode + rate.hashCode


  /**時間量に対してこの割合を適用した場合の金額を返す。
   *
   * @param duration 時間量
   * @return 金額
   */
  def over(duration: Duration): Money =
    over(duration, BigDecimal.RoundingMode.UNNECESSARY)


  /**時間量に対してこの割合を適用した場合の金額を返す。
   *
   * @param duration 時間量
   * @param scale スケール
   * @param roundMode 丸めモード
   * @return 金額
   */
  def over(duration: Duration, scale: Int, roundMode: BigDecimal.RoundingMode.Value): Money =
    Money(rate.over(duration, scale, roundMode), currency)

  /**時間量に対してこの割合を適用した場合の金額を返す。
   *
   * @param duration 時間量
   * @param roundMode 丸めモード
   * @return 金額
   */
  def over(duration: Duration, roundMode: BigDecimal.RoundingMode.Value): Money =
    over(duration, rate.scale, roundMode)

  override def toString =
    currency.getSymbol + " " + rate.toString

}

/**`MoneyTimeRate`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object MoneyTimeRate {

  /**インスタンスを生成する。
   *
   * @param rate 単位時間あたりの数量
   * @param currency 通貨単位
   * @return [[org.sisioh.baseunits.scala.money.MoneyTimeRate]]
   */
  def apply(rate: TimeRate, currency: Currency): MoneyTimeRate = new MoneyTimeRate(rate, currency)

  /**インスタンスを生成する。
   *
   * @param money 金額
   * @param duration 時間量
   */
  def apply(money: Money, duration: Duration): MoneyTimeRate = new MoneyTimeRate(money, duration)

  /**抽出子メソッド。
   *
   * @param [[org.sisioh.baseunits.scala.money.MoneyTimeRate]]
   * @return `Option[(TimeRate, Currency)]`
   */
  def unapply(moneyTimeRate: MoneyTimeRate) = Some(moneyTimeRate.rate, moneyTimeRate.currency)

}
