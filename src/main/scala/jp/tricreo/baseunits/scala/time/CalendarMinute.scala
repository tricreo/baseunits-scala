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

import java.util.TimeZone

/**カレンダー上の特定の「年月日時分」を表すクラス。
 *
 * [[java.util.Date]]と異なり、分未満（秒以下）の概念を持っていない。また、
 * [[jp.tricreo.baseunits.scala.time.TimePoint]]と異なり、
 * その分1分間全ての範囲を表すクラスであり、特定の瞬間をモデリングしたものではない。
 *
 * @author j5ik2o
 * @param date 年月日
 * @param time 時分
 */
class CalendarMinute private[time]
(private[time] val date: CalendarDate,
 private[time] val time: TimeOfDay)
  extends Ordered[CalendarMinute] with Serializable{


  /**指定したタイムゾーンにおける、このインスタンスが表す「年月日時分」の0秒0ミリ秒の瞬間について
   * [[jp.tricreo.baseunits.scala.time.TimePoint]] 型のインスタンスを返す。
   *
   * @param timeZone タイムゾーン
   * @return [[jp.tricreo.baseunits.scala.time.TimePoint]]
   */
  def asTimePoint(timeZone: TimeZone): TimePoint =
    TimePoint.from(date, time, timeZone)

  /**このオブジェクトの`date`フィールド（年月日）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 年月日
   */
  def breachEncapsulationOfDate = date

  /**このオブジェクトの`time`フィールド（時分）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 時分
   */
  def breachEncapsulationOfTime = time

  def compare(other: CalendarMinute): Int = {
    val dateComparance = date.compareTo(other.date)
    if (dateComparance != 0) {
      dateComparance
    } else {
      time.compareTo(other.time)
    }
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: CalendarMinute => date == that.date && time == that.time
    case _ => false
  }

  override def hashCode = date.hashCode + time.hashCode

  /**指定した年月日時分 `other` が、このオブジェクトが表現する年月日時分よりも過去であるかどうかを検証する。
   *
   * お互いが同一日時である場合は `false` を返す。
   *
   * @param other 対象年月日時分
   * @return 過去である場合は`true`、そうでない場合は`false`
   */
  def isAfter(other: CalendarMinute): Boolean = {
    isBefore(other) == false && equals(other) == false
  }

  /**指定した年月日時分 `other` が、このオブジェクトが表現する年月日時分よりも未来であるかどうかを検証する。
   *
   * お互いが同一日時である場合は `false` を返す。
   *
   * @param other 対象年月日時分
   * @return 未来である場合は`true`、そうでない場合は`false`
   */
  def isBefore(other: CalendarMinute) = {
    if (date.isBefore(other.date)) {
      true
    } else if (date.isAfter(other.date)) {
      false
    } else {
      time.isBefore(other.time)
    }
  }

  override def toString = {
    date.toString + " at " + time.toString
  }

  /**この年月日時分を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern [[java.text.SimpleDateFormat]]に基づくパターン
   * @param zone タイムゾーン
   * @return 整形済み時間文字列
   */
  def toString(pattern: String, zone: TimeZone) = {
    val point = asTimePoint(zone)
    point.toString(pattern, zone)
  }

}


/**コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object CalendarMinute {

  /**インスタンスを生成する。
   *
   * @param aDate [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   * @param aTime [[jp.tricreo.baseunits.scala.time.TimeOfDay]]
   * @return [[jp.tricreo.baseunits.scala.time.CalendarMinute]]
   */
  def apply(aDate: CalendarDate, aTime: TimeOfDay) = from(aDate, aTime)

  /**抽出子メソッド。
   *
   * @param calendarMinute [[jp.tricreo.baseunits.scala.time.CalendarMinute]]
   * @return `Option[(CalendarDate,TimeOfDay)]`
   */
  def unapply(calendarMinute:CalendarMinute) =
    Some(calendarMinute.date, calendarMinute.time)


  /**指定した年月日を時分表す、[[jp.tricreo.baseunits.scala.time.CalendarMinute]]のインスタンスを生成する。
   *
   * @param aDate 年月日
   * @param aTime 時分
   * @return [[jp.tricreo.baseunits.scala.time.CalendarMinute]]
   */
  def from(aDate: CalendarDate, aTime: TimeOfDay): CalendarMinute = new CalendarMinute(aDate, aTime)

  /**指定した年月日を時分表す、[[jp.tricreo.baseunits.scala.time.CalendarMinute]]のインスタンスを生成する。
   *
   * @param year 西暦年をあらわす数
   * @param month 月をあらわす正数（1〜12）
   * @param day 日をあらわす正数（1〜31）
   * @param hour 時をあらわす正数（0〜23）
   * @param minute 分をあらわす正数（0〜59）
   * @return [[jp.tricreo.baseunits.scala.time.CalendarMinute]]
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   * 引数`day`が1〜31の範囲ではない場合もしくは、引数`hour`が0〜23の範囲ではない場合もしくは、
   * 引数`minute`が0〜59の範囲ではない場合もしくは、引数`day`が`yearMonth`の月に存在しない場合
   */
  def from(year: Int, month: Int, day: Int, hour: Int, minute: Int): CalendarMinute =
    new CalendarMinute(CalendarDate.from(year, month, day), TimeOfDay.from(hour, minute))

  /**指定した年月日時分を表す、[[jp.tricreo.baseunits.scala.time.CalendarDate]]のインスタンスを生成する。
   *
   * @param dateTimeString 年月日時分を表す文字列
   * @param pattern 解析パターン文字列
   * @return [[jp.tricreo.baseunits.scala.time.CalendarMinute]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  def parse(dateTimeString: String, pattern: String): CalendarMinute = {
    val arbitraryZone = TimeZone.getTimeZone("Universal")
    //Any timezone works, as long as the same one is used throughout.
    val point = TimePoint.parse(dateTimeString, pattern, arbitraryZone)
    CalendarMinute.from(point.calendarDate(arbitraryZone), point.asTimeOfDay(arbitraryZone))
  }
}