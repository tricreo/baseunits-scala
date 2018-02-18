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

import org.sisioh.baseunits.scala.time.{TimePoint, TimeSource}

/**
  * システム時計に基づき、現在の時刻を返すクラス。
  *
  * @author j5ik2o
  */
object SystemClock extends TimeSource {
  def now: TimePoint = TimePoint.from(System.currentTimeMillis())
}
