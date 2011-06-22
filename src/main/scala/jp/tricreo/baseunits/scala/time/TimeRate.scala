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
package jp.tricreo.baseunits.scala.time

/**単位時間あたりの何らかの量（時間に対する割合）を表すクラス。
 *
 * 例えば、時給・時速など。
 *
 * @author j5ik2o
 * @param quantity 単位時間あたりの量
 * @param unit 単位時間
 */
class TimeRate
(val quantity: BigDecimal,
 val unit: Duration) {

  /**インスタンスを生成する。
   *
   * @param quantity 単位時間あたりの量
   * @param unit 単位時間
   */
  def this(quantity: String, unit: Duration) = this (BigDecimal(quantity), unit)

  /**このオブジェクトの`quantity`フィールド（単位時間あたりの量）を返す。
   *
   *　CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 単位時間あたりの量
   */
  def breachEncapsulationOfQuantity = quantity

  /**このオブジェクトの`unit`フィールド（単位時間）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 単位時間
   */
  def breachEncapsulationOfUnit = unit

  override def equals(obj: Any) = obj match {
    case that: TimeRate => quantity == that.quantity && unit == that.unit
    case _ => false
  }

  override def hashCode = quantity.hashCode + unit.hashCode

  /**指定した時間量にこの時間割合を適用した場合の絶対量を取得する。
   *
   * レート計算における数字の丸めは行わない。
   * 例えば、3時間に対して時給1000円を適用すると、3000円となる。
   *
   * @param duration 時間量
   * @return 絶対量
   * @throws IllegalArgumentException 引数durationの単位を、このオブジェクトの単位時間の単位に変換できない場合
   * @throws ArithmeticException 引数`duration`の時間量が単位時間で割り切れない場合
   */
  def over(duration: Duration): BigDecimal = over(duration, BigDecimal.RoundingMode.UNNECESSARY)


  /**指定した時間量にこの時間割合を適用した場合の絶対量を取得する。
   *
   * @param duration 時間量
   * @param scale スケール
   * @param roundingMode 丸めモード
   * @return 絶対量
   * @throws IllegalArgumentException 引数durationの単位を、このオブジェクトの単位時間の単位に変換できない場合
   * @throws ArithmeticException 引数 `roundingMode` に `RoundingMode.UNNECESSARY` を指定したにもかかわらず、
   * 			引数`duration`の時間量が単位時間で割り切れない場合
   */
  def over(duration: Duration, scale: Int, roundingMode: BigDecimal.RoundingMode.Value): BigDecimal =
    duration.dividedBy(unit).times(quantity).decimalValue(scale, roundingMode)

  /**指定した時間量にこの時間割合を適用した場合の絶対量を取得する。
   *
   * @param duration 時間量
   * @param roundingMode 丸めモード
   * @return 絶対量
   * @throws 引数durationの単位を、このオブジェクトの単位時間の単位に変換できない場合
   * @throws ArithmeticException 引数 `roundingMode` に `RoundingMode.UNNECESSARY` を指定したにもかかわらず、
   * 			引数`duration`の時間量が単位時間で割り切れない場合
   */
  def over(duration: Duration, roundingMode: BigDecimal.RoundingMode.Value): BigDecimal = {
    over(duration, scale, roundingMode)
  }

  /**スケールを取得する。
   *
   * @return スケール
   */
  def scale = quantity.scale

  override def toString = {
    val buffer = new StringBuffer
    buffer.append(quantity)
    buffer.append(" per ")
    buffer.append(unit)
    buffer.toString
  }

}

/**`TimeRate`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object TimeRate {

  /**インスタンスを生成する。
   *
   * @param quantity 単位時間あたりの量
   * @param unit 単位時間
   * @return [[jp.tricreo.baseunits.scala.time.TimeRate]]
   */
  def apply(quantity: BigDecimal, unit: Duration): TimeRate = new TimeRate(quantity, unit)

  /**インスタンスを生成する。
   *
   * @param quantity 単位時間あたりの量
   * @param unit 単位時間
   * @return [[jp.tricreo.baseunits.scala.time.TimeRate]]
   */
  def apply(quantity: String, unit: Duration): TimeRate = new TimeRate(quantity, unit)

  /**抽出子メソッド。
   *
   * @param [[jp.tricreo.baseunits.scala.time.TimeRate]]
   * @return `Option[(BigDecimal, Duration)]`
   */
  def unapply(timeRate: TimeRate) = Some(timeRate.quantity, timeRate.unit)

}