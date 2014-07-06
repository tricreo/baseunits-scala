/*
 * Copyright 2011 Sisioh Project and the Others.
 * lastModified : 2011/04/22
 *
 * This file is part of Tricreo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package example.holidayCalendar

import org.hamcrest.CoreMatchers.is
import org.junit.Assert.assertThat
import org.junit.Test
import org.scalatest.{ ShouldMatchers, Assertions }
import org.sisioh.baseunits.scala.time._

class HolidayCalendarExample extends Assertions with ShouldMatchers {

  /**
   * Example.
   */
  @Test
  def testDeriveBirthday {
    // Calculate Martin Luther King, Jr.'s birthday, January 15, for the year 2005:
    val mlkBirthday = DateSpecification.fixed(1, 15)
    // Then you can do checks like
    val jan15_2005 = CalendarDate.from(2005, 1, 15)
    mlkBirthday.isSatisfiedBy(jan15_2005) shouldEqual true
    // Derive the date(s) for an interval
    val mlk2005 = mlkBirthday.firstOccurrenceIn(CalendarInterval.year(2005))
    mlk2005.get shouldEqual jan15_2005
    // Calculate all the birthdays in his lifetime
    val mlkLifetime = CalendarInterval.inclusive(1929, 1, 15, 1968, 4, 4)
    val mlkBirthdays = mlkBirthday.iterateOver(mlkLifetime)
    mlkBirthdays.next shouldEqual CalendarDate.from(1929, 1, 15)
    mlkBirthdays.next shouldEqual CalendarDate.from(1930, 1, 15)
    // etc.
    // By the way, to calculate how long MLK lived,
    mlkLifetime.length shouldEqual Duration.days(14325)
  }

  /**
   * Example.
   */
  @Test
  def testDeriveThanksgiving {
    // Calculate Thanksgiving, the 4th Thursday in November, for the year 2005
    val thanksgiving = DateSpecification.nthOccuranceOfWeekdayInMonth(11, DayOfWeek.Thursday, 4)
    // With the specification, you can do checks like
    thanksgiving.isSatisfiedBy(CalendarDate.from(2005, 11, 24)) shouldEqual true
    thanksgiving.isSatisfiedBy(CalendarDate.from(2005, 11, 25)) shouldEqual false
    // Derive the date(s) for an interval
    thanksgiving.firstOccurrenceIn(CalendarInterval.year(2005)).get shouldEqual CalendarDate.from(2005, 11, 24)

    // Calculate all the Thanksgivings over a three year interval.
    val y2002_2004 = CalendarInterval.inclusive(2002, 1, 1, 2004, 12, 31)
    thanksgiving.firstOccurrenceIn(y2002_2004).get shouldEqual CalendarDate.from(2002, 11, 28)

    val iterator = thanksgiving.iterateOver(y2002_2004)
    iterator.next shouldEqual CalendarDate.from(2002, 11, 28)
    iterator.next shouldEqual CalendarDate.from(2003, 11, 27)
    iterator.next shouldEqual CalendarDate.from(2004, 11, 25)
    iterator.hasNext shouldEqual false
  }
}