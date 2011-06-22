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

import java.text.SimpleDateFormat
import java.util.{Date => JDate, Calendar, TimeZone}
import jp.tricreo.baseunits.scala.intervals.{LimitValue, Limit}

/**ミリ秒精度で、ある時間の一点をあらわすクラス。
 *
 * タイムゾーンを持っている。
 *
 * @author j5ik2o
 * @param millisecondsFromEpoc エポックからの経過ミリ秒
 */

class TimePoint private[time]
(private[time] val millisecondsFromEpoc: Long)
  extends Ordered[TimePoint] with Serializable {

  /**このオブジェクトが表現する瞬間をGMTとして扱い、[[java.util.Calendar]]型として取得する。
   *
   * @return [[java.util.Calendar]]
   */
  def asJavaCalendar: Calendar = asJavaCalendar(TimePoint.GMT)

  /**このオブジェクトが表現する瞬間を指定したタイムゾーンとして扱い、[[java.util.Calendar]]型として取得する。
   *
   * @param zone タイムゾーン
   * @return [[java.util.Calendar]]
   */
  def asJavaCalendar(zone: TimeZone): Calendar = {
    val result = Calendar.getInstance(zone)
    result.setTime(asJavaUtilDate)
    result
  }

  /**このオブジェクトが表現する瞬間を、[[java.util.Date]]型として取得する。
   *
   * @return [[java.util.Date]]
   */
  def asJavaUtilDate = new JDate(millisecondsFromEpoc)

  /**この瞬間を「時分」として返す。
   *
   * @param zone タイムゾーン
   * @return 時分
   */
  def asTimeOfDay(zone: TimeZone) = {
    val calendar = asJavaCalendar(zone)
    TimeOfDay.from(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))
  }

  /**このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日付における午前0時（深夜）の瞬間を表す
   * [[jp.tricreo.baseunits.scala.time.TimePoint]]を取得する。
   *
   * @param zone タイムゾーン
   * @return 午前0時（深夜）の瞬間を表す [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def backToMidnight(zone: TimeZone) = calendarDate(zone).asTimeInterval(zone).start


  /**このオブジェクトの`millisecondsFromEpoc`フィールド（エポックからの経過ミリ秒）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return エポックからの経過ミリ秒
   */
  def breachEncapsulationOfMillisecondsFromEpoc = millisecondsFromEpoc

  /**このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日付を取得する。
   *
   * @param zone タイムゾーン
   * @return 日付
   */
  def calendarDate(zone: TimeZone) = CalendarDate.from(this, zone)

  /**瞬間同士の比較を行う。
   *
   * 相対的に過去である方を「小さい」と判断する。
   *
   * @param otherPoint 比較対象
   * @return [[java.util.Comparable]] compareTo(Object)に準じる
   */
  def compare(otherPoint: TimePoint): Int =
    if (isBefore(otherPoint)) -1
    else if (isAfter(otherPoint)) 1
    else 0

  /**このオブジェクトと、与えたオブジェクト `other`の同一性を検証する。
   *
   * 与えたオブジェクトが[[jp.tricreo.baseunits.scala.time.TimePoint]]型であった場合、
   * 同じ日時を指している場合は`true`、そうでない場合は`false`を返す。
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  override def equals(obj: Any): Boolean = obj match {
    case that: TimePoint => millisecondsFromEpoc == that.millisecondsFromEpoc
    case _ => false
  }

  override def hashCode = (millisecondsFromEpoc ^ (millisecondsFromEpoc >>> 32)).asInstanceOf[Int]


  /**このインスタンスがあらわす瞬間が、指定した期間の終了後に位置するかどうか調べる。
   *
   * @param interval 基準期間
   * @return 期間の終了後に位置する場合は`true`、そうでない場合は`false`
   */
  def isAfter(interval: TimeInterval) = interval.isBefore(Limit(this))

  /**指定した瞬間 `other` が、このオブジェクトが表現する日時よりも未来であるかどうかを検証する。
   *
   * 同一日時である場合は `false` を返す。
   *
   * @param other 対象日時
   * @return 未来である場合は`true`、そうでない場合は`false`
   */
  def isAfter(other: TimePoint) = millisecondsFromEpoc > other.millisecondsFromEpoc


  /**このインスタンスがあらわす瞬間が、指定した期間の開始前に位置するかどうか調べる。
   *
   * @param interval 基準期間
   * @return 期間の開始前に位置する場合は`true`、そうでない場合は`false`
   */
  def isBefore(interval: TimeInterval) = interval.isAfter(Limit(this))

  /**指定した瞬間 `other` が、このオブジェクトが表現する日時よりも過去であるかどうかを検証する。
   *
   * 同一日時である場合は `false` を返す。
   *
   * @param other 対象日時
   * @return 過去である場合は`true`、そうでない場合は`false`
   */
  def isBefore(other: TimePoint) = millisecondsFromEpoc < other.millisecondsFromEpoc

  /**指定したタイムゾーンにおいて、このインスタンスが表現する瞬間と指定した瞬間`other`の年月日が等価であるかを調べる。
   *
   * @param other 対象瞬間
   * @param zone タイムゾーン
   * @return 等価である場合は`true`、そうでない場合は`false`
   */
  def isSameDayAs(other: TimePoint, zone: TimeZone) = calendarDate(zone) == other.calendarDate(zone)

  /**この日時の、指定した時間の長さ分過去の日時を取得する。
   *
   * @param duration 時間の長さ
   * @return 過去の日時
   */
  def minus(duration: Duration) = duration.subtractedFrom(this)

  def -(duration: Duration) = minus(duration)

  /**
   * このオブジェクトが表現する瞬間の、ちょうど1日後を取得する。
   *
   * 日内の時間は変化しない。
   *
   * @return 1日後
   */
  def nextDay = this.+(Duration.days(1))


  /**
   * この日時から、指定した時間の長さ分未来の日時を取得する。
   *
   * @param duration 時間の長さ
   * @return 未来の日時
   */
  def +(duration: Duration) = duration.addedTo(this)


  /**
   * この瞬間の文字列表現を取得する。
   *
   * @see java.lang.Object#toString()
   */
  override def toString = asJavaUtilDate.toString


  /**
   * この瞬間を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern [[java.text.SimpleDateFormat]]に基づくパターン
   * @param zone タイムゾーン
   * @return 整形済み時間文字列
   */
  def toString(pattern: String, zone: TimeZone) = {
    val format = new SimpleDateFormat(pattern)
    format.setTimeZone(zone)
    format.format(asJavaUtilDate)
  }

  /**
   * このインスタンスがあらわす瞬間を開始瞬間、`end`を終了瞬間とする、期間を返す。
   *
   * 生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を生成する。
   *
   * @param end 終了日時（上側限界値）. `LimitValue[TimePoint]`の場合は、限界がないことを表す
   * @return [[jp.tricreo.baseunits.scala.time.TimeInterval]]
   */
  def until(end: LimitValue[TimePoint]) = TimeInterval.over(Limit(this), end)


}

