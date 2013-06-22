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

import org.sisioh.scala.toolbox._


/**`TimeUnit`に変数するためのファクター。
 *
 * @author j5ik2o
 * @param _name 名前
 * @param value 値
 */
private[time] final class TimeUnitConversionFactor
( _name: String, val value: Int) extends EnumEntry {
  override val name = _name
}

/**`TimeUnitConversionFactor`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
private[time] object TimeUnitConversionFactor extends Enum[TimeUnitConversionFactor] {
  val identical = new TimeUnitConversionFactor("identical", 1)
  val millisecondsPerSecond = new TimeUnitConversionFactor("millisecondsPerSecond", 1000)
  val millisecondsPerMinute = new TimeUnitConversionFactor("millisecondsPerMinute", 60 * millisecondsPerSecond.value)
  val millisecondsPerHour = new TimeUnitConversionFactor("millisecondsPerHour", 60 * millisecondsPerMinute.value)
  val millisecondsPerDay = new TimeUnitConversionFactor("millisecondsPerDay", 24 * millisecondsPerHour.value)
  val millisecondsPerWeek = new TimeUnitConversionFactor("millisecondsPerWeek", 7 * millisecondsPerDay.value)
  val monthsPerQuarter = new TimeUnitConversionFactor("monthsPerQuarter", 3)
  val monthsPerYear = new TimeUnitConversionFactor("monthsPerYear", 12)

  identical % millisecondsPerSecond % millisecondsPerMinute % millisecondsPerHour %
    millisecondsPerDay % millisecondsPerWeek % monthsPerQuarter % monthsPerYear

}