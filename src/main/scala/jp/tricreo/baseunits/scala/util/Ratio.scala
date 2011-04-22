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
package jp.tricreo.baseunits.scala.util

import annotation.tailrec


/**[[Ratio]]は、2つ同じ単位を持つの量の商（比率）であり、単位のない値である。
 *
 * <p>このクラスの利点は、比率の計算を遅延評価できることにある。</p>
 *
 * <p>Ratio represents the unitless division of two quantities of the same type.
 * The key to its usefulness is that it defers the calculation of a decimal
 * value for the ratio. An object which has responsibility for the two values in
 * the ratio and understands their quantities can create the ratio, which can
 * then be used by any client in a unitless form, so that the client is not
 * required to understand the units of the quantity. At the same time, this
 * gives control of the precision and rounding rules to the client, when the
 * time comes to compute a decimal value for the ratio. The client typically has
 * the responsibilities that enable an appropriate choice of these parameters.<p>
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

  /**このオブジェクトの[[#denominator]]フィールド（分母をあらわす数）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 分母をあらわず数
   */
  def breachEncapsulationOfDenominator = denominator


  /**このオブジェクトの[[#numerator]]フィールド（分子をあらわす数）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 分子をあらわす数
   */
  def breachEncapsulationOfNumerator = numerator


  /**比率を [[BigDecimal]]型で取得する。
   *
   * @param scale 小数点以下の有効数字
   * @param roundingMode 丸めモード
   * @return この比率の [[BigDecimal]] 型の表現
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def decimalValue(scale: Int, roundingMode: BigDecimal.RoundingMode.Value) = {
    BigDecimal(numerator.bigDecimal.divide(denominator.bigDecimal, scale, roundingMode.id))
  }

  /**このオブジェクトと、与えたオブジェクトの同一性を検証する。
   *
   * <p>与えたオブジェクト {@code anObject} が {@code null}である場合、または[[Ratio]]型や
   * そのサブクラスではない場合、{@code false}を返す。
   * 与えたオブジェクトの、分母と分子が共に一致する場合、{@code true}を返す。</p>
   *
   * <p>{@code 2/3} と {@code 4/6} は、評価結果としては同一であるが、分母同士、分子同士が
   * 異なるため、このメソッドでは {@code true} と判断されず、 {@code false} となる。
   *
   * @param obj 比較対象オブジェクト
   * @return 同一の場合は{@code true}、そうでない場合は{@code false}
   * @see java.lang.Object#equals(java.lang.Object)
   */
  override def equals(obj: Any): Boolean = obj match {
    case that: Ratio => {
      val me = reduce
      val you = that.reduce
      me._1 == you._1 && me._2 == you._2
    }
    case _ => false
  }

  @tailrec
  private def gcd(numerator: BigDecimal, denominator: BigDecimal): BigDecimal =
    if (denominator == 0) numerator
    else gcd(denominator, numerator % denominator)


  private def reduce = {
    val gcd = this.gcd(numerator, denominator)
    (numerator / gcd, denominator / gcd)
  }

  override def hashCode = denominator.hashCode + numerator.hashCode

  /**この比率と {@code multiplier} の積からなる比率。
   *
   * <p>計算結果は、分母は変化せず、分子は分子と {@code multiplyer} の積からなる比率となる。</p>
   *
   * @param multiplier 乗数
   * @return 積
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def times(multiplier: BigDecimal): Ratio =
    Ratio(numerator * multiplier, denominator)


  /**この比率と {@code multiplier} の積からなる比率。
   *
   * <p>計算結果は、分子同士・分母同士の積からなる比率となる。</p>
   *
   * @param multiplier 乗数比率
   * @return 積
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def times(multiplier: Ratio): Ratio =
    Ratio(numerator * multiplier.numerator, denominator * multiplier.denominator)

  /**この比率の文字列表現を取得する。
   *
   * <p>"分子/分母"という表記となる。</p>
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
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def apply(fractional: BigDecimal): Ratio =
    new Ratio(fractional, BigDecimal(1))

  /**インスタンスを生成する。
   *
   * @param numerator 分子
   * @param denominator 分母
   * @return 引数に与えた分子、分母からなる比
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   * @throws ArithmeticException 引数{@code denominator}が0だった場合
   */
  def apply(numerator: BigDecimal, denominator: BigDecimal): Ratio =
    new Ratio(numerator, denominator)

  /**インスタンスを生成する。
   *
   * @param numerator 分子
   * @param denominator 分母
   * @return 引数に与えた分子、分母からなる比率
   * @throws ArithmeticException 引数{@code denominator}が0だった場合
   */
  def apply(numerator: Long, denominator: Long): Ratio =
    new Ratio(BigDecimal(numerator), BigDecimal(denominator))

  def unapply(ratio: Ratio) = Some(ratio.numerator, ratio.denominator)


}