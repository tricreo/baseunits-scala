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
 * ある特定の年月日を表す日付仕様。
 *
 * @author j5ik2o
 * @param date 年月日
 */
class FixedDateSpecification private[time]
(private[time] val date: CalendarDate)
  extends DateSpecification {

  def iterateOver(interval: CalendarInterval): Iterator[CalendarDate] = {
    if (firstOccurrenceIn(interval) == None) {
      return Iterator.empty
    }
    new Iterator[CalendarDate] {

      var end: Boolean = _

      override def hasNext = end

      override def next = {
        if (hasNext == false) {
          throw new NoSuchElementException
        }
        end = true
        date
      }
    }
  }


  override def isSatisfiedBy(date: CalendarDate): Boolean =
    date == this.date

  def firstOccurrenceIn(interval: CalendarInterval): Option[CalendarDate] =
    if (interval.includes(Limit(date))) Some(date)
    else None
}
