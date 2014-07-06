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

import java.text.SimpleDateFormat
import java.util.{ Calendar, TimeZone, Date => JDate }

import org.sisioh.baseunits.scala.intervals.{ Limit, LimitValue }

/**
 * ミリ秒精度で、ある時間の一点をあらわすクラス。
 *
 * タイムゾーンを持っている。
 *
 * @author j5ik2o
 * @param millisecondsFromEpoc エポックからの経過ミリ秒
 */
class TimePoint private[time] (private[time] val millisecondsFromEpoc: Long)
    extends Ordered[TimePoint] with Serializable {

  /**
   * このオブジェクトが表現する瞬間を指定したタイムゾーンとして扱い、`java.util.Calendar`型として取得する。
   *
   * @param timeZone タイムゾーン
   * @return `java.util.Calendar`
   */
  def asJavaCalendar(timeZone: TimeZone = TimeZones.Default): Calendar = {
    val result = Calendar.getInstance(timeZone)
    result.setTime(asJavaUtilDate)
    result
  }

  /**
   * このオブジェクトが表現する瞬間を、`java.util.Date` 型として取得する。
   *
   * @return [[java.util.Date]]
   */
  lazy val asJavaUtilDate = new JDate(millisecondsFromEpoc)

  /**
   * この瞬間を「時分」として返す。
   *
   * @param timeZone タイムゾーン
   * @return 時分
   */
  def asTimeOfDay(timeZone: TimeZone = TimeZones.Default): TimeOfDay = {
    val calendar = asJavaCalendar(timeZone)
    TimeOfDay.from(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))
  }

  /**
   * このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日付における午前0時（深夜）の瞬間を表す
   * [[TimePoint]]を取得する。
   *
   * @param timeZone タイムゾーン
   * @return 午前0時（深夜）の瞬間を表す [[TimePoint]]
   */
  def backToMidnight(timeZone: TimeZone = TimeZones.Default): LimitValue[TimePoint] = asCalendarDate(timeZone).asTimeInterval(timeZone).start

  /**
   * このオブジェクトの`millisecondsFromEpoc`フィールド（エポックからの経過ミリ秒）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return エポックからの経過ミリ秒
   */
  val breachEncapsulationOfMillisecondsFromEpoc = millisecondsFromEpoc

  /**
   * このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日付を取得する。
   *
   * @param timeZone タイムゾーン
   * @return 日付
   */
  def asCalendarDate(timeZone: TimeZone = TimeZones.Default): CalendarDate =
    CalendarDate.from(this, timeZone)

  def asCalendarDateTime(timeZone: TimeZone = TimeZones.Default): CalendarDateTime =
    CalendarDateTime.from(asCalendarDate(timeZone), asTimeOfDay(timeZone))

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
    case that: TimePoint => millisecondsFromEpoc == that.millisecondsFromEpoc
    case _               => false
  }

  override def hashCode = 31 * (millisecondsFromEpoc ^ (millisecondsFromEpoc >>> 32)).asInstanceOf[Int]

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
  def isAfter(other: TimePoint): Boolean = millisecondsFromEpoc > other.millisecondsFromEpoc

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
  def isBefore(other: TimePoint): Boolean = millisecondsFromEpoc < other.millisecondsFromEpoc

  /**
   * 指定したタイムゾーンにおいて、このインスタンスが表現する瞬間と指定した瞬間`other`の年月日が等価であるかを調べる。
   *
   * @param other 対象瞬間
   * @param timeZone タイムゾーン
   * @return 等価である場合は`true`、そうでない場合は`false`
   */
  def isSameDayAs(other: TimePoint, timeZone: TimeZone = TimeZones.Default): Boolean =
    asCalendarDate(timeZone) == other.asCalendarDate(timeZone)

  /**
   * この日時の、指定した時間の長さ分過去の日時を取得する。
   *
   * @param duration 時間の長さ
   * @return 過去の日時
   */
  def minus(duration: Duration, timeZone: TimeZone = TimeZones.Default): TimePoint =
    duration.subtractedFrom(this, timeZone)

  def -(duration: Duration)(implicit timeZone: TimeZone) = minus(duration, timeZone)

  /**
   * このオブジェクトが表現する瞬間の、ちょうど1日後を取得する。
   *
   * 日内の時間は変化しない。
   *
   * @return 1日後
   */
  def nextDay(timeZone: TimeZone = TimeZones.Default) = this.+(Duration.days(1))(timeZone)

  /**
   * この日時から、指定した時間の長さ分未来の日時を取得する。
   *
   * @param duration 時間の長さ
   * @return 未来の日時
   */
  def +(duration: Duration)(implicit timeZone: TimeZone) = duration.addedTo(this, timeZone)

  /**
   * この瞬間の文字列表現を取得する。
   *
   * @see java.lang.Object#toString()
   */
  override def toString = toString("yyyy/MM/dd HH:mm:ss", TimeZones.Default)

  /**
   * この瞬間を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern [[java.text.SimpleDateFormat]]に基づくパターン
   * @param timeZone タイムゾーン
   * @return 整形済み時間文字列
   */
  def toString(pattern: String, timeZone: TimeZone = TimeZones.Default) = {
    val format = new SimpleDateFormat(pattern)
    format.setTimeZone(timeZone)
    format.format(asJavaUtilDate)
  }

  /**
   * このインスタンスがあらわす瞬間を開始瞬間、`end`を終了瞬間とする、期間を返す。
   *
   * 生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を生成する。
   *
   * @param end 終了日時（上側限界値）. `LimitValue[TimePoint]`の場合は、限界がないことを表す
   * @return [[org.sisioh.baseunits.scala.time.TimeInterval]]
   */
  def until(end: LimitValue[TimePoint], timeZone: TimeZone = TimeZones.Default) =
    TimeInterval.over(Limit(this), end, timeZone)

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
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def apply(milliseconds: Long): TimePoint = from(milliseconds)

  /**
   * 抽出子メソッド。
   *
   * @param timePoint [[TimePoint]]
   * @return `Option[(Long)]`
   */
  def unapply(timePoint: TimePoint) = Some(timePoint.millisecondsFromEpoc)

  /**
   * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param yearMonth 年月
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(yearMonth: CalendarYearMonth, date: DayOfMonth, hour: Int,
         minute: Int, second: Int, millisecond: Int): TimePoint =
    at(yearMonth, date, hour, minute, second, millisecond, TimeZones.Default)

  /**
   * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param yearMonth 年月
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(yearMonth: CalendarYearMonth, date: DayOfMonth, hour: Int,
         minute: Int, second: Int, millisecond: Int, timeZone: TimeZone): TimePoint = {
    at(yearMonth.breachEncapsulationOfYear,
      yearMonth.breachEncapsulationOfMonth.value + 1,
      date.value, hour, minute, second, millisecond, timeZone)
  }

  /**
   * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, millisecond: Int): TimePoint =
    at(year, month, date, hour, minute, second, millisecond, TimeZones.Default)

  /**
   * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, millisecond: Int, timeZone: TimeZone): TimePoint = {
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

  /**
   * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int): TimePoint =
    at(year, month, date, hour, minute, second, TimeZones.Default)

  /**
   * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, timeZone: TimeZone): TimePoint =
    at(year, month, date, hour, minute, second, 0, timeZone)

  /**
   * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int): TimePoint =
    at(year, month, date, hour, minute, TimeZones.Default)

  /**
   * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, timeZone: TimeZone): TimePoint =
    at(year, month, date, hour, minute, 0, 0, timeZone)

  /**
   * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: MonthOfYear, date: DayOfMonth, hour: Int, minute: Int, second: Int, millisecond: Int): TimePoint =
    at(year, month, date, hour, minute, second, millisecond, TimeZones.Default)

  /**
   * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: MonthOfYear, date: DayOfMonth, hour: Int, minute: Int, second: Int,
         millisecond: Int, timeZone: TimeZone): TimePoint =
    at(year, month.value, date.value, hour, minute, second, millisecond, timeZone)

  /**
   * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param amPm 午前午後を表す文字列("AM", "PM"など)
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   * @throws IllegalArgumentException 引数`hour`の値が0〜11の範囲ではない場合もしくは、
   *                                  引数`amPm`の値が `"AM"` または `"PM"` ではない場合
   */
  def at12hr(year: Int, month: Int, date: Int, hour: Int, amPm: String, minute: Int, second: Int, millisecond: Int) = {
    at(year, month, date, HourOfDay.convertTo24hour(hour, amPm), minute, second, millisecond, TimeZones.Default)
  }

  /**
   * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param amPm 午前午後を表す文字列("AM", "PM"など)
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   * @throws IllegalArgumentException 引数`hour`の値が0〜11の範囲ではない場合もしくは、
   *                                  引数`amPm`の値が `"AM"` または `"PM"` ではない場合もしく
   */
  def at12hr(year: Int, month: Int, date: Int, hour: Int, amPm: String, minute: Int, second: Int,
             millisecond: Int, timeZone: TimeZone): TimePoint =
    at(year, month, date, HourOfDay.convertTo24hour(hour, amPm), minute, second, millisecond, timeZone)

  /**
   * デフォルトタイムゾーンにおける、指定した日時の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param calendarDate 日付
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def atMidnight(calendarDate: CalendarDate): TimePoint =
    atMidnight(calendarDate, TimeZones.Default)

  /**
   * 指定したタイムゾーンにおける、指定した日時の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param calendarDate 日付
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def atMidnight(calendarDate: CalendarDate, timeZone: TimeZone): TimePoint =
    at(calendarDate.asCalendarMonth,
      calendarDate.breachEncapsulationOfDay, 0, 0, 0, 0, timeZone)

  /**
   * デフォルトタイムゾーンにおける、指定した日付の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def atMidnight(year: Int, month: Int, date: Int): TimePoint =
    atMidnight(year, month, date, TimeZones.Default)

  /**
   * 指定したタイムゾーンにおける、指定した日付の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def atMidnight(year: Int, month: Int, date: Int, timeZone: TimeZone): TimePoint =
    at(year, month, date, 0, 0, 0, 0, timeZone)

  /**
   * [[java.util.Calendar]]を[[org.sisioh.baseunits.scala.time.TimePoint]]に変換する。
   *
   * @param calendar 元となる日時情報を表す [[java.util.Calendar]]インスタンス
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def from(calendar: Calendar): TimePoint =
    from(calendar.getTime)

  /**
   * デフォルトタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param date 日付
   * @param time 時間
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def from(date: CalendarDate, time: TimeOfDay): TimePoint =
    from(date, time, TimeZones.Default)

  /**
   * 指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param date 日付
   * @param time 時間
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def from(date: CalendarDate, time: TimeOfDay, timeZone: TimeZone): TimePoint =
    at(date.asCalendarMonth, date.breachEncapsulationOfDay,
      time.breachEncapsulationOfHour.value, time.breachEncapsulationOfMinute.value,
      0, 0, timeZone)

  /**
   * `java.util.Date`を[[org.sisioh.baseunits.scala.time.TimePoint]]に変換する。
   *
   * @param javaDate 元となる日時情報を表す [[java.util.Date]]インスタンス
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def from(javaDate: JDate): TimePoint =
    from(javaDate.getTime)

  /**
   * エポックからの経過ミリ秒を [[org.sisioh.baseunits.scala.time.TimePoint]] に変換する。
   *
   * @param milliseconds エポックからの経過ミリ秒
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   */
  def from(milliseconds: Long): TimePoint = {
    val result = new TimePoint(milliseconds)
    //assert FAR_FUTURE == null || result.isBefore(FAR_FUTURE);
    //assert FAR_PAST == null || result.isAfter(FAR_PAST);
    result
  }

  /**
   * 日時を表す文字列を、指定したパターンで指定したタイムゾーンとして解析し、その日時を表す
   * [[org.sisioh.baseunits.scala.time.TimePoint]]を返す。
   *
   * @param dateTimeString 日時を表す文字列
   * @param pattern 解析パターン
   * @param timeZone タイムゾーン
   * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  def parse(dateTimeString: String, pattern: String, timeZone: TimeZone = TimeZones.Default) = {
    val sdf = new SimpleDateFormat(pattern)
    sdf.setTimeZone(timeZone)
    val date = sdf.parse(dateTimeString)
    from(date)
  }

}