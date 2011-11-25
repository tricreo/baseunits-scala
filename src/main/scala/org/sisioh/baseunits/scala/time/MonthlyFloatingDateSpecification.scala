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

/**毎月の第?曜日を表す日付仕様。
 *
 * @author j5ik2o
 * @param dayOfWeek [[jp.tricreo.baseunits.scala.time.DayOfWeek]]
 * @param occurrence 周回数（1〜5）
 */
class MonthlyFloatingDateSpecification private[time]
(private[time] val dayOfWeek: DayOfWeek,
 private[time] val occurrence: Int)
  extends MonthlyDateSpecification {

  require(1 <= occurrence && occurrence <= 5)

  override def isSatisfiedBy(date: CalendarDate) =
    ofYearMonth(date.asCalendarMonth) == date

  override def ofYearMonth(month: CalendarMonth) = {
    val firstOfMonth = CalendarDate.from(month, DayOfMonth(1))
    val dayOfWeekOffset = dayOfWeek.value - firstOfMonth.dayOfWeek.value
    val dateOfFirstOccurrenceOfDayOfWeek = dayOfWeekOffset + (if (dayOfWeekOffset < 0) 8 else 1)
    val date = ((occurrence - 1) * 7) + dateOfFirstOccurrenceOfDayOfWeek
    CalendarDate.from(month, DayOfMonth(date))
  }

}