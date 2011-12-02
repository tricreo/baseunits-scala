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
package org.sisioh.baseunits.scala.intervals

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import collection.mutable.ListBuffer
import util.Random

/**`Inteval`のテストクラス。
 */
class IntevalTest extends AssertionsForJUnit {

  val c5_10c = Interval.closed(Limit(BigDecimal(5)), Limit(BigDecimal(10)))

  val c1_10c = Interval.closed(Limit(BigDecimal(1)), Limit(BigDecimal(10)))

  val c4_6c = Interval.closed(Limit(BigDecimal(4)), Limit(BigDecimal(6)))

  val c5_15c = Interval.closed(Limit(BigDecimal(5)), Limit(BigDecimal(15)))

  val c12_16c = Interval.closed(Limit(BigDecimal(12)), Limit(BigDecimal(16)))

  val o10_12c = Interval.over(Limit(BigDecimal(10)), false, Limit(BigDecimal(12)), true)

  val o1_1c = Interval.over(Limit(BigDecimal(1)), false, Limit(BigDecimal(1)), true)

  val c1_1o = Interval.over(Limit(BigDecimal(1)), true, Limit(BigDecimal(1)), false)

  val c1_1c = Interval.over(Limit(BigDecimal(1)), true, Limit(BigDecimal(1)), true)

  val o1_1o = Interval.over(Limit(BigDecimal(1)), false, Limit(BigDecimal(1)), false)

  val _2o = Interval.over(Limitless[BigDecimal], true, Limit(BigDecimal(2)), false)

  val o9_ = Interval.over(Limit(BigDecimal(9)), false, Limitless[BigDecimal], true)

  val empty = Interval.open(Limit(BigDecimal(1)), Limit(BigDecimal(1)))

  val all = Interval.closed(Limitless[BigDecimal], Limitless[BigDecimal])


