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

import java.text.{ParseException, SimpleDateFormat}
import java.time._
import java.util.{Calendar, TimeZone, Date => JDate}

import org.sisioh.baseunits.scala.intervals.{Limit, LimitValue}

/**
  * ミリ秒精度で、ある時間の一点をあらわすクラス。
  *
  * タイムゾーンを持っている。
  *
  * @author j5ik2o
  * @param millisecondsFromEpoc エポックからの経過ミリ秒
  */
class TimePoint private[time] (val millisecondsFromEpoc: Long)
    extends Ordered[TimePoint]
    with Serializable {

  lazy val asInstant: Instant = Instant.ofEpochMilli(millisecondsFromEpoc)

  /**
    * このオブジェクトが表現する瞬間を指定したタイムゾーンとして扱い、[[java.util.Calendar]]型として取得する。
    *
    * @param timeZone タイムゾーン
    * @return [[java.util.Calendar]]
    */
  @deprecated("Use asJavaZonedDateTime method instead", "0.1.18")
  def asJavaCalendar(timeZone: TimeZone = TimeZones.Default): Calendar = {
    val result = Calendar.getInstance(timeZone)
    result.setTime(new JDate(millisecondsFromEpoc))
    result
  }

  def asJavaZonedDateTime(zoneId: ZoneId = ZoneIds.Default): ZonedDateTime =
    ZonedDateTime.ofInstant(asInstant, zoneId)

  def asJavaLocalDateTime(zoneId: ZoneId = ZoneIds.Default): LocalDateTime =
    asJavaZonedDateTime(zoneId).toLocalDateTime

  /**
    * このオブジェクトが表現する瞬間を、[[java.util.Date]]型として取得する。
    *
    * @return [[java.util.Date]]
    */
  @deprecated("Not Use this method", "0.1.18")
  lazy val asJavaUtilDate = new JDate(millisecondsFromEpoc)

  /**
    * この瞬間を「時分」として返す。
    *
    * @param timeZone タイムゾーン
    * @return 時分
    */
  @deprecated("Use def asTimeOfDay(zoneId: ZoneId) method instead", "0.1.18")
  def asTimeOfDay(timeZone: TimeZone): TimeOfDay = {
    asTimeOfDay(timeZone.toZoneId)
  }

  def asTimeOfDay(zoneId: ZoneId = ZoneIds.Default): TimeOfDay = {
    val localDateTime = LocalDateTime.ofInstant(asInstant, zoneId)
    TimeOfDay.from(localDateTime.getHour, localDateTime.getMinute)
  }

  /**
    * このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日付における午前0時（深夜）の瞬間を表す
    * [[TimePoint]]を取得する。
    *
    * @param timeZone タイムゾーン
    * @return 午前0時（深夜）の瞬間を表す [[TimePoint]]
    */
  @deprecated("Use backToMidnight(zoneId: ZoneId) method instead", "0.1.18")
  def backToMidnight(timeZone: TimeZone): LimitValue[TimePoint] =
    asCalendarDate(timeZone.toZoneId).asTimeInterval(timeZone.toZoneId).start

  def backToMidnight(zoneId: ZoneId = ZoneIds.Default): LimitValue[TimePoint] =
    asCalendarDate(zoneId).asTimeInterval(zoneId).start

  /**
    * このオブジェクトの`millisecondsFromEpoc`フィールド（エポックからの経過ミリ秒）を返す。
    *
    * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
    *
    * @return エポックからの経過ミリ秒
    */
  @deprecated("Use millisecondsFromEpoc property instead", "0.1.18")
  val breachEncapsulationOfMillisecondsFromEpoc = millisecondsFromEpoc

  /**
    * このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日付を取得する。
    *
    * @param timeZone [[TimeZone]]
    * @return [[CalendarDate]]
    */
  @deprecated("Use asCalendarDate(zoneId: ZoneId) method instead", "0.1.18")
  def asCalendarDate(timeZone: TimeZone): CalendarDate =
    CalendarDate.from(this, timeZone.toZoneId)

  /**
    * このインスタンスが表現する瞬間の、指定したゾーンIDにおける日付を取得する。
    *
    * @param zoneId [[ZoneId]]
    * @return [[CalendarDate]]
    */
  def asCalendarDate(zoneId: ZoneId = ZoneIds.Default): CalendarDate =
    CalendarDate.from(this, zoneId)

  /**
    * このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日時を取得する。
    *
    * @param timeZone [[TimeZone]]
    * @return [[CalendarDateTime]]
    */
  @deprecated("Use asCalendarDateTime(zoneId: ZoneId) method instead", "0.1.18")
  def asCalendarDateTime(timeZone: TimeZone): CalendarDateTime =
    CalendarDateTime.from(asCalendarDate(timeZone.toZoneId), asTimeOfDay(timeZone.toZoneId))

  /**
    * このインスタンスが表現する瞬間の、指定したゾーンIDにおける日時を取得する。
    *
    * @param zoneId [[ZoneId]]
    * @return [[CalendarDateTime]]
    */
  def asCalendarDateTime(zoneId: ZoneId = ZoneIds.Default): CalendarDateTime =
    CalendarDateTime.from(asCalendarDate(zoneId), asTimeOfDay(zoneId))

  /**
    * 瞬間同士の比較を行う。
    *
    * 相対的に過去である方を「小さい」と判断する。
    *
    * @param otherPoint 比較対象
    * @return `java.util.Comparable` compareTo(Object)に準じる
    */
  override def compare(otherPoint: TimePoint): Int =
    if (isBefore(otherPoint)) -1
    else if (isAfter(otherPoint)) 1
    else 0

  /**
    * このオブジェクトと、与えたオブジェクト `other`の同一性を検証する。
    *
    * 与えたオブジェクトが [[TimePoint]] 型であった場合、
    * 同じ日時を指している場合は`true`、そうでない場合は`false`を返す。
    *
    * @see java.lang.Object#equals(java.lang.Object)
    */
  override def equals(obj: Any): Boolean = obj match {
    case that: TimePoint => asInstant == that.asInstant
    case _               => false
  }

  override def hashCode: Int = 31 * asInstant.hashCode()

  /**
    * このインスタンスがあらわす瞬間が、指定した期間の終了後に位置するかどうか調べる。
    *
    * @param interval 基準期間
    * @return 期間の終了後に位置する場合は`true`、そうでない場合は`false`
    */
  def isAfter(interval: TimeInterval): Boolean = interval.isBefore(Limit(this))

  /**
    * 指定した瞬間 `other` が、このオブジェクトが表現する日時よりも未来であるかどうかを検証する。
    *
    * 同一日時である場合は `false` を返す。
    *
    * @param other 対象日時
    * @return 未来である場合は`true`、そうでない場合は`false`
    */
  def isAfter(other: TimePoint): Boolean = asInstant.isAfter(other.asInstant)

  /**
    * このインスタンスがあらわす瞬間が、指定した期間の開始前に位置するかどうか調べる。
    *
    * @param interval 基準期間
    * @return 期間の開始前に位置する場合は`true`、そうでない場合は`false`
    */
  def isBefore(interval: TimeInterval): Boolean = interval.isAfter(Limit(this))

  /**
    * 指定した瞬間 `other` が、このオブジェクトが表現する日時よりも過去であるかどうかを検証する。
    *
    * 同一日時である場合は `false` を返す。
    *
    * @param other 対象日時
    * @return 過去である場合は`true`、そうでない場合は`false`
    */
  def isBefore(other: TimePoint): Boolean = asInstant.isBefore(other.asInstant)

  /**
    * 指定したタイムゾーンにおいて、このインスタンスが表現する瞬間と指定した瞬間`other`の年月日が等価であるかを調べる。
    *
    * @param other    対象瞬間
    * @param timeZone タイムゾーン
    * @return 等価である場合は`true`、そうでない場合は`false`
    */
  def isSameDayAs(other: TimePoint, timeZone: TimeZone): Boolean =
    asCalendarDate(timeZone.toZoneId) == other.asCalendarDate(timeZone.toZoneId)

  def isSameDayAs(other: TimePoint, zoneId: ZoneId = ZoneIds.Default): Boolean =
    isSameDayAs(other, TimeZone.getTimeZone(zoneId))

  /**
    * この日時の、指定した時間の長さ分過去の日時を取得する。
    *
    * @param duration 時間の長さ
    * @return 過去の日時
    */
  def minus(duration: Duration): TimePoint =
    duration.subtractedFrom(this)

  def -(duration: Duration): TimePoint = minus(duration)

  /**
    * この日時から、指定した時間の長さ分未来の日時を取得する。
    *
    * @param duration 時間の長さ
    * @return 未来の日時
    */
  def plus(duration: Duration): TimePoint =
    duration.addedTo(this)

  def +(duration: Duration): TimePoint = plus(duration)

  /**
    * このオブジェクトが表現する瞬間の、ちょうど1日後を取得する。
    *
    * 日内の時間は変化しない。
    *
    * @return 1日後
    */
  lazy val nextDay: TimePoint = this.+(Duration.days(1))

  /**
    * この瞬間の文字列表現を取得する。
    *
    * @see java.lang.Object#toString()
    */
  override def toString: String = toString("yyyy/MM/dd HH:mm:ss")

  /**
    * この瞬間を、指定したパターンで整形し、その文字列表現を取得する。
    *
    * @param pattern  [[java.text.SimpleDateFormat]]に基づくパターン
    * @param timeZone タイムゾーン
    * @return 整形済み時間文字列
    */
  @deprecated("Use def toString(pattern: String, zoneId: ZoneId) method instead", "0.1.18")
  def toString(pattern: String, timeZone: TimeZone): String =
    toString(pattern, timeZone.toZoneId)

  def toString(pattern: String, zoneId: ZoneId = ZoneIds.Default): String = {
    val format = new SimpleDateFormat(pattern)
    format.setTimeZone(TimeZone.getTimeZone(zoneId))
    format.format(new JDate(millisecondsFromEpoc))
  }

  /**
    * このインスタンスがあらわす瞬間を開始瞬間、`end`を終了瞬間とする、期間を返す。
    *
    * 生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を生成する。
    *
    * @param end 終了日時（上側限界値）. `LimitValue[TimePoint]`の場合は、限界がないことを表す
    * @return [[TimeInterval]]
    */
  def until(end: LimitValue[TimePoint]): TimeInterval =
    TimeInterval.over(Limit(this), end)

}

