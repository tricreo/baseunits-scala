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
package example.appointmentsCalendar

import org.scalatest.junit.AssertionsForJUnit
import java.util.TimeZone
import org.junit.Test
import org.hamcrest.CoreMatchers.is
import org.junit.Assert.assertThat
import org.sisioh.baseunits.scala.time.{Duration, TimePoint, CalendarDate, TimeOfDay}

/**Example.
 */
class RecurringAppointmentScenario extends AssertionsForJUnit {

  val HonoluluTime = TimeZone.getTimeZone("Pacific/Honolulu")


  /**Example.
   *
   * Daily stand-up meeting at 10:00am each work day. (We work in Honolulu, of course.)
   * Notify 5 minutes before meeting starts.
   * Derive the TimePoint at which I should notify on April 19 2006.
   */
  @Test
  def testDailyMeetingAlert {
    val scheduledMeetingTime = TimeOfDay.from(10, 0)
    val dayOfMeeting = CalendarDate.from(2006, 4, 19)
    val meetingTimeThisDay = scheduledMeetingTime.on(dayOfMeeting)
    val sameMeetingTimeThisDay = dayOfMeeting.at(scheduledMeetingTime)
    assertThat(sameMeetingTimeThisDay, is(meetingTimeThisDay))

    val meetingTimePoint = meetingTimeThisDay.asTimePoint(HonoluluTime)
    assertThat(meetingTimePoint, is(TimePoint.at(2006, 4, 19, 10, 0, 0, 0, HonoluluTime)))

    // The expressions can be strung together.
    assertThat(meetingTimeThisDay.asTimePoint(HonoluluTime).minus(Duration.minutes(5)),
      is(TimePoint.at(2006, 4, 19, 9, 55, 0, 0, HonoluluTime)));
  }
}