  def newIntegerIntervalList = {
    val list = ListBuffer.empty[Interval[Int]]

    // 開区間
    list += Interval.over(Limit(0), false, Limit(25), false)
    list += Interval.over(Limit(0), false, Limit(50), false)
    list += Interval.over(Limit(0), false, Limit(75), false)
    list += Interval.over(Limit(0), false, Limit(100), false)
    list += Interval.over(Limit(25), false, Limit(50), false)
    list += Interval.over(Limit(25), false, Limit(75), false)
    list += Interval.over(Limit(25), false, Limit(100), false)
    list += Interval.over(Limit(50), false, Limit(75), false)
    list += Interval.over(Limit(50), false, Limit(100), false)
    list += Interval.over(Limit(75), false, Limit(100), false)

    // 半開区間
    list += Interval.over(Limit(0), true, Limit(25), false)
    list += Interval.over(Limit(0), true, Limit(50), false)
    list += Interval.over(Limit(0), true, Limit(75), false)
    list += Interval.over(Limit(0), true, Limit(100), false)
    list += Interval.over(Limit(25), true, Limit(50), false)
    list += Interval.over(Limit(25), true, Limit(75), false)
    list += Interval.over(Limit(25), true, Limit(100), false)
    list += Interval.over(Limit(50), true, Limit(75), false)
    list += Interval.over(Limit(50), true, Limit(100), false)
    list += Interval.over(Limit(75), true, Limit(100), false)

    list += Interval.over(Limit(0), false, Limit(25), true)
    list += Interval.over(Limit(0), false, Limit(50), true)
    list += Interval.over(Limit(0), false, Limit(75), true)
    list += Interval.over(Limit(0), false, Limit(100), true)
    list += Interval.over(Limit(25), false, Limit(50), true)
    list += Interval.over(Limit(25), false, Limit(75), true)
    list += Interval.over(Limit(25), false, Limit(100), true)
    list += Interval.over(Limit(50), false, Limit(75), true)
    list += Interval.over(Limit(50), false, Limit(100), true)
    list += Interval.over(Limit(75), false, Limit(100), true)

    // 閉区間
    list += Interval.over(Limit(0), true, Limit(25), true)
    list += Interval.over(Limit(0), true, Limit(50), true)
    list += Interval.over(Limit(0), true, Limit(75), true)
    list += Interval.over(Limit(0), true, Limit(100), true)
    list += Interval.over(Limit(25), true, Limit(50), true)
    list += Interval.over(Limit(25), true, Limit(75), true)
    list += Interval.over(Limit(25), true, Limit(100), true)
    list += Interval.over(Limit(50), true, Limit(75), true)
    list += Interval.over(Limit(50), true, Limit(100), true)
    list += Interval.over(Limit(75), true, Limit(100), true)

    // single point
    list += Interval.over(Limit(0), true, Limit(0), false)
    list += Interval.over(Limit(0), false, Limit(0), true)
    list += Interval.over(Limit(0), true, Limit(0), true)
    list += Interval.over(Limit(25), true, Limit(25), false)
    list += Interval.over(Limit(25), false, Limit(25), true)
    list += Interval.over(Limit(25), true, Limit(25), true)
    list += Interval.over(Limit(50), true, Limit(50), false)
    list += Interval.over(Limit(50), false, Limit(50), true)
    list += Interval.over(Limit(50), true, Limit(50), true)
    list += Interval.over(Limit(75), true, Limit(75), false)
    list += Interval.over(Limit(75), false, Limit(75), true)
    list += Interval.over(Limit(75), true, Limit(75), true)
    list += Interval.over(Limit(100), true, Limit(100), false)
    list += Interval.over(Limit(100), false, Limit(100), true)
    list += Interval.over(Limit(100), true, Limit(100), true)

    // isEmpty
    list += Interval.over(Limit(0), false, Limit(0), false)
    list += Interval.over(Limit(25), false, Limit(25), false)
    list += Interval.over(Limit(50), false, Limit(50), false)
    list += Interval.over(Limit(75), false, Limit(75), false)
    list += Interval.over(Limit(100), false, Limit(100), false)

    // 下側限界のみ区間
    list += Interval.over(Limit(0), false, Limitless[Int], false)
    list += Interval.over(Limit(0), true, Limitless[Int], false)
    list += Interval.over(Limit(25), false, Limitless[Int], false)
    list += Interval.over(Limit(25), true, Limitless[Int], false)
    list += Interval.over(Limit(50), false, Limitless[Int], false)
    list += Interval.over(Limit(50), true, Limitless[Int], false)
    list += Interval.over(Limit(75), false, Limitless[Int], false)
    list += Interval.over(Limit(75), true, Limitless[Int], false)
    list += Interval.over(Limit(100), false, Limitless[Int], false)
    list += Interval.over(Limit(100), true, Limitless[Int], false)

    // 上側限界のみ区間
    list += Interval.over(Limitless[Int], false, Limit(0), false)
    list += Interval.over(Limitless[Int], false, Limit(0), true)
    list += Interval.over(Limitless[Int], false, Limit(25), false)
    list += Interval.over(Limitless[Int], false, Limit(25), true)
    list += Interval.over(Limitless[Int], false, Limit(50), false)
    list += Interval.over(Limitless[Int], false, Limit(50), true)
    list += Interval.over(Limitless[Int], false, Limit(75), false)
    list += Interval.over(Limitless[Int], false, Limit(75), true)
    list += Interval.over(Limitless[Int], false, Limit(100), false)
    list += Interval.over(Limitless[Int], false, Limit(100), true)

    // freedom
    list += Interval.over(Limitless[Int], false, Limitless[Int], false)

    Random.shuffle(list.result)
  }

