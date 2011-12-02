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
package org.sisioh.baseunits.scala.time

import org.scalatest.FunSuite
import java.util.{Calendar, Date => JDate}
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test


/**`TimePoint`のテストクラス。
 */
class TimePointTest extends AssertionsForJUnit {

  @Test
  def test {
    val tp = TimePoint.from(0L)
    assert(tp == TimePoint.from(0L))

    val date = new JDate
    val tp2 = TimePoint.from(date)
    assert(tp2 == TimePoint.from(date))

    val calendar = Calendar.getInstance
    val tp3 = TimePoint.from(calendar)
    assert(tp3 == TimePoint.from(calendar))


    val tp4 = TimePoint.at(2010, 1, 1, 1, 1, TimePoint.GMT)
    assert(tp4 == TimePoint.at(2010, 1, 1, 1, 1, TimePoint.GMT))
  }

}