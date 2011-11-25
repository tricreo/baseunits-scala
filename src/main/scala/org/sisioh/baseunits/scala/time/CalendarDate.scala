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

import java.util.{Calendar, TimeZone}
import jp.tricreo.baseunits.scala.intervals.Limit

/**カレンダー上の特定の「年月日」を表すクラス。
 *
 * [[java.util.Date]]と異なり、時間の概念を持っていない。また、
 * [[jp.tricreo.baseunits.scala.time.TimePoint]]と異なり、
 * その日1日間全ての範囲を表すクラスであり、特定の瞬間をモデリングしたものではない。
 *
 * @author j5ik2o
 * @param yearMonth 年月
 * @param day 日
 */
class CalendarDate private[time]
(private[time] val yearMonth: CalendarMonth,
 private[time] val day: DayOfMonth)
  extends Ordered[CalendarDate] with Serializable {

  /**年月日同士の比較を行う。
   *
   * 相対的に過去である方を「小さい」と判断する。
   *
   * @param other 比較対象
   * @return [[java.util.Comparable#compareTo(Object)]]に準じる
   */
  def compare(other: CalendarDate): Int = {
    if (isBefore(other)) {
      -1
    } else if (isAfter(other)) {
      1
    } else 0
  }

  /**このインスタンスが表現する日を含む年月を表す[[jp.tricreo.baseunits.scala.time.CalendarMonth]]を取得する。
   *
   * @return このインスタンスが表現する日を含む年月を表す期間
   */
  def asCalendarMonth = yearMonth

  /**
   * このインスタンスが表現する日を含む年月を表す期間を取得する。
   *
   * @return このインスタンスが表現する日を含む年月を表す期間
   */
  def asMonthInterval =
    CalendarInterval.month(yearMonth)

  /**このインスタンスが表現する日の午前0時から丸一日を期間として取得する。
   *
   * 生成する期間の開始日時は区間に含み（閉じている）、終了日時は区間に含まない（開いている）半開区間を生成する。
   *
   * @param zone タイムゾーン
   * @return このインスタンスが表現する日の午前0時から丸一日を表現する期間
   */
  def asTimeInterval(zone: TimeZone) =
    TimeInterval.startingFrom(Limit(startAsTimePoint(zone)), true, Duration.days(1), false)

  /**このインスタンスが表現する日を含む年を表す期間を取得する。
   *
   * @return このインスタンスが表現する日を含む年を表す期間
   */
  def asYearInterval =
    CalendarInterval.year(yearMonth.breachEncapsulationOfYear)

  /**このインスタンスが表す日付で、引数`timeOfDay`で表す時を表す日時を返す。
   *
   * @param timeOfDay 時
   * @return 日時
   */
  def at(timeOfDay: TimeOfDay) =
    CalendarMinute.from(this, timeOfDay)

  /**このオブジェクトの`day`フィールド（日）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 日
   */
  def breachEncapsulationOfDay = day

  /**このオブジェクトの`yearMonth`フィールド（年月）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 年月
   */
  def breachEncapsulationOfYearMonth = yearMonth


  /**この日付の曜日を返す。
   *
   * @return 曜日
   */
  def dayOfWeek = {
    val calendar = asJavaCalendarUniversalZoneMidnight
    DayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
  }


  override def equals(obj: Any) = obj match {
    case that: CalendarDate => this.day == that.day && this.yearMonth == that.yearMonth
    case _ => false
  }

  override def hashCode = day.hashCode + yearMonth.hashCode

  /**指定した日 `other` が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
   *
   * お互いが同一日時である場合は `false` を返す。
   *
   * @param other 対象日時
   * @return 過去である場合は`true`、そうでない場合は`false`
   */
  def isAfter(other: CalendarDate) =
    isBefore(other) == false && equals(other) == false

  /**指定した日 `other` が、このオブジェクトが表現する日よりも未来であるかどうかを検証する。
   *
   * お互いが同一日時である場合は `false` を返す。
   *
   * @param other 対象日時
   * @return 未来である場合は`true`、そうでない場合は`false`
   */
  def isBefore(other: CalendarDate) =
    if (yearMonth.isBefore(other.yearMonth)) {
      true
    } else if (yearMonth.isAfter(other.yearMonth)) {
      false
    } else day.isBefore(other.day)

  /**このインスタンスが表現する日の翌日を返す。
   *
   * @return 翌日
   */
  def nextDay = plusDays(1)


  /**このオブジェクトが表現する日付に、指定した長さの時間を加えた、未来の日付を取得する。
   *
   * 引数の長さの単位が "日" 未満である場合は、元の日付をそのまま返す。
   *
   * @param length 時間の長さ
   * @return 未来の日付
   */
  def plus(length: Duration) = length.addedTo(this)

  /**このインスタンスが表現する日の `increment` 日後を返す。
   *
   *  `increment`に負数を与えてもよい。
   *
   * @param increment 加える日数
   * @return 計算結果
   */
  def plusDays(increment: Int) = {
    val calendar = asJavaCalendarUniversalZoneMidnight
    calendar.add(Calendar.DATE, increment)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DATE)
    CalendarDate.from(year, month, day)
  }

  /**このインスタンスが表現する日の `increment` ヶ月後を返す。
   *
   *  `increment`に負数を与えてもよい。
   *
   * @param increment 加える月数
   * @return 計算結果
   */
  def plusMonths(increment: Int) = {
    val calendar = asJavaCalendarUniversalZoneMidnight
    calendar.add(Calendar.MONTH, increment)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DATE)
    CalendarDate.from(year, month, day)
  }

  /**このインスタンスが表現する日の前日を返す。
   *
   * @return 前日
   */
  def previousDay = plusDays(-1)

  /**このインスタンスが表現する日付の午前0時を、日時として取得する。
   *
   * @param zone タイムゾーン
   * @return このインスタンスが表現する日の午前0時を表現する日時
   */
  def startAsTimePoint(zone: TimeZone) = TimePoint.atMidnight(this, zone)

  /**このインスタンスが表現する日付を開始日とし、指定した日付 `otherDate` を終了日とする期間を取得する。
   *
   * @param otherDate 終了日
   * @return 期間
   */
  def through(otherDate: CalendarDate) =
    CalendarInterval.inclusive(Limit(this), Limit(otherDate))

  /**この日付の文字列表現を取得する。
   *
   * [[java.text.SimpleDateFormat]]の使用に基づく `"yyyy-MM-dd"`のパターンで整形する。
   *
   * @see java.lang.Object#toString()
   */
  override def toString =
    toString("yyyy-MM-dd")

  //default for console

  /**この日付を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern [[java.text.SimpleDateFormat]]に基づくパターン
   * @return 整形済み時間文字列
   */
  def toString(pattern: String) = {
    // Any timezone works, as long as the same one is used throughout.
    val arbitraryZone = TimeZone.getTimeZone("Universal")
    val point = startAsTimePoint(arbitraryZone)
    point.toString(pattern, arbitraryZone)
  }

  private[time] def asJavaCalendarUniversalZoneMidnight = {
    val zone = TimeZone.getTimeZone("Universal")
    val calendar = Calendar.getInstance(zone)
    calendar.set(Calendar.YEAR, yearMonth.breachEncapsulationOfYear)
    calendar.set(Calendar.MONTH, yearMonth.breachEncapsulationOfMonth.value - 1)
    calendar.set(Calendar.DATE, day.value)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar
  }
}

