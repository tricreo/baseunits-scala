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
package org.sisioh.baseunits.scala.timeutil

import java.util.TimeZone

import org.sisioh.baseunits.scala.time._

/**
 * 時計を表すクラス。
 *
 * @author j5ik2o
 */
case class Clock(timeSource: TimeSource = SystemClock, timeZone: TimeZone = TimeZones.Default) {

  /**
   * 現在時刻を取得する。
   *
   * @return 現在時刻
   */
  def now: TimePoint = timeSource.now

  /**
   * 今日の日付を所得する。
   *
   * @return 今日の日付
   */
  def todayAsDate: CalendarDate = now.asCalendarDate(timeZone)

  /**
   * 今日の日時を所得する。
   *
   * @return 今日の日時
   */
  def todayAsDateTime: CalendarDateTime = now.asCalendarDateTime(timeZone)

  /**
   * 今日の曜日を取得する。
   *
   * @return 今日の曜日
   */
  def dayOfWeek: DayOfWeek = todayAsDate.dayOfWeek

  /**
   * 今月を所得する。
   *
   * @return 今月
   */
  def month: CalendarYearMonth =
    todayAsDate.asCalendarMonth

  /**
   * 今年を取得する。
   *
   * @return 今年
   */
  def year: Int = month.breachEncapsulationOfYear

}

object Clock extends Clock(SystemClock, TimeZones.Default)