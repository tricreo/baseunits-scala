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

import java.time.ZonedDateTime

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import java.util.{ Calendar, TimeZone }

/**
 * `CalendarDate`のテストクラス。
 */
class CalendarDateTest extends AssertionsForJUnit {
  val feb17 = CalendarDate.from(2003, 2, 17, ZoneIds.Default)

  val mar13 = CalendarDate.from(2003, 3, 13, ZoneIds.Default)

  val may1 = CalendarDate.from(2004, 5, 1, ZoneIds.Default)

  val may20 = CalendarDate.from(2004, 5, 20, ZoneIds.Default)

  val gmt = TimeZone.getTimeZone("Universal")

  val ct = TimeZone.getTimeZone("America/Chicago").toZoneId

  /**
   * [[CalendarDate]]のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Serialization() {
    //SerializationTester.assertCanBeSerialized(feb17)
  }

  /**
   * [[CalendarDate#isBefore(CalendarDate)]] と [[CalendarDate#isAfter(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_Comparison() {
    assert(feb17.isBefore(mar13) == true)
    assert(mar13.isBefore(feb17) == false)
    assert(feb17.isBefore(feb17) == false)
    assert(feb17.isAfter(mar13) == false)
    assert(mar13.isAfter(feb17) == true)
    assert(feb17.isAfter(feb17) == false)
  }

  /**
   * [[CalendarDate#startAsTimePoint(TimeZone)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_StartAsTimePoint() {
    val feb17StartAsCt = feb17.startAsTimePoint(ct)
    val feb17Hour0Ct = TimePoint.atMidnight(2003, 2, 17, ct)
    assert(feb17StartAsCt == feb17Hour0Ct)
  }

  /**
   * [[CalendarDate#asTimeInterval(TimeZone)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_AsTimeInterval() {
    val feb17AsCt = feb17.asTimeInterval(ct)
    val feb17Hour0Ct = TimePoint.atMidnight(2003, 2, 17, ct)
    val feb18Hour0Ct = TimePoint.atMidnight(2003, 2, 18, ct)
    assert(feb17AsCt.start.toValue == feb17Hour0Ct, "start")
    assert(feb17AsCt.end.toValue == feb18Hour0Ct, "end")
  }

  /**
   * [[CalendarDate#toString(String)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_FormattedString() {
    assert(feb17.toString("M/d/yyyy") == "2/17/2003")
    //Now a nonsense pattern, to make sure it isn't succeeding by accident.
    assert(feb17.toString("#d-yy/MM yyyy") == "#17-03/02 2003")
  }

  /**
   * [[CalendarDate#parse(String, String)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_FromFormattedString() {
    assert(CalendarDate.parse("2/17/2003", "M/d/yyyy", ZoneIds.Default) == (feb17))
    //Now a nonsense pattern, to make sure it isn't succeeding by accident.
    assert(CalendarDate.parse("#17-03/02 2003", "#d-yy/MM yyyy", ZoneIds.Default) == (feb17))
  }

  /**
   * [[CalendarDate#from(TimePoint, TimeZone)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_FromTimePoint() {
    val feb18Hour0Ct = TimePoint.atMidnight(2003, 2, 18, gmt.toZoneId)
    val mapped = CalendarDate.from(feb18Hour0Ct)
    assert(mapped == CalendarDate.from(2003, 2, 18))
  }

  /**
   * [[CalendarDate#equals(Object)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_Equals() {
    assert(feb17.equals(feb17) == (true))
    assert(feb17.equals(mar13) == (false))
  }

  /**
   * [[CalendarDate#dayOfWeek()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_DayOfWeek() {
    val date1 = CalendarDate.from(2004, 11, 6)
    assert(date1.dayOfWeek == DayOfWeek.Saturday)
    val date2 = CalendarDate.from(2007, 1, 1)
    assert(date2.dayOfWeek == DayOfWeek.Monday)
  }

  /**
   * [[CalendarDate#nextDay()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_NextDay() {
    val feb28_2004 = CalendarDate.from(2004, 2, 28)
    assert(feb28_2004.nextDay == CalendarDate.from(2004, 2, 29))
    assert(feb28_2004.nextDay.nextDay == CalendarDate.from(2004, 3, 1))
  }

  /**
   * [[CalendarDate#previousDay()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_PreviousDay() {
    val mar1_2004 = CalendarDate.from(2004, 3, 1)
    assert(mar1_2004.previousDay == CalendarDate.from(2004, 2, 29))
    assert(mar1_2004.previousDay.previousDay == CalendarDate.from(2004, 2, 28))
  }

  /**
   * [[CalendarDate#asMonthInterval()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_Month() {
    val nov6_2004 = CalendarDate.from(2004, 11, 6, ZoneIds.Default)
    val nov2004 = CalendarInterval.inclusive(2004, 11, 1, 2004, 11, 30, ZoneIds.Default)
    assert(nov6_2004.asMonthInterval == nov2004)

    val dec6_2004 = CalendarDate.from(2004, 12, 6, ZoneIds.Default)
    val dec2004 = CalendarInterval.inclusive(2004, 12, 1, 2004, 12, 31, ZoneIds.Default)
    assert(dec6_2004.asMonthInterval == dec2004)

    val feb9_2004 = CalendarDate.from(2004, 2, 9, ZoneIds.Default)
    val feb2004 = CalendarInterval.inclusive(2004, 2, 1, 2004, 2, 29, ZoneIds.Default)
    assert(feb9_2004.asMonthInterval == feb2004)

    val feb9_2003 = CalendarDate.from(2003, 2, 9, ZoneIds.Default)
    val feb2003 = CalendarInterval.inclusive(2003, 2, 1, 2003, 2, 28, ZoneIds.Default)
    assert(feb9_2003.asMonthInterval == feb2003)

  }

  /**
   * [[CalendarDate#toString()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_ToString() {
    val date = CalendarDate.from(2004, 5, 28, ZoneIds.Default)
    assert(date.toString() == "2004/05/28")
  }

  /**
   * [[CalendarDate#asJavaCalendarUniversalZoneMidnight()]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test14_ConversionToJavaUtil() {
    val expected = Calendar.getInstance(gmt)
    expected.set(Calendar.YEAR, 1969)
    expected.set(Calendar.MONTH, Calendar.JULY)
    expected.set(Calendar.DATE, 20)
    expected.set(Calendar.HOUR, 0)
    expected.set(Calendar.AM_PM, Calendar.AM)
    expected.set(Calendar.MINUTE, 0)
    expected.set(Calendar.SECOND, 0)
    expected.set(Calendar.MILLISECOND, 0)

    val date = CalendarDate.from(1969, 7, 20, gmt.toZoneId)
    val actual = date.asJavaCalendarOnMidnight
    assert(actual.get(Calendar.HOUR) == expected.get(Calendar.HOUR))
    assert(actual.get(Calendar.AM_PM) == expected.get(Calendar.AM_PM))
    assert(actual.get(Calendar.HOUR_OF_DAY) == expected.get(Calendar.HOUR_OF_DAY))
    assert(actual == expected)
  }

  @Test
  def test15_ConversionToJavaUtil() {
    val expected = ZonedDateTime.of(1969, 7, 20, 0, 0, 0, 0, gmt.toZoneId)

    val date = CalendarDate.from(1969, 7, 20, gmt.toZoneId)
    val actual = date.asJavaZonedDateTimeOnMidnight(gmt.toZoneId)
    assert(actual == expected)
  }

  /**
   * [[CalendarDate#plusDays(int)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test16_DaysAdd() {
    assert(may1.plusDays(19) == may20)
  }
}