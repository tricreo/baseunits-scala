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

import java.util.Calendar
import org.sisioh.baseunits.scala.util.Ratio
import org.sisioh.baseunits.scala.intervals.{Limit, LimitValue}

/**時間量（時間の長さ・期間の長さなど）を表すクラス。
 *
 * 負の時間量は表現しない。
 *
 * @author j5ik2o
 * @param quantity 時間の長さ
 * @param unit 時間の単位
 */
class Duration
(val quantity: Long,
 val unit: TimeUnit)
  extends Ordered[Duration] with Serializable {

  require(quantity >= 0, "Quantity: " + quantity + " must be zero or positive")


  /**指定した日付に、このオブジェクトが表現する長さの時間を加えた、未来の日付を取得する。
   *
   * このオブジェクトが表現する時間の長さの単位が 日 未満である場合は、元の日付をそのまま返す。
   *
   * @param day 元となる日付
   * @return このオブジェクトが表現する長さの時間が経過した未来の日付
   */
  def addedTo(day: CalendarDate) = {
    //		only valid for days and larger units
    if (unit.compareTo(TimeUnit.day) < 0) {
      day
    } else {
      val calendar = day.asJavaCalendarUniversalZoneMidnight
      if (unit == TimeUnit.day) {
        calendar.add(Calendar.DATE, quantity.asInstanceOf[Int])
      } else {
        addAmountToCalendar(inBaseUnits, calendar)
      }
      CalendarDate.from(calendar)
    }
  }

  /**指定した年月に、このオブジェクトが表現する長さの時間を加えた、未来の年月を取得する。
   *
   * このオブジェクトが表現する時間の長さの単位が 月 未満である場合は、元の年月をそのまま返す。
   *
   * @param month 元となる年月
   * @return このオブジェクトが表現する長さの時間が経過した未来の年月
   */
  def addedTo(month: CalendarMonth) = {
    //		only valid for days and larger units
    if (unit.compareTo(TimeUnit.month) < 0) {
      month
    } else {
      val calendar = month.asJavaCalendarUniversalZoneMidnight
      if (unit == TimeUnit.month) {
        calendar.add(Calendar.MONTH, quantity.asInstanceOf[Int])
      } else {
        addAmountToCalendar(inBaseUnits, calendar)
      }
      CalendarMonth.from(calendar)
    }
  }

  /**指定した日時に、このオブジェクトが表現する長さの時間を加えた、未来の日時を取得する。
   *
   * @param point 元となる日時
   * @return このオブジェクトが表現する長さの時間が経過した未来の日時
   * @see addAmountToTimePoint(long, TimePoint)
   */
  def addedTo(point: TimePoint): TimePoint =
    addAmountToTimePoint(inBaseUnits, point)

  /**このオブジェクトの`quantity`フィールド（量）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 量
   */
  def breachEncapsulationOfQuantity = quantity

  /**このオブジェクトの`unit`フィールド（単位）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 単位
   */
  def breachEncapsulationOfUnit = unit

  /**時間量同士の比較を行う。
   *
   * 基本単位(baseUnit)換算で比較し、時間量の少ない方を「小さい」と判断する。
   * 同じ基本単位に変換できない場合は[[java.lang.ClassCastException]]をスローする。
   *
   * 例えば「1ヶ月間」と「30日間」は、同じ基本単位に変換できないため、比較不能である。
   *
   * @param other 比較対照
   * @return [[java.util.Comparable]] compareTo(Object)に準じる
   * @throws ClassCastException 引数`other`の単位を、このオブジェクトの単位に変換できない場合
   */
  def compare(other: Duration): Int = {
    if (other.unit.isConvertibleTo(unit) == false && quantity != 0 && other.quantity != 0) {
      throw new ClassCastException(other.toString() + " is not convertible to: " + toString())
    }
    val difference = inBaseUnits - other.inBaseUnits
    if (difference > 0) 1
    else if (difference < 0) -1
    else 0
  }

  /**この時間量を、指定した時間量 `other` で割った商（割合）を取得する。
   *
   * @param divisor 割る数
   * @return 割合
   * @throws IllegalArgumentException 引数divisorの単位を、このオブジェクトの単位に変換できない場合
   * @throws ArithmeticException 引数`divisor`が0だった場合
   */
  def dividedBy(divisor: Duration): Ratio = {
    checkConvertible(divisor)
    Ratio(inBaseUnits, divisor.inBaseUnits)
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: Duration => isConvertibleTo(that) && inBaseUnits == that.inBaseUnits
    case _ => false
  }

  override def hashCode: Int = {
    (inBaseUnits ^ (inBaseUnits >>> 32)).asInstanceOf[Int] + unit.valueBaseType.hashCode
  }


  /**このオブジェクトが表現する時間量と、引数 `other` に与えた時間量の差を返す。
   *
   * @param other 期間
   * @return 時間量の差
   * @throws IllegalArgumentException 引数otherの単位を、このオブジェクトの単位に変換できず、かつ、
   * どちらのquantityも0ではない場合もしくは、引数otherの長さが、このオブジェクトよりも長い場合
   */
  def minus(other: Duration): Duration = {
    checkConvertible(other)
    checkGreaterThanOrEqualTo(other)
    val newQuantity = inBaseUnits - other.inBaseUnits
    new Duration(newQuantity, if (other.quantity == 0) unit.baseUnit else other.unit.baseUnit)
  }

  /**この期間を1単位で表せる最大の時間単位を求める。
   *
   * 例えば、1〜23時間, 25〜47時間は hours だが、24時間, 48時間は days である。
   *
   * @return 時間単位
   */
  def normalizedUnit: TimeUnit = {
    val units = unit.descendingUnits
    val baseAmount = inBaseUnits
    units.find(e => (baseAmount % e.getFactor) == 0).get
  }

  /**このオブジェクトが表現する時間量と、引数 `other` に与えた時間量の和を返す。
   *
   * @param other 期間
   * @return 時間量の和
   * @throws IllegalArgumentException 引数otherの単位を、このオブジェクトの単位に変換できず、かつ、どちらのquantityも0ではない場合
   */
  def plus(other: Duration): Duration = {
    checkConvertible(other)
    val newQuantity = inBaseUnits + other.inBaseUnits
    new Duration(newQuantity, if (other.quantity == 0) unit.baseUnit else other.unit.baseUnit)
  }

  /**終了日時とこのオブジェクトが表現する時間量より、期間を生成する。
   *
   * @param end 終了日時（上側限界値）
   * @return 期間
   */
  def preceding(end: LimitValue[TimePoint]): TimeInterval =
    TimeInterval.preceding(end.asInstanceOf[Limit[TimePoint]], this)

  /**指定した日付を開始日とする、このオブジェクトが表現する長さを持つ期間を生成する。
   *
   * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
   *
   * この時間量の単位が "日" 未満である場合は、開始日と終了日は同日となる。
   *
   * @param start 開始日時（下側限界値）. `Limitless[CalendarDate]`の場合は、限界がないことを表す
   * @return 期間
   */
  def startingFromCalendarDate(start: LimitValue[CalendarDate]): CalendarInterval =
    CalendarInterval.startingFrom(start, this)

  /**指定した日時を開始日時とする、このオブジェクトが表現する長さを持つ期間を生成する。
   *
   * 生成する期間の開始日時は区間に含み（閉じている）、終了日時は区間に含まない（開いている）半開期間を生成する。
   *
   * @param start 開始日時（下側限界値）. `Limitless[CalendarDate]`の場合は、限界がないことを表す
   * @return 期間
   */
  def startingFromTimePoint(start: LimitValue[TimePoint]): TimeInterval =
    TimeInterval.startingFrom(start.asInstanceOf[Limit[TimePoint]], this)


  /**指定した日付に、このオブジェクトが表現する長さの時間を引いた、過去の日付を取得する。
   *
   * このオブジェクトが表現する時間の長さの単位が 日 未満である場合は、元の日付をそのまま返す。
   *
   * @param day 元となる日付
   * @return このオブジェクトが表現する長さのを引いた、過去の日付
   */
  def subtractedFrom(day: CalendarDate): CalendarDate = {
    //		only valid for days and larger units
    if (unit.compareTo(TimeUnit.day) < 0) {
      day
    } else {
      val calendar = day.asJavaCalendarUniversalZoneMidnight
      if (unit.equals(TimeUnit.day)) {
        calendar.add(Calendar.DATE, -1 * quantity.asInstanceOf[Int])
      } else {
        subtractAmountFromCalendar(inBaseUnits, calendar)
      }
      CalendarDate.from(calendar)
    }
  }

  /**指定した日時に、このオブジェクトが表現する長さの時間を引いた、過去の日時を取得する。
   *
   * @param point 元となる日時
   * @return このオブジェクトが表現する長さのを引いた、過去の日時
   * @see #addAmountToTimePoint(long, TimePoint)
   */
  def subtractedFrom(point: TimePoint): TimePoint =
    addAmountToTimePoint(-1 * inBaseUnits, point)

  /**この時間量の文字列表現を返す。
   *
   * @return 時間量の文字列表現
   */
  def toNormalizedString: String = toNormalizedString(unit.descendingUnits)

  /**この時間量の文字列表現を返す。
   *
   * @return 時間量の文字列表現
   * @see #toNormalizedString()
   */
  override def toString: String = toNormalizedString(unit.descendingUnitsForDisplay)

  def addAmountToCalendar(amount: Long, calendar: Calendar) {
    if (unit.isConvertibleToMilliseconds) {
      calendar.setTimeInMillis(calendar.getTimeInMillis + amount)
    } else {
      checkAmountValid(amount)
      calendar.add(unit.javaCalendarConstantForBaseType, amount.asInstanceOf[Int])
    }
  }

  private[time] def addAmountToTimePoint(amount: Long, point: TimePoint): TimePoint = {
    if (unit.isConvertibleToMilliseconds) {
      TimePoint.from(amount + point.millisecondsFromEpoc)
    } else {
      val calendar = point.asJavaCalendar
      addAmountToCalendar(amount, calendar)
      TimePoint.from(calendar)
    }
  }

  def inBaseUnits =
    quantity * unit.getFactor


  def subtractAmountFromCalendar(amount: Long, calendar: Calendar) =
    addAmountToCalendar(-1 * amount, calendar)


  private def checkAmountValid(amount: Long) {
    require(amount >= Int.MinValue && amount <= Int.MaxValue, amount + " is not valid")
  }

  private def checkConvertible(other: Duration) {
    require((other.unit.isConvertibleTo(unit) == false && quantity != 0 && other.quantity != 0) == false,
      other.toString() + " is not convertible to: " + toString())
  }

  private def checkGreaterThanOrEqualTo(other: Duration) {
    require((compareTo(other) < 0) == false, this + " is before " + other)
  }

  private def isConvertibleTo(other: Duration) =
    unit.isConvertibleTo(other.unit)

  private def toNormalizedString(units: List[TimeUnit]): String = {
    val buffer = new StringBuffer
    var remainder = inBaseUnits
    var first = true
    units.foreach{ aUnit =>
      val portion = remainder / aUnit.getFactor
      if (portion > 0) {
        if (first == false) {
          buffer.append(", ")
        } else {
          first = false;
        }
        buffer.append(aUnit.toString(portion))
      }
      remainder %= aUnit.getFactor
    }
    buffer.toString
  }
}

