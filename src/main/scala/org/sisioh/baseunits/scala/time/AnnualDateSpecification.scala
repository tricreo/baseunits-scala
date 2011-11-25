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

import jp.tricreo.baseunits.scala.intervals.Limit

/**
 * 1年間に1度だけ仕様を満たす日付仕様。
 *
 * @author j5ik2o
 */
abstract class AnnualDateSpecification extends DateSpecification {

  override def firstOccurrenceIn(interval: CalendarInterval) = {
    val firstTry = ofYear(interval.start.toValue.asCalendarMonth.breachEncapsulationOfYear)
    if (interval.includes(Limit(firstTry))) {
      Some(firstTry)
    } else {
      val secondTry = ofYear(interval.start.toValue.asCalendarMonth.breachEncapsulationOfYear + 1)
      if (interval.includes(Limit(secondTry))) {
        Some(secondTry)
      } else None
    }
  }

  override def iterateOver(interval: CalendarInterval) = {
    new Iterator[CalendarDate] {

      var _next = firstOccurrenceIn(interval)

      var year = _next map {
        o => o.asCalendarMonth.breachEncapsulationOfYear
      } getOrElse (-1)

      override def hasNext = _next != None

      override def next = {
        if (hasNext == false) {
          throw new NoSuchElementException
        }
        val current = _next
        year += 1
        _next = Some(ofYear(year))
        if (interval.includes(Limit(_next.get)) == false) {
          _next = None
        }
        current.get
      }
    }
  }

  /**
   * 指定した年においてこの日付仕様を満たす年月日を返す。
   *
   * @param year 西暦年をあらわす数
   * @return [[jp.tricreo.baseunits.scala.time.CalendarDate]]
   */
  def ofYear(year: Int): CalendarDate
}