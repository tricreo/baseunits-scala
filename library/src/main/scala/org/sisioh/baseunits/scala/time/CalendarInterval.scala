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

import java.time.ZoneId
import java.util.{Calendar, TimeZone}

import org.sisioh.baseunits.scala.intervals.{Interval, Limit, LimitValue, Limitless}

/**
  * 期間（日付の区間）を表すクラス。
  *
  * 限界の表現には [[org.sisioh.baseunits.scala.time.CalendarDate]]を利用する。
  * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
  *
  * @author j5ik2o
  * @param startValue 開始日
  * @param endValue   終了日
  */
class CalendarInterval protected (
    startValue: LimitValue[CalendarDate],
    endValue: LimitValue[CalendarDate]
) extends Interval[CalendarDate](startValue, true, endValue, true)
    with Serializable {

  /**
    * この期間の開始日の午前0時を開始日時、この期間の終了日の翌日午前0時を終了日時とする時間の期間を生成する。
    *
    * 生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を生成する。
    *
    * @param timeZone タイムゾーン
    * @return 時間の期間
    */
  @deprecated("Use asTimeInterval(zoneId: ZoneId) method instead", "0.1.18")
  def asTimeInterval(timeZone: TimeZone): TimeInterval =
    asTimeInterval(timeZone.toZoneId)

  def asTimeInterval(zoneId: ZoneId = ZoneIds.Default): TimeInterval = {
    val startPoint = lowerLimit.toValue.asTimeInterval(zoneId).start
    val endPoint   = upperLimit.toValue.asTimeInterval(zoneId).end
    TimeInterval.over(startPoint, endPoint)
  }

  /**
    * この期間の終了日を起点として、前回の日付の前日をこの期間の開始日を超過しない範囲で順次取得する反復子を取得する。
    *
    * 例えば [2009/01/01, 2009/01/04] で表される期間に対してこのメソッドを呼び出した場合、
    * その戻り値の反復子からは、以下の要素が取得できる。
    * <ol>
    * <li>2009/01/04</li>
    * <li>2009/01/03</li>
    * <li>2009/01/02</li>
    * <li>2009/01/01</li>
    * </ol>
    *
    * この期間が開始日（下側限界）を持たない場合、 [[scala.collection.Iterator]] `hasNext()`は常に
    * `true`を返すので、無限ループに注意すること。
    *
    * @return 日付の反復子
    * @throws IllegalStateException この期間が終了日（上側限界）を持たない場合
    */
  lazy val daysInReverseIterator: Iterator[CalendarDate] = {
    if (!hasUpperLimit) {
      throw new IllegalStateException
    }

    val start = upperLimit
    val end   = lowerLimit

    new Iterator[CalendarDate] {

      var _next = start

      override def hasNext = {
        end match {
          case _: Limitless[CalendarDate] => true
          case Limit(end)                 => !_next.toValue.isBefore(end)
        }
      }

      override def next = {
        if (!hasNext) {
          throw new NoSuchElementException
        }
        val current = _next
        _next = Limit(_next.toValue.plusDays(-1))
        current.toValue
      }
    }
  }

  /**
    * この期間の開始日を起点として、前回の日付の翌日をこの期間の終了日を超過しない範囲で順次取得する反復子を取得する。
    *
    * 例えば [2009/01/01, 2009/01/04] で表される期間に対してこのメソッドを呼び出した場合、
    * その戻り値の反復子からは、以下の要素が取得できる。
    * <ol>
    * <li>2009/01/01</li>
    * <li>2009/01/02</li>
    * <li>2009/01/03</li>
    * <li>2009/01/04</li>
    * </ol>
    *
    * この期間が終了日（上側限界）を持たない場合、 [[scala.collection.Iterator]] `hasNext()`は常に
    * `true`を返すので、無限ループに注意すること。
    *
    * @return 日付の反復子
    * @throws IllegalStateException この期間が開始日（下側限界）を持たない場合
    */
  lazy val daysIterator: Iterator[CalendarDate] = {
    if (!hasLowerLimit) {
      throw new IllegalStateException
    }

    val start = lowerLimit
    val end   = upperLimit

    new Iterator[CalendarDate] {

      var _next = start

      override def hasNext = {
        end match {
          case _: Limitless[CalendarDate] => true
          case Limit(end)                 => !_next.toValue.isAfter(end)
        }
      }

      override def next: CalendarDate = {
        if (!hasNext) {
          throw new NoSuchElementException
        }
        val current = _next
        _next = Limit(_next.toValue.plusDays(1))
        current.toValue
      }
    }
  }

  /**
    * 終了日を取得する。
    *
    * @return 終了日. 開始日がない場合は`Limitless[CalendarDate]`
    */
  val end = upperLimit

  /**
    * この期間の日数としての長さを取得する。
    *
    * @return 期間の長さ
    * @see #length()
    */
  lazy val length: Duration =
    Duration.days(lengthInDaysInt)

  /**
    * この期間が、日数にして何日の長さがあるかを取得する。
    *
    * @return 日数
    * @throws IllegalStateException この期間が開始日（下側限界）または終了日（下側限界）を持たない場合
    */
  lazy val lengthInDaysInt: Int = {
    require(hasLowerLimit && hasUpperLimit)
    val calStart = start.toValue.asJavaZonedDateTimeOnMidnight
    val calEnd   = end.toValue.plusDays(1).asJavaZonedDateTimeOnMidnight
    val diffMillis = calEnd.toInstant.toEpochMilli - calStart.toInstant
      .toEpochMilli
    (diffMillis / TimeUnitConversionFactor.MillisecondsPerDay.value)
      .asInstanceOf[Int]
  }

  /**
    * この期間の月数としての長さを取得する。
    *
    * 開始日と終了日が同月であれば`0`ヶ月となる。
    *
    * @return 期間の長さ
    * @see #lengthInMonthsInt()
    */
  lazy val lengthInMonths: Duration =
    Duration.months(lengthInMonthsInt)

  /**
    * 限界日の「日」要素を考慮せず、この期間が月数にして何ヶ月の長さがあるかを取得する。
    *
    * 開始日と終了日が同月であれば`0`となる。
    *
    * @return 月数
    * @throws IllegalStateException この期間が開始日（下側限界）または終了日（下側限界）を持たない場合
    */
  lazy val lengthInMonthsInt: Int = {
    require(hasLowerLimit && hasUpperLimit)
    val calStart  = start.toValue.asJavaZonedDateTimeOnMidnight
    val calEnd    = end.toValue.plusDays(1).asJavaZonedDateTimeOnMidnight
    val yearDiff  = calEnd.getYear - calStart.getYear
    val monthDiff = yearDiff * 12 + calEnd.getMonth.getValue - calStart.getMonth.getValue
    monthDiff
  }

  override def newOfSameType(lower: LimitValue[CalendarDate],
                             isLowerClosed: Boolean,
                             upper: LimitValue[CalendarDate],
                             isUpperClosed: Boolean): Interval[CalendarDate] = {
    val includedLower =
      if (isLowerClosed) lower else Limit(lower.toValue.plusDays(1))
    val includedUpper =
      if (isUpperClosed) upper else Limit(upper.toValue.plusDays(-1))
    CalendarInterval.inclusive(includedLower, includedUpper)
  }

  /**
    * 開始日を取得する。
    *
    * @return 開始日. 開始日がない場合は`Limitless[CalendarDate]`
    */
  val start = lowerLimit

  /**
    * この期間の開始日を起点として、指定した時間の長さを持ち前回の終了日の翌日を開始日とする期間
    * [[CalendarInterval]] を
    * この期間の終了日を超過しない範囲で順次取得する反復子を取得する。
    *
    * 例えば [2009/01/01, 2009/01/11] で表される期間に対して、
    * 2日間の `subintervalLength` を与えた場合、
    * その戻り値の反復子からは、以下の要素が取得できる。
    * <ol>
    * <li>[2009/01/01, 2009/01/02]</li>
    * <li>[2009/01/03, 2009/01/04]</li>
    * <li>[2009/01/05, 2009/01/06]</li>
    * <li>[2009/01/07, 2009/01/08]</li>
    * <li>[2009/01/09, 2009/01/10]</li>
    * </ol>
    *
    * この期間が終了日（上側限界）を持たない場合、 [[scala.collection.Iterator]] `hasNext()`は常に
    * `true`を返すので、無限ループに注意すること。
    *
    * @param subintervalLength 反復子が返す期間の長さ
    * @return 期間の反復子
    * @throws IllegalStateException    この期間が開始日（下側限界）を持たない場合
    * @throws IllegalArgumentException 引数subintervalLengthの長さ単位が「日」未満の場合
    */
  def subintervalIterator(subintervalLength: Duration): Iterator[CalendarInterval] = {
    if (!hasLowerLimit) {
      throw new IllegalStateException
    }
    require(
      TimeUnit.Day.compareTo(subintervalLength.normalizedUnit) <= 0,
      "CalendarIntervals must be a whole number of days or months."
    )

    val segmentLength = subintervalLength

    new Iterator[CalendarInterval] {

      var _next = segmentLength.startingFromCalendarDate(start)

      override def hasNext: Boolean =
        CalendarInterval.this.covers(_next)

      override def next: CalendarInterval = {
        if (!hasNext) {
          throw new NoSuchElementException
        }
        val current = _next
        _next = segmentLength.startingFromCalendarDate(Limit(_next.end.toValue.plusDays(1)))
        current
      }
    }
  }

}

