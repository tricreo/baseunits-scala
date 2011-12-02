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

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test

/**`IntervalMap`のテストクラス。
 */
class IntervalMapTest extends AssertionsForJUnit {
  /**[[org.sisioh.baseunits.scala.intervals.IntervalMap]]に対する参照メソッドのテスト。
   *
   * <ul>
   *   <li>[[IntervalMap#containsKey(Comparable)]]</li>
   *   <li>[[IntervalMap#get(Comparable)]]</li>
   * </ul>
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Lookup {

    var map = new LinearIntervalMap[Int, String]
    map += (Interval.closed(Limit(1), Limit(3)) -> "one-three")
    map += (Interval.closed(Limit(5), Limit(9)) -> "five-nine")
    map += (Interval.open(Limit(9), Limit(12)) -> "ten-eleven")

    assert(map.contains(Limit(0)) == false)
    assert(map.contains(Limit(1)) == true)
    assert(map.contains(Limit(2)) == true)
    assert(map.contains(Limit(3)) == true)
    assert(map.contains(Limit(4)) == false)
    assert(map.contains(Limit(5)) == true)
    assert(map.contains(Limit(9)) == true)
    assert(map.contains(Limit(11)) == true)
    assert(map.contains(Limit(12)) == false)
    assert(map.contains(Limit(13)) == false)
    assert(map.contains(Limitless[Int]) == false)

    assert(map.get(Limit(0)) == None)
    assert(map.get(Limit(1)) == Some("one-three"))
    assert(map.get(Limit(2)) == Some("one-three"))
    assert(map.get(Limit(3)) == Some("one-three"))
    assert(map.get(Limit(4)) == None)
    assert(map.get(Limit(5)) == Some("five-nine"))
    assert(map.get(Limit(9)) == Some("five-nine"))
    assert(map.get(Limit(10)) == Some("ten-eleven"))
    assert(map.get(Limit(11)) == Some("ten-eleven"))
    assert(map.get(Limit(12)) == None)
    assert(map.get(Limit(13)) == None)
    assert(map.get(Limitless[Int]) == None)
  }

  /**
   * [[org.sisioh.baseunits.scala.intervals.IntervalMap#remove(Interval)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_Remove {
    var map = new LinearIntervalMap[Int, String]()
    map += (Interval.closed(Limit(1), Limit(10)) -> "one-ten")
    map -= (Interval.closed(Limit(3), Limit(5)))
    assert(map.get(Limit(2)) == Some("one-ten"))
    assert(map.get(Limit(3)) == None)
    assert(map.get(Limit(4)) == None)
    assert(map.get(Limit(5)) == None)
    assert(map.get(Limit(6)) == Some("one-ten"))
  }

  /**
   * [[org.sisioh.baseunits.scala.intervals.IntervalMap#put(Interval, Object)]]で割り当て区間が重複した場合、後勝ちになることを確認するテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_ConstructionOverwriteOverlap {
    var map = new LinearIntervalMap[Int, String]()
    map += (Interval.closed(Limit(1), Limit(3)) -> "one-three")
    map += (Interval.closed(Limit(5), Limit(9)) -> "five-nine")
    map += (Interval.open(Limit(9), Limit(12)) -> "ten-eleven")
    assert(map.get(Limit(10)) == Some("ten-eleven"))
    assert(map.get(Limit(11)) == Some("ten-eleven"))
    assert(map.get(Limit(12)) == None)

    val eleven_thirteen = Interval.closed(Limit(11), Limit(13))
    assert(map.containsIntersectingKey(eleven_thirteen) == true)

    map += (eleven_thirteen -> "eleven-thirteen")
    assert(map.get(Limit(10)) == Some("ten-eleven"))
    assert(map.get(Limit(11)) == Some("eleven-thirteen"))
    assert(map.get(Limit(12)) == Some("eleven-thirteen"))
  }

  /**
   * [[org.sisioh.baseunits.scala.intervals.IntervalMap#put(Interval, Object)]]で割り当て区間が重複した場合、後勝ちになることを確認するテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_ConstructionOverwriteMiddle {
    var map = new LinearIntervalMap[Int, String]()
    map += (Interval.closed(Limit(1), Limit(3)) -> "one-three")
    map += (Interval.closed(Limit(5), Limit(9)) -> "five-nine")
    map += (Interval.open(Limit(9), Limit(12)) -> "ten-eleven")
    assert(map.get(Limit(6)) == Some("five-nine"))
    assert(map.get(Limit(7)) == Some("five-nine"))
    assert(map.get(Limit(8)) == Some("five-nine"))
    assert(map.get(Limit(9)) == Some("five-nine"))

    val seven_eight = Interval.closed(Limit(7), Limit(8))
    assert(map.containsIntersectingKey(seven_eight) == true)
    map += (seven_eight -> "seven-eight")
    assert(map.get(Limit(6)) == Some("five-nine"))
    assert(map.get(Limit(7)) == Some("seven-eight"))
    assert(map.get(Limit(8)) == Some("seven-eight"))
    assert(map.get(Limit(9)) == Some("five-nine"))
  }

  /**
   * [[IntervalMap#put(Interval, Object)]]で割り当て区間が重複した場合、後勝ちになることを確認するテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_ConstructionOverwriteMultiple {
    var map = new LinearIntervalMap[Int, String]()
    map += (Interval.closed(Limit(1), Limit(2)) -> "one-two")
    map += (Interval.closed(Limit(3), Limit(4)) -> "three-four")
    map += (Interval.closed(Limit(5), Limit(6)) -> "five-six")
    map += (Interval.closed(Limit(8), Limit(9)) -> "eight-nine")
    map += (Interval.closed(Limit(3), Limit(8)) -> "three-eight")
    assert(map.get(Limit(2)) == Some("one-two"))
    assert(map.get(Limit(3)) == Some("three-eight"))
    assert(map.get(Limit(4)) == Some("three-eight"))
    assert(map.get(Limit(5)) == Some("three-eight"))
    assert(map.get(Limit(6)) == Some("three-eight"))
    assert(map.get(Limit(7)) == Some("three-eight"))
    assert(map.get(Limit(8)) == Some("three-eight"))
    assert(map.get(Limit(9)) == Some("eight-nine"))
  }

}