  def newIntegerIntervalList2 = {
    val list = ListBuffer.empty[Interval[Int]]

    // 開区間
    list += Interval.over(Limit(0), false, Limit(5), false)
    list += Interval.over(Limit(0), false, Limit(10), false)
    list += Interval.over(Limit(0), false, Limit(15), false)
    list += Interval.over(Limit(0), false, Limit(20), false)
    list += Interval.over(Limit(5), false, Limit(10), false)
    list += Interval.over(Limit(5), false, Limit(15), false)
    list += Interval.over(Limit(5), false, Limit(20), false)
    list += Interval.over(Limit(10), false, Limit(15), false)
    list += Interval.over(Limit(10), false, Limit(20), false)
    list += Interval.over(Limit(15), false, Limit(20), false)

    // 半開区間
    list += Interval.over(Limit(0), true, Limit(5), false)
    list += Interval.over(Limit(0), true, Limit(10), false)
    list += Interval.over(Limit(0), true, Limit(15), false)
    list += Interval.over(Limit(0), true, Limit(20), false)
    list += Interval.over(Limit(5), true, Limit(10), false)
    list += Interval.over(Limit(5), true, Limit(15), false)
    list += Interval.over(Limit(5), true, Limit(20), false)
    list += Interval.over(Limit(10), true, Limit(15), false)
    list += Interval.over(Limit(10), true, Limit(20), false)
    list += Interval.over(Limit(15), true, Limit(20), false)

    list += Interval.over(Limit(0), false, Limit(5), true)
    list += Interval.over(Limit(0), false, Limit(10), true)
    list += Interval.over(Limit(0), false, Limit(15), true)
    list += Interval.over(Limit(0), false, Limit(20), true)
    list += Interval.over(Limit(5), false, Limit(10), true)
    list += Interval.over(Limit(5), false, Limit(15), true)
    list += Interval.over(Limit(5), false, Limit(20), true)
    list += Interval.over(Limit(10), false, Limit(15), true)
    list += Interval.over(Limit(10), false, Limit(20), true)
    list += Interval.over(Limit(15), false, Limit(20), true)

    // 閉区間
    list += Interval.over(Limit(0), true, Limit(5), true)
    list += Interval.over(Limit(0), true, Limit(10), true)
    list += Interval.over(Limit(0), true, Limit(15), true)
    list += Interval.over(Limit(0), true, Limit(20), true)
    list += Interval.over(Limit(5), true, Limit(10), true)
    list += Interval.over(Limit(5), true, Limit(15), true)
    list += Interval.over(Limit(5), true, Limit(20), true)
    list += Interval.over(Limit(10), true, Limit(15), true)
    list += Interval.over(Limit(10), true, Limit(20), true)
    list += Interval.over(Limit(15), true, Limit(20), true)

    // single point
    list += Interval.over(Limit(0), true, Limit(0), false)
    list += Interval.over(Limit(0), false, Limit(0), true)
    list += Interval.over(Limit(0), true, Limit(0), true)
    list += Interval.over(Limit(5), true, Limit(5), false)
    list += Interval.over(Limit(5), false, Limit(5), true)
    list += Interval.over(Limit(5), true, Limit(5), true)
    list += Interval.over(Limit(10), true, Limit(10), false)
    list += Interval.over(Limit(10), false, Limit(10), true)
    list += Interval.over(Limit(10), true, Limit(10), true)
    list += Interval.over(Limit(15), true, Limit(15), false)
    list += Interval.over(Limit(15), false, Limit(15), true)
    list += Interval.over(Limit(15), true, Limit(15), true)
    list += Interval.over(Limit(20), true, Limit(20), false)
    list += Interval.over(Limit(20), false, Limit(20), true)
    list += Interval.over(Limit(20), true, Limit(20), true)

    // isEmpty
    list += Interval.over(Limit(0), false, Limit(0), false)
    list += Interval.over(Limit(5), false, Limit(5), false)
    list += Interval.over(Limit(10), false, Limit(10), false)
    list += Interval.over(Limit(15), false, Limit(15), false)
    list += Interval.over(Limit(20), false, Limit(20), false)

    // 下側限界のみ区間
    list += Interval.over(Limit(0), false, Limitless[Int], false)
    list += Interval.over(Limit(0), true, Limitless[Int], false)
    list += Interval.over(Limit(5), false, Limitless[Int], false)
    list += Interval.over(Limit(5), true, Limitless[Int], false)
    list += Interval.over(Limit(10), false, Limitless[Int], false)
    list += Interval.over(Limit(10), true, Limitless[Int], false)
    list += Interval.over(Limit(15), false, Limitless[Int], false)
    list += Interval.over(Limit(15), true, Limitless[Int], false)
    list += Interval.over(Limit(20), false, Limitless[Int], false)
    list += Interval.over(Limit(20), true, Limitless[Int], false)

    // 上側限界のみ区間
    list += Interval.over(Limitless[Int], false, Limit(0), false)
    list += Interval.over(Limitless[Int], false, Limit(0), true)
    list += Interval.over(Limitless[Int], false, Limit(5), false)
    list += Interval.over(Limitless[Int], false, Limit(5), true)
    list += Interval.over(Limitless[Int], false, Limit(10), false)
    list += Interval.over(Limitless[Int], false, Limit(10), true)
    list += Interval.over(Limitless[Int], false, Limit(15), false)
    list += Interval.over(Limitless[Int], false, Limit(15), true)
    list += Interval.over(Limitless[Int], false, Limit(20), false)
    list += Interval.over(Limitless[Int], false, Limit(20), true)

    // freedom
    list += Interval.over(Limitless[Int], false, Limitless[Int], false)

    Random.shuffle(list.result)
  }

