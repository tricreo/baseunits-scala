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
import java.util.Calendar

/**{@link TimeUnit}のテストクラス。
 * @author j5ik2o
 */
class TimeUnitTest extends AssertionsForJUnit {
  /**[[TimeUnit]]のインスタンスがシリアライズできるかどうか検証する。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Serialization {
    //SerializationTester.assertCanBeSerialized(TimeUnit.month);
  }

  /**
   * [[TimeUnit#toString()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_ToString {
    assert(TimeUnit.month.name == "month")
  }

  /**
   * [[TimeUnit#isConvertibleToMilliseconds()]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_ConvertibleToMilliseconds {
    assert(TimeUnit.millisecond.isConvertibleToMilliseconds == true)
    assert(TimeUnit.hour.isConvertibleToMilliseconds == true)
    assert(TimeUnit.day.isConvertibleToMilliseconds == true)
    assert(TimeUnit.week.isConvertibleToMilliseconds == true)
    assert(TimeUnit.month.isConvertibleToMilliseconds == false)
    assert(TimeUnit.year.isConvertibleToMilliseconds == false)
  }

  /**
   * [[TimeUnit#compareTo(TimeUnit)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_Comparison {
    assert(TimeUnit.hour.compareTo(TimeUnit.hour) == 0)
    assert(TimeUnit.hour.compareTo(TimeUnit.millisecond) > 0)
    assert(TimeUnit.millisecond.compareTo(TimeUnit.hour) < 0)
    assert(TimeUnit.day.compareTo(TimeUnit.hour) > 0)
    assert(TimeUnit.hour.compareTo(TimeUnit.day) < 0)

    assert(TimeUnit.month.compareTo(TimeUnit.day) > 0)
    assert(TimeUnit.day.compareTo(TimeUnit.month) < 0)
    assert(TimeUnit.quarter.compareTo(TimeUnit.hour) > 0)

    assert(TimeUnit.month.compareTo(TimeUnit.month) == 0)
    assert(TimeUnit.quarter.compareTo(TimeUnit.year) < 0)
    assert(TimeUnit.year.compareTo(TimeUnit.quarter) > 0)
  }

  /**
   * [[TimeUnit#javaCalendarConstantForBaseType()]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_JavaCalendarConstantForBaseType {
    assert(TimeUnit.millisecond.javaCalendarConstantForBaseType == Calendar.MILLISECOND)
    assert(TimeUnit.hour.javaCalendarConstantForBaseType == Calendar.MILLISECOND)
    assert(TimeUnit.day.javaCalendarConstantForBaseType == Calendar.MILLISECOND)
    assert(TimeUnit.week.javaCalendarConstantForBaseType == Calendar.MILLISECOND)
    assert(TimeUnit.month.javaCalendarConstantForBaseType == Calendar.MONTH)
    assert(TimeUnit.quarter.javaCalendarConstantForBaseType == Calendar.MONTH)
    assert(TimeUnit.year.javaCalendarConstantForBaseType == Calendar.MONTH)
  }

  /**
   * [[TimeUnit#isConvertibleTo(TimeUnit)]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_IsConvertableTo {
    assert(TimeUnit.hour.isConvertibleTo(TimeUnit.minute) == true)
    assert(TimeUnit.minute.isConvertibleTo(TimeUnit.hour) == true)
    assert(TimeUnit.year.isConvertibleTo(TimeUnit.month) == true)
    assert(TimeUnit.month.isConvertibleTo(TimeUnit.year) == true)
    assert(TimeUnit.month.isConvertibleTo(TimeUnit.hour) == false)
    assert(TimeUnit.hour.isConvertibleTo(TimeUnit.month) == false)
  }

  /**
   * [[TimeUnit#nextFinerUnit()]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_NextFinerUnit {
    assert(TimeUnit.hour.nextFinerUnit == TimeUnit.minute)
    assert(TimeUnit.quarter.nextFinerUnit == TimeUnit.month)
  }
}