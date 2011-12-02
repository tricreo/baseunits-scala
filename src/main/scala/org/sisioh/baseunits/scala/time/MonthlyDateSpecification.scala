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

import org.sisioh.baseunits.scala.intervals.Limit

/**毎月1度だけ仕様を満たす日付仕様。
 *
 * @author j5ik2o
 */
abstract class MonthlyDateSpecification extends DateSpecification {

  override def firstOccurrenceIn(interval: CalendarInterval) = {
    val month = interval.start.toValue.asCalendarMonth

    val firstTry = ofYearMonth(month)
    if (interval.includes(Limit(firstTry))) {
      Some(firstTry)
    } else {
      val secondTry = ofYearMonth(month.nextMonth)
      if (interval.includes(Limit(secondTry))) {
        Some(secondTry)
      } else None
    }
  }

  override def iterateOver(interval: CalendarInterval) = {
    new Iterator[CalendarDate] {

      var _next = firstOccurrenceIn(interval)

      var _month = next.asCalendarMonth

      override def hasNext = next != None

      override def next = {
        if (hasNext == false) {
          throw new NoSuchElementException
        }
        val current = _next
        _month = _month.nextMonth
        _next = Some(MonthlyDateSpecification.this.ofYearMonth(_month))
        if (interval.includes(Limit(_next.get)) == false) {
          _next = None
        }
        current.get
      }
    }
  }

  /**指定した年月においてこの日付仕様を満たす年月日を返す。
   *
   * @param month 年月
   * @return [[org.sisioh.baseunits.scala.time.CalendarDate]]
   */
  def ofYearMonth(month: CalendarMonth): CalendarDate
}