/**
  * `CalendarInterval`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object CalendarInterval {

  /**
    * インスタンスを生成する。
    *
    * @param startValue 開始日
    * @param endValue   終了日
    * @return [[org.sisioh.baseunits.scala.time.CalendarInterval]]
    */
  def apply(startValue: LimitValue[CalendarDate],
            endValue: LimitValue[CalendarDate]): CalendarInterval =
    new CalendarInterval(startValue, endValue)

  /**
    * 抽出子メソッド。
    *
    * @param calendarInterval [[CalendarInterval]]
    * @return `Option[(CalendarInterval)]`
    */
  def unapply(calendarInterval: CalendarInterval)
    : Option[(LimitValue[CalendarDate], LimitValue[CalendarDate])] =
    Some(calendarInterval.start, calendarInterval.end)

  /**
    * 開始日より、下側限界のみを持つ期間を生成する。
    *
    * 開始日は期間に含む（閉じている）区間である。
    *
    * @param startDate 開始日（下側限界値）. `Limitless[CalendarDate]`の場合は、限界がないことを表す
    * @return 期間
    */
  def everFrom(startDate: LimitValue[CalendarDate]): CalendarInterval =
    inclusive(startDate, Limitless[CalendarDate]())

  /**
    * 終了日より、上側限界のみを持つ期間を生成する。
    *
    * 終了日は期間に含む（閉じている）区間である。
    *
    * @param endDate 終了日（上側限界値）. `Limitless[CalendarDate]`の場合は、限界がないことを表す
    * @return 期間
    */
  def everPreceding(endDate: LimitValue[CalendarDate]): CalendarInterval =
    inclusive(Limitless[CalendarDate](), endDate)

  /**
    * 開始日と終了日より、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * @param start 開始日
    * @param end   終了日
    * @return 期間
    * @throws IllegalArgumentException 下限値が上限値より大きい（未来である）場合
    */
  def inclusive(start: LimitValue[CalendarDate], end: LimitValue[CalendarDate]): CalendarInterval =
    new CalendarInterval(start, end)

  /**
    * 開始日と終了日より、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * @param startYear  開始日の年
    * @param startMonth 開始日の月（1〜12）
    * @param startDay   開始日の日
    * @param endYear    終了日の年
    * @param endMonth   終了日の月（1〜12）
    * @param endDay     終了日の日
    * @return 期間
    * @throws IllegalArgumentException 下限値が上限値より大きい（未来である）場合
    */
  @deprecated(
    "Use inclusive(startYear: Int, startMonth: Int, startDay: Int, endYear: Int, endMonth: Int, endDay: Int, zoneId: ZoneId) method instead",
    "0.1.18")
  def inclusive(startYear: Int,
                startMonth: Int,
                startDay: Int,
                endYear: Int,
                endMonth: Int,
                endDay: Int,
                timeZone: TimeZone): CalendarInterval = {
    val startDate =
      CalendarDate.from(startYear, startMonth, startDay, timeZone)
    val endDate = CalendarDate.from(endYear, endMonth, endDay, timeZone)
    new CalendarInterval(Limit(startDate), Limit(endDate))
  }

  def inclusive(startYear: Int,
                startMonth: Int,
                startDay: Int,
                endYear: Int,
                endMonth: Int,
                endDay: Int,
                zoneId: ZoneId): CalendarInterval = {
    val startDate = CalendarDate.from(startYear, startMonth, startDay, zoneId)
    val endDate   = CalendarDate.from(endYear, endMonth, endDay, zoneId)
    new CalendarInterval(Limit(startDate), Limit(endDate))
  }

  def inclusive(startYear: Int,
                startMonth: Int,
                startDay: Int,
                endYear: Int,
                endMonth: Int,
                endDay: Int): CalendarInterval =
    inclusive(startYear, startMonth, startDay, endYear, endMonth, endDay, ZoneIds.Default)

  /**
    * 指定した年月の1日からその月末までの、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * @param month 開始日の年月
    * @return 期間
    */
  def month(month: CalendarYearMonth): CalendarInterval = {
    val startDate = CalendarDate.from(month, DayOfMonth(1), month.zoneId)
    val endMonth  = startDate.plusMonths(1)
    val endDate   = endMonth.plusDays(-1)
    CalendarInterval.inclusive(Limit(startDate), Limit(endDate))
  }

  /**
    * 指定した年月の1日からその月末までの、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * @param year   開始日の年
    * @param month 開始日の月（1〜12）
    * @return 期間
    */
  @deprecated("Use month(year: Int, _month: Int, zoneId: ZoneId) method instead", "0.1.18")
  def month(year: Int, month: Int, timeZone: TimeZone): CalendarInterval =
    CalendarInterval.month(year, MonthOfYear(month), timeZone.toZoneId)

  def month(year: Int, month: Int, zoneId: ZoneId): CalendarInterval =
    CalendarInterval.month(year, MonthOfYear(month), zoneId)

  def month(year: Int, month: Int): CalendarInterval =
    CalendarInterval.month(year, MonthOfYear(month), ZoneIds.Default)

  /**
    * 指定した年月の1日からその月末までの、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * @param year  開始日の年
    * @param month 開始日の月
    * @return 期間
    */
  @deprecated("Use month(year: Int, month: MonthOfYear, zoneId: ZoneId) method instead", "0.1.18")
  def month(year: Int, month: MonthOfYear, timeZone: TimeZone): CalendarInterval =
    CalendarInterval.month(year, month, timeZone.toZoneId)

  def month(year: Int, month: MonthOfYear, zoneId: ZoneId): CalendarInterval = {
    val startDate = CalendarDate.from(year, month, DayOfMonth(1), zoneId)
    val endDate   = startDate.plusMonths(1).plusDays(-1)
    CalendarInterval.inclusive(Limit(startDate), Limit(endDate))
  }

  def month(year: Int, month: MonthOfYear): CalendarInterval =
    CalendarInterval.month(year, month, ZoneIds.Default)

  /**
    * 開始日と期間の長さより、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * 引数 `length` の期間の長さの単位が "日" 未満である場合は、開始日と終了日は同日となる。
    *
    * @param start  開始日（下側限界値）
    * @param length 期間の長さ
    * @return 期間
    */
  def startingFrom(start: LimitValue[CalendarDate], length: Duration): CalendarInterval = {
    // Uses the common default for calendar intervals, [start, end].
    if (length.unit.compareTo(TimeUnit.Day) < 0) {
      CalendarInterval.inclusive(start, start)
    } else {
      CalendarInterval.inclusive(start, Limit(start.toValue.plus(length).plusDays(-1)))
    }
  }

  /**
    * 指定した年の元旦からその年の大晦日までの、期間を生成する。
    *
    * 生成する期間の開始日と終了日は期間に含む（閉じている）開区間を生成する。
    *
    * @param year 開始日の年
    * @return 期間
    */
  @deprecated("Use year(year: Int, zoneId: ZoneId) method instead", "0.1.18")
  def year(year: Int, timeZone: TimeZone): CalendarInterval = {
    val startDate = CalendarDate.from(year, 1, 1, timeZone)
    val endDate   = CalendarDate.from(year + 1, 1, 1, timeZone).plusDays(-1)
    CalendarInterval.inclusive(Limit(startDate), Limit(endDate))
  }

  def year(year: Int, zoneId: ZoneId): CalendarInterval = {
    val startDate = CalendarDate.from(year, 1, 1, zoneId)
    val endDate   = CalendarDate.from(year + 1, 1, 1, zoneId).plusDays(-1)
    CalendarInterval.inclusive(Limit(startDate), Limit(endDate))
  }

}
