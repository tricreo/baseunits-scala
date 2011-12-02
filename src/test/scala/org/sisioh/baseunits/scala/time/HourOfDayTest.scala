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
import org.junit.Test

/**`HourOfDay`のテストクラス。
 */
class HourOfDayTest extends AssertionsForJUnit {
  /**
   * [[HourOfDay#valueOf(int)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_24Simple {
    assert(HourOfDay(22).breachEncapsulationOfValue == 22)
  }

  /**
   * [[HourOfDay#valueOf(int, String)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_12Simple {
    assert(HourOfDay(10, "PM") == HourOfDay(22))
    assert(HourOfDay(3, "am") == HourOfDay(3))
  }

  /**
   * [[HourOfDay#valueOf(int)]]の不正引数テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_24IllegalLessThanZero {
    try {
      HourOfDay(-1)
      fail
    } catch {
      case e:IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[HourOfDay#valueOf(int)]]の不正引数テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_24GreaterThan {
    try {
      HourOfDay(24)
      fail;
    } catch {
      case e:IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[HourOfDay#valueOf(int, String)]]の不正引数テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_12IllegalLessThanZero {
    try {
      HourOfDay(-1, "PM")
      fail
    } catch {
      case e:IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[HourOfDay#valueOf(int, String)]]の不正引数テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_12GreaterThan {
    try {
      HourOfDay(13, "AM")
      fail
    } catch {
      case e:IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[HourOfDay#valueOf(int, String)]]の不正引数テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_12BadAmPm {
    try {
      HourOfDay(5, "FD")
      fail
    } catch {
      case e:IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[HourOfDay#isAfter(HourOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_LaterAfterEarlier {
    val later = HourOfDay(8)
    val earlier = HourOfDay(6)
    assert(later.isAfter(earlier) == true)
  }

  /**
   * [[HourOfDay#isAfter(HourOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_EarlierAfterLater {
    val earlier = HourOfDay(8)
    val later = HourOfDay(20)
    assert(earlier.isAfter(later) == false)
  }

  /**
   * [[HourOfDay#isAfter(HourOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_EqualAfterEqual {
    val anHour = HourOfDay(8)
    val anotherHour = HourOfDay(8)
    assert(anHour.isAfter(anotherHour) == false)
  }

  /**
   * [[HourOfDay#isBefore(HourOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_LaterBeforeEarlier {
    val later = HourOfDay(8)
    val earlier = HourOfDay(6)
    assert(later.isBefore(earlier) == false)
  }

  /**
   * [[HourOfDay#isBefore(HourOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test12_EarlierBeforeLater {
    val earlier = HourOfDay(8)
    val later = HourOfDay(20)
    assert(earlier.isBefore(later) == true)
  }

  /**
   * [[HourOfDay#isBefore(HourOfDay)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test13_EqualBeforeEqual {
    val anHour = HourOfDay(8)
    val anotherHour = HourOfDay(8)
    assert(anHour.isBefore(anotherHour) == false)
  }
}