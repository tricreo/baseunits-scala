/*
 * Copyright 2011 Tricreo Inc and the Others.
 * lastModified : 2011/04/21
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
 * <p>[[java.util.Date]]と異なり、日付や時、秒以下（分未満）の概念を持っていない。またタイムゾーンの概念もない。</p>
 * @param value 分をあらわす正数
 */
@serializable
class MinuteOfHour private[time]
(private[time] val value: Int)
  extends Ordered[MinuteOfHour] {

  require(MinuteOfHour.MIN <= value && value <= MinuteOfHour.MAX,
    "Illegal value for 24 hour: " + value + ", please use a value between 0 and 23")

  /**このオブジェクトの[[#value]]フィールド（時をあらわす正数）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
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

  /**同時(hour)において、このインスタンスが表す分が、引数{@code another}で表される時よりも未来かどうか調べる。
   *
   * <p>等価である場合は{@code false}を返す。</p>
   *
   * @param another 基準分
   * @return 同日において、このインスタンスが表す分が、引数{@code another}で表される時よりも未来である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isAfter(another: MinuteOfHour) = value > another.value;

  /**同時(hour)において、このインスタンスが表す分が、引数{@code another}で表される時よりも過去かどうか調べる。
   *
   * <p>等価である場合は{@code false}を返す。</p>
   *
   * @param another 基準分
   * @return 同日において、このインスタンスが表す分が、引数{@code another}で表される時よりも過去である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isBefore(another: MinuteOfHour) = value < another.value

  override def toString = "%02d".format(value)

}

object MinuteOfHour {

  val MIN = 0

  val MAX = 59

  def apply(value: Int) = new MinuteOfHour(value)

  def unapply(minuteOfHour:MinuteOfHour) = Some(minuteOfHour.value)

}