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
package jp.tricreo.baseunits.scala.time

import java.util.{GregorianCalendar, Calendar}
import jp.tricreo.baseunits.scala.util.{AbstractEnum, EnumEntry}

/**1年の中の特定の「月」を表す列挙型。
 *
 * @param value 1 based: January = 1, February = 2, ...
 * @param lastDayOfThisMonth その月の最終日
 * @param calendarValue [[java.util.Calendar]]に定義する月をあらわす定数値
 */
sealed class MonthOfYear
(private[time] val lastDayOfThisMonth: DayOfMonth,
 private[time] val calendarValue: Int) extends EnumEntry {

  private[time] def value = ordinal

  /**このオブジェクトの`calendarValue`フィールド（[[java.util.Calendar]]に定義する月をあらわす定数値）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return [[java.util.Calendar]]に定義する月をあらわす定数値（JANUARY〜DECEMBER）
   */
  def breachEncapsulationOfCalendarValue = calendarValue;

  /**このオブジェクトの`value`フィールド（月をあらわす数 1〜12）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 月をあらわす数（1〜12）
   */
  def breachEncapsulationOfValue = value

  /**指定した日 `other` が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
   *
   * お互いが同一日時である場合は `false` を返す。
   *
   * @param other 対象日時
   * @return 過去である場合は`true`、そうでない場合は`false`
   */
  def isAfter(other: MonthOfYear) = isBefore(other) == false && equals(other) == false

  /**指定した日 `other` が、このオブジェクトが表現する日よりも未来であるかどうかを検証する。
   *
   * お互いが同一日時である場合は `false` を返す。
   *
   * @param other 対象日
   * @return 未来である場合は`true`、そうでない場合は`false`
   */
  def isBefore(other: MonthOfYear) = value < other.value

  //	public DayOfYear at(DayOfMonth month) {
  //		// ...
  //	}

  /**指定した年の、この月を表す年月を返す。
   *
   * @param year 年
   * @return 年月
   */
  def on(year: Int): CalendarMonth = CalendarMonth.from(year, this)

  /**その月の最終日を取得する。
   *
   * @param year 該当年. 2月の閏年判定に関わらない場合は、何でも良い。
   * @return 最終日
   */
  private[time] def getLastDayOfThisMonth(year: Int) = lastDayOfThisMonth

}

/**`MonthOfYear`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object MonthOfYear extends AbstractEnum[MonthOfYear] {

  /**January */
  val Jan = new MonthOfYear(DayOfMonth(31), Calendar.JANUARY)

  /**Feburary */
  val Feb = new MonthOfYear(DayOfMonth(28), Calendar.FEBRUARY) {
    override def getLastDayOfThisMonth(year: Int): DayOfMonth = {
      val calendar = new GregorianCalendar(year, 2, 1)
      if (calendar.isLeapYear(year)) DayOfMonth(29) else DayOfMonth(28)
    }
  }
  /**March */
  val Mar = new MonthOfYear(DayOfMonth(31), Calendar.MARCH)

  /**April */
  val Apr = new MonthOfYear(DayOfMonth(30), Calendar.APRIL)

  /**May */
  val May = new MonthOfYear(DayOfMonth(31), Calendar.MAY)

  /**June */
  val Jun = new MonthOfYear(DayOfMonth(30), Calendar.JUNE)

  /**July */
  val Jul = new MonthOfYear(DayOfMonth(31), Calendar.JULY)

  /**August */
  val Aug = new MonthOfYear(DayOfMonth(31), Calendar.AUGUST)

  /**September */
  val Sep = new MonthOfYear(DayOfMonth(30), Calendar.SEPTEMBER)

  /**October */
  val Oct = new MonthOfYear(DayOfMonth(31), Calendar.OCTOBER)

  /**November */
  val Nov = new MonthOfYear(DayOfMonth(30), Calendar.NOVEMBER)

  /**December */
  val Dec = new MonthOfYear(DayOfMonth(31), Calendar.DECEMBER)

  Jan % Feb % Mar % Apr % May % Jun % Jul % Aug % Sep % Oct % Nov % Dec

}