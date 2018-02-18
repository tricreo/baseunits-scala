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

import org.junit.{ After, Before, Test }
import org.scalatest.junit.AssertionsForJUnit
import org.sisioh.baseunits.scala.intervals.Limit
/**
 * `BusinessCalendar`のテストクラス。
 */
class BusinessCalendarTest extends AssertionsForJUnit {

  def japaneseBusinessCalendar: BusinessCalendar = {
    val calendar = BusinessCalendar().
      addHolidaySpec(DateSpecification.fixed(1, 1, ZoneIds.Default)). // 元旦
      addHolidaySpec(DateSpecification.nthOccuranceOfWeekdayInMonth(1, DayOfWeek.Monday, 2, ZoneIds.Default)). // 成人の日
      addHolidaySpec(DateSpecification.fixed(2, 11, ZoneIds.Default)). // 建国記念日
      addHoliday(CalendarDate.from(2010, 3, 21, ZoneIds.Default)). // 春分の日
      addHolidaySpec(DateSpecification.fixed(4, 29, ZoneIds.Default)). // 昭和の日
      addHolidaySpec(DateSpecification.fixed(5, 3, ZoneIds.Default)). // 憲法記念日
      addHolidaySpec(DateSpecification.fixed(5, 4, ZoneIds.Default)). // みどりの日
      addHolidaySpec(DateSpecification.fixed(5, 5, ZoneIds.Default)). // こどもの日
      addHolidaySpec(DateSpecification.nthOccuranceOfWeekdayInMonth(7, DayOfWeek.Monday, 3, ZoneIds.Default)). // 海の日
      addHolidaySpec(DateSpecification.nthOccuranceOfWeekdayInMonth(9, DayOfWeek.Monday, 3, ZoneIds.Default)). // 敬老の日
      addHoliday(CalendarDate.from(2010, 9, 23, ZoneIds.Default)). // 秋分の日
      addHolidaySpec(DateSpecification.nthOccuranceOfWeekdayInMonth(10, DayOfWeek.Monday, 2, ZoneIds.Default)). // 体育の日
      addHolidaySpec(DateSpecification.fixed(11, 3, ZoneIds.Default)). // 文化の日
      addHolidaySpec(DateSpecification.fixed(11, 23, ZoneIds.Default)). // 勤労感謝の日
      addHolidaySpec(DateSpecification.fixed(12, 23, ZoneIds.Default)) // 天皇誕生日

    // それぞれの日が「営業日」にあたるかどうかチェック。
    assert(calendar.isBusinessDay(CalendarDate.from(2010, 10, 8, ZoneIds.Default))) // 金曜日
    assert(!calendar.isBusinessDay(CalendarDate.from(2010, 10, 9, ZoneIds.Default))) // 土曜日
    assert(!calendar.isBusinessDay(CalendarDate.from(2010, 10, 10, ZoneIds.Default))) // 日曜日
    assert(!calendar.isBusinessDay(CalendarDate.from(2010, 10, 11, ZoneIds.Default))) // 月曜日体育の日
    assert(calendar.isBusinessDay(CalendarDate.from(2010, 10, 12, ZoneIds.Default))) // 火曜日平日

    assert(calendar.isBusinessDay(CalendarDate.from(2010, 11, 22, ZoneIds.Default))) // 月曜日平日
    assert(!calendar.isBusinessDay(CalendarDate.from(2010, 11, 23, ZoneIds.Default))) // 火曜日祝日
    assert(calendar.isBusinessDay(CalendarDate.from(2010, 11, 24, ZoneIds.Default))) // 水曜日平日

    // 振替休日（「国民の祝日」が日曜日にあたる場合、その直後の「国民の祝日」でない日を休日とする）とか、
    // 国民の休日（「国民の祝日」と次の「国民の祝日」の間隔が中1日しかなくその中日（なかび）が「国民の祝日」でない場合、その日を休日とする）
    // なんかには、まだ対応していないけど、DateSpecification実装すればどうにかならんかな、と思っている。

    calendar
  }

  var cal: BusinessCalendar = _

  /**
   * テストを初期化する。
   *
   * @throws Exception 例外が発生した場合
   */
  @Before
  def setUp() {
    cal = BusinessCalendar().addHolidays(_HolidayDates.defaultHolidays)
  }

