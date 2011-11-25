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
import java.util.TimeZone
import org.junit.{Test, After}
import jp.tricreo.baseunits.scala.time.{CalendarDate, TimeSource, TimePoint}

/**`Clock`のテストクラス。
 * @author j5ik2o
 */
class ClockTest extends AssertionsForJUnit {

  val dec1_5am_gmt = TimePoint.atGMT(2004, 12, 1, 5, 0)

  val gmt = TimeZone.getTimeZone("Universal")

  val pt = TimeZone.getTimeZone("America/Los_Angeles")

  val ct = TimeZone.getTimeZone("America/Chicago")

  /** 現在時間を問われた時、常に[[#dec1_5am_gmt]]を返す [[TimeSource]] */
  val dummySourceDec1_5h = new TimeSource() {
    override def now = dec1_5am_gmt
  }


  /**テストの情報を破棄する。
   * @throws Exception 例外が発生した場合
   */
  @After
  def tearDown {
    Clock.reset
  }

  /**[[Clock#now()]]のテスト。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Now {
    Clock.timeSource = dummySourceDec1_5h
    assert(Clock.now == dec1_5am_gmt)
  }

  /**[[Clock#now()]]で例外が発生しないこと。
   * [ 1466694 ] Clock.now() should use default TimeSource
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_NowDoesntBreak {
    Clock.now
  }

  /**[[Clock#setDefaultTimeZone(TimeZone)]]のテスト。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_Today {
    Clock.timeSource = dummySourceDec1_5h

    Clock.defaultTimeZone = gmt
    assert(Clock.today == CalendarDate.from(2004, 12, 1))
    assert(Clock.now == dec1_5am_gmt)

    Clock.defaultTimeZone = pt
    assert(Clock.today == CalendarDate.from(2004, 11, 30))
    assert(Clock.now == dec1_5am_gmt)
  }

  /**[[Clock#today()]]のテスト。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_TodayWithoutTimeZone {
    Clock.timeSource = dummySourceDec1_5h
    try {
      Clock.today
      fail("Clock cannot answer today() without a timezone.")
    } catch {
      case _: RuntimeException => // Correctly threw exception
      case _ => fail
    }
  }
}