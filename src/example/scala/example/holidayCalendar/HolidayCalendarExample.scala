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

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.hamcrest.CoreMatchers.is
import org.junit.Assert.assertThat
import org.sisioh.baseunits.scala.time._

class HolidayCalendarExample extends AssertionsForJUnit {

  /**Example.
   */
  @Test
  def testDeriveBirthday {
    // Calculate Martin Luther King, Jr.'s birthday, January 15, for the year 2005:
    val mlkBirthday = DateSpecification.fixed(1, 15)
    // Then you can do checks like
    val jan15_2005 = CalendarDate.from(2005, 1, 15)
    assertThat(mlkBirthday.isSatisfiedBy(jan15_2005), is(true))
    // Derive the date(s) for an interval
    val mlk2005 = mlkBirthday.firstOccurrenceIn(CalendarInterval.year(2005))
    assertThat(mlk2005.get, is(jan15_2005))
    // Calculate all the birthdays in his lifetime
    val mlkLifetime = CalendarInterval.inclusive(1929, 1, 15, 1968, 4, 4)
    val mlkBirthdays = mlkBirthday.iterateOver(mlkLifetime)
    assertThat(mlkBirthdays.next, is(CalendarDate.from(1929, 1, 15)))
    assertThat(mlkBirthdays.next, is(CalendarDate.from(1930, 1, 15)))
    // etc.
    // By the way, to calculate how long MLK lived,
    assertThat(mlkLifetime.length, is(Duration.days(14325)))
  }

  /**Example.
   */
  @Test
  def testDeriveThanksgiving {
    // Calculate Thanksgiving, the 4th Thursday in November, for the year 2005
    val thanksgiving = DateSpecification.nthOccuranceOfWeekdayInMonth(11, DayOfWeek.Thursday, 4)
    // With the specification, you can do checks like
    assertThat(thanksgiving.isSatisfiedBy(CalendarDate.from(2005, 11, 24)), is(true))
    assertThat(thanksgiving.isSatisfiedBy(CalendarDate.from(2005, 11, 25)), is(false))
    // Derive the date(s) for an interval
    assertThat(thanksgiving.firstOccurrenceIn(CalendarInterval.year(2005)).get, is(CalendarDate.from(2005, 11, 24)))

    // Calculate all the Thanksgivings over a three year interval.
    val y2002_2004 = CalendarInterval.inclusive(2002, 1, 1, 2004, 12, 31)
    assertThat(thanksgiving.firstOccurrenceIn(y2002_2004).get, is(CalendarDate.from(2002, 11, 28)))
    val iterator = thanksgiving.iterateOver(y2002_2004)
    assertThat(iterator.next, is(CalendarDate.from(2002, 11, 28)))
    assertThat(iterator.next, is(CalendarDate.from(2003, 11, 27)))
    assertThat(iterator.next, is(CalendarDate.from(2004, 11, 25)))
    assertThat(iterator.hasNext, is(false))
  }
}