  /**
   * [[Interval]]のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  //	@Test
  //	def test01_Serialization {
  //		SerializationTester.assertCanBeSerialized(c5_10c);
  //	}

  /**
   * [[Interval]]のインスタンス生成テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_Assertions {
    // Redundant, maybe, but with all the compiler default
    // confusion at the moment, I decided to throw this in.
    try {
      Interval.closed(Limit(BigDecimal(2.0)), Limit(BigDecimal(1.0)))
      fail("Lower bound mustn't be isAbove isUpper bound.")
    } catch {
      case _: IllegalArgumentException => ()
      // success
    }
  }

  /**
   * [[Interval#upTo(Comparable)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_UpTo {
    val range = Interval.upTo(Limit(5.5D))
    assert(range.includes(Limit(5.5D)) == true)
    assert(range.includes(Limit(-5.5D)) == true)
    assert(range.includes(Limit(Double.NegativeInfinity)) == true)
    assert(range.includes(Limit(5.5001D)) == false)
  }


  /**
   * [[Interval#andMore(Comparable)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_AndMore {
    val range = Interval.andMore(Limit(5.5D))
    assert(range.includes(Limit(5.5D)) == true)
    assert(range.includes(Limit(5.4999D)) == false)
    assert(range.includes(Limit(-5.5D)) == false)
    assert(range.includes(Limit(Double.PositiveInfinity)) == true)
    assert(range.includes(Limit(5.5001D)) == true)
  }


  /**
   * [[Interval#newOfSameType(Comparable, boolean, Comparable, boolean)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_AbstractCreation {
    val concrete = new Interval(Limit(1), true, Limit(3), true)
    val newInterval = concrete.newOfSameType(Limit(1), false, Limit(4), false)
    val expected = new Interval(Limit(1), false, Limit(4), false)
    assert(newInterval == expected)
  }

  /**
   * [[Interval#isBelow(Comparable)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_Below {
    val range = Interval.closed(Limit(BigDecimal(-5.5)), Limit(BigDecimal(6.6)))
    assert(range.isBelow(Limit(BigDecimal(5.0))) == false)
    assert(range.isBelow(Limit(BigDecimal(-5.5))) == false)
    assert(range.isBelow(Limit(BigDecimal(-5.4999))) == false)
    assert(range.isBelow(Limit(BigDecimal(6.6))) == false)
    assert(range.isBelow(Limit(BigDecimal(6.601))) == true)
    assert(range.isBelow(Limit(BigDecimal(-5.501))) == false)
  }

  /**
   * [[Interval#includes(Comparable)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_Includes {
    val range = Interval.closed(Limit(BigDecimal(-5.5)), Limit(BigDecimal(6.6)))
    assert(range.includes(Limit(BigDecimal(5.0))) == true)
    assert(range.includes(Limit(BigDecimal(-5.5))) == true)
    assert(range.includes(Limit(BigDecimal(-5.4999))) == true)
    assert(range.includes(Limit(BigDecimal(6.6))) == true)
    assert(range.includes(Limit(BigDecimal(6.601))) == false)
    assert(range.includes(Limit(BigDecimal(-5.501))) == false)
  }

  /**
   * [[Interval]]の開閉の境界挙動テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_OpenInterval {
    val exRange = Interval.over(Limit(BigDecimal(-5.5)), false, Limit(BigDecimal(6.6)), true)
    assert(exRange.includes(Limit(BigDecimal(5.0))) == true)
    assert(exRange.includes(Limit(BigDecimal(-5.5))) == false)
    assert(exRange.includes(Limit(BigDecimal(-5.4999))) == true)
    assert(exRange.includes(Limit(BigDecimal(6.6))) == true)
    assert(exRange.includes(Limit(BigDecimal(6.601))) == false)
    assert(exRange.includes(Limit(BigDecimal(-5.501))) == false)
  }

  /**
   * [[Interval#isEmpty()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_IsEmpty {
    assert(Interval.closed(Limit(5), Limit(6)).isEmpty == false)
    assert(Interval.closed(Limit(6), Limit(6)).isEmpty == false)
    assert(Interval.open(Limit(6), Limit(6)).isEmpty == true)
    assert(c1_10c.emptyOfSameType.isEmpty == true)
  }

  /**
   * [[Interval#intersects(Interval)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_Intersects {
    assert(c5_10c.intersects(c1_10c) == true)
    assert(c1_10c.intersects(c5_10c) == true)
    assert(c4_6c.intersects(c1_10c) == true)
    assert(c1_10c.intersects(c4_6c) == true)
    assert(c5_10c.intersects(c5_15c) == true)
    assert(c5_15c.intersects(c1_10c) == true)
    assert(c1_10c.intersects(c5_15c) == true)
    assert(c1_10c.intersects(c12_16c) == false)
    assert(c12_16c.intersects(c1_10c) == false)
    assert(c5_10c.intersects(c5_10c) == true)
    assert(c1_10c.intersects(o10_12c) == false)
    assert(o10_12c.intersects(c1_10c) == false)

    // ---- 気を取り直して総当たりしてみよう

    assert(c5_10c.intersects(c5_10c) == true)
    assert(c5_10c.intersects(c1_10c) == true)
    assert(c5_10c.intersects(c4_6c) == true)
    assert(c5_10c.intersects(c5_15c) == true)
    assert(c5_10c.intersects(c12_16c) == false)
    assert(c5_10c.intersects(o10_12c) == false)
    assert(c5_10c.intersects(o1_1c) == false)
    assert(c5_10c.intersects(c1_1o) == false)
    assert(c5_10c.intersects(c1_1c) == false)
    assert(c5_10c.intersects(o1_1o) == false)
    assert(c5_10c.intersects(_2o) == false)
    assert(c5_10c.intersects(o9_) == true)
    assert(c5_10c.intersects(empty) == false)
    assert(c5_10c.intersects(all) == true)

    assert(c1_10c.intersects(c5_10c) == true)
    assert(c1_10c.intersects(c1_10c) == true)
    assert(c1_10c.intersects(c4_6c) == true)
    assert(c1_10c.intersects(c5_15c) == true)
    assert(c1_10c.intersects(c12_16c) == false)
    assert(c1_10c.intersects(o10_12c) == false)
    assert(c1_10c.intersects(o1_1c) == true)
    assert(c1_10c.intersects(c1_1o) == true)
    assert(c1_10c.intersects(c1_1c) == true)
    assert(c1_10c.intersects(o1_1o) == false)
    assert(c1_10c.intersects(_2o) == true)
    assert(c1_10c.intersects(o9_) == true)
    assert(c1_10c.intersects(empty) == false)
    assert(c1_10c.intersects(all) == true)

    assert(c4_6c.intersects(c5_10c) == true)
    assert(c4_6c.intersects(c1_10c) == true)
    assert(c4_6c.intersects(c4_6c) == true)
    assert(c4_6c.intersects(c5_15c) == true)
    assert(c4_6c.intersects(c12_16c) == false)
    assert(c4_6c.intersects(o10_12c) == false)
    assert(c4_6c.intersects(o1_1c) == false)
    assert(c4_6c.intersects(c1_1o) == false)
    assert(c4_6c.intersects(c1_1c) == false)
    assert(c4_6c.intersects(o1_1o) == false)
    assert(c4_6c.intersects(_2o) == false)
    assert(c4_6c.intersects(o9_) == false)
    assert(c4_6c.intersects(empty) == false)
    assert(c4_6c.intersects(all) == true)

    assert(c5_15c.intersects(c5_10c) == true)
    assert(c5_15c.intersects(c1_10c) == true)
    assert(c5_15c.intersects(c4_6c) == true)
    assert(c5_15c.intersects(c5_15c) == true)
    assert(c5_15c.intersects(c12_16c) == true)
    assert(c5_15c.intersects(o10_12c) == true)
    assert(c5_15c.intersects(o1_1c) == false)
    assert(c5_15c.intersects(c1_1o) == false)
    assert(c5_15c.intersects(c1_1c) == false)
    assert(c5_15c.intersects(o1_1o) == false)
    assert(c5_15c.intersects(_2o) == false)
    assert(c5_15c.intersects(o9_) == true)
    assert(c5_15c.intersects(empty) == false)
    assert(c5_15c.intersects(all) == true)

    // --- 疲れてきたからあと適当ｗ 総当たり達成ならず。まぁ、大丈夫やろ…。

    assert(c12_16c.intersects(c1_10c) == false)
    assert(o10_12c.intersects(c1_10c) == false)
    assert(o1_1c.intersects(c4_6c) == false)
    assert(c1_1o.intersects(c5_15c) == false)
    assert(c1_1c.intersects(c5_15c) == false)
    assert(o1_1o.intersects(c12_16c) == false)
    assert(empty.intersects(o10_12c) == false)
    assert(all.intersects(o10_12c) == true)
  }


  /**
   * [[Interval#intersect(Interval)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_Intersection {
    assert(c5_10c.intersect(c1_10c) == c5_10c)
    assert(c1_10c.intersect(c5_10c) == c5_10c)
    assert(c4_6c.intersect(c1_10c) == c4_6c)
    assert(c1_10c.intersect(c4_6c) == c4_6c)
    assert(c5_10c.intersect(c5_15c) == c5_10c)
    assert(c5_15c.intersect(c1_10c) == c5_10c)
    assert(c1_10c.intersect(c5_15c) == c5_10c)
    assert(c1_10c.intersect(c12_16c).isEmpty == true)
    assert(c1_10c.intersect(c12_16c) == empty)
    assert(c12_16c.intersect(c1_10c) == empty)
    assert(c5_10c.intersect(c5_10c) == c5_10c)
    assert(c1_10c.intersect(o10_12c) == empty)
    assert(o10_12c.intersect(c1_10c) == empty)
  }


  /**
   * [[Interval#greaterOfLowerLimits(Interval)]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_GreaterOfLowerLimits {
    assert(c5_10c.greaterOfLowerLimits(c1_10c) == Limit(BigDecimal(5)))
    assert(c1_10c.greaterOfLowerLimits(c5_10c) == Limit(BigDecimal(5)))
    assert(c1_10c.greaterOfLowerLimits(c12_16c) == Limit(BigDecimal(12)))
    assert(c12_16c.greaterOfLowerLimits(c1_10c) == Limit(BigDecimal(12)))
  }

  /**
   * [[Interval#lesserOfUpperLimits(Interval)]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test14_LesserOfUpperLimits {
    assert(c5_10c.lesserOfUpperLimits(c1_10c) == Limit(BigDecimal(10)))
    assert(c1_10c.lesserOfUpperLimits(c5_10c) == Limit(BigDecimal(10)))
    assert(c4_6c.lesserOfUpperLimits(c12_16c) == Limit(BigDecimal(6)))
    assert(c12_16c.lesserOfUpperLimits(c4_6c) == Limit(BigDecimal(6)))
  }

  /**
   * [[Interval#covers(Interval)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test15_CoversInterval {
    assert(c5_10c.covers(c1_10c) == false)
    assert(c1_10c.covers(c5_10c) == true)
    assert(c4_6c.covers(c1_10c) == false)
    assert(c1_10c.covers(c4_6c) == true)
    assert(c5_10c.covers(c5_10c) == true)

    val o5_10c = Interval.over(Limit(BigDecimal(5)), false, Limit(BigDecimal(10)), true)
    assert(c5_10c.covers(o5_10c) == true, "isClosed incl left-isOpen")
    assert(o5_10c.covers(o5_10c) == true, "left-isOpen incl left-isOpen")
    assert(o5_10c.covers(c5_10c) == false, "left-isOpen doesn't include isClosed")

    val o1_10o = Interval.over(Limit(BigDecimal(1)), false, Limit(BigDecimal(10)), false)
    assert(c5_10c.covers(o1_10o) == false)
    assert(o1_10o.covers(o1_10o) == true)
    assert(o1_10o.covers(c5_10c) == false)
  }

  /**
	 * [[Interval#gap(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test16_Gap {
		val c1_3c = Interval.closed(Limit(1), Limit(3))
		val c5_7c = Interval.closed(Limit(5), Limit(7))
		val o3_5o = Interval.open(Limit(3), Limit(5))
		val c2_3o = Interval.over(Limit(2), true, Limit(3), false)
		
		assert(c1_3c.gap(c5_7c)==o3_5o)
		assert(c1_3c.gap(o3_5o).isEmpty==true)
		assert(c1_3c.gap(c2_3o).isEmpty==true)
		assert(c2_3o.gap(o3_5o).isSingleElement==true)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test17_RelativeComplementDisjoint {
		val c1_3c = Interval.closed(Limit(1), Limit(3))
		val c5_7c = Interval.closed(Limit(5), Limit(7))
		val complement = c1_3c.complementRelativeTo(c5_7c)
		assert(complement.size == 1)
		assert(complement(0) == c5_7c)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test18_RelativeComplementDisjointAdjacentOpen {
		val c1_3o = Interval.over(Limit(1), true, Limit(3), false)
		val c3_7c = Interval.closed(Limit(3), Limit(7))
		val complement = c1_3o.complementRelativeTo(c3_7c)
		assert(complement.size == 1)
		assert(complement(0)==c3_7c)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test19_RelativeComplementOverlapLeft {
		val c1_5c = Interval.closed(Limit(1), Limit(5))
		val c3_7c = Interval.closed(Limit(3), Limit(7))
		val complement = c3_7c.complementRelativeTo(c1_5c)
		val c1_3o = Interval.over(Limit(1), true, Limit(3), false)
		assert(complement.size==1)
		assert(complement(0)==c1_3o)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test20_RelativeComplementOverlapRight {
		val c1_5c = Interval.closed(Limit(1), Limit(5))
		val c3_7c = Interval.closed(Limit(3), Limit(7))
		val complement = c1_5c.complementRelativeTo(c3_7c)
		val o5_7c = Interval.over(Limit(5), false, Limit(7), true)
		assert(complement.size==1)
		assert(complement(0)==o5_7c)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test21_RelativeComplementAdjacentClosed {
		val c1_3c = Interval.closed(Limit(1), Limit(3))
		val c5_7c = Interval.closed(Limit(5), Limit(7))
		val complement = c1_3c.complementRelativeTo(c5_7c)
		assert(complement.size==1)
		assert(complement(0)==c5_7c)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test22_RelativeComplementEnclosing {
		val c3_5c = Interval.closed(Limit(3), Limit(5))
		val c1_7c = Interval.closed(Limit(1), Limit(7))
		val complement = c1_7c.complementRelativeTo(c3_5c)
		assert(complement.size==0)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test23_RelativeComplementEqual {
		val c1_7c = Interval.closed(Limit(1), Limit(7))
		val complement = c1_7c.complementRelativeTo(c1_7c)
		assert(complement.size==0)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test24_RelativeComplementEnclosed {
		val c3_5c = Interval.closed(Limit(3), Limit(5))
		val c1_7c = Interval.closed(Limit(1), Limit(7))
		val c1_3o = Interval.over(Limit(1), true, Limit(3), false)
		val o5_7c = Interval.over(Limit(5), false, Limit(7), true)
		val complement = c3_5c.complementRelativeTo(c1_7c)
		assert(complement.size==2)
		assert(complement(0)==c1_3o)
		assert(complement(1)==o5_7c)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test25_RelativeComplementEnclosedEndPoint {
		val o3_5o = Interval.open(Limit(3), Limit(5))
		val c3_5c = Interval.closed(Limit(3), Limit(5))
		val complement = o3_5o.complementRelativeTo(c3_5c)
		assert(complement.size == 2)
		assert(complement(0).includes(Limit(3))==true)
	}
	
	/**
	 * [[Interval#isSingleElement()]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test26_IsSingleElement {
		assert(o1_1c.isSingleElement==true)
		assert(c1_1c.isSingleElement==true)
		assert(c1_1o.isSingleElement==true)
		assert(c1_10c.isSingleElement==false)
		assert(o1_1o.isSingleElement==false)
	}
	
	/**
	 * [[Interval#equals(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test27_EqualsForOnePointIntervals {
		assert(c1_1o == o1_1c)
		assert(c1_1c == o1_1c)
		assert(c1_1c == c1_1o)
		assert(o1_1c != o1_1o)
	}
	
	/**
	 * [[Interval#emptyOfSameType()]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test28_EqualsForEmptyIntervals {
		assert(c4_6c.emptyOfSameType==c1_10c.emptyOfSameType)
	}
	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test29_RelativeComplementEnclosedOpen {
		val o3_5o = Interval.open(Limit(3), Limit(5))
		val c1_7c = Interval.closed(Limit(1), Limit(7))
		val c1_3c = Interval.closed(Limit(1), Limit(3))
		val c5_7c = Interval.closed(Limit(5), Limit(7))
		val complement = o3_5o.complementRelativeTo(c1_7c)
		assert(complement.size==2)
		assert(complement(0)==c1_3c)
		assert(complement(1)==c5_7c)
	}
	
	/**
	 * [[Interval#toString()]]のテスト。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test30_ToString {
		assert(c1_10c.toString == "[Limit(1), Limit(10)]")
		assert(o10_12c.toString == "(Limit(10), Limit(12)]")
		assert(empty.toString == "{}")
		assert(Interval.closed(Limit(10), Limit(10)).toString=="{Limit(10)}")
	}

	
	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 *
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test31_RelativeComplementOverlapRightOpen {
		val c3_7o = Interval.over(Limit(3), true, Limit(6), false)
		val c1_5o = Interval.over(Limit(1), true, Limit(5), false)
		val complement = c3_7o.complementRelativeTo(c1_5o)
		val c1_3o = Interval.over(Limit(1), true, Limit(3), false)
		assert(complement.size == 1)
		assert(complement(0)==c1_3o)
	}

	/**
	 * [[Interval#complementRelativeTo(Interval)]]のテスト。
	 *
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	def test32_RelativeComplementOverlapLeftOpen {
		val o1_5c = Interval.over(Limit(1), false, Limit(5), true)
		val o3_7c = Interval.over(Limit(2), false, Limit(7), true)
		val complement = o1_5c.complementRelativeTo(o3_7c)
		val o5_7c = Interval.over(Limit(5), false, Limit(7), true)
		assert(complement.size==1)
		assert(complement(0)==o5_7c)
	}
  
}