/**`TimePoint`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object TimePoint {

  val GMT = TimeZone.getTimeZone("Universal")

  /**インスタンスを生成する。
   *
   * @param milliseconds エポックからの経過ミリ秒
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def apply(milliseconds: Long): TimePoint = from(milliseconds)

  /**抽出子メソッド。
   *
   * @param [[jp.tricreo.baseunits.scala.time.TimePoint]]
   * @return `Option[(Long)]`
   */
  def unapply(timePoint: TimePoint) = Some(timePoint.millisecondsFromEpoc)

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param yearMonth 年月
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def at(yearMonth: CalendarMonth, date: DayOfMonth, hour: Int,
         minute: Int, second: Int, millisecond: Int, zone: TimeZone): TimePoint = {
    at(yearMonth.breachEncapsulationOfYear,
      yearMonth.breachEncapsulationOfMonth.value,
      date.value, hour, minute, second, millisecond, zone)
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, millisecond: Int, zone: TimeZone): TimePoint = {
    val calendar = Calendar.getInstance(zone)
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DATE, date)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, second)
    calendar.set(Calendar.MILLISECOND, millisecond)
    from(calendar.getTime.getTime)
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, zone: TimeZone): TimePoint = {
    at(year, month, date, hour, minute, second, 0, zone)
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, zone: TimeZone): TimePoint =
    at(year, month, date, hour, minute, 0, 0, zone)

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def at(year: Int, month: MonthOfYear, date: DayOfMonth, hour: Int, minute: Int, second: Int,
         millisecond: Int, zone: TimeZone): TimePoint =
    at(year, month.value, date.value, hour, minute, second, millisecond, zone)

  /**世界標準時における、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param amPm 午前午後を表す文字列("AM", "PM"など)
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   * @throws IllegalArgumentException 引数`hour`の値が0〜11の範囲ではない場合もしくは、
   *  引数`amPm`の値が `"AM"` または `"PM"` ではない場合
   */
  def at12hr(year: Int, month: Int, date: Int, hour: Int, amPm: String, minute: Int, second: Int,
             millisecond: Int) = {
    at(year, month, date, HourOfDay.convertTo24hour(hour, amPm), minute, second, millisecond, GMT)
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param amPm 午前午後を表す文字列("AM", "PM"など)
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   * @throws IllegalArgumentException 引数`hour`の値が0〜11の範囲ではない場合もしくは、
   * 引数`amPm`の値が `"AM"` または `"PM"` ではない場合もしく
   */
  def at12hr(year: Int, month: Int, date: Int, hour: Int, amPm: String, minute: Int, second: Int,
             millisecond: Int, zone: TimeZone): TimePoint =
    at(year, month, date, HourOfDay.convertTo24hour(hour, amPm), minute, second, millisecond, zone)

  /**世界標準時における、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def atGMT(year: Int, month: Int, date: Int, hour: Int, minute: Int): TimePoint =
    atGMT(year, month, date, hour, minute, 0, 0)

  /**世界標準時における、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def atGMT(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int): TimePoint =
    atGMT(year, month, date, hour, minute, second, 0)

  /**世界標準時における、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def atGMT(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, millisecond: Int): TimePoint =
    at(year, month, date, hour, minute, second, millisecond, GMT)

  /**指定したタイムゾーンにおける、指定した日時の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param calendarDate 日付
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def atMidnight(calendarDate: CalendarDate, zone: TimeZone): TimePoint =
    at(calendarDate.asCalendarMonth,
      calendarDate.breachEncapsulationOfDay, 0, 0, 0, 0, zone)

  /**指定したタイムゾーンにおける、指定した日付の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def atMidnight(year: Int, month: Int, date: Int, zone: TimeZone): TimePoint =
    at(year, month, date, 0, 0, 0, 0, zone)

  /**世界標準時における、指定した日付の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def atMidnightGMT(year: Int, month: Int, date: Int): TimePoint =
    atMidnight(year, month, date, GMT)

  /**[[java.util.Calendar]]を[[jp.tricreo.baseunits.scala.time.TimePoint]]に変換する。
   *
   * @param calendar 元となる日時情報を表す [[java.util.Calendar]]インスタンス
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def from(calendar: Calendar): TimePoint =
    from(calendar.getTime)

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param date 日付
   * @param time 時間
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def from(date: CalendarDate, time: TimeOfDay, zone: TimeZone): TimePoint =
    at(date.asCalendarMonth, date.breachEncapsulationOfDay,
      time.breachEncapsulationOfHour.value, time.breachEncapsulationOfMinute.value,
      0, 0, zone)

  /**[[java.utilDate]]を[[jp.tricreo.baseunits.scala.time.TimePoint]]に変換する。
   *
   * @param javaDate 元となる日時情報を表す [[java.util.Date]]インスタンス
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def from(javaDate: JDate): TimePoint =
    from(javaDate.getTime)

  /**エポックからの経過ミリ秒を [[jp.tricreo.baseunits.scala.time.TimePoint]] に変換する。
   *
   * @param milliseconds エポックからの経過ミリ秒
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def from(milliseconds: Long): TimePoint = {
    val result = new TimePoint(milliseconds)
    //assert FAR_FUTURE == null || result.isBefore(FAR_FUTURE);
    //assert FAR_PAST == null || result.isAfter(FAR_PAST);
    result
  }

  /**日時を表す文字列を、指定したパターンで指定したタイムゾーンとして解析し、その日時を表す
   * [[jp.tricreo.baseunits.scala.time.TimePoint]]を返す。
   *
   * @param dateTimeString 日時を表す文字列
   * @param pattern 解析パターン
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  def parse(dateTimeString: String, pattern: String, timeZone: TimeZone) = {
    val sdf = new SimpleDateFormat(pattern)
    sdf.setTimeZone(timeZone)
    val date = sdf.parse(dateTimeString)
    from(date)
  }

  /**日時を表す文字列を、指定したパターンで世界標準時として解析し、その日時を表す
   * [[jp.tricreo.baseunits.scala.time.TimePoint]]を返す。
   *
   * @param dateString 日時を表す文字列
   * @param pattern 解析パターン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  def parseGMTFrom(dateTimeString: String, pattern: String) =
    parse(dateTimeString, pattern, GMT)

}