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

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import java.math.RoundingMode
import jp.tricreo.baseunits.scala.intervals.Limit

/**`Duration`のテストクラス。
 */
class DurationTest extends AssertionsForJUnit {
  /**
   * [[Duration]]のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Serialization {
    //SerializationTester.assertCanBeSerialized(Duration.days(1))
  }

  /**
   * [[Duration#addedTo(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_AddMillisecondsToPoint {
    val dec20At1 = TimePoint.atGMT(2003, 12, 20, 01, 0, 0, 0)
    val dec22At1 = TimePoint.atGMT(2003, 12, 22, 01, 0, 0, 0)
    val twoDays = Duration.days(2)
    assert(twoDays.addedTo(dec20At1) == dec22At1)

    val fourtyEightHours = Duration.hours(48)
    assert(fourtyEightHours.addedTo(dec20At1) == dec22At1)
  }

  /**
   * [[Duration#addedTo(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_AddMonthsToPoint {
    val oct20At1 = TimePoint.atGMT(2003, 10, 20, 01, 0, 0, 0)
    val dec20At1 = TimePoint.atGMT(2003, 12, 20, 01, 0, 0, 0)
    val twoMonths = Duration.months(2)
    assert(twoMonths.addedTo(oct20At1) == dec20At1)
  }

  /**
   * [[Duration#subtractedFrom(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_SubtractMillisecondsFromPoint {
    val dec20At1 = TimePoint.atGMT(2003, 12, 20, 01, 0, 0, 0)
    val dec18At1 = TimePoint.atGMT(2003, 12, 18, 01, 0, 0, 0)
    val twoDays = Duration.days(2)
    assert(twoDays.subtractedFrom(dec20At1) == dec18At1)

    val fourtyEightHours = Duration.hours(48)
    assert(fourtyEightHours.subtractedFrom(dec20At1) == dec18At1)
  }

  /**
   * [[Duration#subtractedFrom(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_SubtractMonthsFromPoint {
    val oct20At1 = TimePoint.atGMT(2003, 10, 20, 01, 0, 0, 0)
    val dec20At1 = TimePoint.atGMT(2003, 12, 20, 01, 0, 0, 0)
    val twoMonths = Duration.months(2)
    assert(twoMonths.subtractedFrom(dec20At1) == oct20At1)

    val dec20At1_2001 = TimePoint.atGMT(2001, 12, 20, 01, 0, 0, 0)
    val twoYears = Duration.years(2)
    assert(twoYears.subtractedFrom(dec20At1) == dec20At1_2001)
  }

  /**
   * [[Duration#subtractedFrom(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_SubtractFromCalendarDate {
    val oct20 = CalendarDate.from(2003, 10, 20)
    val dec20 = CalendarDate.from(2003, 12, 20)

    val twoMonths = Duration.months(2)
    assert(twoMonths.subtractedFrom(dec20) == oct20)

    val sixtyoneDays = Duration.days(61)
    assert(sixtyoneDays.subtractedFrom(dec20) == oct20)

    val dec20_2001 = CalendarDate.from(2001, 12, 20)
    val twoYears = Duration.years(2)
    assert(twoYears.subtractedFrom(dec20) == dec20_2001)
  }

  /**
   * [[Duration#addedTo(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_AddToCalendarDate {
    val oct20_2003 = CalendarDate.from(2003, 10, 20)
    val dec20_2003 = CalendarDate.from(2003, 12, 20)

    val twoMonths = Duration.months(2)
    assert(twoMonths.addedTo(oct20_2003) == dec20_2003)

    val sixtyoneDays = Duration.days(61)
    assert(sixtyoneDays.addedTo(oct20_2003) == dec20_2003)

    val dec20_2001 = CalendarDate.from(2001, 12, 20)
    val twoYears = Duration.years(2)
    assert(twoYears.addedTo(dec20_2001) == dec20_2003)

    // 単位が日未満の時は日付を変えない。
    val threeHours = Duration.milliseconds(30)
    assert(threeHours.addedTo(CalendarDate.from(2010, 11, 27)) == CalendarDate.from(2010, 11, 27))
  }

  /**
   * [[Duration#inBaseUnits]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_ConversionToBaseUnits {
    val twoSeconds = Duration.seconds(2)
    assert(twoSeconds.inBaseUnits == 2000L)
  }

  /**
   * [[Duration#equals(Object)]]のテスト。
   *
   * 単位が違っていても、baseUnit換算できちんと比較できること。
   * baseUnitに互換性がなければ、必ず{@code false}となること。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_Equals {
    assert(Duration.hours(48) == Duration.days(2))
    assert(Duration.quarters(4) == Duration.years(1))
    assert(Duration.months(6) == Duration.quarters(2))

    assert(Duration.months(1) != Duration.days(28))
    assert(Duration.months(1) != Duration.days(29))
    assert(Duration.months(1) != Duration.days(30))
    assert(Duration.months(1) != Duration.days(31))
  }

  /**
   * [[Duration#plus(Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_Add {
    assert(Duration.hours(24).plus(Duration.days(1)) == Duration.days(2))
    assert(Duration.months(1).plus(Duration.quarters(1)) == Duration.months(4))

    try {
      Duration.days(1).plus(Duration.months(1))
      fail
    } catch {
      case e:IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[Duration#minus(Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_Subtract {
    assert(Duration.days(3).minus(Duration.hours(24)) == Duration.days(2))
    assert(Duration.quarters(1).minus(Duration.months(1)) == Duration.months(2))

    try {
      Duration.months(2).plus(Duration.days(3))
      fail;
    } catch {
      case e:IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[Duration#dividedBy(Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_Divide {
    assert(Duration.days(3).dividedBy(Duration.days(2)).decimalValue(1, BigDecimal.RoundingMode.DOWN) == BigDecimal(
      1.5))
  }

  /**
   * [[Duration#toNormalizedString]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_ToNormalizedString {
    assert(Duration.minutes(0).toNormalizedString == "")
    assert(Duration.days(0).toNormalizedString == "")

    assert(Duration.days(1).toNormalizedString == "1 day")
    assert(Duration.days(2).toNormalizedString == "2 days")

    val complicatedDuration = Duration.daysHoursMinutesSecondsMilliseconds(5, 4, 3, 2, 1)
    assert(complicatedDuration.toNormalizedString == "5 days, 4 hours, 3 minutes, 2 seconds, 1 millisecond")
    assert(Duration.days(365).toNormalizedString == "52 weeks, 1 day")
  }

  /**
   * [[Duration#toNormalizedString]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test14_ToNormalizedStringMonthBased {
    assert(Duration.months(2).toNormalizedString == "2 months")
    assert(Duration.months(16).toNormalizedString == "1 year, 1 quarter, 1 month")

    assert(Duration.quarters(3).toNormalizedString == "3 quarters")
    assert(Duration.quarters(4).toNormalizedString == "1 year")
    assert(Duration.quarters(8).toNormalizedString == "2 years")
  }

  /**
   * [[Duration#toString]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test15_ToString {
    assert(Duration.weeks(3).toString == "21 days") //Weeks are not conventional to read.
    assert(Duration.months(2).toNormalizedString == "2 months")
    assert(Duration.months(16).toString == "1 year, 4 months") //Quarters are not conventionalto read.
    assert(Duration.quarters(3).toString == "9 months")
    assert(Duration.quarters(4).toString == "1 year")
    assert(Duration.quarters(5).toString == "1 year, 3 months")
    assert(Duration.quarters(8).toString == "2 years")
  }

  /**
   * [[Duration#compareTo(Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test16_Compare {
    // convertable units
    val oneHour = Duration.hours(1)
    val twoHours = Duration.hours(2)
    val sixtyMinutes = Duration.minutes(60)
    val sixtyMinutesAndOneMillisec = Duration.daysHoursMinutesSecondsMilliseconds(0, 0, 60, 0, 1)
    assert(oneHour.compareTo(twoHours) < 0)
    assert(oneHour.compareTo(sixtyMinutes) == 0)
    assert(twoHours.compareTo(oneHour) > 0)
    assert(oneHour.compareTo(sixtyMinutesAndOneMillisec) < 0)

    // nonconvertable units
    val twoQuarters = Duration.quarters(2)
    try {
      oneHour.compareTo(twoQuarters)
      fail
    } catch {
      case e:ClassCastException => // success
      case _ => fail
    }

    val threeHundredsAndSixtyFiveDays = Duration.days(365)
    val anYear = Duration.years(1)
    try {
      threeHundredsAndSixtyFiveDays.compareTo(anYear)
      fail
    } catch {
      case e:ClassCastException => // success
      case _ => fail
    }

    // nonconvertable units but zero
    val zeroMonths = Duration.months(0)
    val zeroMillisecs = Duration.milliseconds(0)
    assert(twoHours.compareTo(zeroMonths) > 0)
    assert(zeroMonths.compareTo(zeroMillisecs) == (0))

    try {
      twoHours.compareTo(null)
      fail
    } catch {
      case e:NullPointerException => // success
      case _ => fail
    }
  }

  /**
   * [[Duration#startingFrom(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test17_StartingFromTimePoint {
    val dec20At1 = TimePoint.atGMT(2003, 12, 20, 01, 0, 0, 0)
    val dec20At3 = TimePoint.atGMT(2003, 12, 20, 03, 0, 0, 0)
    val dec20_1_3 = dec20At1.until(Limit(dec20At3))
    assert(Duration.hours(2).startingFromTimePoint(Limit(dec20At1)) == dec20_1_3)
  }

  /**
   * [[Duration#startingFrom(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test18_StartingFromCalendarDate {
    val dec20 = CalendarDate.from(2004, 12, 20)
    val dec26 = CalendarDate.from(2004, 12, 26)
    val dec20_26 = dec20.through(dec26)
    assert(Duration.days(7).startingFromCalendarDate(Limit(dec20)) == dec20_26)
  }

  /**
   * [[Duration#normalizedUnit]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test19_NormalizedUnit {
    assert(Duration.seconds(30).normalizedUnit == TimeUnit.second)
    assert(Duration.seconds(120).normalizedUnit == TimeUnit.minute)
    assert(Duration.hours(24).normalizedUnit == TimeUnit.day)
    assert(Duration.hours(25).normalizedUnit == TimeUnit.hour)
  }

  /**
   * [[Duration#addedTo(CalendarMonth)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test2o_AddToCalendarMonth {
    val oct2003 = CalendarMonth.from(2003, 10)
    val dec2003 = CalendarMonth.from(2003, 12)

    val twoMonths = Duration.months(2)
    assert(twoMonths.addedTo(oct2003) == dec2003)

    val sixtyoneDays = Duration.days(61)
    assert(sixtyoneDays.addedTo(oct2003) == oct2003)

    val dec2001 = CalendarMonth.from(2001, 12)
    val twoYears = Duration.years(2)
    assert(twoYears.addedTo(dec2001) == dec2003)

    // 単位が日未満の時は日付を変えない。
    val threeHours = Duration.days(30)
    assert(threeHours.addedTo(CalendarMonth.from(2010, 11)) == CalendarMonth.from(2010, 11))
  }
}