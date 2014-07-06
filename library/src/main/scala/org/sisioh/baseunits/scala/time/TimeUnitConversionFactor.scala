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

/**
 * `TimeUnit`に変数するためのファクター。
 *
 * @author j5ik2o
 * @param _name 名前
 * @param value 値
 */
private[time] final class TimeUnitConversionFactor(_name: String, val value: Int) {
  val name = _name
}

/**
 * `TimeUnitConversionFactor`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
private[time] object TimeUnitConversionFactor {

  val Identical = new TimeUnitConversionFactor("identical", 1)
  val MillisecondsPerSecond = new TimeUnitConversionFactor("millisecondsPerSecond", 1000)
  val MillisecondsPerMinute = new TimeUnitConversionFactor("millisecondsPerMinute", 60 * MillisecondsPerSecond.value)
  val MillisecondsPerHour = new TimeUnitConversionFactor("millisecondsPerHour", 60 * MillisecondsPerMinute.value)
  val MillisecondsPerDay = new TimeUnitConversionFactor("millisecondsPerDay", 24 * MillisecondsPerHour.value)
  val MillisecondsPerWeek = new TimeUnitConversionFactor("millisecondsPerWeek", 7 * MillisecondsPerDay.value)
  val MonthsPerQuarter = new TimeUnitConversionFactor("monthsPerQuarter", 3)
  val MonthsPerYear = new TimeUnitConversionFactor("monthsPerYear", 12)

}