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
package example.doctorAppointments

import java.util.TimeZone
import org.sisioh.baseunits.scala.time.CalendarDate
import collection.mutable.ListBuffer

class AppointmentCalendar private[doctorAppointments]
(private[doctorAppointments] val defaultZone: TimeZone) {

  var events = Set.empty[Appointment]

  def add(anEvent: Appointment) {
    events += anEvent
  }

  def dailyScheduleFor(calDate: CalendarDate): List[Appointment] = {
//    val daysAppointments = ListBuffer.empty[Appointment]
    val day = calDate.asTimeInterval(defaultZone)

    events.filter(_.timeInterval.intersects(day)).toList

//    for (event <- events) {
//      if (event.timeInterval.intersects(day)) {
//        daysAppointments += event
//      }
//    }
//    daysAppointments.result
  }

}