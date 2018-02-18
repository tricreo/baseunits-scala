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
import java.time.{YearMonth, ZoneId, ZonedDateTime}
import java.util.{Calendar, TimeZone}

/**
  * カレンダー上の特定の「年月」を表すクラス。
  *
  * [[java.util.Date]]と異なり、月未満（日以下）の概念を持っていない。また、
  * [[org.sisioh.baseunits.scala.time.TimePoint]]と異なり、
  * その月1ヶ月間全ての範囲を表すクラスであり、特定の瞬間をモデリングしたものではない。
  *
  * @author j5ik2o
  */
class CalendarYearMonth private[time] (
    val year: Int,
    val month: MonthOfYear,
    val zoneId: ZoneId
) extends Ordered[CalendarYearMonth]
    with Serializable {

  lazy val asYearMonth: YearMonth = YearMonth.of(year, month.value)

  @deprecated("Use zoneId property instead", "0.1.18")
  val timeZone = TimeZone.getTimeZone(zoneId)

  /**
    * このインスタンスが表現する年月の1日からその月末までの、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * @return このインスタンスが表現する年月の1日からその月末までを表現する期間
    */
  lazy val asCalendarInterval: CalendarInterval =
    CalendarInterval.month(year, month, zoneId)

  /**
    * このインスタンスが表現する年月を含む年の元旦からその大晦日までの、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * @return このインスタンスが表現する年月の1日からその月末までを表現する期間
    */
  lazy val asYearInterval: CalendarInterval =
    CalendarInterval.year(year, zoneId)

  /**
    * 月末の日付を取得する。
    *
    * @return [[org.sisioh.baseunits.scala.time.DayOfMonth]]
    */
  lazy val lastDay =
    CalendarDate.from(year, month, lastDayOfMonth, zoneId)

  /**
    * 月末の日を取得する。
    *
    * @return [[org.sisioh.baseunits.scala.time.DayOfMonth]]
    */
  lazy val lastDayOfMonth: DayOfMonth = month.getLastDayOfThisMonth(year)

  /**
    * このインスタンスが表現する年月の翌月を返す。
    *
    * @return 翌月
    */
  lazy val nextMonth: CalendarYearMonth = plusMonths(1)

  /**
    * このインスタンスが表現する年月の前月を返す。
    *
    * @return 前月
    */
  lazy val previousDay: CalendarYearMonth = plusMonths(-1)

  @deprecated("Use asJavaZonedDateTimeOnMidnight property instead", "0.1.18")
  lazy val asJavaCalendarOnMidnight: Calendar =
    asJavaCalendarOnMidnight(TimeZone.getTimeZone(zoneId))

  lazy val asJavaZonedDateTimeOnMidnight: ZonedDateTime =
    asJavaZonedDateTimeOnMidnight(zoneId)

  /**
    * このオブジェクトの`month`フィールド（月）を返す。
    *
    * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
    *
    * @return 月
    */
  @deprecated("Use month property instead", "0.1.18")
  val breachEncapsulationOfMonth = month

  /**
    * このオブジェクトの`year`フィールド（西暦年をあらわす数）を返す。
    *
    * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
    *
    * @return 西暦年をあらわす数
    */
  @deprecated("Use year property instead", "0.1.18")
  val breachEncapsulationOfYear = year

  /**
    * このインスタンスが表す年月で、引数`day`で表す日を表す年月日を返す。
    *
    * @param day 日（1〜31）
    * @return 日時
    * @throws IllegalArgumentException 引数`day`がこの月に存在しない場合
    */
  def atCalendarDate(day: DayOfMonth): CalendarDate =
    CalendarDate.from(year, month, day, zoneId)

  /**
    * 年月日同士の比較を行う。
    *
    * 相対的に過去である方を「小さい」と判断する。
    *
    * @param other 比較対象
    * @return `java.util.Comparable` compareTo(Object)に準じる
    */
  override def compare(other: CalendarYearMonth): Int =
    if (isBefore(other)) {
      -1
    } else if (isAfter(other)) {
      1
    } else 0

  /**
    * 指定した日 `other`が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
    *
    * お互いが同一日時である場合は `false` を返す。
    *
    * @param other 対象日時
    * @return 過去である場合は`true`、そうでない場合は`false`
    */
  def isAfter(other: CalendarYearMonth): Boolean =
    !isBefore(other) && !equals(other)

  override def equals(obj: Any): Boolean = obj match {
    case that: CalendarYearMonth =>
      this.month == that.month && this.year == that.year
    case _ => false
  }

  /**
    * 指定した年月 `other`が、このオブジェクトが表現する年月よりも未来であるかどうかを検証する。
    *
    * お互いが同一日時である場合は `false` を返す。
    *
    * @param other 対象年月
    * @return 未来である場合は`true`、そうでない場合は`false`
    */
  def isBefore(other: CalendarYearMonth): Boolean =
    if (year < other.year) {
      true
    } else if (year > other.year) {
      false
    } else month.isBefore(other.month)

  override def hashCode: Int = 31 * (month.hashCode + year.hashCode)

  /**
    * このオブジェクトが表現する日付に、指定した長さの時間を加えた、未来の日付を取得する。
    *
    * 引数の長さの単位が "月" 未満である場合は、元の年月をそのまま返す。
    *
    * @param length 時間の長さ
    * @return 未来の年月
    */
  def plus(length: Duration): CalendarYearMonth =
    length.addedTo(this, zoneId)

  /**
    * このインスタンスが表現する年月の `increment` ヶ月後を返す。
    *
    * `increment`に負数を与えてもよい。
    *
    * @param increment 加える月数
    * @return 計算結果
    */
  def plusMonths(increment: Int): CalendarYearMonth = {
    val zonedDateTime    = asJavaZonedDateTimeOnMidnight
    val newZonedDateTime = zonedDateTime.plusMonths(increment)
    val year             = newZonedDateTime.getYear
    val month            = newZonedDateTime.getMonthValue
    CalendarYearMonth.from(year, month, zoneId)
  }

  @deprecated("Use asJavaZonedDateTimeOnMidnight method instead", "0.1.18")
  def asJavaCalendarOnMidnight(timeZone: TimeZone): Calendar = {
    val calendar = Calendar.getInstance(timeZone)
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month.calendarValue)
    calendar.set(Calendar.DATE, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar
  }

  def asJavaZonedDateTimeOnMidnight(zoneId: ZoneId): ZonedDateTime = {
    ZonedDateTime.of(year, month.value, 1, 0, 0, 0, 0, zoneId)
  }

  /**
    * この日付の文字列表現を取得する。
    *
    * [[java.text.SimpleDateFormat]]の使用に基づく `"yyyy/MM"` のパターンで整形する。
    *
    * @see java.lang.Object#toString()
    */
  override def toString: String = toString("yyyy/MM")

  /**
    * この日付を、指定したパターンで整形し、その文字列表現を取得する。
    *
    * @param pattern パターン
    * @return 文字列表現
    */
  def toString(pattern: String): String = {
    // Any timezone works, as long as the same one is used throughout.
    val point = asTimePoint(zoneId)
    point.toString(pattern, zoneId)
  }

  /**
    * 指定したタイムゾーンにおける、このインスタンスが表す「年月」の1日0時0分0秒0ミリ秒の瞬間について
    * [[org.sisioh.baseunits.scala.time.TimePoint]] 型のインスタンスを返す。
    *
    * @param timeZone タイムゾーン
    * @return [[org.sisioh.baseunits.scala.time.TimePoint]]
    */
  def asTimePoint(timeZone: TimeZone): TimePoint =
    asTimePoint(timeZone.toZoneId)

  def asTimePoint(zoneId: ZoneId = this.zoneId): TimePoint =
    TimePoint.at(year, month, DayOfMonth(1), 0, 0, 0, 0, zoneId)

}

