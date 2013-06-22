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


sealed trait Shifter extends EnumEntry {
  def shift(date: CalendarDate, cal: BusinessCalendar): CalendarDate
}

/**コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object Shifter extends Enum[Shifter] {

  case object Next extends Shifter {
    def shift(date: CalendarDate, cal: BusinessCalendar) =
      cal.nearestNextBusinessDay(date)
  }

  case object Prev extends Shifter {
    def shift(date: CalendarDate, cal: BusinessCalendar) =
      cal.nearestPrevBusinessDay(date)
  }

  Prev % Next

}


/**指定日が非営業日の場合のシフト戦略。
 */
class MonthlyFixedBusinessDateSpecification
(val day: DayOfMonth,
 val shifter: Shifter,
 val cal: BusinessCalendar) extends MonthlyDateSpecification {

  def ofYearMonth(month: CalendarMonth) =
    shifter.shift(CalendarDate.from(month.breachEncapsulationOfYear,
      month.breachEncapsulationOfMonth, day), cal)

  override def isSatisfiedBy(date: CalendarDate) =
    if (cal.isBusinessDay(date)) {
      val thisMonth = ofYearMonth(date.breachEncapsulationOfYearMonth)
      thisMonth.equals(date)
    } else false

}