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
import org.sisioh.baseunits.scala.intervals.{Limitless, Limit}

/**`CalendarInterval`のテストクラス。
 */
class CalendarIntervalTest extends AssertionsForJUnit {

  val may1 = CalendarDate.from(2004, 5, 1)

  val may2 = CalendarDate.from(2004, 5, 2)

  val may3 = CalendarDate.from(2004, 5, 3)

  val may14 = CalendarDate.from(2004, 5, 14)

  val may20 = CalendarDate.from(2004, 5, 20)

  val may31 = CalendarDate.from(2004, 5, 31)

  val apr15 = CalendarDate.from(2004, 4, 15)

  val jun1 = CalendarDate.from(2004, 6, 1)

  val may = CalendarInterval.inclusive(2004, 5, 1, 2004, 5, 31)

  val ct = TimeZone.getTimeZone("America/Chicago")


  /**
   * [[CalendarInterval]]のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  //@Test
  //def test01_Serialization {
    //SerializationTester.assertCanBeSerialized(may)
  //}

  /**
   * [[CalendarInterval#asTimeInterval(TimeZone)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_TranslationToTimeInterval {
    val day = may20.asTimeInterval(ct)
    assert(day.start == TimePoint.atMidnight(2004, 5, 20, ct), "May20Ct")
  }

  /**
   * [[CalendarInterval#equals(Interval)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_Equals {
    assert(may == CalendarInterval.inclusive(Limit(may1), Limit(may31)))
    assert(may != may1)
    assert(may != CalendarInterval.inclusive(Limit(may1), Limit(may20)))
  }

  /**
   * [[CalendarInterval#includes(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_Includes {
    assert(may.includes(Limit(apr15)) == false, "apr15")
    assert(may.includes(Limit(may1)) == true, "may1")
    assert(may.includes(Limit(may20)) == true, "may20")
    assert(may.includes(Limit(jun1)) == false, "jun1")
    assert(may.covers(may) == true, "may")
  }

  /**
   * [[CalendarInterval#daysIterator]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_DaysIterator {
    val iterator = CalendarInterval.inclusive(Limit(may1), Limit(may3)).daysIterator;
    assert(iterator.hasNext == true)
    assert(iterator.next == may1)
    assert(iterator.hasNext == true)
    assert(iterator.next == may2)
    assert(iterator.hasNext == true)
    assert(iterator.next == may3)
    assert(iterator.hasNext == false)
    try {
      iterator.next
      fail
    } catch {
      case e: NoSuchElementException => // success
      case _ => fail
    }

    try {
      CalendarInterval.everPreceding(Limit(may1)).daysIterator
      fail
    } catch {
      case e: IllegalStateException => // success
      case _ => fail
    }
  }

  /**
   * [[CalendarInterval#subintervalIterator(Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_SubintervalIterator {
    val may1_3 = CalendarInterval.inclusive(Limit(may1), Limit(may3))
    var iterator = may1_3.subintervalIterator(Duration.days(1))
    assert(iterator.hasNext == true)
    assert(iterator.next.start == may1)
    assert(iterator.hasNext == true)
    assert(iterator.next.start == may2)
    assert(iterator.hasNext == true)
    assert(iterator.next.start == may3)
    assert(iterator.hasNext == false)

    iterator = may1_3.subintervalIterator(Duration.days(2))
    assert(iterator.hasNext == true)
    assert(iterator.next == may1.through(may2))
    assert(iterator.hasNext == false)

    try {
      iterator = may1_3.subintervalIterator(Duration.hours(25))
      fail("CalendarInterval should not accept subinterval length that is not a multiple of days.")
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }

    iterator = may1_3.subintervalIterator(Duration.months(1))
    assert(iterator.hasNext == (false))
    try {
      iterator.next
      fail
    } catch {
      case e: NoSuchElementException => // success
      case _ => fail
    }

    val apr15_jun1 = CalendarInterval.inclusive(Limit(apr15), Limit(jun1))
    iterator = apr15_jun1.subintervalIterator(Duration.months(1))
    assert(iterator.hasNext == true)
    assert(iterator.next == apr15.through(may14))
    assert(iterator.hasNext == false)

    try {
      CalendarInterval.everPreceding(Limit(may1)).subintervalIterator(Duration.months(1))
      fail
    } catch {
      case e: IllegalStateException => // success
      case _ => fail
    }
  }

  /**
   * [[CalendarInterval#length]] [[CalendarInterval#lengthInMonths]] のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_Length {
    assert(may1.through(may3).length == Duration.days(3))
    val may2002_july2004 = CalendarInterval.inclusive(2002, 5, 1, 2004, 7, 1)
    // (5/1/2002-4/30/2003) 365 days + (-4/30/2004) 366 + (5/1-7/31) 31+30+1 = 793 days
    assert(may2002_july2004.length == (Duration.days(793)))
    assert(may2002_july2004.lengthInMonths == (Duration.months(26)))
    assert(apr15.through(may14).lengthInMonths == (Duration.months(1)))
  }

  /**
   * [[CalendarInterval#complementRelativeTo(Interval)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_Complements {
    val may1Onward = CalendarInterval.inclusive(Limit(may1), Limitless[CalendarDate])
    val may2Onward = CalendarInterval.inclusive(Limit(may2), Limitless[CalendarDate])
    val complementList = may2Onward.complementRelativeTo(may1Onward)
    assert(complementList.size == 1)

    val complement = complementList.iterator.next.asInstanceOf[CalendarInterval]
    assert(complement.isClosed == true)
    assert(complement.start == may1)
    assert(complement.end == may1)
  }

  /**
   * [[CalendarInterval#everFrom(CalendarDate)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_EverFromToString {
    val x = CalendarDate.from(2007, 6, 5)
    val i = CalendarInterval.everFrom(Limit(x))
    assert(i.toString == "[Limit(2007-06-05), Infinity)")
  }

  /**
   * [[CalendarInterval]]のインスタンス生成テスト。
   *
   * [ 1792849 ] ConcreteCalendarInterval allows misordered limits
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_BackwardCalendarIvalIntersection {
    try {
      CalendarInterval.inclusive(2001, 1, 1, 1776, 7, 4)
      fail
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[CalendarInterval#startingFrom(CalendarDate, Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_StartingFrom {
    val d1 = CalendarInterval.startingFrom(Limit(may1), Duration.days(2))
    val expected1 = CalendarInterval.inclusive(2004, 5, 1, 2004, 5, 2)
    assert(d1 == (expected1))

    val d2 = CalendarInterval.startingFrom(Limit(may1), Duration.minutes(2))
    val expected2 = CalendarInterval.inclusive(2004, 5, 1, 2004, 5, 1)
    assert(d2 == (expected2))
  }
}