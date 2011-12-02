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

import org.scalatest.junit.AssertionsForJUnit
import java.util.TimeZone
import org.junit.Test


/**`TimeOfDay`のテストクラス。
 */
class TimeOfDayTest extends AssertionsForJUnit {

  val CST = TimeZone.getTimeZone("CST")

  val feb17 = CalendarDate.from(2006, 2, 17)

  val midnight = TimeOfDay.from(0, 0)

  val morning = TimeOfDay.from(10, 20)

  val noon = TimeOfDay.from(12, 0)

  val afternoon = TimeOfDay.from(15, 40)

  val twoMinutesBeforeMidnight = TimeOfDay.from(23, 58)

  val tenMinutesBeforeMidnight = TimeOfDay.from(23, 50)


  /**
   * [[TimeOfDay#on(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_OnStartOfDay {
    val feb17AtStartOfDay = CalendarMinute.from(2006, 2, 17, 0, 0)
    assert(midnight.on(feb17) == feb17AtStartOfDay)
  }

  /**
   * [[TimeOfDay#on(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_OnMiddleOfDay {
    val feb17AtMiddleOfDay = CalendarMinute.from(2006, 2, 17, 12, 0)
    assert(noon.on(feb17) == feb17AtMiddleOfDay)
  }

  /**
   * [[TimeOfDay#on(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_OnEndOfDay {
    val feb17AtEndOfDay = CalendarMinute.from(2006, 2, 17, 23, 58)
    assert(twoMinutesBeforeMidnight.on(feb17) == (feb17AtEndOfDay))
  }

  /**
   * [[TimeOfDay#equals(Object)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_Equals {
    assert(midnight.equals(TimeOfDay.from(0, 0)) == true)
    assert(morning.equals(TimeOfDay.from(10, 20)) == true)
    assert(noon.equals(TimeOfDay.from(12, 0)) == true)
    assert(afternoon.equals(TimeOfDay.from(15, 40)) == true)
    assert(twoMinutesBeforeMidnight.equals(TimeOfDay.from(23, 58)) == true)

    assert(midnight.equals(morning) == (false))
    assert(morning.equals(null) == (false))
    assert(tenMinutesBeforeMidnight.equals(twoMinutesBeforeMidnight) == false)
    assert(noon.equals(new TimeOfDay(HourOfDay(12), MinuteOfHour(0))) == true)
//    assert(noon.equals(new TimeOfDay(HourOfDay(12), MinuteOfHour(0)) {
//    }) == false)
  }

  /**
   * [[TimeOfDay#hashCode]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_HashCode {
    assert(midnight.hashCode == TimeOfDay.from(0, 0).hashCode)
    assert(morning.hashCode == TimeOfDay.from(10, 20).hashCode)
    assert(noon.hashCode == TimeOfDay.from(12, 0).hashCode)
    assert(afternoon.hashCode == TimeOfDay.from(15, 40).hashCode)
    assert(twoMinutesBeforeMidnight.hashCode == TimeOfDay.from(23, 58).hashCode)
  }

  /**
   * [[TimeOfDay#isAfter(TimeOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_AfterWithEarlierTimeOfDay {
    assert(twoMinutesBeforeMidnight.isAfter(midnight) == true, "expected twoMinutesBeforeMidnight to be after midnight")
    assert(afternoon.isAfter(morning) == true, "expected afternoon to be after morning")
    assert(noon.isAfter(midnight) == true, "expected noon to be after midnight")
  }

  /**
   * [[TimeOfDay#isAfter(TimeOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_AfterWithLaterTimeOfDay {
    assert(midnight.isAfter(twoMinutesBeforeMidnight) == false, "expected midnight not after twoMinutesBeforeMidnight")
    assert(morning.isAfter(afternoon) == false, "expected morning not after afternoon")
    assert(noon.isAfter(twoMinutesBeforeMidnight) == false, "expected noon not after twoMinutesBeforeMidnight")
  }

  /**
   * [[TimeOfDay#isAfter(TimeOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_AfterWithSameTimeOfDay {
    assert(midnight.isAfter(midnight) == false, "expected midnight not after midnight")
    assert(morning.isAfter(morning) == false, "expected morning not after morning")
    assert(afternoon.isAfter(afternoon) == false, "expected afternoon not after afternoon")
    assert(noon.isAfter(noon) == false, "expected noon not after noon")
  }

  /**
   * [[TimeOfDay#isBefore(TimeOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_BeforeWithEarlierTimeOfDay {
    assert(twoMinutesBeforeMidnight.isBefore(midnight) == false, "expected twoMinutesBeforeMidnight not after midnight")
    assert(afternoon.isBefore(morning) == false, "expected afternoon not after morning")
    assert(noon.isBefore(midnight) == false, "expected noon not after midnight")
  }

  /**
   * [[TimeOfDay#isBefore(TimeOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_BeforeWithLaterTimeOfDay {
    assert(midnight.isBefore(twoMinutesBeforeMidnight) == true, "expected midnight not after twoMinutesBeforeMidnight")
    assert(morning.isBefore(afternoon) == true, "expected morning not after afternoon")
    assert(noon.isBefore(twoMinutesBeforeMidnight) == true, "expected noon not after twoMinutesBeforeMidnight")
  }

  /**
   * [[TimeOfDay#isBefore(TimeOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_BeforeWithSameTimeOfDay {
    assert(midnight.isBefore(midnight) == false, "expected midnight not after midnight")
    assert(morning.isBefore(morning) == false, "expected morning not after morning")
    assert(afternoon.isBefore(afternoon) == false, "expected afternoon not after afternoon")
    assert(noon.isBefore(noon) == false, "expected noon not after noon")
  }

  /**
   * [[TimeOfDay#breachEncapsulationOfHour]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_GetHour {
    assert(midnight.breachEncapsulationOfHour == HourOfDay(0))
    assert(morning.breachEncapsulationOfHour == HourOfDay(10))
    assert(noon.breachEncapsulationOfHour == HourOfDay(12))
    assert(afternoon.breachEncapsulationOfHour == HourOfDay(15))
    assert(twoMinutesBeforeMidnight.breachEncapsulationOfHour == HourOfDay(23))
  }

  /**
   * [[TimeOfDay#breachEncapsulationOfMinute]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_GetMinute {
    assert(midnight.breachEncapsulationOfMinute == MinuteOfHour(0))
    assert(morning.breachEncapsulationOfMinute == MinuteOfHour(20))
    assert(noon.breachEncapsulationOfMinute == MinuteOfHour(0))
    assert(afternoon.breachEncapsulationOfMinute == MinuteOfHour(40))
    assert(twoMinutesBeforeMidnight.breachEncapsulationOfMinute == MinuteOfHour(58))
  }

  /**
   * [[TimeOfDay#asTimePointGiven(CalendarDate, TimeZone)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test14_AsTimePoint {
    val fiveFifteen = TimeOfDay.from(17, 15)
    val mayEleventh = CalendarDate.from(2006, 5, 11)
    val mayEleventhAtFiveFifteen = fiveFifteen.asTimePointGiven(mayEleventh, CST)
    assert(mayEleventhAtFiveFifteen == TimePoint.at(2006, 5, 11, 17, 15, 0, 0, CST))
  }
}