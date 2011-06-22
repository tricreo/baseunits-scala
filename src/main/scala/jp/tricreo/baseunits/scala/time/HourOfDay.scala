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

/**1日の中の特定の「時」を表すクラス。
 *
 * [[java.util.Date]]と異なり、日付や分以下（時未満）の概念を持っていない。またタイムゾーンの概念もない。
 *
 * @author j5ik2o
 * @param value 時をあらわす正数
 */
class HourOfDay private
(private[time] val value: Int)
  extends Ordered[HourOfDay] with Serializable {

  require(value >= HourOfDay.MIN && value <= HourOfDay.MAX,
    "Illegal value for 24 hour: %d , please use a value between 0 and 23".format(value))

  /**このオブジェクトの`value`フィールド（時をあらわす正数）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 時をあらわす正数（0〜23）
   */
  def breachEncapsulationOfValue = value

  def compare(that: HourOfDay): Int = value - that.value

  override def equals(obj: Any): Boolean = obj match {
    case that: HourOfDay => this.value == that.value
    case _ => false
  }

  override def hashCode: Int = value.hashCode

  /**同日において、このインスタンスが表す時が、引数`another`で表される時よりも未来かどうか調べる。
   *
   * @param another 基準時
   * @return 同日において、このインスタンスが表す時が、引数`another`で表される時よりも未来である場合は`true`、そうでない場合は`false`
   */
  def isAfter(another: HourOfDay) = value > another.value;

  /**
   * 同日において、このインスタンスが表す時が、引数`another`で表される時よりも過去かどうか調べる。
   *
   * @param another 基準時
   * @return 同日において、このインスタンスが表す時が、引数`another`で表される時よりも過去である場合は`true`、そうでない場合は`false`
   */
  def isBefore(another: HourOfDay) = value < another.value;

  override def toString = "%02d".format(value)
}

/**コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object HourOfDay {

  val MIN = 0

  val MAX = 23

  /**インスタンスを生成する。
   *
   * @param initial 時をあらわす正数
   * @return 時（0〜23）
   * @throws IllegalArgumentException 引数の値が0〜23の範囲ではない場合
   */
  def apply(initial: Int) = new HourOfDay(initial)

  /**
   * インスタンスを生成する。
   *
   * @param initial 時をあらわす正数
   * @param amPm 午前午後を表す文字列
   * @return 時（0〜11）
   * @throws IllegalArgumentException 引数`initial`の値が0〜11の範囲ではない場合もしくは、引数`amPm`の値が `"AM"` または `"PM"` ではない場合
   */
  def apply(initial: Int, amPm: String) =
    new HourOfDay(convertTo24hour(initial, amPm))

  /**抽出子メソッド。
   *
   * @param hourOfDay [[jp.tricreo.baseunits.scala.time.HourOfDay]]
   * @return `Option[Int]`
   */
  def unapply(hourOfDay: HourOfDay) = Some(hourOfDay.value)


  /**午前午後記号付き12時間制の時を24時間制の値に変換する。
   *
   * @param hour 時（0〜11）
   * @param amPm 午前午後を表す文字列
   * @return 24時間制における時
   * @throws IllegalArgumentException 引数`initial`の値が0〜11の範囲ではない場合もしくは、
   * 引数`amPm`の値が `"AM"` または `"PM"` ではない場合
   */
  def convertTo24hour(hour: Int, amPm: String): Int = {
    require("AM".equalsIgnoreCase(amPm) || "PM".equalsIgnoreCase(amPm),
      "AM PM indicator invalid: %s, please use AM or PM".format(amPm))
    require(hour >= MIN && hour <= 12,
      "Illegal value for 12 hour: %d, please use a value between 0 and 11".format(hour))

    var translatedAmPm = if ("AM".equalsIgnoreCase(amPm)) 0 else 12
    val delta = if (hour == 12) 12 else 0
    translatedAmPm -= delta
    return hour + translatedAmPm;
  }

}