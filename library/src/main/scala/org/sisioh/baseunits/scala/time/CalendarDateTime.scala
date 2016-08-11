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

import java.text.ParseException
import java.time.{ ZoneId, ZonedDateTime }
import java.util.TimeZone

/**
 * カレンダー上の特定の「年月日時分」を表すクラス。
 *
 * [[java.util.Date]]と異なり、分未満（秒以下）の概念を持っていない。また、
 * [[TimePoint]]と異なり、
 * その分1分間全ての範囲を表すクラスであり、特定の瞬間をモデリングしたものではない。
 *
 * @author j5ik2o
 * @param date 年月日
 * @param time 時分
 */
class CalendarDateTime private[time] (
  val date: CalendarDate,
  val time: TimeOfDay
)
    extends Ordered[CalendarDateTime] with Serializable {

  /**
   * 指定したタイムゾーンにおける、このインスタンスが表す「年月日時分」の0秒0ミリ秒の瞬間について
   * [[TimePoint]] 型のインスタンスを返す。
   *
   * @param timeZone タイムゾーン
   * @return [[TimePoint]]
   */
  @deprecated("Use asTimePoint(zoneId: ZoneId) method instead", "0.1.18")
  def asTimePoint(timeZone: TimeZone): TimePoint =
    TimePoint.from(date, time, timeZone.toZoneId)

  /**
   * 指定したゾーンIDにおける、このインスタンスが表す「年月日時分」の0秒0ミリ秒の瞬間について
   * [[TimePoint]] 型のインスタンスを返す。
   *
   * @param zoneId [[ZoneId]]
   * @return [[TimePoint]]
   */
  def asTimePoint(zoneId: ZoneId = ZoneIds.Default): TimePoint =
    TimePoint.from(date, time, zoneId)

  /**
   * デフォルトのゾーンIDにおける、このインスタンスが表す「年月日時分」の0秒0ミリ秒の瞬間について
   * [[TimePoint]] 型のインスタンスを返す。
   *
   * @return [[TimePoint]]
   */
  def asZonedDateTime(zoneId: ZoneId = ZoneIds.Default): ZonedDateTime =
    ZonedDateTime.of(date.asLocalDate, time.asLocalTime, zoneId)

  /**
   * このオブジェクトの`date`フィールド（年月日）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 年月日
   */
  @deprecated("Use date property instead", "0.1.18")
  val breachEncapsulationOfDate: CalendarDate = date

  /**
   * このオブジェクトの`time`フィールド（時分）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 時分
   */
  @deprecated("Use time property instead", "0.1.18")
  val breachEncapsulationOfTime: TimeOfDay = time

  override def compare(other: CalendarDateTime): Int = {
    val dateComparance = date.compareTo(other.date)
    if (dateComparance != 0) {
      dateComparance
    } else {
      time.compareTo(other.time)
    }
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: CalendarDateTime => date == that.date && time == that.time
    case that: CalendarDate     => date == that
    case _                      => false
  }

  override def hashCode: Int = 31 * (date.hashCode + time.hashCode)

  /**
   * 指定した年月日時分 `other` が、このオブジェクトが表現する年月日時分よりも過去であるかどうかを検証する。
   *
   * お互いが同一日時である場合は `false` を返す。
   *
   * @param other 対象年月日時分
   * @return 過去である場合は`true`、そうでない場合は`false`
   */
  def isAfter(other: CalendarDateTime): Boolean =
    !isBefore(other) && !equals(other)

  /**
   * 指定した年月日時分 `other` が、このオブジェクトが表現する年月日時分よりも未来であるかどうかを検証する。
   *
   * お互いが同一日時である場合は `false` を返す。
   *
   * @param other 対象年月日時分
   * @return 未来である場合は`true`、そうでない場合は`false`
   */
  def isBefore(other: CalendarDateTime): Boolean = {
    if (date.isBefore(other.date)) {
      true
    } else if (date.isAfter(other.date)) {
      false
    } else {
      time.isBefore(other.time)
    }
  }

  override def toString: String =
    date.toString + " at " + time.toString

  /**
   * この年月日時分を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern  [[java.text.SimpleDateFormat]]に基づくパターン
   * @param timeZone タイムゾーン
   * @return 整形済み時間文字列
   */
  @deprecated("Use toString(pattern: String, zoneId: ZoneId) method instead", "0.1.18")
  def toString(pattern: String, timeZone: TimeZone): String = toString(pattern, timeZone.toZoneId)

  /**
   * この年月日時分を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern [[java.text.SimpleDateFormat]]に基づくパターン
   * @param zoneId  ゾーンID
   * @return 整形済み時間文字列
   */
  def toString(pattern: String, zoneId: ZoneId = ZoneIds.Default): String = {
    val point = asTimePoint(zoneId)
    point.toString(pattern, zoneId)
  }

}

