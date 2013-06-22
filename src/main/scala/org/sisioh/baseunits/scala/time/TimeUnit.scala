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
import org.sisioh.scala.toolbox._

/**時間の単位を表す列挙型。
 *
 * @author j5ik2o
 */
final class TimeUnit private[time]
(_name:String,
 private[time] val valueType: TimeUnit.Type,
 private[time] val valueBaseType: TimeUnit.Type,
 private[time] val factor: TimeUnitConversionFactor)
  extends EnumEntry {

  override val name = _name

  /**この単位で表される値を、指定した単位に変換できるかどうかを検証する。
   * 例えば、分単位はミリ秒単位に変換できるが、四半期単位は（一ヶ月の長さが毎月異なるため）日単位に変換できない。
   *
   * @param other 変換先単位
   * @return 変換できる場合は`true`、そうでない場合は`false`
   */
  def isConvertibleTo(other: TimeUnit) = valueBaseType == other.valueBaseType

  /**この単位で表される値を、ミリ秒単位に変換できるかどうかを検証する。
   * 例えば、分単位はミリ秒単位に変換できるが、四半期単位は（一ヶ月の長さが毎月異なるため）ミリ秒単位に変換できない。
   *
   * @return 変換できる場合は`true`、そうでない場合は`false`
   */
  def isConvertibleToMilliseconds = isConvertibleTo(TimeUnit.millisecond)

//  override def toString = valueType.name

  /**この単位の計数の基数とすることができる最小の単位を取得する。
   * 例えば、分単位はミリ秒単位で計数できるが、四半期単位は（一ヶ月の長さが毎月異なるため）月単位までしか計数できない。
   *
   * @return この単位の計数の基数とすることができる最小の単位
   */
  def baseUnit: TimeUnit =
    if (valueBaseType == TimeUnit.Type.millisecond) TimeUnit.millisecond else TimeUnit.month

  /**この単位から変換可能な全ての単位を含み、大きい単位から降順にソートした配列を取得する。
   *
   * @return この単位から変換可能な全ての単位を含み、大きい単位から降順にソートした配列
   */
  def descendingUnits =
    if (isConvertibleToMilliseconds) TimeUnit.DESCENDING_MS_BASED else TimeUnit.DESCENDING_MONTH_BASED

  /**この単位から変換可能な単位のうち、しばしば表示に利用する単位を、大きい単位から降順にソートした配列を取得する。
   *
   * @return この単位から変換可能な全ての単位のうち、しばしば表示に利用する単位を、大きい単位から降順にソートした配列
   */
  def descendingUnitsForDisplay =
    if (isConvertibleToMilliseconds) TimeUnit.DESCENDING_MS_BASED_FOR_DISPLAY else TimeUnit.DESCENDING_MONTH_BASED_FOR_DISPLAY;

  private[time] def getFactor = factor.value

  private[time] def javaCalendarConstantForBaseType = {
    if (valueBaseType == TimeUnit.Type.millisecond) {
      Calendar.MILLISECOND
    } else if (valueBaseType == TimeUnit.Type.month) {
      Calendar.MONTH
    } else 0
  }

  /**この単位から変換可能な単位のうち、現在の単位より一つ小さい単位を取得する。
   *
   * @return この単位から変換可能な単位のうち、現在の単位より一つ小さい単位
   */
  def nextFinerUnit = {
    val descending = descendingUnits
    var index = -1
    for (i <- 0 until descending.length) {
      if (descending(i) == this) {
        index = i
      }
    }
    if (index == descending.length - 1) None
    else descending(index + 1)
  }

  private[time] def toString(quantity: Long) = {
    val buffer = new StringBuffer
    buffer.append(quantity)
    buffer.append(" ")
    buffer.append(valueType.name)
    buffer.append(if (quantity == 1) "" else "s")
    buffer.toString
  }

}

/**`TimeUnit`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object TimeUnit extends Enum[TimeUnit] {

  private[time] final class Type(override val ordinal: Int,override val name: String) extends EnumEntry {
    override def equals(obj: Any): Boolean = obj match {
      case that: Type => ordinal == that.ordinal && name == that.name
      case _ => false
    }

    override def hashCode: Int = ordinal.hashCode + name.hashCode
  }

  private[time] object Type extends Enum[Type]{

    val millisecond = new Type(1, "millisecond")
    val second = new Type(2, "second")
    val minute = new Type(3, "minute")
    val hour = new Type(4, "hour")
    val day = new Type(5, "day")
    val week = new Type(6, "week")
    val month = new Type(7, "month")
    val quarter = new Type(8, "quarter")
    val year = new Type(9, "year")

    Type.millisecond % Type.second % Type.minute %
      Type.hour % Type.day % Type.week %
      Type.month % Type.year

  }

  /**ミリ秒単位 */
  val millisecond = new TimeUnit("millisecond", Type.millisecond, Type.millisecond, TimeUnitConversionFactor.identical)

  /**秒単位 */
  val second = new TimeUnit("second", Type.second, Type.millisecond, TimeUnitConversionFactor.millisecondsPerSecond)

  /**分単位 */
  val minute = new TimeUnit("minute", Type.minute, Type.millisecond, TimeUnitConversionFactor.millisecondsPerMinute)

  /**時単位 */
  val hour = new TimeUnit("hour", Type.hour, Type.millisecond, TimeUnitConversionFactor.millisecondsPerHour)

  /**日単位 */
  val day = new TimeUnit("day", Type.day, Type.millisecond, TimeUnitConversionFactor.millisecondsPerDay)

  /**週単位 */
  val week = new TimeUnit("week",Type.week, Type.millisecond, TimeUnitConversionFactor.millisecondsPerWeek)

  /**月単位 */
  val month = new TimeUnit("month",Type.month, Type.month, TimeUnitConversionFactor.identical)

  /**四半期単位 */
  val quarter = new TimeUnit("quarter",Type.quarter, Type.month, TimeUnitConversionFactor.monthsPerQuarter)

  /**年単位 */
  val year = new TimeUnit("year",Type.year, Type.month, TimeUnitConversionFactor.monthsPerYear)

  millisecond % second % minute % hour % day % week % month % quarter % year

  private val DESCENDING_MS_BASED = List(
    week,
    day,
    hour,
    minute,
    second,
    millisecond
  )

  private val DESCENDING_MS_BASED_FOR_DISPLAY = List(
    day,
    hour,
    minute,
    second,
    millisecond
  )

  private val DESCENDING_MONTH_BASED = List(
    year,
    quarter,
    month
  )

  private val DESCENDING_MONTH_BASED_FOR_DISPLAY = List(
    year,
    month
  )

}