/**
  * `CalendarMonth`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object CalendarYearMonth {

  /**
    * インスタンスを生成する。
    *
    * @param year  年
    * @param month 月
    * @return [[org.sisioh.baseunits.scala.time.CalendarYearMonth]]
    */
  @deprecated("Use apply(year: Int, month: Int, zoneId: ZoneId) method instead", "0.1.18")
  def apply(year: Int, month: Int, timeZone: TimeZone): CalendarYearMonth =
    from(year, month, timeZone.toZoneId)

  def apply(year: Int, month: Int, zoneId: ZoneId = ZoneIds.Default): CalendarYearMonth =
    from(year, month, zoneId)

  /**
    * 指定した年月を表す、[[CalendarYearMonth]]のインスタンスを生成する。
    *
    * @param year     西暦年をあらわす数
    * @param month    月をあらわす正数（1〜12）
    * @param timeZone タイムゾーン
    * @return [[CalendarDate]]
    * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合
    */
  @deprecated("Use from(year: Int, month: Int, zoneId: ZoneId) method instead", "0.1.18")
  def from(year: Int, month: Int, timeZone: TimeZone): CalendarYearMonth =
    new CalendarYearMonth(year, MonthOfYear(month), timeZone.toZoneId)

  def from(year: Int, month: Int, zoneId: ZoneId): CalendarYearMonth =
    new CalendarYearMonth(year, MonthOfYear(month), zoneId)

  /**
    * 抽出子メソッド。
    *
    * @param calendarMonth [[org.sisioh.baseunits.scala.time.CalendarYearMonth]]
    * @return `Option[(Int, Int)]`
    */
  def unapply(calendarMonth: CalendarYearMonth): Option[(Int, MonthOfYear)] =
    Some(calendarMonth.year, calendarMonth.month)

  /**
    * 指定した年月を表す、[[CalendarYearMonth]]のインスタンスを生成する。
    *
    * @param year  西暦年をあらわす数
    * @param month 月をあらわす正数（1〜12）
    * @return [[CalendarDate]]
    * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合
    */
  def from(year: Int, month: Int): CalendarYearMonth =
    from(year, month, ZoneIds.Default)

  /**
    * 指定した年月を表す、[[CalendarYearMonth]]のインスタンスを生成する。
    *
    * @param year  年
    * @param month 月
    * @return [[CalendarYearMonth]]
    */
  def from(year: Int, month: MonthOfYear): CalendarYearMonth =
    from(year, month, ZoneIds.Default)

  /**
    * 指定した年月を表す、[[CalendarYearMonth]] のインスタンスを生成する。
    *
    * @param year     年
    * @param month    月
    * @param timeZone タイムゾーン
    * @return [[CalendarYearMonth]]
    */
  @deprecated("Use from(year: Int, month: MonthOfYear, zoneId: ZoneId) method instead", "0.1.18")
  def from(year: Int, month: MonthOfYear, timeZone: TimeZone): CalendarYearMonth =
    new CalendarYearMonth(year, month, timeZone.toZoneId)

  def from(year: Int, month: MonthOfYear, zoneId: ZoneId): CalendarYearMonth =
    new CalendarYearMonth(year, month, zoneId)

  /**
    * 指定した年月を表す、[[CalendarYearMonth]] のインスタンスを生成する。
    *
    * @param dateString 年月を表す文字列
    * @param pattern    解析パターン文字列
    * @return [[CalendarYearMonth]]
    * @throws ParseException 文字列の解析に失敗した場合
    */
  @deprecated("Use parse(dateString: String, pattern: String, zoneId: ZoneId) method instead",
              "0.1.18")
  def parse(dateString: String, pattern: String, timeZone: TimeZone): CalendarYearMonth = {
    //Any timezone works, as long as the same one is used throughout.
    val point = TimePoint.parse(dateString, pattern, timeZone)
    CalendarYearMonth.from(point, timeZone.toZoneId)
  }

  def parse(dateString: String,
            pattern: String,
            zoneId: ZoneId = ZoneIds.Default): CalendarYearMonth = {
    //Any timezone works, as long as the same one is used throughout.
    val point = TimePoint.parse(dateString, pattern, zoneId)
    CalendarYearMonth.from(point, zoneId)
  }

  /**
    * 指定したタイムゾーン上で指定した瞬間が属する日付を元に、
    * [[CalendarDate]]のインスタンスを生成する。
    *
    * @param timePoint 瞬間
    * @return [[CalendarDate]]
    */
  @deprecated("Use from(timePoint: TimePoint, zoneId: ZoneId) method instead", "0.1.18")
  def from(timePoint: TimePoint, timeZone: TimeZone): CalendarYearMonth =
    from(timePoint, timeZone.toZoneId)

  def from(timePoint: TimePoint, zoneId: ZoneId = ZoneIds.Default): CalendarYearMonth = {
    val zonedDateTime = timePoint.asJavaZonedDateTime(zoneId)
    CalendarYearMonth.from(zonedDateTime)
  }

  @deprecated("Use from(zonedDateTime: ZonedDateTime) method instead", "0.1.18")
  private[time] def from(calendar: Calendar): CalendarYearMonth = {
    // CHECKSTYLE IGNORE THIS LINE
    // Use timezone already set in calendar.
    val year  = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // T&M Lib counts January as 1
    CalendarYearMonth.from(year, month, calendar.getTimeZone)
  }

  private[time] def from(zonedDateTime: ZonedDateTime): CalendarYearMonth = {
    // CHECKSTYLE IGNORE THIS LINE
    // Use timezone already set in calendar.
    val year  = zonedDateTime.getYear
    val month = zonedDateTime.getMonthValue
    CalendarYearMonth.from(year, month, zonedDateTime.getZone)
  }

}
