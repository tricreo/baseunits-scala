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
package org.sisioh.baseunits.scala.util

import annotation.tailrec


/**[[org.sisioh.baseunits.scala.util.Ratio]]は、2つ同じ単位を持つの量の商（比率）であり、単位のない値である。
 *
 * このクラスの利点は、比率の計算を遅延評価できることにある。
 *
 * Ratio represents the unitless division of two quantities of the same type.
 * The key to its usefulness is that it defers the calculation of a decimal
 * value for the ratio. An object which has responsibility for the two values in
 * the ratio and understands their quantities can create the ratio, which can
 * then be used by any client in a unitless form, so that the client is not
 * required to understand the units of the quantity. At the same time, this
 * gives control of the precision and rounding rules to the client, when the
 * time comes to compute a decimal value for the ratio. The client typically has
 * the responsibilities that enable an appropriate choice of these parameters.
 *
 * @param numerator 分子をあらわす数
 * @param denominator 分母をあらわす数
 */
class Ratio
(private val numerator: BigDecimal,
 private val denominator: BigDecimal) {

  if (denominator == BigDecimal(0)) {
    throw new ArithmeticException("denominator is zero")
  }

  /**このオブジェクトの`denominator`フィールド（分母をあらわす数）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 分母をあらわず数
   */
  def breachEncapsulationOfDenominator = denominator


  /**このオブジェクトの`numerator`フィールド（分子をあらわす数）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 分子をあらわす数
   */
  def breachEncapsulationOfNumerator = numerator


  /**比率を[[scala.math.BigDecimal]]型で取得する。
   *
   * @param scale 小数点以下の有効数字
   * @param roundingMode 丸めモード
   * @return この比率の [[scala.math.BigDecimal]] 型の表現
   */
  def decimalValue(scale: Int, roundingMode: BigDecimal.RoundingMode.Value) = {
    BigDecimal(numerator.bigDecimal.divide(denominator.bigDecimal, scale, roundingMode.id))
  }

  /**このオブジェクトと、与えたオブジェクトの同一性を検証する。
   *
   * 与えたオブジェクト[[org.sisioh.baseunits.scala.util.Ratio]]型や
   * そのサブクラスではない場合、`false`を返す。
   * 与えたオブジェクトの、分母と分子が共に一致する場合、`true`を返す。
   *
   * `2/3` と `4/6` は、評価結果としては同一であるが、分母同士、分子同士が
   * 異なるため、このメソッドでは `true` と判断されず、 `false` となる。
   *
   * @param obj 比較対象オブジェクト
   * @return 同一の場合は`true`、そうでない場合は`false`
   * @see java.lang.Object#equals(java.lang.Object)
   */
  override def equals(obj: Any): Boolean = obj match {
    case that: Ratio => this.denominator == that.denominator && this.numerator == that.numerator
    case _ => false
  }

  @tailrec
  private def gcd(numerator: BigDecimal, denominator: BigDecimal): BigDecimal =
    if (denominator == 0) numerator
    else gcd(denominator, numerator % denominator)

  /**通分した[[org.sisioh.baseunits.scala.util.Ratio]]を返す。
   *
   * @return 通分した[[org.sisioh.baseunits.scala.util.Ratio]]
   */
  def reduce = {
    val gcd = this.gcd(numerator, denominator)
    new Ratio(numerator / gcd, denominator / gcd)
  }

  override def hashCode = denominator.## + numerator.##

  /**この比率と `multiplier` の積からなる比率。
   *
   * 計算結果は、分母は変化せず、分子は分子と `multiplyer` の積からなる比率となる。
   *
   * @param multiplier 乗数
   * @return 積
   */
  def times(multiplier: BigDecimal): Ratio =
    Ratio(numerator * multiplier, denominator)


  /**この比率と `multiplier` の積からなる比率。
   *
   * 計算結果は、分子同士・分母同士の積からなる比率となる。
   *
   * @param multiplier 乗数比率
   * @return 積
   */
  def times(multiplier: Ratio): Ratio =
    Ratio(numerator * multiplier.numerator, denominator * multiplier.denominator)

  /**この比率の文字列表現を取得する。
   *
   * "分子/分母"という表記となる。
   *
   * @see java.lang.Object#toString()
   */
  override def toString = numerator.toString + "/" + denominator

}

object Ratio {

  /**インスタンスを生成する。
   *
   * @param fractional 分数
   * @return 与えた分数であらわされる比率
   */
  def apply(fractional: BigDecimal): Ratio =
    new Ratio(fractional, BigDecimal(1))

  /**インスタンスを生成する。
   *
   * @param numerator 分子
   * @param denominator 分母
   * @return 引数に与えた分子、分母からなる比
   * @throws ArithmeticException 引数`denominator`が0だった場合
   */
  def apply(numerator: BigDecimal, denominator: BigDecimal): Ratio =
    new Ratio(numerator, denominator)

  /**インスタンスを生成する。
   *
   * @param numerator 分子
   * @param denominator 分母
   * @return 引数に与えた分子、分母からなる比率
   * @throws ArithmeticException 引数`denominator`が0だった場合
   */
  def apply(numerator: Long, denominator: Long): Ratio =
    new Ratio(BigDecimal(numerator), BigDecimal(denominator))

  def unapply(ratio: Ratio) = Some(ratio.numerator, ratio.denominator)

}