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
package org.sisioh.baseunits.scala.time

import java.time.{ LocalTime, ZoneId }
import java.util.TimeZone

/**
 * 1日の中の特定の「時分」を表すクラス。
 *
 * [[java.util.Date]]と異なり、日付の概念を持っていない。またタイムゾーンの概念もない。
 *
 * @param hour   時
 * @param minute 分
 */
class TimeOfDay private[time] (
  val hour:   HourOfDay,
  val minute: MinuteOfHour
)
    extends Ordered[TimeOfDay] with Serializable {

  lazy val asLocalTime: LocalTime = LocalTime.of(hour.value, minute.value)

  /**
   * 指定した年月日とタイムゾーンにおける、このインスタンスがあらわす時分の0秒0ミリ秒の瞬間について
   * [[TimePoint]] 型のインスタンスを返す。
   *
   * @param date     年月日
   * @param timeZone タイムゾーン
   * @return 瞬間
   */
  @deprecated("Use asTimePointGiven(date: CalendarDate, zoneId: ZoneId) method instead", "0.1.18")
  def asTimePointGiven(date: CalendarDate, timeZone: TimeZone): TimePoint = asTimePointGiven(date, timeZone.toZoneId)

  def asTimePointGiven(date: CalendarDate, zoneId: ZoneId = ZoneIds.Default): TimePoint = {
    val timeOfDayOnDate = on(date)
    timeOfDayOnDate.asTimePoint(zoneId)
  }

  /**
   * このオブジェクトの`hour`フィールド（時）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 時
   */
  @deprecated("Use hour property instead", "0.1.18")
  val breachEncapsulationOfHour = hour

  /**
   * このオブジェクトの`minute`フィールド（分）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 分
   */
  @deprecated("Use minute property instead", "0.1.18")
  val breachEncapsulationOfMinute = minute

  override def compare(other: TimeOfDay): Int = {
    val hourComparance = hour compare other.hour
    if (hourComparance != 0) hourComparance
    else minute compare other.minute
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: TimeOfDay => hour == that.hour && minute == that.minute
    case _               => false
  }

  override def hashCode: Int = 31 * (hour.hashCode + minute.hashCode)

  /**
   * このインスタンスがあらわす時分が、指定した時分よりも未来であるかどうか調べる。
   *
   * 等価の場合は`false`を返す。
   *
   * @param another 基準時分
   * @return 未来である場合は`true`、そうでない場合は`false`
   */
  def isAfter(another: TimeOfDay): Boolean = {
    hour.isAfter(another.hour) || (hour == another.hour && minute.isAfter(another.minute))
  }

  /**
   * このインスタンスがあらわす時分が、指定した時分よりも過去であるかどうか調べる。
   *
   * 等価の場合は`false`を返す。
   *
   * @param another 基準時分
   * @return 過去である場合は`true`、そうでない場合は`false`
   */
  def isBefore(another: TimeOfDay): Boolean = {
    hour.isBefore(another.hour) || (hour == another.hour && minute.isBefore(another.minute))
  }

  /**
   * 指定した年月日における、このインスタンスがあらわす時分について
   * [[org.sisioh.baseunits.scala.time.CalendarDateTime]] 型のインスタンスを返す。
   *
   * @param date 年月日
   * @return [[org.sisioh.baseunits.scala.time.CalendarDateTime]]
   */
  def on(date: CalendarDate): CalendarDateTime =
    CalendarDateTime.from(date, this)

  override def toString: String =
    hour.toString + ":" + minute.toString

}

/**
 * `TimeOfDay`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object TimeOfDay {

  /**
   * インスタンスを生成する。
   *
   * @param hour   時
   * @param minute 分
   * @throws IllegalArgumentException 引数`hour`が0〜23の範囲ではない場合もしくは、引数`minute`が0〜59の範囲ではない場合
   */
  def apply(hour: HourOfDay, minute: MinuteOfHour): TimeOfDay = new TimeOfDay(hour, minute)

  /**
   * 抽出子メソッド。
   *
   * @param timeOfDay [[TimeOfDay]]
   */
  def unapply(timeOfDay: TimeOfDay): Option[(HourOfDay, MinuteOfHour)] = Some(timeOfDay.hour, timeOfDay.minute)

  /**
   * 指定した時分を表す、[[TimeOfDay]]のインスタンスを生成する。
   *
   * @param hour   時
   * @param minute 分
   * @return [[TimeOfDay]]
   * @throws IllegalArgumentException 引数`hour`が0〜23の範囲ではない場合もしくは、引数`minute`が0〜59の範囲ではない場合
   */
  def from(hour: HourOfDay, minute: MinuteOfHour): TimeOfDay = apply(hour, minute)

  /**
   * 指定した時分を表す、[[TimeOfDay]]のインスタンスを生成する。
   *
   * @param hour   時をあらわす正数（0〜23）
   * @param minute 分をあらわす正数（0〜59）
   * @return [[TimeOfDay]]
   * @throws IllegalArgumentException 引数`hour`が0〜23の範囲ではない場合もしくは、引数`minute`が0〜59の範囲ではない場合
   */
  def from(hour: Int, minute: Int): TimeOfDay = new TimeOfDay(HourOfDay(hour), MinuteOfHour(minute))

  /**
   * 指定した時分を表す、[[TimeOfDay]]のインスタンスを生成する。
   *
   * @param localTime [[LocalTime]]
   * @return [[TimeOfDay]]
   * @throws IllegalArgumentException 引数`hour`が0〜23の範囲ではない場合もしくは、引数`minute`が0〜59の範囲ではない場合
   */
  def from(localTime: LocalTime): TimeOfDay =
    from(localTime.getHour, localTime.getMinute)

}
