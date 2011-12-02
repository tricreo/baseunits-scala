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
 * 毎年X月Y日、を表す日付仕様。
 *
 * @author j5ik2o
 * @param month 月
 * @param day 日
 */
class AnnualFixedDateSpecification private[time] (private[time] val month: MonthOfYear,
  private[time] val day: DayOfMonth)
  extends AnnualDateSpecification {

  override def isSatisfiedBy(date: CalendarDate) = {
    day == date.breachEncapsulationOfDay &&
      month == date.asCalendarMonth.breachEncapsulationOfMonth
  }

  override def ofYear(year: Int) = CalendarDate.from(year, month, day)

  override def toString = day.toString + " " + month.toString

}