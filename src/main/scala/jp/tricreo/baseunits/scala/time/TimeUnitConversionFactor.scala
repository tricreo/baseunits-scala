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

private[time] final class TimeUnitConversionFactor
(val value: Int)
  extends Ordered[TimeUnitConversionFactor] with Serializable {
  def compare(that: TimeUnitConversionFactor) = value - that.value
}

private[time] object TimeUnitConversionFactor {
  val identical = new TimeUnitConversionFactor(1)
  val millisecondsPerSecond = new TimeUnitConversionFactor(1000)
  val millisecondsPerMinute = new TimeUnitConversionFactor(60 * millisecondsPerSecond.value)
  val millisecondsPerHour = new TimeUnitConversionFactor(60 * millisecondsPerMinute.value)
  val millisecondsPerDay = new TimeUnitConversionFactor(24 * millisecondsPerHour.value)
  val millisecondsPerWeek = new TimeUnitConversionFactor(7 * millisecondsPerDay.value)
  val monthsPerQuarter = new TimeUnitConversionFactor(3)
  val monthsPerYear = new TimeUnitConversionFactor(12)

  private val values = List(identical, millisecondsPerSecond, millisecondsPerMinute, millisecondsPerHour,
    millisecondsPerDay, millisecondsPerWeek, monthsPerQuarter, monthsPerYear)

  def apply(value: Int) = values.find(_.value == value).get

  def unapply(timeUnitConversionFactor: TimeUnitConversionFactor) = Some(timeUnitConversionFactor.value)

}