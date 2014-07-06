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

import org.scalatest.junit.AssertionsForJUnit
import java.util.TimeZone
import org.junit.{ Test, After }
import org.sisioh.baseunits.scala.time.{ CalendarDate, TimeSource, TimePoint }

/**
 * `Clock`のテストクラス。
 * @author j5ik2o
 */
class ClockTest extends AssertionsForJUnit {

  val gmt = TimeZone.getTimeZone("Universal")

  val dec1_5am_gmt = TimePoint.at(2004, 12, 1, 5, 0, gmt)

  val pt = TimeZone.getTimeZone("America/Los_Angeles")

  //  val ct = TimeZone.getTimeZone("America/Chicago")

  /** 現在時間を問われた時、常に[[#dec1_5am_gmt]]を返す [[TimeSource]] */
  val dummySourceDec1_5h = new TimeSource() {
    override def now = dec1_5am_gmt
  }

  /**
   * テストの情報を破棄する。
   * @throws Exception 例外が発生した場合
   */
  @After
  def tearDown {
  }

  /**
   * [[Clock#now()]]のテスト。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Now {
    val clock = Clock(dummySourceDec1_5h)
    assert(clock.now == dec1_5am_gmt)
  }

  /**
   * [[Clock#now()]]で例外が発生しないこと。
   * [ 1466694 ] Clock.now() should use default TimeSource
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_NowDoesntBreak {
    Clock.now
  }

  /**
   * [[Clock#setDefaultTimeZone(TimeZone)]]のテスト。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_Today {
    val clock = Clock(dummySourceDec1_5h, gmt)

    assert(clock.todayAsDate == CalendarDate.from(2004, 12, 1, gmt))
    assert(clock.now == dec1_5am_gmt)

    val clock2 = clock.copy(timeZone = pt)
    assert(clock2.todayAsDate == CalendarDate.from(2004, 11, 30, pt))
    assert(clock2.now == dec1_5am_gmt)
  }

}