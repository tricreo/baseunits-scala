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

sealed trait Shifter {
  def shift(date: CalendarDate, cal: BusinessCalendar): CalendarDate
}

/**
  * コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object Shifter {

  case object Next extends Shifter {
    def shift(date: CalendarDate, cal: BusinessCalendar): CalendarDate =
      cal.nearestNextBusinessDay(date)
  }

  case object Prev extends Shifter {
    def shift(date: CalendarDate, cal: BusinessCalendar): CalendarDate =
      cal.nearestPrevBusinessDay(date)
  }

}

/**
  * 指定日が非営業日の場合のシフト戦略。
  */
class MonthlyFixedBusinessDateSpecification(
    val day: DayOfMonth,
    val shifter: Shifter,
    val cal: BusinessCalendar
) extends MonthlyDateSpecification {

  def ofYearMonth(month: CalendarYearMonth): CalendarDate =
    shifter.shift(CalendarDate.from(month.year, month.month, day, month.zoneId), cal)

  override def isSatisfiedBy(date: CalendarDate): Boolean =
    if (cal.isBusinessDay(date)) {
      val thisMonth = ofYearMonth(date.yearMonth)
      thisMonth.equals(date)
    } else false

}
