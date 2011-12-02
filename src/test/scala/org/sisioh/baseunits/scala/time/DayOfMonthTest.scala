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

/**`DayOfMonth`のテストクラス。
 */
class DayOfMonthTest extends AssertionsForJUnit {
  /**
   * インスタンス生成テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_create {
    for (i <- 1 until 31) {
      val m = DayOfMonth(i)
      assert(m.breachEncapsulationOfValue == (i))
    }

    try {
      DayOfMonth(0)
      fail
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
    try {
      DayOfMonth(-1)
      fail
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
    try {
      DayOfMonth(32)
      fail
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * インスタンス生成テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_create2 {
    for (i <- 1 until 31) {
      DayOfMonth(i)
    }
    try {
      DayOfMonth(0)
      fail
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
    try {
      DayOfMonth(-1)
      fail
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
    try {
      DayOfMonth(32)
      fail;
    } catch {
      case e: IllegalArgumentException => // success
      case _ => fail
    }
  }

  /**
   * [[DayOfMonth#isBefore(DayOfMonth)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_isBefore {
    assert(DayOfMonth(1).isBefore(DayOfMonth(1)) == false)
    assert(DayOfMonth(1).isBefore(DayOfMonth(2)) == true)

    assert(DayOfMonth(2).isBefore(DayOfMonth(1)) == false)
    assert(DayOfMonth(2).isBefore(DayOfMonth(2)) == false)
    assert(DayOfMonth(2).isBefore(DayOfMonth(3)) == true)

    assert(DayOfMonth(3).isBefore(DayOfMonth(2)) == false)
    assert(DayOfMonth(3).isBefore(DayOfMonth(3)) == false)
    assert(DayOfMonth(3).isBefore(DayOfMonth(4)) == true)

    assert(DayOfMonth(12).isBefore(DayOfMonth(11)) == false)
    assert(DayOfMonth(12).isBefore(DayOfMonth(12)) == false)

    //assert(DayOfMonth(12).isBefore(null) == false)
  }

  /**
   * [[DayOfMonth#isAfter(DayOfMonth)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_isAfter {
    assert(DayOfMonth(1).isAfter(DayOfMonth(1)) == false)
    assert(DayOfMonth(1).isAfter(DayOfMonth(2)) == false)

    assert(DayOfMonth(2).isAfter(DayOfMonth(1)) == true)
    assert(DayOfMonth(2).isAfter(DayOfMonth(2)) == false)
    assert(DayOfMonth(2).isAfter(DayOfMonth(3)) == false)

    assert(DayOfMonth(3).isAfter(DayOfMonth(2)) == true)
    assert(DayOfMonth(3).isAfter(DayOfMonth(3)) == false)
    assert(DayOfMonth(3).isAfter(DayOfMonth(4)) == false)

    assert(DayOfMonth(12).isAfter(DayOfMonth(11)) == true)
    assert(DayOfMonth(12).isAfter(DayOfMonth(12)) == false)

    // assert (DayOfMonth(12).isAfter(null) == false)
  }

  /**
   * [[DayOfMonth#compareTo(DayOfMonth)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_compareTo {
    assert(DayOfMonth(10).compareTo(DayOfMonth(10)) == 0)
    assert(DayOfMonth(10).compareTo(DayOfMonth(11)) < 0)
    assert(DayOfMonth(10).compareTo(DayOfMonth(31)) < 0)
    assert(DayOfMonth(10).compareTo(DayOfMonth(1)) > 0)
    assert(DayOfMonth(10).compareTo(DayOfMonth(2)) > 0)
    assert(DayOfMonth(10).compareTo(DayOfMonth(5)) > 0)
  }

  /**
   * [[DayOfMonth#toString]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_toString {
    assert(DayOfMonth(1).toString == ("1"))
    assert(DayOfMonth(10).toString == ("10"))
    assert(DayOfMonth(31).toString == ("31"))
  }

  /**
   * [[DayOfMonth#equals(Object)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_equals {
    val dom1 = DayOfMonth(1)
    assert(dom1.equals(DayOfMonth(1)) == true)
    assert(dom1.equals(dom1) == true)
    assert(dom1.equals(DayOfMonth(2)) == false)
    assert(dom1.equals(null) == false)
    //		assert(dom1.equals(new DayOfMonth(1) {
    //		})==false)
  }
}