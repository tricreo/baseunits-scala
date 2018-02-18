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

/**
  * 1年間に1度だけ仕様を満たす日付仕様。
  *
  * @author j5ik2o
  */
abstract class AnnualDateSpecification extends DateSpecification {

  override def firstOccurrenceIn(interval: CalendarInterval): Option[CalendarDate] = {
    val firstTry = ofYear(interval.start.toValue.asCalendarMonth.year)
    if (interval.includes(Limit(firstTry))) {
      Some(firstTry)
    } else {
      val secondTry = ofYear(interval.start.toValue.asCalendarMonth.year + 1)
      if (interval.includes(Limit(secondTry))) {
        Some(secondTry)
      } else None
    }
  }

  override def iterateOver(interval: CalendarInterval): Iterator[CalendarDate] = {
    new Iterator[CalendarDate] {

      private var _next = firstOccurrenceIn(interval)

      private var year = _next map { o =>
        o.asCalendarMonth.year
      } getOrElse (-1)

      override def hasNext: Boolean = _next.isDefined

      override def next: CalendarDate = {
        if (!hasNext) {
          throw new NoSuchElementException
        }
        val current = _next
        year += 1
        _next = Some(ofYear(year))
        if (!interval.includes(Limit(_next.get))) {
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
    * @return [[org.sisioh.baseunits.scala.time.CalendarDate]]
    */
  def ofYear(year: Int): CalendarDate

}