/**`CalendarDate`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object CalendarDate {

  /**インスタンスを生成する。
   *
   * @param yearMonth 年月
   * @param day 日
   * @return [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   */
  def apply(yearMonth: CalendarMonth, day: DayOfMonth) =
    from(yearMonth, day)

  /**抽出子メソッド。
   *
   * @param [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   * @return `Option[(CalendarMonth, DayOfMonth)]`
   */
  def unapply(calendarDate: CalendarDate) =
    Some(calendarDate.yearMonth, calendarDate.day)

  /**指定した年月日を表す、[[jp.tricreo.baseunits.scala.time.CalendarDate]]のインスタンスを生成する。
   *
   * @param yearMonth 年月
   * @param day 日
   * @return [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   * @throws IllegalArgumentException 引数`day`が`yearMonth`の月に存在しない場合
   */
  def from(yearMonth: CalendarMonth, day: DayOfMonth): CalendarDate =
    new CalendarDate(yearMonth, day)

  /**指定した年月日を表す、[[jp.tricreo.baseunits.scala.time.CalendarDate]]のインスタンスを生成する。
   *
   * @param year 西暦年をあらわす数
   * @param month 月をあらわす正数（1〜12）
   * @param day 日をあらわす正数（1〜31）
   * @return [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   * 引数`day`が1〜31の範囲ではない場合もしくは、引数`day`が`yearMonth`の月に存在しない場合
   */
  def from(year: Int, month: Int, day: Int): CalendarDate =
    new CalendarDate(CalendarMonth.from(year, month), DayOfMonth(day))

  /**指定した年月日を表す、[[jp.tricreo.baseunits.scala.time.CalendarDate]]のインスタンスを生成する。
   *
   * @param year 年
   * @param month 月
   * @param day 日
   * @return [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   * @throws IllegalArgumentException 引数`day`が`year`年の`month`の月に存在しない場合
   */
  def from(year: Int, month: MonthOfYear, day: DayOfMonth): CalendarDate =
    from(CalendarMonth.from(year, month), day)

  /**指定したタイムゾーン上で指定した瞬間が属する日付を元に、[[jp.tricreo.baseunits.scala.time.CalendarDate]]のインスタンスを生成する。
   *
   * @param timePoint 瞬間
   * @param zone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   */
  def from(timePoint: TimePoint, zone: TimeZone): CalendarDate = {
    val calendar = timePoint.asJavaCalendar
    calendar.setTimeZone(zone)
    from(calendar)
  }

  /**指定した年月日を表す、[[jp.tricreo.baseunits.scala.time.CalendarDate]]のインスタンスを生成する。
   *
   * @param dateString 年月日を表す文字列
   * @param pattern 解析パターン文字列
   * @return [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  def parse(dateString: String, pattern: String) = {
    val arbitraryZone = TimeZone.getTimeZone("Universal")
    // Any timezone works, as long as the same one is used throughout.
    val point = TimePoint.parse(dateString, pattern, arbitraryZone)
    CalendarDate.from(point, arbitraryZone)
  }

  private[time] def from(calendar: Calendar): CalendarDate = {
    // Use timezone already set in calendar.
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // T&M Lib counts January as 1
    val date = calendar.get(Calendar.DATE)
    from(year, month, date)
  }

}