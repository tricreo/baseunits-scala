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

import java.util.Calendar

import org.junit.Test
import org.scalatest.Assertions

/**
 * {@link TimeUnit}のテストクラス。
 * @author j5ik2o
 */
class TimeUnitTest extends Assertions {
  /**
   * [[TimeUnit]]のインスタンスがシリアライズできるかどうか検証する。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Serialization() {
    //SerializationTester.assertCanBeSerialized(TimeUnit.month);
  }

  /**
   * [[TimeUnit# t o S t r i n g ( )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_ToString() {
    assert(TimeUnit.Month.name == "month")
  }

  /**
   * [[TimeUnit# i s C o n v e r t i b l e T o M i l l i s e c o n d s ( )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_ConvertibleToMilliseconds() {
    assert(TimeUnit.Millisecond.isConvertibleToMilliseconds == true)
    assert(TimeUnit.Hour.isConvertibleToMilliseconds == true)
    assert(TimeUnit.Day.isConvertibleToMilliseconds == true)
    assert(TimeUnit.Week.isConvertibleToMilliseconds == true)
    assert(TimeUnit.Month.isConvertibleToMilliseconds == false)
    assert(TimeUnit.Year.isConvertibleToMilliseconds == false)
  }

  /**
   * [[TimeUnit# c o m p a r e T o ( T i m e U n i t )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_Comparison() {
    assert(TimeUnit.Hour.compareTo(TimeUnit.Hour) == 0)
    assert(TimeUnit.Hour.compareTo(TimeUnit.Millisecond) > 0)
    assert(TimeUnit.Millisecond.compareTo(TimeUnit.Hour) < 0)
    assert(TimeUnit.Day.compareTo(TimeUnit.Hour) > 0)
    assert(TimeUnit.Hour.compareTo(TimeUnit.Day) < 0)

    assert(TimeUnit.Month.compareTo(TimeUnit.Day) > 0)
    assert(TimeUnit.Day.compareTo(TimeUnit.Month) < 0)
    assert(TimeUnit.Quarter.compareTo(TimeUnit.Hour) > 0)

    assert(TimeUnit.Month.compareTo(TimeUnit.Month) == 0)
    assert(TimeUnit.Quarter.compareTo(TimeUnit.Year) < 0)
    assert(TimeUnit.Year.compareTo(TimeUnit.Quarter) > 0)
  }

  /**
   * [[TimeUnit# j a v a C a l e n d a r C o n s t a n t F o r B a s e T y p e ( )]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_JavaCalendarConstantForBaseType() {
    assert(TimeUnit.Millisecond.javaCalendarConstantForBaseType == Calendar.MILLISECOND)
    assert(TimeUnit.Hour.javaCalendarConstantForBaseType == Calendar.MILLISECOND)
    assert(TimeUnit.Day.javaCalendarConstantForBaseType == Calendar.MILLISECOND)
    assert(TimeUnit.Week.javaCalendarConstantForBaseType == Calendar.MILLISECOND)
    assert(TimeUnit.Month.javaCalendarConstantForBaseType == Calendar.MONTH)
    assert(TimeUnit.Quarter.javaCalendarConstantForBaseType == Calendar.MONTH)
    assert(TimeUnit.Year.javaCalendarConstantForBaseType == Calendar.MONTH)
  }

  /**
   * [[TimeUnit# i s C o n v e r t i b l e T o ( T i m e U n i t )]]のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_IsConvertableTo() {
    assert(TimeUnit.Hour.isConvertibleTo(TimeUnit.Minute) == true)
    assert(TimeUnit.Minute.isConvertibleTo(TimeUnit.Hour) == true)
    assert(TimeUnit.Year.isConvertibleTo(TimeUnit.Month) == true)
    assert(TimeUnit.Month.isConvertibleTo(TimeUnit.Year) == true)
    assert(TimeUnit.Month.isConvertibleTo(TimeUnit.Hour) == false)
    assert(TimeUnit.Hour.isConvertibleTo(TimeUnit.Month) == false)
  }

  /**
   * [[TimeUnit# n e x t F i n e r U n i t ( )]]のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_NextFinerUnit() {
    assert(TimeUnit.Hour.nextFinerUnit == Some(TimeUnit.Minute))
    assert(TimeUnit.Quarter.nextFinerUnit == Some(TimeUnit.Month))
  }
}