/**
  * `TimePoint`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object TimePoint {

  /**
    * インスタンスを生成する。
    *
    * @param milliseconds エポックからの経過ミリ秒
    * @return [[TimePoint]]
    */
  def apply(milliseconds: Long): TimePoint = from(milliseconds)

  /**
    * 抽出子メソッド。
    *
    * @param timePoint [[TimePoint]]
    * @return `Option[(Long)]`
    */
  def unapply(timePoint: TimePoint): Option[Long] =
    Some(timePoint.millisecondsFromEpoc)

  /**
    * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param yearMonth   年月
    * @param date        日
    * @param hour        時
    * @param minute      分
    * @param second      秒
    * @param millisecond ミリ秒
    * @return [[TimePoint]]
    */
  def at(yearMonth: CalendarYearMonth,
         date: DayOfMonth,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int): TimePoint =
    at(yearMonth, date, hour, minute, second, millisecond, ZoneIds.Default)

  /**
    * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param yearMonth   年月
    * @param date        日
    * @param hour        時
    * @param minute      分
    * @param second      秒
    * @param millisecond ミリ秒
    * @param timeZone    タイムゾーン
    * @return [[TimePoint]]
    */
  @deprecated(
    "Use at(yearMonth: CalendarYearMonth, date: DayOfMonth, hour: Int, minute: Int, second: Int, millisecond: Int, zoneId: ZoneId) method instead",
    "0.1.18"
  )
  def at(yearMonth: CalendarYearMonth,
         date: DayOfMonth,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int,
         timeZone: TimeZone): TimePoint = {
    at(
      yearMonth.year,
      yearMonth.month.value,
      date.value,
      hour,
      minute,
      second,
      millisecond,
      timeZone.toZoneId
    )
  }

  def at(yearMonth: CalendarYearMonth,
         date: DayOfMonth,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int,
         zoneId: ZoneId): TimePoint = {
    at(
      yearMonth.year,
      yearMonth.month.value,
      date.value,
      hour,
      minute,
      second,
      millisecond,
      zoneId
    )
  }

  /**
    * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year        年
    * @param month       月（1〜12）
    * @param date        日
    * @param hour        時
    * @param minute      分
    * @param second      秒
    * @param millisecond ミリ秒
    * @return [[TimePoint]]
    */
  def at(year: Int,
         month: Int,
         date: Int,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int): TimePoint =
    at(year, month, date, hour, minute, second, millisecond, ZoneIds.Default)

  /**
    * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year        年
    * @param month       月（1〜12）
    * @param date        日
    * @param hour        時
    * @param minute      分
    * @param second      秒
    * @param millisecond ミリ秒
    * @param timeZone    タイムゾーン
    * @return [[TimePoint]]
    */
  @deprecated(
    "Use at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, millisecond: Int, zoneId: ZoneId) method instead",
    "0.1.18")
  def at(year: Int,
         month: Int,
         date: Int,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int,
         timeZone: TimeZone): TimePoint = {
    val calendar = Calendar.getInstance(timeZone)
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DATE, date)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, second)
    calendar.set(Calendar.MILLISECOND, millisecond)
    from(calendar.getTime.getTime)
  }

  def at(year: Int,
         month: Int,
         date: Int,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int,
         zoneId: ZoneId): TimePoint = {
    val zonedDateTime =
      ZonedDateTime.of(year, month, date, hour, minute, second, millisecond * 1000000, zoneId)
    from(zonedDateTime)
  }

  /**
    * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year   年
    * @param month  月（1〜12）
    * @param date   日
    * @param hour   時
    * @param minute 分
    * @param second 秒
    * @return [[TimePoint]]
    */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int): TimePoint =
    at(year, month, date, hour, minute, second, ZoneIds.Default)

  /**
    * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year     年
    * @param month    月（1〜12）
    * @param date     日
    * @param hour     時
    * @param minute   分
    * @param second   秒
    * @param timeZone タイムゾーン
    * @return [[TimePoint]]
    */
  @deprecated(
    "Use at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, zoneId: ZoneId) method instead",
    "0.1.18")
  def at(year: Int,
         month: Int,
         date: Int,
         hour: Int,
         minute: Int,
         second: Int,
         timeZone: TimeZone): TimePoint =
    at(year, month, date, hour, minute, second, 0, timeZone.toZoneId)

  def at(year: Int,
         month: Int,
         date: Int,
         hour: Int,
         minute: Int,
         second: Int,
         zoneId: ZoneId): TimePoint =
    at(year, month, date, hour, minute, second, 0, zoneId)

  /**
    * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year   年
    * @param month  月（1〜12）
    * @param date   日
    * @param hour   時
    * @param minute 分
    * @return [[TimePoint]]
    */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int): TimePoint =
    at(year, month, date, hour, minute, ZoneIds.Default)

  /**
    * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year     年
    * @param month    月（1〜12）
    * @param date     日
    * @param hour     時
    * @param minute   分
    * @param timeZone タイムゾーン
    * @return [[TimePoint]]
    */
  @deprecated(
    "Use at(year: Int, month: Int, date: Int, hour: Int, minute: Int, zoneId: ZoneId) method instead",
    "0.1.18")
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, timeZone: TimeZone): TimePoint =
    at(year, month, date, hour, minute, 0, 0, timeZone.toZoneId)

  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, zoneId: ZoneId): TimePoint =
    at(year, month, date, hour, minute, 0, 0, zoneId)

  /**
    * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year        年
    * @param month       月
    * @param date        日
    * @param hour        時
    * @param minute      分
    * @param second      秒
    * @param millisecond ミリ秒
    * @return [[TimePoint]]
    */
  def at(year: Int,
         month: MonthOfYear,
         date: DayOfMonth,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int): TimePoint =
    at(year, month, date, hour, minute, second, millisecond, ZoneIds.Default)

  /**
    * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year        年
    * @param month       月
    * @param date        日
    * @param hour        時
    * @param minute      分
    * @param second      秒
    * @param millisecond ミリ秒
    * @param timeZone    タイムゾーン
    * @return [[TimePoint]]
    */
  @deprecated(
    "Use at(year: Int, month: MonthOfYear, date: DayOfMonth, hour: Int, minute: Int, second: Int, millisecond: Int, zoneId: ZoneId) method instead",
    "0.1.18"
  )
  def at(year: Int,
         month: MonthOfYear,
         date: DayOfMonth,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int,
         timeZone: TimeZone): TimePoint =
    at(year, month.value, date.value, hour, minute, second, millisecond, timeZone.toZoneId)

  def at(year: Int,
         month: MonthOfYear,
         date: DayOfMonth,
         hour: Int,
         minute: Int,
         second: Int,
         millisecond: Int,
         zoneId: ZoneId): TimePoint =
    at(year, month.value, date.value, hour, minute, second, millisecond, zoneId)

  /**
    * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year        年
    * @param month       月（1〜12）
    * @param date        日
    * @param hour        時
    * @param amPm        午前午後を表す文字列("AM", "PM"など)
    * @param minute      分
    * @param second      秒
    * @param millisecond ミリ秒
    * @return [[TimePoint]]
    * @throws IllegalArgumentException 引数`hour`の値が0〜11の範囲ではない場合もしくは、
    *                                  引数`amPm`の値が `"AM"` または `"PM"` ではない場合
    */
  def at12hr(year: Int,
             month: Int,
             date: Int,
             hour: Int,
             amPm: String,
             minute: Int,
             second: Int,
             millisecond: Int): TimePoint = {
    at(year,
       month,
       date,
       HourOfDay.convertTo24hour(hour, amPm),
       minute,
       second,
       millisecond,
       ZoneIds.Default)
  }

  /**
    * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param year        年
    * @param month       月（1〜12）
    * @param date        日
    * @param hour        時
    * @param amPm        午前午後を表す文字列("AM", "PM"など)
    * @param minute      分
    * @param second      秒
    * @param millisecond ミリ秒
    * @param timeZone    タイムゾーン
    * @return [[TimePoint]]
    * @throws IllegalArgumentException 引数`hour`の値が0〜11の範囲ではない場合もしくは、
    *                                  引数`amPm`の値が `"AM"` または `"PM"` ではない場合もしく
    */
  @deprecated(
    "Use at12hr(year: Int, month: Int, date: Int, hour: Int, amPm: String, minute: Int, second: Int, millisecond: Int, zoneId: ZoneId) method instead",
    "0.1.18"
  )
  def at12hr(year: Int,
             month: Int,
             date: Int,
             hour: Int,
             amPm: String,
             minute: Int,
             second: Int,
             millisecond: Int,
             timeZone: TimeZone): TimePoint =
    at(year,
       month,
       date,
       HourOfDay.convertTo24hour(hour, amPm),
       minute,
       second,
       millisecond,
       timeZone.toZoneId)

  def at12hr(year: Int,
             month: Int,
             date: Int,
             hour: Int,
             amPm: String,
             minute: Int,
             second: Int,
             millisecond: Int,
             zoneId: ZoneId): TimePoint =
    at(year,
       month,
       date,
       HourOfDay.convertTo24hour(hour, amPm),
       minute,
       second,
       millisecond,
       zoneId)

  /**
    * デフォルトタイムゾーンにおける、指定した日時の午前0時（深夜）を表すインスタンスを取得する。
    *
    * @param calendarDate 日付
    * @return [[TimePoint]]
    */
  def atMidnight(calendarDate: CalendarDate): TimePoint =
    atMidnight(calendarDate, ZoneIds.Default)

  /**
    * 指定したタイムゾーンにおける、指定した日時の午前0時（深夜）を表すインスタンスを取得する。
    *
    * @param calendarDate 日付
    * @param timeZone     タイムゾーン
    * @return [[TimePoint]]
    */
  @deprecated("Use atMidnight(calendarDate: CalendarDate, zoneId: ZoneId) method instead", "0.1.18")
  def atMidnight(calendarDate: CalendarDate, timeZone: TimeZone): TimePoint =
    at(
      calendarDate.asCalendarMonth,
      calendarDate.day,
      0,
      0,
      0,
      0,
      timeZone.toZoneId
    )

  def atMidnight(calendarDate: CalendarDate, zoneId: ZoneId): TimePoint =
    at(
      calendarDate.asCalendarMonth,
      calendarDate.day,
      0,
      0,
      0,
      0,
      zoneId
    )

  /**
    * デフォルトタイムゾーンにおける、指定した日付の午前0時（深夜）を表すインスタンスを取得する。
    *
    * @param year  年
    * @param month 月（1〜12）
    * @param date  日
    * @return [[TimePoint]]
    */
  def atMidnight(year: Int, month: Int, date: Int): TimePoint =
    atMidnight(year, month, date, ZoneIds.Default)

  /**
    * 指定したタイムゾーンにおける、指定した日付の午前0時（深夜）を表すインスタンスを取得する。
    *
    * @param year     年
    * @param month    月（1〜12）
    * @param date     日
    * @param timeZone タイムゾーン
    * @return [[TimePoint]]
    */
  @deprecated("Use atMidnight(year: Int, month: Int, date: Int, zoneId: ZoneId) method instead",
              "0.1.18")
  def atMidnight(year: Int, month: Int, date: Int, timeZone: TimeZone): TimePoint =
    at(year, month, date, 0, 0, 0, 0, timeZone.toZoneId)

  def atMidnight(year: Int, month: Int, date: Int, zoneId: ZoneId): TimePoint =
    at(year, month, date, 0, 0, 0, 0, zoneId)

  /**
    * [[java.util.Calendar]]を[[TimePoint]]に変換する。
    *
    * @param calendar 元となる日時情報を表す [[java.util.Calendar]]インスタンス
    * @return [[TimePoint]]
    */
  def from(calendar: Calendar): TimePoint =
    from(calendar.getTime)

  /**
    * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param date 日付
    * @param time 時間
    * @return [[TimePoint]]
    */
  def from(date: CalendarDate, time: TimeOfDay): TimePoint =
    from(date, time, ZoneIds.Default)

  /**
    * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
    *
    * @param date     日付
    * @param time     時間
    * @param timeZone タイムゾーン
    * @return [[TimePoint]]
    */
  @deprecated("Use from(date: CalendarDate, time: TimeOfDay, zoneId: ZoneId) method instead",
              "0.1.18")
  def from(date: CalendarDate, time: TimeOfDay, timeZone: TimeZone): TimePoint =
    at(date.asCalendarMonth, date.day, time.hour.value, time.minute.value, 0, 0, timeZone.toZoneId)

  def from(date: CalendarDate, time: TimeOfDay, zoneId: ZoneId): TimePoint =
    at(date.asCalendarMonth, date.day, time.hour.value, time.minute.value, 0, 0, zoneId)

  /**
    * [[java.util.Date]]を[[TimePoint]]に変換する。
    *
    * @param javaDate 元となる日時情報を表す [[java.util.Date]]インスタンス
    * @return [[TimePoint]]
    */
  def from(javaDate: JDate): TimePoint =
    from(javaDate.getTime)

  /**
    * [[java.time.Instant]]を[[TimePoint]]に変換する。
    *
    * @param instant 元となる日時情報を表す [[java.time.Instant]]インスタンス
    * @return [[TimePoint]]
    */
  def from(instant: Instant): TimePoint =
    from(instant.toEpochMilli)

  /**
    * [[java.time.ZonedDateTime]]を[[TimePoint]]に変換する。
    *
    * @param zonedDateTime 元となる日時情報を表す [[java.time.ZonedDateTime]]インスタンス
    * @return [[TimePoint]]
    */
  def from(zonedDateTime: ZonedDateTime): TimePoint =
    from(zonedDateTime.toInstant.toEpochMilli)

  /**
    * エポックからの経過ミリ秒を [[TimePoint]] に変換する。
    *
    * @param milliseconds エポックからの経過ミリ秒
    * @return [[TimePoint]]
    */
  def from(milliseconds: Long): TimePoint = {
    val result = new TimePoint(milliseconds)
    //assert FAR_FUTURE == null || result.isBefore(FAR_FUTURE);
    //assert FAR_PAST == null || result.isAfter(FAR_PAST);
    result
  }

  /**
    * 日時を表す文字列を、指定したパターンで指定したタイムゾーンとして解析し、その日時を表す
    * [[TimePoint]]を返す。
    *
    * @param dateTimeString 日時を表す文字列
    * @param pattern        解析パターン
    * @param timeZone       タイムゾーン
    * @return [[TimePoint]]
    * @throws ParseException 文字列の解析に失敗した場合
    */
  @deprecated("Use parse(dateTimeString: String, pattern: String, zoneId: ZoneId) method instead",
              "0.1.18")
  def parse(dateTimeString: String, pattern: String, timeZone: TimeZone): TimePoint =
    parse(dateTimeString, pattern, timeZone.toZoneId)

  /**
    * 日時を表す文字列を、指定したパターンで指定したタイムゾーンとして解析し、その日時を表す
    * [[TimePoint]]を返す。
    *
    * @param dateTimeString 日時を表す文字列
    * @param pattern        解析パターン
    * @param zoneId       ゾーンID
    * @return [[TimePoint]]
    * @throws ParseException 文字列の解析に失敗した場合
    */
  def parse(dateTimeString: String,
            pattern: String,
            zoneId: ZoneId = ZoneIds.Default): TimePoint = {
    val sdf = new SimpleDateFormat(pattern)
    sdf.setTimeZone(TimeZone.getTimeZone(zoneId))
    val date = sdf.parse(dateTimeString)
    from(date)
  }

}