  /**
   * テストの情報を破棄する。
   *
   * @throws Exception 例外が発生した場合
   */
  @After
  def tearDown() {
    cal = null
  }

  /**
   * [[BusinessCalendar# g e t E l a p s e d B u s i n e s s D a y s ( C a l e n d a r I n t e r v a l )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_ElapsedBusinessDays() {
    val nov1 = CalendarDate.from(2004, 11, 1)
    val nov30 = CalendarDate.from(2004, 11, 30)
    val interval = CalendarInterval.inclusive(Limit(nov1), Limit(nov30))

    assert(interval.length == Duration.days(30))
    // 1 holiday (Thanksgiving on a Thursday) + 8 weekend days.
    assert(cal.getElapsedBusinessDays(interval) == 21)
  }

  /**
   * [[BusinessCalendar# i s W e e k e n d ( C a l e n d a r D a t e )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_IsWeekend() {
    val saturday = CalendarDate.from(2004, 1, 10)
    assert(cal.isWeekend(saturday))

    val sunday = saturday.nextDay
    assert(cal.isWeekend(sunday))

    var day = sunday
    for (i <- 0 until 5) {
      day = day.nextDay
      assert(cal.isWeekend(day) == false, "it's a midweek day")
    }
    day = day.nextDay
    assert(cal.isWeekend(day) == true, "finally, the weekend is here...")

    val newYearEve = CalendarDate.from(2004, 1, 1) // it's a Holiday
    assert(cal.isWeekend(newYearEve) == false, "a holiday is not necessarily a weekend day")
  }

  /**
   * [[BusinessCalendar# i s H o l i d a y ( C a l e n d a r D a t e )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_IsHoliday {
    val newYearEve = CalendarDate.from(2004, 1, 1) // it's a Holiday
    assert(cal.isHoliday(newYearEve) == true, "New Years Eve is a holiday.")
    assert(cal.isHoliday(newYearEve.nextDay) == false, "The day after New Years Eve is not a holiday.")
  }

  /**
   * [[BusinessCalendar# i s B u s i n e s s D a y ( C a l e n d a r D a t e )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_IsBusinessDay {
    var day = CalendarDate.from(2004, 1, 12) // it's a Monday
    for (i <- 0 until 5) {
      assert(cal.isBusinessDay(day) == true, "another working day")
      day = day.nextDay
    }
    assert(cal.isBusinessDay(day) == false, "finally, saturday arrived ...")
    assert(cal.isBusinessDay(day.nextDay) == false, "... then sunday")

    val newYearEve = CalendarDate.from(2004, 1, 1) // it's a Holiday
    assert(cal.isBusinessDay(newYearEve) == false, "hey, it's a holiday")
  }

  /**
   * [[BusinessCalendar# n e a r e s t N e x t B u s i n e s s D a y ( C a l e n d a r D a t e )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_NearestNextBusinessDay {
    val friday = CalendarDate.from(2004, 1, 9)
    val saturday = friday.nextDay
    val sunday = saturday.nextDay
    val monday = sunday.nextDay
    assert(cal.nearestNextBusinessDay(friday) == friday)
    assert(cal.nearestNextBusinessDay(saturday) == monday)
    assert(cal.nearestNextBusinessDay(sunday) == monday)
    assert(cal.nearestNextBusinessDay(monday) == monday)

    val newYearEve = CalendarDate.from(2004, 1, 1) // it's a Holiday
    assert(cal.nearestNextBusinessDay(newYearEve) == newYearEve.nextDay, "it's a holiday & a thursday; wait till friday")

    val christmas = CalendarDate.from(2004, 12, 24); // it's a Holiday
    assert(cal.nearestNextBusinessDay(christmas) == CalendarDate.from(2004, 12, 27), "it's a holiday & a friday; wait till monday")
  }

  /**
   * [[BusinessCalendar# n e a r e s t N e x t B u s i n e s s D a y ( C a l e n d a r D a t e )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_NearestPrevBusinessDay {
    val tuesday = CalendarDate.from(2004, 1, 8)
    val friday = tuesday.nextDay
    val saturday = friday.nextDay
    val sunday = saturday.nextDay
    val monday = sunday.nextDay
    assert(cal.nearestPrevBusinessDay(tuesday) == tuesday)
    assert(cal.nearestPrevBusinessDay(friday) == friday)
    assert(cal.nearestPrevBusinessDay(saturday) == friday)
    assert(cal.nearestPrevBusinessDay(sunday) == friday)
    assert(cal.nearestPrevBusinessDay(monday) == monday)

    val newYearEve = CalendarDate.from(2004, 1, 1); // it's a Holiday
    assert(cal.nearestPrevBusinessDay(newYearEve) == newYearEve.previousDay)

    val christmas = CalendarDate.from(2004, 12, 26); // it's a Holiday
    assert(cal.nearestPrevBusinessDay(christmas) == CalendarDate.from(2004, 12, 23))
  }

  /**
   * [[BusinessCalendar# b u s i n e s s D a y s O n l y ( I t e r a t o r )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_BusinessDaysIterator {
    val feb5 = CalendarDate.from(2004, 2, 5)
    val feb8 = CalendarDate.from(2004, 2, 8)
    val interval = CalendarInterval.inclusive(Limit(feb5), Limit(feb8))

    val it = cal.businessDaysOnly(interval.daysIterator)
    assert(it.hasNext == true)
    assert(it.next == feb5)
    assert(it.hasNext == true)
    assert(it.next == CalendarDate.from(2004, 2, 6))
    assert(it.hasNext == false)
  }

  /**
   * [[org.sisioh.baseunits.scala.time.BusinessCalendar# n e x t B u s i n e s s D a y ( C a l e n d a r D a t e )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_NextBusinessDayOverWeekend {
    val tuesday = CalendarDate.from(2006, 6, 15)
    val friday = tuesday.nextDay
    val saturday = friday.nextDay
    val monday = CalendarDate.from(2006, 6, 19)
    assert(cal.nextBusinessDay(tuesday) == friday)
    assert(cal.nextBusinessDay(friday) == monday)
    assert(cal.nextBusinessDay(saturday) == monday)
  }

  /**
   * [[org.sisioh.baseunits.scala.time.BusinessCalendar# n e x t B u s i n e s s D a y ( C a l e n d a r D a t e )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_NextBusinessDayOverWeekday {
    val monday = CalendarDate.from(2006, 6, 19)
    val tuesday = CalendarDate.from(2006, 6, 20)
    val actual = cal.nextBusinessDay(monday)
    assert(actual == tuesday)
  }

  /**
   * [[org.sisioh.baseunits.scala.time.BusinessCalendar# p l u s B u s i n e s s D a y s ( C a l e n d a r D a t e, i n t )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_PlusBusinessDayZero {
    val monday = CalendarDate.from(2006, 6, 19)
    assert(cal.plusBusinessDays(monday, 0) == monday)
  }

  /**
   * [[org.sisioh.baseunits.scala.time.BusinessCalendar# p l u s B u s i n e s s D a y s ( C a l e n d a r D a t e, i n t )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_PlusNonBusinessDayZero {
    val saturday = CalendarDate.from(2006, 6, 17)
    val monday = CalendarDate.from(2006, 6, 19)
    assert(cal.plusBusinessDays(saturday, 0) == monday)

  }

  /**
   * [[org.sisioh.baseunits.scala.time.BusinessCalendar# m i n u s B u s i n e s s D a y s ( C a l e n d a r D a t e, i n t )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_MinusNonBusinessDayZero {
    val saturday = CalendarDate.from(2006, 6, 17)
    val friday = CalendarDate.from(2006, 6, 16)
    val actual = cal.minusBusinessDays(saturday, 0)
    assert(actual == friday)
  }

  /**
   * [[org.sisioh.baseunits.scala.time.BusinessCalendar# b u s i n e s s D a y s O n l y ( I t e r a t o r )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_BusinessDayReverseIterator {
    val friday = CalendarDate.from(2006, 6, 16)
    val nextMonday = CalendarDate.from(2006, 6, 19)
    val nextTuesday = CalendarDate.from(2006, 6, 20)
    val interval = CalendarInterval.inclusive(Limit(friday), Limit(nextTuesday))
    val it = cal.businessDaysOnly(interval.daysInReverseIterator)
    assert(it.hasNext)
    assert(it.next == nextTuesday)
    assert(it.hasNext)
    assert(it.next == nextMonday)
    assert(it.hasNext)
    assert(it.next == friday)
    assert(it.hasNext == false)
  }

  /**
   * 日本の祝日テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_JapaneseHoliday() {
    val calendar = japaneseBusinessCalendar

    val itr =
      calendar.businessDaysOnly(CalendarInterval.inclusive(2010, 10, 1, 2010, 11, 30, ZoneIds.Default).daysIterator)
    val sb = new StringBuilder
    while (itr.hasNext) {
      val calendarDate = itr.next()
      sb.append(calendarDate).append(" ")
    }
    assert(sb.toString() == ("2010/10/01 " +
      "2010/10/04 2010/10/05 2010/10/06 2010/10/07 2010/10/08 " +
      "2010/10/12 2010/10/13 2010/10/14 2010/10/15 " +
      "2010/10/18 2010/10/19 2010/10/20 2010/10/21 2010/10/22 " +
      "2010/10/25 2010/10/26 2010/10/27 2010/10/28 2010/10/29 " +
      "2010/11/01 2010/11/02 2010/11/04 2010/11/05 " +
      "2010/11/08 2010/11/09 2010/11/10 2010/11/11 2010/11/12 " +
      "2010/11/15 2010/11/16 2010/11/17 2010/11/18 2010/11/19 " +
      "2010/11/22 2010/11/24 2010/11/25 2010/11/26 " +
      "2010/11/29 2010/11/30 "))
  }

  /**
   * dates are taken from: http://www.opm.gov/fedhol/index.htm note: when a
   * holiday falls on a non-workday -- Saturday or Sunday -- the holiday usually
   * is observed on Monday (if the holiday falls on Sunday) or Friday (if the
   * holiday falls on Saturday). a holiday falls on a nonworkday will be referred
   * to as a "deferred" holiday.
   */
  object _HolidayDates {