/**`Duration`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object Duration {

  /**長さ `0` の期間 */
  val None = milliseconds(0)


  /**長さが `howMany` 日の時間量を取得する。
   *
   * @param howMany 時間の長さ（日）
   * @return 時間量
   */
  def days(howMany: Int): Duration =
    Duration(howMany, TimeUnit.day)

  /**長さが `days`日 + `hours`時間 + `minute`分 + `seconds`秒
   * + `milliseconds`ミリ秒 の時間量を取得する。
   *
   * @param days 時間の長さ（日）
   * @param hours 時間の長さ（時間）
   * @param minutes 時間の長さ（分）
   * @param seconds 時間の長さ（秒）
   * @param milliseconds  時間の長さ（ミリ秒）
   * @return 時間量
   */
  def daysHoursMinutesSecondsMilliseconds(days: Int, hours: Int, minutes: Int, seconds: Int,
                                          milliseconds: Long): Duration = {
    var result = Duration.days(days)
    if (hours != 0) {
      result = result.plus(Duration.hours(hours))
    }
    if (minutes != 0) {
      result = result.plus(Duration.minutes(minutes))
    }
    if (seconds != 0) {
      result = result.plus(Duration.seconds(seconds))
    }
    if (milliseconds != 0) {
      result = result.plus(Duration.milliseconds(milliseconds))
    }
    result
  }

  /**長さが `howMany` 時間の時間量を取得する。
   *
   * @param howMany 時間の長さ（時間）
   * @return 時間量
   */
  def hours(howMany: Int): Duration =
    Duration(howMany, TimeUnit.hour)

  /**長さが `howMany` ミリ秒の時間量を取得する。
   *
   * @param howMany 時間の長さ（ミリ秒）
   * @return 時間量
   */
  def milliseconds(howMany: Long): Duration =
    Duration(howMany, TimeUnit.millisecond)

  /**長さが `howMany` 分の時間量を取得する。
   *
   * @param howMany 時間の長さ（分）
   * @return 時間量
   */
  def minutes(howMany: Int): Duration =
    Duration(howMany, TimeUnit.minute)

  /**長さが `howMany` ヶ月の時間量を取得する。
   *
   * @param howMany 時間の長さ（月）
   * @return 時間量
   */
  def months(howMany: Int): Duration =
    Duration(howMany, TimeUnit.month)

  /**長さが `howMany` 四半期の時間量を取得する。
   *
   * @param howMany 時間の長さ（四半期）
   * @return 時間量
   */
  def quarters(howMany: Int): Duration = {
    Duration(howMany, TimeUnit.quarter)
  }

  /**長さが `howMany` ミリの時間量を取得する。
   *
   * @param howMany 時間の長さ（ミリ）
   * @return 時間量
   */
  def seconds(howMany: Int): Duration =
    Duration(howMany, TimeUnit.second)

  /**長さが `howMany` 週間の時間量を取得する。
   *
   * @param howMany 時間の長さ（週）
   * @return 時間量
   */
  def weeks(howMany: Int): Duration =
    Duration(howMany, TimeUnit.week)

  /**長さが `howMany` 年の時間量を取得する。
   *
   * @param howMany 時間の長さ（年）
   * @return 時間量
   */
  def years(howMany: Int): Duration =
    Duration(howMany, TimeUnit.year)


  private[time] def apply(howMany: Long, unit: TimeUnit): Duration =
    new Duration(howMany, unit)

  private[time] def unapply(duration: Duration) =
    Some(duration.quantity, duration.unit)
}