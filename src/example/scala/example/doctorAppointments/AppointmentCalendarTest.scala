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
package example.doctorAppointments

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import java.util.TimeZone
import org.sisioh.baseunits.scala.time.{CalendarDate, Duration, TimeInterval, TimePoint}
import org.hamcrest.CoreMatchers.is
import org.junit.Assert.assertThat
import org.sisioh.baseunits.scala.intervals.Limit

class AppointmentCalendarTest extends AssertionsForJUnit {

  /**Example.
   */
  @Test
  def testEventsForDate {
    val pt = TimeZone.getTimeZone("America/Los_Angeles")

    val jun7at10 = TimePoint.at(2004, 6, 7, 10, 0, 0, 0, pt)
    val shortTime = TimeInterval.startingFrom(Limit(jun7at10), Duration.hours(3))
    val shortEvent = Appointment(shortTime)

    val jun9at13 = TimePoint.at(2004, 6, 9, 13, 0, 0, 0, pt)
    val longTime = TimeInterval.over(Limit(jun7at10), Limit(jun9at13))
    val longEvent = Appointment(longTime)

    val cal = new AppointmentCalendar(pt)
    cal.add(shortEvent)
    cal.add(longEvent)

    assertThat(cal.dailyScheduleFor(CalendarDate.from(2004, 6, 6)).size, is(0))
    assertThat(cal.dailyScheduleFor(CalendarDate.from(2004, 6, 7)).size, is(2))
    assertThat(cal.dailyScheduleFor(CalendarDate.from(2004, 6, 8)).size, is(1))
    assertThat(cal.dailyScheduleFor(CalendarDate.from(2004, 6, 9)).size, is(1))
    assertThat(cal.dailyScheduleFor(CalendarDate.from(2004, 6, 10)).size, is(0))
  }

}