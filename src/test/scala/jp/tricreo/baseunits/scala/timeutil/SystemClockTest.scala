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
package jp.tricreo.baseunits.scala.timeutil

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import jp.tricreo.baseunits.scala.time.TimePoint
import java.util.Date
import scala.math._

/**`SystemClock`のテストクラス。
 * @author j5ik2o
 */
class SystemClockTest extends AssertionsForJUnit {
  /**[[SystemClock]]のテスト。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_SystemClockTimeSource {
    // The following calls allow polymorphic substitution of TimeSources
    // either in applications or, more often, in testing.
    val source = SystemClock
    val expectedNow = TimePoint.from(new Date).breachEncapsulationOfMillisecondsFromEpoc
    val now = source.now.breachEncapsulationOfMillisecondsFromEpoc

    // タイミングによって成功しない、微妙なテスト…。
    assert(abs((now.toDouble - expectedNow)) <= 50)
  }
}