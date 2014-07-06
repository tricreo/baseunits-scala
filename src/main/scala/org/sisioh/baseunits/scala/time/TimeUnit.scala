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
import java.lang.String

/**
 * 時間の単位を表す列挙型。
 *
 * @author j5ik2o
 */
final class TimeUnit private[time] (_name: String,
                                    private[time] val valueType: TimeUnit.Type,
                                    private[time] val valueBaseType: TimeUnit.Type,
                                    private[time] val factor: TimeUnitConversionFactor) extends Ordered[TimeUnit] {

  val name = _name

  /**
   * この単位で表される値を、指定した単位に変換できるかどうかを検証する。
   * 例えば、分単位はミリ秒単位に変換できるが、四半期単位は（一ヶ月の長さが毎月異なるため）日単位に変換できない。
   *
   * @param other 変換先単位
   * @return 変換できる場合は`true`、そうでない場合は`false`
   */
  def isConvertibleTo(other: TimeUnit) = valueBaseType == other.valueBaseType

  /**
   * この単位で表される値を、ミリ秒単位に変換できるかどうかを検証する。
   * 例えば、分単位はミリ秒単位に変換できるが、四半期単位は（一ヶ月の長さが毎月異なるため）ミリ秒単位に変換できない。
   *
   * @return 変換できる場合は`true`、そうでない場合は`false`
   */
  lazy val isConvertibleToMilliseconds = isConvertibleTo(TimeUnit.Millisecond)

  //  override def toString = valueType.name

  /**
   * この単位の計数の基数とすることができる最小の単位を取得する。
   * 例えば、分単位はミリ秒単位で計数できるが、四半期単位は（一ヶ月の長さが毎月異なるため）月単位までしか計数できない。
   *
   * @return この単位の計数の基数とすることができる最小の単位
   */
  lazy val baseUnit: TimeUnit =
    if (valueBaseType == TimeUnit.Type.Millisecond) TimeUnit.Millisecond else TimeUnit.Month

  /**
   * この単位から変換可能な全ての単位を含み、大きい単位から降順にソートした配列を取得する。
   *
   * @return この単位から変換可能な全ての単位を含み、大きい単位から降順にソートした配列
   */
  lazy val descendingUnits: Seq[TimeUnit] =
    if (isConvertibleToMilliseconds) TimeUnit.DescendingMsBased else TimeUnit.DescendingMonthBased

  /**
   * この単位から変換可能な単位のうち、しばしば表示に利用する単位を、大きい単位から降順にソートした配列を取得する。
   *
   * @return この単位から変換可能な全ての単位のうち、しばしば表示に利用する単位を、大きい単位から降順にソートした配列
   */
  lazy val descendingUnitsForDisplay: Seq[TimeUnit] =
    if (isConvertibleToMilliseconds) TimeUnit.DescendingMsBasedForDisplay else TimeUnit.DescendingMonthBasedForDisplay;

  private[time] lazy val getFactor = factor.value

  private[time] lazy val javaCalendarConstantForBaseType = {
    if (valueBaseType == TimeUnit.Type.Millisecond) {
      Calendar.MILLISECOND
    } else if (valueBaseType == TimeUnit.Type.Month) {
      Calendar.MONTH
    } else 0
  }

  /**
   * この単位から変換可能な単位のうち、現在の単位より一つ小さい単位を取得する。
   *
   * @return この単位から変換可能な単位のうち、現在の単位より一つ小さい単位
   */
  lazy val nextFinerUnit: Option[TimeUnit] = {
    val descending = descendingUnits
    (0 until descending.length).find(descending(_) == this).flatMap {
      index =>
        if (index == descending.length - 1) None
        else Some(descending(index + 1))
    }
  }

  private[time] def toString(quantity: Long) = {
    val buffer = new StringBuffer
    buffer.append(quantity)
    buffer.append(" ")
    buffer.append(valueType.name)
    buffer.append(if (quantity == 1) "" else "s")
    buffer.toString
  }

  def compare(that: TimeUnit): Int = valueType.ordinal compare that.valueType.ordinal
}

/**
 * `TimeUnit`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object TimeUnit {

  private[time] case class Type(ordinal: Int, name: String)

  private[time] object Type {

    val Millisecond = Type(1, "millisecond")
    val Second = Type(2, "second")
    val Minute = Type(3, "minute")
    val Hour = Type(4, "hour")
    val Day = Type(5, "day")
    val Week = Type(6, "week")
    val Month = Type(7, "month")
    val Quarter = Type(8, "quarter")
    val Year = Type(9, "year")

  }

  /** ミリ秒単位 */
  val Millisecond = new TimeUnit("millisecond", Type.Millisecond, Type.Millisecond, TimeUnitConversionFactor.Identical)

  /** 秒単位 */
  val Second = new TimeUnit("second", Type.Second, Type.Millisecond, TimeUnitConversionFactor.MillisecondsPerSecond)

  /** 分単位 */
  val Minute = new TimeUnit("minute", Type.Minute, Type.Millisecond, TimeUnitConversionFactor.MillisecondsPerMinute)

  /** 時単位 */
  val Hour = new TimeUnit("hour", Type.Hour, Type.Millisecond, TimeUnitConversionFactor.MillisecondsPerHour)

  /** 日単位 */
  val Day = new TimeUnit("day", Type.Day, Type.Millisecond, TimeUnitConversionFactor.MillisecondsPerDay)

  /** 週単位 */
  val Week = new TimeUnit("week", Type.Week, Type.Millisecond, TimeUnitConversionFactor.MillisecondsPerWeek)

  /** 月単位 */
  val Month = new TimeUnit("month", Type.Month, Type.Month, TimeUnitConversionFactor.Identical)

  /** 四半期単位 */
  val Quarter = new TimeUnit("quarter", Type.Quarter, Type.Month, TimeUnitConversionFactor.MonthsPerQuarter)

  /** 年単位 */
  val Year = new TimeUnit("year", Type.Year, Type.Month, TimeUnitConversionFactor.MonthsPerYear)

  private val DescendingMsBased = Seq(
    Week,
    Day,
    Hour,
    Minute,
    Second,
    Millisecond
  )

  private val DescendingMsBasedForDisplay = Seq(
    Day,
    Hour,
    Minute,
    Second,
    Millisecond
  )

  private val DescendingMonthBased = Seq(
    Year,
    Quarter,
    Month
  )

  private val DescendingMonthBasedForDisplay = Seq(
    Year,
    Month
  )

}