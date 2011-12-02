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

import org.sisioh.dddbase.spec.Specification


/**日付の仕様を表現するオブジェクト。
 *
 * @author j5ik2o
 */
abstract class DateSpecification extends Specification[CalendarDate] {

  /**指定した期間の中で、この日付仕様を満たす最初の年月日を返す。
   *
   * @param interval 期間
   * @return 年月日。但し、仕様を満たす日がなかった場合は`None`
   */
  def firstOccurrenceIn(interval: CalendarInterval): Option[CalendarDate]

  /**与えた日付が、この日付仕様を満たすかどうか検証する。
   *
   * @param date 検証対象の日付
   * @return 仕様を満たす場合は`true`、そうでない場合は`false`
   */
  override def isSatisfiedBy(date: CalendarDate): Boolean

  /**
   * 指定した期間の中で、この日付仕様を満たす年月日を順次取得する反復子を返す。
   *
   * @param interval 期間
   * @return 反復子
   */
  def iterateOver(interval: CalendarInterval): Iterator[CalendarDate]

}

/**`DateSpecification`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object DateSpecification {
  /**
   * 特定のある1日だけにマッチする日付仕様のインスタンスを返す。
   *
   * 毎月31日を指定した場合、該当月に31日が存在しなければ、その月にはヒットしない。
   *
   * @param date マッチする日
   * @return 日付仕様
   */
  def fixed(date: CalendarDate) =
    new FixedDateSpecification(date)

  /**
   * 日付仕様「毎月`day`日」のインスタンスを生成する。
   *
   * 毎月31日を指定した場合、該当月に31日が存在しなければ、その月にはヒットしない。
   *
   * @param day 日を表す正数（1〜31）
   * @throws IllegalArgumentException 引数`day`が1〜31の範囲ではない場合
   * @return 日付仕様
   */
  def fixed(day: Int) =
    new MonthlyFixedDateSpecification(DayOfMonth(day))

  /**
   * 日付仕様のインスタンスを生成する。
   *
   * @param month 月を表す正数（1〜12）
   * @param day 日を表す正数（1〜31）
   * @throws IllegalArgumentException 引数`month`が1〜12の範囲ではない場合もしくは、
   * 引数`day`が1〜31の範囲ではない場合
   * @return 日付仕様
   */
  def fixed(month: Int, day: Int) =
    new AnnualFixedDateSpecification(MonthOfYear(month), DayOfMonth(day))

  /**
   * どの日付にもマッチしない日付仕様を返す。
   *
   * @return 日付仕様
   */
  def never =
    new DateSpecification {

      override def firstOccurrenceIn(interval: CalendarInterval): Option[CalendarDate] =
        throw new UnsupportedOperationException

      override def isSatisfiedBy(date: CalendarDate): Boolean = false

      override def iterateOver(interval: CalendarInterval): Iterator[CalendarDate] = Iterator.empty

    }

  /**毎月第Y◎曜日仕様のインスタンスを生成する。
   *
   * @param dayOfWeek 曜日
   * @param n 周回数（1〜5）
   * @return 日付仕様
   */
  def nthOccuranceOfWeekdayInEveryMonth(dayOfWeek: DayOfWeek, n: Int): DateSpecification =
    new MonthlyFloatingDateSpecification(dayOfWeek, n)

  /**X月の第Y◎曜日仕様のインスタンスを生成する。
   *
   * @param month 月を表す正数（1〜12）
   * @param dayOfWeek 曜日
   * @param n 周回数（1〜5）
   * @return 日付仕様
   */
  def nthOccuranceOfWeekdayInMonth(month: Int, dayOfWeek: DayOfWeek, n: Int): DateSpecification =
    new AnnualFloatingDateSpecification(month, dayOfWeek, n)

}
