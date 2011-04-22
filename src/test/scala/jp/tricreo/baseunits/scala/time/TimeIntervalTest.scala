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
import jp.tricreo.baseunits.scala.intervals.Limit
import jp.tricreo.baseunits.scala.tests.SerializationTester

/**`TimeInterval`のテストクラス。
 */
class TimeIntervalTest extends AssertionsForJUnit {

  val dec19_2003 = TimePoint.atMidnightGMT(2003, 12, 19)

  val dec20_2003 = TimePoint.atMidnightGMT(2003, 12, 20)

  val dec21_2003 = TimePoint.atMidnightGMT(2003, 12, 21)

  val dec22_2003 = TimePoint.atMidnightGMT(2003, 12, 22)

  val dec23_2003 = TimePoint.atMidnightGMT(2003, 12, 23)


  /**
   * [[TimeInterval]]のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Serialization {
    val interval = TimeInterval.closed(Limit(dec20_2003), Limit(dec22_2003))
    SerializationTester.assertCanBeSerialized(interval)
  }

  /**
   * [[TimeInterval#isBefore(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_BeforeClosed {
    val interval = TimeInterval.closed(Limit(dec20_2003), Limit(dec22_2003))
    // Only the upper end should matter for this test.
    assert(interval.isBefore(Limit(dec21_2003)) == false)
    assert(interval.isBefore(Limit(dec22_2003)) == false)
    assert(interval.isBefore(Limit(dec23_2003)) == true)
  }

  /**
   * [[TimeInterval#isAfter(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_AfterClosed {
    val interval = TimeInterval.closed(Limit(dec20_2003), Limit(dec22_2003))
    // Only the lower end should matter for this test.
    assert(interval.isAfter(Limit(dec19_2003)) == true)
    assert(interval.isAfter(Limit(dec20_2003)) == false)
    assert(interval.isAfter(Limit(dec21_2003)) == false)
  }

  /**
   * [[TimeInterval#includes(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_IncludesClosed {
    val interval = TimeInterval.closed(Limit(dec20_2003), Limit(dec22_2003))
    assert(interval.includes(Limit(dec19_2003)) == false)
    assert(interval.includes(Limit(dec20_2003)) == true)
    assert(interval.includes(Limit(dec21_2003)) == true)
    assert(interval.includes(Limit(dec22_2003)) == true)
    assert(interval.includes(Limit(dec23_2003)) == false)
  }

  /**
   * [[TimeInterval#isBefore(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_BeforeOpen {
    val interval = TimeInterval.open(Limit(dec20_2003), Limit(dec22_2003))
    // Only the upper end should matter for this test.
    assert(interval.isBefore(Limit(dec21_2003)) == false)
    assert(interval.isBefore(Limit(dec22_2003)) == true)
    assert(interval.isBefore(Limit(dec23_2003)) == true)
  }

  /**
   * [[TimeInterval#isAfter(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_AfterOpen {
    val interval = TimeInterval.open(Limit(dec20_2003), Limit(dec22_2003))
    // Only the lower end should matter for this test.
    assert(interval.isAfter(Limit(dec19_2003)) == true)
    assert(interval.isAfter(Limit(dec20_2003)) == true)
    assert(interval.isAfter(Limit(dec21_2003)) == false)
  }

  /**
   * [[TimeInterval#includes(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_IncludesOpen {
    val interval = TimeInterval.open(Limit(dec20_2003), Limit(dec22_2003))
    assert(interval.includes(Limit(dec19_2003)) == false)
    assert(interval.includes(Limit(dec20_2003)) == false)
    assert(interval.includes(Limit(dec21_2003)) == true)
    assert(interval.includes(Limit(dec22_2003)) == false)
    assert(interval.includes(Limit(dec23_2003)) == false)
  }

  /**
   * [[TimeInterval#includes(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_IncludesHalfOpen {
    val interval = TimeInterval.over(Limit(dec20_2003), true, Limit(dec22_2003), false)
    assert(interval.includes(Limit(dec19_2003)) == false)
    assert(interval.includes(Limit(dec20_2003)) == true)
    assert(interval.includes(Limit(dec21_2003)) == true)
    assert(interval.includes(Limit(dec22_2003)) == false)
    assert(interval.includes(Limit(dec23_2003)) == false)
  }

  /**
   * [[TimeInterval#startingFrom(TimePoint, Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_CreateWithDurationFrom {
    val twoDays = Duration.days(2)
    val following = TimeInterval.startingFrom(Limit(dec20_2003), true, twoDays, true)
    assert(following.start == Limit(dec20_2003), "[ dec20")
    assert(following.end == Limit(dec22_2003), "dec 22]")

  }

  /**
   * [[TimeInterval#preceding(TimePoint, Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_CreateWithDurationUntil {
    val twoDays = Duration.days(2)
    val preceding = TimeInterval.preceding(Limit(dec21_2003), true, twoDays, true)
    assert(preceding.start == Limit(dec19_2003), "[ dec19")
    assert(preceding.end == Limit(dec21_2003), "dec21 )")
  }

  /**
   * [[TimeInterval#over(TimePoint, TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_DefaultFromPoints {
    /*       Default is closed start, open end [start, end)
              which is the most common convention. For example,
              Days include 12:00am at their start, but do not
              include the 12:00am that end them.
     */
    val interval = TimeInterval.over(Limit(dec20_2003), Limit(dec22_2003))
    assert(interval.includes(Limit(dec19_2003)) == false)
    assert(interval.includes(Limit(dec20_2003)) == true)
    assert(interval.includes(Limit(dec21_2003)) == true)
    assert(interval.includes(Limit(dec22_2003)) == false)
    assert(interval.includes(Limit(dec23_2003)) == false)
  }

  /**
   * [[TimeInterval#startingFrom(TimePoint, Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_DefaultFromDuration {
    /*       Default is closed start, open end [start, end)
              which is the most common convention. For example,
              Days include 12:00am at their start, but do not
              include the 12:00am that end them.
     */
    val interval = TimeInterval.startingFrom(Limit(dec20_2003), Duration.hours(48))
    assert(interval.includes(Limit(dec19_2003)) == false)
    assert(interval.includes(Limit(dec20_2003)) == true)
    assert(interval.includes(Limit(dec21_2003)) == true)
    assert(interval.includes(Limit(dec22_2003)) == false)
    assert(interval.includes(Limit(dec23_2003)) == false)
  }

  /**
   * [[TimeInterval#everFrom(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_EverFrom {
    val afterDec20 = TimeInterval.everFrom(Limit(dec20_2003))
    assert(afterDec20.includes(Limit(TimePoint.atMidnightGMT(2062, 3, 5))) == true)
    assert(afterDec20.includes(Limit(TimePoint.atMidnightGMT(1776, 7, 4))) == false)
    assert(afterDec20.includes(Limit(dec20_2003)) == true)
  }

  /**
   * [[TimeInterval#everPreceding(TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test14_EverUntil {
    val afterDec20 = TimeInterval.everPreceding(Limit(dec20_2003))
    assert(afterDec20.includes(Limit(TimePoint.atMidnightGMT(2062, 3, 5))) == false)
    assert(afterDec20.includes(Limit(TimePoint.atMidnightGMT(1776, 7, 4))) == true)
    assert(afterDec20.includes(Limit(dec20_2003)) == false)
  }

  /**
   * [[TimeInterval#open(TimePoint, TimePoint)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test15_Length {
    var interval = TimeInterval.open(Limit(dec20_2003), Limit(dec22_2003))
    assert(interval.length == Duration.days(2))

    val first = TimePoint.atGMT(2004, 1, 1, 1, 1, 1, 1)
    val second = TimePoint.atGMT(2004, 1, 6, 5, 4, 3, 2)
    interval = TimeInterval.closed(Limit(first), Limit(second))
    val expectedLength = Duration.daysHoursMinutesSecondsMilliseconds(5, 4, 3, 2, 1)
    assert(interval.length == (expectedLength))
  }

  /**
   * [[TimeInterval#daysIterator]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test16_DaysIterator {
    val start = TimePoint.atGMT(2004, 2, 5, 10, 0)
    val end = TimePoint.atGMT(2004, 2, 8, 2, 0)
    val interval = TimeInterval.over(Limit(start), Limit(end))
    val it = interval.daysIterator;
    assert(it.hasNext == true)
    assert(it.next == (start))
    assert(it.hasNext == true)
    assert(it.next == (TimePoint.atGMT(2004, 2, 6, 10, 0)))
    assert(it.hasNext == true)
    assert(it.next == (TimePoint.atGMT(2004, 2, 7, 10, 0)))
    assert(it.hasNext == false)
    try {
      it.next
      fail
    } catch {
      case e: NoSuchElementException => // success
      case _ => fail
    }

    val interval2 = TimeInterval.everPreceding(Limit(end))
    try {
      interval2.daysIterator;
      fail;
    } catch {
      case e: IllegalStateException => // success
      case _ => fail
    }

    val interval3 = TimeInterval.everFrom(Limit(start))
    val it3 = interval3.daysIterator
    for (i <- 0 until 100) {
      assert(it3.hasNext == true)
    }
  }

  /**
   * [[TimeInterval#subintervalIterator(Duration)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test17_SubintervalIterator {
    val d4_h10 = TimePoint.atGMT(2004, 2, 4, 10, 0)
    val d6_h10 = TimePoint.atGMT(2004, 2, 6, 10, 0)
    val d8_h10 = TimePoint.atGMT(2004, 2, 8, 10, 0)
    val d9_h2 = TimePoint.atGMT(2004, 2, 9, 2, 0)

    val interval = TimeInterval.over(Limit(d4_h10), Limit(d9_h2))
    var iterator = interval.subintervalIterator(Duration.days(2))
    assert(iterator.hasNext == true)
    assert(iterator.next == (TimeInterval.over(Limit(d4_h10), Limit(d6_h10))))
    assert(iterator.hasNext == true)
    assert(iterator.next == (TimeInterval.over(Limit(d6_h10), Limit(d8_h10))))
    assert(iterator.hasNext == false)
    try {
      iterator.next
      fail;
    } catch {
      case e: NoSuchElementException => // success
      case _ => fail()
    }

    iterator = interval.subintervalIterator(Duration.weeks(1))
    assert(iterator.hasNext == false)

    val h2 = d9_h2;
    val h3_m30 = TimePoint.atGMT(2004, 2, 9, 3, 30)
    val h5 = TimePoint.atGMT(2004, 2, 9, 5, 0)
    val h6_m30 = TimePoint.atGMT(2004, 2, 9, 6, 30)
    val h8 = TimePoint.atGMT(2004, 2, 9, 8, 0)

    val interval2 = TimeInterval.over(Limit(h2), Limit(h8))
    iterator = interval2.subintervalIterator(Duration.minutes(90))
    assert(iterator.hasNext == true)
    assert(iterator.next == TimeInterval.over(Limit(h2), Limit(h3_m30)))
    assert(iterator.hasNext == true)
    assert(iterator.next == TimeInterval.over(Limit(h3_m30), Limit(h5)))
    assert(iterator.hasNext == true)
    assert(iterator.next == TimeInterval.over(Limit(h5), Limit(h6_m30)))
    assert(iterator.hasNext == true)
    assert(iterator.next == TimeInterval.over(Limit(h6_m30), Limit(h8)))
    assert(iterator.hasNext == false)

    try {
      iterator.next
      fail
    } catch {
      case e: NoSuchElementException => // success
      case _ => fail
    }
  }

  /**
   * [[TimeInterval#intersects(Interval)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test18_Intersection {
    val i19_22 = TimeInterval.over(Limit(dec19_2003), Limit(dec22_2003))
    val i20_23 = TimeInterval.over(Limit(dec20_2003), Limit(dec23_2003))
    val intersection = i19_22.intersect(i20_23)
    assert(intersection.start == Limit(dec20_2003))
    assert(intersection.end == Limit(dec22_2003))
    assert(i19_22.intersects(i20_23) == true, "intersects true")

    val i19_21 = TimeInterval.over(Limit(dec19_2003), Limit(dec21_2003))
    val i22_23 = TimeInterval.over(Limit(dec22_2003), Limit(dec23_2003))
    assert(i19_21.intersects(i22_23) == false, "intersects false")
  }
}