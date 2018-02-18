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

import java.time.{LocalDate, Month, ZoneId}
import java.util.{Calendar, GregorianCalendar, TimeZone}

/**
  * 1年の中の特定の「月」を表す列挙型。
  *
  * @param lastDayOfThisMonth その月の最終日
  * @param calendarValue      [[java.util.Calendar]]に定義する月をあらわす定数値
  */
sealed class MonthOfYear private[time] (
    val lastDayOfThisMonth: DayOfMonth,
    val calendarValue: Int
) {

  val value = calendarValue + 1

  lazy val asJavaMonth: Month =
    java.time.Month.of(value)

  /**
    * このオブジェクトの`calendarValue`フィールド（[[java.util.Calendar]]に定義する月をあらわす定数値）を返す。
    *
    * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
    *
    * @return [[java.util.Calendar]]に定義する月をあらわす定数値（JANUARY〜DECEMBER）
    */
  @deprecated("Use calendarValue property instead", "0.1.18")
  val breachEncapsulationOfCalendarValue = calendarValue

  /**
    * このオブジェクトの`value`フィールド（月をあらわす数 1〜12）を返す。
    *
    * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
    *
    * @return 月をあらわす数（1〜12）
    */
  @deprecated("Use value property instead", "0.1.18")
  val breachEncapsulationOfValue = calendarValue + 1

  /**
    * 指定した日 `other` が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
    *
    * お互いが同一日時である場合は `false` を返す。
    *
    * @param other 対象日時
    * @return 過去である場合は`true`、そうでない場合は`false`
    */
  def isAfter(other: MonthOfYear): Boolean = !isBefore(other) && !equals(other)

  /**
    * 指定した日 `other` が、このオブジェクトが表現する日よりも未来であるかどうかを検証する。
    *
    * お互いが同一日時である場合は `false` を返す。
    *
    * @param other 対象日
    * @return 未来である場合は`true`、そうでない場合は`false`
    */
  def isBefore(other: MonthOfYear): Boolean =
    calendarValue < other.calendarValue

  //	public DayOfYear at(DayOfMonth month) {
  //		// ...
  //	}

  /**
    * 指定した年の、この月を表す年月を返す。
    *
    * @param year 年
    * @return 年月
    */
  @deprecated("Use on(year: Int, zoneId: ZoneId) method instead", "0.1.18")
  def on(year: Int, timeZone: TimeZone): CalendarYearMonth =
    CalendarYearMonth.from(year, this, timeZone.toZoneId)

  def on(year: Int, zoneId: ZoneId = ZoneIds.Default): CalendarYearMonth =
    CalendarYearMonth.from(year, this, zoneId)

  /**
    * その月の最終日を取得する。
    *
    * @param year 該当年. 2月の閏年判定に関わらない場合は、何でも良い。
    * @return 最終日
    */
  private[time] def getLastDayOfThisMonth(year: Int) = lastDayOfThisMonth

}

/**
  * `MonthOfYear`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object MonthOfYear {

  def apply(month: Int): MonthOfYear = {
    Seq(Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)
      .find(
        _.value == month
      )
      .get
  }

  /** January */
  val Jan = new MonthOfYear(DayOfMonth(31), Calendar.JANUARY)

  /** Feburary */
  val Feb = new MonthOfYear(DayOfMonth(28), Calendar.FEBRUARY) {
    override def getLastDayOfThisMonth(year: Int): DayOfMonth = {
      val date = LocalDate.of(year, 2, 1)
      if (date.isLeapYear) DayOfMonth(29) else DayOfMonth(28)
    }
  }

  /** March */
  val Mar = new MonthOfYear(DayOfMonth(31), Calendar.MARCH)

  /** April */
  val Apr = new MonthOfYear(DayOfMonth(30), Calendar.APRIL)

  /** May */
  val May = new MonthOfYear(DayOfMonth(31), Calendar.MAY)

  /** June */
  val Jun = new MonthOfYear(DayOfMonth(30), Calendar.JUNE)

  /** July */
  val Jul = new MonthOfYear(DayOfMonth(31), Calendar.JULY)

  /** August */
  val Aug = new MonthOfYear(DayOfMonth(31), Calendar.AUGUST)

  /** September */
  val Sep = new MonthOfYear(DayOfMonth(30), Calendar.SEPTEMBER)

  /** October */
  val Oct = new MonthOfYear(DayOfMonth(31), Calendar.OCTOBER)

  /** November */
  val Nov = new MonthOfYear(DayOfMonth(30), Calendar.NOVEMBER)

  /** December */
  val Dec = new MonthOfYear(DayOfMonth(31), Calendar.DECEMBER)

}
