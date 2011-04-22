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
import java.util.TimeZone
import org.junit.Test

/**`CalendarMinute`のテストクラス。
 */
class CalendarMinuteTest extends AssertionsForJUnit {
  val feb17_1_23 = CalendarMinute.from(2003, 2, 17, 1, 23)

  val feb17_3_45 = CalendarMinute.from(2003, 2, 17, 3, 45)

  val feb17_19_42 = CalendarMinute.from(2003, 2, 17, 19, 42)

  val mar13_3_45 = CalendarMinute.from(2003, 3, 13, 3, 45)

  val ct = TimeZone.getTimeZone("America/Chicago")


  /**
   * [[CalendarMinute]]のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Serialization {
    //SerializationTester.assertCanBeSerialized(feb17_1_23)
  }

  /**
   * [[CalendarMinute#isBefore(CalendarMinute)]] と [[CalendarMinute#isAfter(CalendarMinute)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_Comparison {
    assert(feb17_1_23.isBefore(feb17_1_23) == false)
    assert(feb17_1_23.compareTo(feb17_1_23) == 0)

    assert(feb17_3_45.isBefore(feb17_3_45) == false)
    assert(feb17_3_45.compareTo(feb17_3_45) == 0)

    assert(feb17_1_23.isBefore(feb17_3_45) == true)
    assert(feb17_1_23.compareTo(feb17_3_45) < 0)

    assert(feb17_3_45.isBefore(feb17_1_23) == false)
    assert(feb17_3_45.compareTo(feb17_1_23) > 0)

    assert(feb17_1_23.isBefore(feb17_19_42) == true)
    assert(feb17_19_42.isBefore(feb17_1_23) == false)

    assert(feb17_1_23.isBefore(mar13_3_45) == true)
    assert(feb17_1_23.compareTo(mar13_3_45) < 0)

    assert(mar13_3_45.isBefore(feb17_1_23) == false)
    assert(mar13_3_45.isBefore(feb17_1_23) == false)
    assert(feb17_1_23.isBefore(feb17_1_23) == false)
    assert(feb17_1_23.isBefore(feb17_1_23) == false)
    assert(feb17_1_23.isAfter(mar13_3_45) == false)
    assert(mar13_3_45.isAfter(feb17_1_23) == true)
    assert(feb17_1_23.isAfter(feb17_1_23) == false)
  }

  /**
   * [[CalendarMinute#asTimePoint(TimeZone)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_StartAsTimePoint {
    val feb17_1_23AsCt = feb17_1_23.asTimePoint(ct)
    assert(feb17_1_23AsCt == TimePoint.at(2003, 2, 17, 1, 23, ct))
  }

  /**
   * [[CalendarMinute#toString(String, TimeZone)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_FormattedString {
    val zone = TimeZone.getTimeZone("Universal")
    assert(feb17_1_23.toString("M/d/yyyy", zone) == ("2/17/2003"))
    //Now a nonsense pattern, to make sure it isn't succeeding by accident.
    assert(feb17_1_23.toString("#d-yy/MM yyyy", zone) == ("#17-03/02 2003"))
  }

  /**
   * [[CalendarMinute#parse(String, String)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_FromFormattedString {
    assert(CalendarMinute.parse("2/17/2003 01:23", "M/d/yyyy HH:mm") == feb17_1_23)
    //Now a nonsense pattern, to make sure it isn't succeeding by accident.
    assert(CalendarMinute.parse("#17-03/02 2003, 01:23", "#d-yy/MM yyyy, HH:mm") == feb17_1_23)
  }

  /**
   * [[CalendarMinute#equals(Object)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_Equals {
    assert(feb17_1_23.equals(feb17_1_23) == true)
    assert(feb17_1_23.equals(feb17_3_45) == false)
    assert(feb17_1_23.equals(mar13_3_45) == false)
    assert(feb17_1_23.equals(null) == false)
    assert(new CalendarMinute(CalendarDate.from(2003, 2, 17),
      TimeOfDay.from(1, 23)).equals(feb17_1_23) == true)
//    assert(new CalendarMinute(CalendarDate.from(2003, 2, 17),
//      TimeOfDay.from(1, 23)) {
//
//      private static final long serialVersionUID = 8307944665463538049L;
//    }.equals(feb17_1_23) == false)

    assert(feb17_1_23.hashCode == feb17_1_23.hashCode)
    assert(feb17_1_23.hashCode != feb17_3_45.hashCode)
    assert(feb17_1_23.hashCode != mar13_3_45.hashCode)

  }

  /**
   * [[CalendarMinute#breachEncapsulationOfDate()]]
   * [[CalendarMinute#breachEncapsulationOfTime()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_breachEncapsulationOf {
    assert(feb17_1_23.breachEncapsulationOfDate == CalendarDate.from(2003, 2, 17))
    assert(feb17_1_23.breachEncapsulationOfTime == TimeOfDay.from(1, 23))
  }

  /**
   * [[CalendarMinute#toString()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_ToString {
    val date = CalendarMinute.from(2004, 5, 28, 23, 21)
    assert(date.toString == "2004-05-28 at 23:21")
  }
}