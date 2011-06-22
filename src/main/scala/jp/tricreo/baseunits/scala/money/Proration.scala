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

import jp.tricreo.baseunits.scala.util.Ratio


/**比例配分の為のユーティリティ。
 *
 * @author j5ik2o
 */
object Proration {

  /**指定した金額を`n`等分した金額の配列を返す。
   *
   * 但し、割り切れなかった分（余り）は、最小単位金額に分割し、配列の頭から順に上乗せする。
   *
   * 例えば、53円を5人で等分した場合は、`{11, 11, 11, 10, 10}`となる。
   *
   * @param total 合計金額
   * @param n 分割数
   * @return 分割結果
   */
  def dividedEvenlyIntoParts(total: Money, n: Int) = {
    val lowResult = total.dividedBy(BigDecimal(n), BigDecimal.RoundingMode.DOWN)
    val lowResults = Array.fill(n)(lowResult)
    val remainder = total.minus(sum(lowResults))
    distributeRemainderOver(lowResults, remainder)
  }

  /**`total`のうち、`portion / whole`の割合の金額を返す。割り切れない場合は切り捨てる。
   *
   * @param total 合計額
   * @param portion 部分量をあらわす値
   * @param whole 全体量をあらわす値
   * @return 部分の金額
   * @throws ArithmeticException 引数`whole`が0だった場合
   */
  def partOfWhole(total: Money, portion: Long, whole: Long): Money =
    partOfWhole(total, Ratio(portion, whole))

  /**`total`のうち、`ratio`の割合の金額を返す。割り切れない場合は切り捨てる。
   *
   * @param total 合計額
   * @param ratio 割合
   * @return 指定した割合の金額
   */
  def partOfWhole(total: Money, ratio: Ratio): Money = {
    val scale = defaultScaleForIntermediateCalculations(total)
    val multiplier = ratio.decimalValue(scale, BigDecimal.RoundingMode.DOWN)
    total.times(multiplier, BigDecimal.RoundingMode.DOWN)
  }

  /**指定した金額を`proportions`であらわす割合で分割した金額の配列を返す。
   *
   * 但し、割り切れなかった分（余り）は、最小単位金額に分割し、配列の頭から順に上乗せする。
   *
   * 例えば、52円を1:3:1で等分した場合は、`{11, 31, 10}`となる。
   *
   * @param total 合計金額
   * @param proportions 比数の配列
   * @return 分割結果
   */
  def proratedOver(total: Money, proportions: Array[BigDecimal]): Array[Money] = {
    val scale = defaultScaleForIntermediateCalculations(total)
    val simpleResult = ratios(proportions).map{
      e =>
      val multiplier = e.decimalValue(scale, BigDecimal.RoundingMode.DOWN)
      total.times(multiplier, BigDecimal.RoundingMode.DOWN)
    }
    val remainder = total.minus(sum(simpleResult))
    distributeRemainderOver(simpleResult, remainder)
  }

  /**
   * 指定した金額を`proportions`であらわす割合で分割した金額の配列を返す。
   *
   * 但し、割り切れなかった分（余り）は、最小単位金額に分割し、配列の頭から順に上乗せする。
   *
   * 例えば、52円を1:3:1で等分した場合は、`{11, 31, 10}`となる。
   *
   * @param total 合計金額
   * @param longProportions 比数の配列
   * @return 分割結果
   */
  def proratedOver[T <% Number](total: Money, longProportions: Array[T]): Array[Money] = {
    val proportions = longProportions.map(e => BigDecimal(e.longValue))
    proratedOver(total, proportions)
  }

  private[money] def distributeRemainderOver(amounts: Array[Money], remainder: Money) = {
    val increments = remainder.dividedBy(remainder.minimumIncrement)
      .decimalValue(0, BigDecimal.RoundingMode.UNNECESSARY).intValue

    assert(increments <= amounts.length)

    val results = new Array[Money](amounts.length)
    for (i <- 0 until increments) {
      results(i) = amounts(i).incremented
    }
    for (i <- increments until amounts.length) {
      results(i) = amounts(i)
    }
    results
  }

  /**比数の配列を割合の配列に変換する。
   *
   * @param proportions 比の配列
   * @return 割合の配列
   */
  def ratios(proportions: Array[BigDecimal]) = {
    val total = sum(proportions)
    proportions.map(e => Ratio(e, total))
  }

  /**`elements`の要素の和を返す。
   *
   * @param elements 配列
   * @return 和
   */
  def sum(elements: Array[BigDecimal]) =
    elements.sum

  /**`elements`の要素の和を返す。
   *
   * @param elements 配列
   * @return 和
   * @throws IllegalArgumentException 引数`elements`の要素数が0の場合
   */
  def sum(elements: Array[Money]) = {
    require(elements.size > 0)
    val sum = Money.adjustBy(0, elements(0).breachEncapsulationOfCurrency)
    elements.foldLeft(sum)(_.plus(_))
  }

  private def defaultScaleForIntermediateCalculations(total: Money) =
    total.breachEncapsulationOfAmount.precision + 2


}