/**
 * コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object CalendarDateTime {

  /**
   * インスタンスを生成する。
   *
   * @param aDate [[org.sisioh.baseunits.scala.time.CalendarDate]]
   * @param aTime [[org.sisioh.baseunits.scala.time.TimeOfDay]]
   * @return [[CalendarDateTime]]
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   *                                  引数`day`が1〜31の範囲ではない場合もしくは、引数`hour`が0〜23の範囲ではない場合もしくは、
   *                                  引数`minute`が0〜59の範囲ではない場合もしくは、引数`day`が`yearMonth`の月に存在しない場合
   */
  def apply(aDate: CalendarDate, aTime: TimeOfDay): CalendarDateTime = from(aDate, aTime)

  /**
   * 抽出子メソッド。
   *
   * @param calendarDateTime [[CalendarDateTime]]
   * @return `Option[(CalendarDate,TimeOfDay)]`
   */
  def unapply(calendarDateTime: CalendarDateTime): Option[(CalendarDate, TimeOfDay)] =
    Some(calendarDateTime.date, calendarDateTime.time)

  /**
   * 指定した年月日時分を表す、[[CalendarDateTime]]のインスタンスを生成する。
   *
   * @param aDate 年月日
   * @param aTime 時分
   * @return [[CalendarDateTime]]
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   *                                  引数`day`が1〜31の範囲ではない場合もしくは、引数`hour`が0〜23の範囲ではない場合もしくは、
   *                                  引数`minute`が0〜59の範囲ではない場合もしくは、引数`day`が`yearMonth`の月に存在しない場合
   */
  def from(aDate: CalendarDate, aTime: TimeOfDay): CalendarDateTime = new CalendarDateTime(aDate, aTime)

  /**
   * 指定した年月日時分を表す、[[CalendarDateTime]]のインスタンスを生成する。
   *
   * @param zonedDateTime [[ZonedDateTime]]
   * @return [[CalendarDateTime]]
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   *                                  引数`day`が1〜31の範囲ではない場合もしくは、引数`hour`が0〜23の範囲ではない場合もしくは、
   *                                  引数`minute`が0〜59の範囲ではない場合もしくは、引数`day`が`yearMonth`の月に存在しない場合
   */
  def from(zonedDateTime: ZonedDateTime): CalendarDateTime = {
    val calendarDate = CalendarDate.from(zonedDateTime.toLocalDate)
    val timeOfDay = TimeOfDay.from(zonedDateTime.toLocalTime)
    from(calendarDate, timeOfDay)
  }

  /**
   * 指定した年月日を時分表す、[[CalendarDateTime]]のインスタンスを生成する。
   *
   * @param year   西暦年をあらわす数
   * @param month  月をあらわす正数（1〜12）
   * @param day    日をあらわす正数（1〜31）
   * @param hour   時をあらわす正数（0〜23）
   * @param minute 分をあらわす正数（0〜59）
   * @return [[CalendarDateTime]]
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   *                                  引数`day`が1〜31の範囲ではない場合もしくは、引数`hour`が0〜23の範囲ではない場合もしくは、
   *                                  引数`minute`が0〜59の範囲ではない場合もしくは、引数`day`が`yearMonth`の月に存在しない場合
   */
  def from(year: Int, month: Int, day: Int, hour: Int, minute: Int): CalendarDateTime =
    from(year, month, day, hour, minute, ZoneIds.Default)

  /**
   * 指定した年月日を時分表す、[[CalendarDateTime]]のインスタンスを生成する。
   *
   * @param year     西暦年をあらわす数
   * @param month    月をあらわす正数（1〜12）
   * @param day      日をあらわす正数（1〜31）
   * @param hour     時をあらわす正数（0〜23）
   * @param minute   分をあらわす正数（0〜59）
   * @param timeZone [[TimeZone]]
   * @return [[CalendarDateTime]]
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   *                                  引数`day`が1〜31の範囲ではない場合もしくは、引数`hour`が0〜23の範囲ではない場合もしくは、
   *                                  引数`minute`が0〜59の範囲ではない場合もしくは、引数`day`が`yearMonth`の月に存在しない場合
   */
  @deprecated("Use from(year: Int, month: Int, day: Int, hour: Int, minute: Int, zoneId: ZoneId) method instead", "0.1.18")
  def from(year: Int, month: Int, day: Int, hour: Int, minute: Int, timeZone: TimeZone): CalendarDateTime =
    new CalendarDateTime(CalendarDate.from(year, month, day, timeZone.toZoneId), TimeOfDay.from(hour, minute))

  /**
   * 指定した年月日を時分表す、[[CalendarDateTime]]のインスタンスを生成する。
   *
   * @param year   西暦年をあらわす数
   * @param month  月をあらわす正数（1〜12）
   * @param day    日をあらわす正数（1〜31）
   * @param hour   時をあらわす正数（0〜23）
   * @param minute 分をあらわす正数（0〜59）
   * @param zoneId [[ZoneId]]
   * @return [[CalendarDateTime]]
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   *                                  引数`day`が1〜31の範囲ではない場合もしくは、引数`hour`が0〜23の範囲ではない場合もしくは、
   *                                  引数`minute`が0〜59の範囲ではない場合もしくは、引数`day`が`yearMonth`の月に存在しない場合
   */
  def from(year: Int, month: Int, day: Int, hour: Int, minute: Int, zoneId: ZoneId): CalendarDateTime =
    new CalendarDateTime(CalendarDate.from(year, month, day, zoneId), TimeOfDay.from(hour, minute))

  /**
   * 指定した年月日時分を表す、[[CalendarDate]]のインスタンスを生成する。
   *
   * @param dateTimeString 年月日時分を表す文字列
   * @param pattern        解析パターン文字列
   * @param timeZone       [[TimeZone]]
   * @return [[CalendarDateTime]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  @deprecated("Use parse(dateTimeString: String, pattern: String, zoneId: ZoneId) method instead", "0.1.18")
  def parse(dateTimeString: String, pattern: String, timeZone: TimeZone): CalendarDateTime = parse(dateTimeString, pattern, timeZone.toZoneId)

  /**
   * 指定した年月日時分を表す、[[CalendarDate]]のインスタンスを生成する。
   *
   * @param dateTimeString 年月日時分を表す文字列
   * @param pattern        解析パターン文字列
   * @param zoneId         [[ZoneId]]
   * @return [[CalendarDateTime]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  def parse(dateTimeString: String, pattern: String, zoneId: ZoneId = ZoneIds.Default): CalendarDateTime = {
    //Any timezone works, as long as the same one is used throughout.
    val point = TimePoint.parse(dateTimeString, pattern, zoneId)
    CalendarDateTime.from(point.asCalendarDate(zoneId), point.asTimeOfDay(zoneId))
  }

}