    val COMMON_US_HOLIDAYS = Array[String](

      // 2004
      "2004/01/01", /* New Year's Day */
      "2004/01/19", /* Birthday of Martin Luther King */
      "2004/02/16", /* Washington's Birthday */
      "2004/05/31", /* Memorial Day */
      "2004/07/05", /* United States of America's Independence Day, July 4 */
      //revisit:defered
      "2004/09/06", /* Labor Day */
      "2004/11/25", /* Thanksgiving Day */
      "2004/12/24", /*
							 * Christmas Day, December 25 - Friday - deferred from
							 * Saturday
							 */
      "2004/12/31", /*
							 * New Year's Day for January 1, 2005 - Friday -
							 * deferred from Saturday
							 */

      // 2005
      "2005/01/17", /* Birthday of Martin Luther King */
      "2005/02/21", /* Washington's Birthday */
      "2005/05/30", /* Memorial Day */
      "2005/07/04", /* United States of America's Independence Day, July 4 */
      "2005/09/05", /* Labor Day */
      "2005/11/24", /* Thanksgiving Day */
      "2005/12/26", /*
							 * Christmas Day, December 25 - Monday - deferred from
							 * Sunday
							 */

      // 2006
      "2006/01/02", /* New Year's Day, January 1 */
      "2006/01/16", /* Birthday of Martin Luther King */
      "2006/02/20", /* Washington's Birthday */
      "2006/05/29", /* Memorial Day */
      "2006/07/04", /* United States of America's Independence Day, July 4 */
      "2006/09/04", /* Labor Day */
      "2006/11/23", /* Thanksgiving Day */
      "2006/12/25" /* Christmas Day, December 25 */
    )

    def defaultHolidays: Set[CalendarDate] = {
      val dates = collection.mutable.Set[CalendarDate]()
      val strings = COMMON_US_HOLIDAYS
      for (string <- strings) {
        try {
          dates += CalendarDate.parse(string, "yyyy/MM/dd", ZoneIds.Default)
        } catch {
          case e: ParseException => throw new Error(e)
        }
      }
      dates.toSet
    }

  }

}