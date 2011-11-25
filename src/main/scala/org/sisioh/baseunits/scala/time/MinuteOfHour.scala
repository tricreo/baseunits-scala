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

/**1時間の中の特定の「分」を表すクラス。
 *
 * [[java.util.Date]]と異なり、日付や時、秒以下（分未満）の概念を持っていない。またタイムゾーンの概念もない。
 *
 * @author j5ik2o
 * @param value 分をあらわす正数
 */
class MinuteOfHour private[time]
(private[time] val value: Int)
  extends Ordered[MinuteOfHour] with Serializable {

  require(MinuteOfHour.MIN <= value && value <= MinuteOfHour.MAX,
    "Illegal value for 24 hour: " + value + ", please use a value between 0 and 23")

  /**このオブジェクトの`value`フィールド（時をあらわす正数）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 時をあらわす正数（0〜23）
   */
  def breachEncapsulationOfValue = value

  def compare(other: MinuteOfHour): Int = value - other.value

  override def equals(obj: Any): Boolean = obj match {
    case that: MinuteOfHour => value == that.value
    case _ => false
  }

  override def hashCode = value.hashCode

  /**同時(hour)において、このインスタンスが表す分が、引数`another`で表される時よりも未来かどうか調べる。
   *
   * 等価である場合は`false`を返す。
   *
   * @param another 基準分
   * @return 同日において、このインスタンスが表す分が、引数`another`で表される時よりも未来である場合は`true`、そうでない場合は`false`
   */
  def isAfter(another: MinuteOfHour) = value > another.value;

  /**同時(hour)において、このインスタンスが表す分が、引数`another`で表される時よりも過去かどうか調べる。
   *
   * 等価である場合は`false`を返す。
   *
   * @param another 基準分
   * @return 同日において、このインスタンスが表す分が、引数`another`で表される時よりも過去である場合は`true`、そうでない場合は`false`
   */
  def isBefore(another: MinuteOfHour) = value < another.value

  override def toString = "%02d".format(value)

}

/**`MinuteOfHour`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object MinuteOfHour {

  val MIN = 0

  val MAX = 59

  /**インスタンスを生成する。
   *
   * @param value 分をあらわす正数
   * @return [[jp.tricreo.baseunits.scala.time.MinuteOfHour]]
   */
  def apply(value: Int) = new MinuteOfHour(value)

  /**抽出しメソッド。
   *
   * @param [[jp.tricreo.baseunits.scala.time.MinuteOfHour]]
   * @return `Option[Int]`
   */
  def unapply(minuteOfHour:MinuteOfHour) = Some(minuteOfHour.value)

}