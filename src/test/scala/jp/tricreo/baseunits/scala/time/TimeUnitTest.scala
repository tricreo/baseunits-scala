package jp.tricreo.baseunits.scala.time

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import java.util.Calendar

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/15
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */

class TimeUnitTest extends AssertionsForJUnit {
  /**
   * {@link TimeUnit}のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Serialization {
    //SerializationTester.assertCanBeSerialized(TimeUnit.month);
  }

  /**
   * {@link TimeUnit#toString()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_ToString {
    assert(TimeUnit.month.toString() == "month")
  }

  /**
   * {@link TimeUnit#isConvertibleToMilliseconds()}のテスト。
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
   * {@link TimeUnit#compareTo(TimeUnit)}のテスト。
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
   * {@link TimeUnit#javaCalendarConstantForBaseType()}のテスト。（内部API）
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
   * {@link TimeUnit#isConvertibleTo(TimeUnit)}のテスト。
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
   * {@link TimeUnit#nextFinerUnit()}のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_NextFinerUnit {
    assert(TimeUnit.hour.nextFinerUnit == TimeUnit.minute)
    assert(TimeUnit.quarter.nextFinerUnit == TimeUnit.month)
  }
}