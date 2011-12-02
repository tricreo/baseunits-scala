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
import org.sisioh.baseunits.scala.time.{CalendarDate, TimeSource}

/**時計を表すクラス。
 * このクラスはステートを持つstaticなユーティリティクラスである。
 *
 * @author j5ik2o
 */
object Clock {

  private[this] var _timeSourceOption: Option[TimeSource] = None

  /**日付の算出に使用する [[java.util.TimeZone]] */
  private[this] var _defaultTimeZoneOption: Option[TimeZone] = None

  /**この時計が日付の算出に使用する [[java.util.TimeZone]] を取得する。
   *
   * @return 日付の算出に使用する [[java.util.TimeZone]]
   */
  def defaultTimeZone: TimeZone = {
    // There is no reasonable automatic default.
    _defaultTimeZoneOption.get
  }

  def defaultTimeZone_=(value: TimeZone) {
    _defaultTimeZoneOption = Some(value)
  }

  /**現在時刻を取得する。
   *
   * @return 現在時刻
   */
  def now = timeSource.now

  /**このクラスが保持するステートをリセットする。
   *
   * このクラスは、[[java.util.TimeZone]]
   * と[[org.sisioh.baseunits.scala.time.TimeSource]] を保持している。
   */
  def reset() {
    _defaultTimeZoneOption = None
    _timeSourceOption = None
  }

  /**[[org.sisioh.baseunits.scala.timeutil.SystemClock]]を取得する。
   *
   * デフォルトでは [[org.sisioh.baseunits.scala.timeutil.SystemClock]] を使用する。
   *
   * @return [[org.sisioh.baseunits.scala.time.TimeSource]]
   */
  def timeSource: TimeSource = _timeSourceOption match {
    case None => { _timeSourceOption = Some(SystemClock); _timeSourceOption.get }
    case Some(timeSource) => timeSource
  }

  def timeSource_=(value: TimeSource) {
    _timeSourceOption = Some(value)
  }

  /**今日の日付を所得する。
   *
   * 日付は、あらかじめ設定済みの [[org.sisioh.baseunits.scala.time.TimeZone]] に基づき計算する。
   * `TimeZone`を未設定の状態でこのメソッドを呼び出してはならない。
   *
   * @return 今日の日付
   * @throws IllegalStateException [[org.sisioh.baseunits.scala.time.TimeZone]]が未設定の場合
   */
  def today: CalendarDate = {
    if (_defaultTimeZoneOption == None) {
      throw new IllegalStateException("CalendarDate cannot be computed without setting a default TimeZone.")
    }
    now.calendarDate(defaultTimeZone)
  }

}