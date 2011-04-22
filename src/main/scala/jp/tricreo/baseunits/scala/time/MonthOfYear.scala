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

/**1年の中の特定の「月」を表す列挙型。
 *
 * @param value 1 based: January = 1, February = 2, ...
 * @param lastDayOfThisMonth その月の最終日
 * @param calendarValue [[Calendar]]に定義する月をあらわす定数値
 */
@serializable
sealed class MonthOfYear
(private[time] val value: Int,
 private[time] val lastDayOfThisMonth: DayOfMonth,
 private[time] val calendarValue: Int) {

  /**このオブジェクトの[[#calendarValue]]フィールド（[[Calendar]]に定義する月をあらわす定数値）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return [[Calendar]]に定義する月をあらわす定数値（[[Calendar#JANUARY]]〜[[Calendar#DECEMBER]]）
   */
  def breachEncapsulationOfCalendarValue = calendarValue;

  /**このオブジェクトの[[#value]]フィールド（月をあらわす数 1〜12）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 月をあらわす数（1〜12）
   */
  def breachEncapsulationOfValue = value

  /**指定した日 {@code other} が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
   *
   * <p>{@code other} が {@code null} である場合と、お互いが同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象日時
   * @return 過去である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isAfter(other: MonthOfYear) = isBefore(other) == false && equals(other) == false;

  /**指定した日 {@code other} が、このオブジェクトが表現する日よりも未来であるかどうかを検証する。
   *
   * <p>{@code other} が {@code null} である場合と、お互いが同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象日
   * @return 未来である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
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

object MonthOfYear {
  /**January */
  val Jan = MonthOfYear(1, DayOfMonth(31), Calendar.JANUARY)

  /**Feburary */
  val Feb = new MonthOfYear(2, DayOfMonth(28), Calendar.FEBRUARY) {
    override def getLastDayOfThisMonth(year: Int): DayOfMonth = {
      val calendar = new GregorianCalendar(year, 2, 1)
      if (calendar.isLeapYear(year)) DayOfMonth(29) else DayOfMonth(28)
    }
  }
  /**March */
  val Mar = MonthOfYear(3, DayOfMonth(31), Calendar.MARCH)

  /**April */
  val Apr = MonthOfYear(4, DayOfMonth(30), Calendar.APRIL)

  /**May */
  val May = MonthOfYear(5, DayOfMonth(31), Calendar.MAY)

  /**June */
  val Jun = MonthOfYear(6, DayOfMonth(30), Calendar.JUNE)

  /**July */
  val Jul = MonthOfYear(7, DayOfMonth(31), Calendar.JULY)

  /**August */
  val Aug = MonthOfYear(8, DayOfMonth(31), Calendar.AUGUST)

  /**September */
  val Sep = MonthOfYear(9, DayOfMonth(30), Calendar.SEPTEMBER)

  /**October */
  val Oct = MonthOfYear(10, DayOfMonth(31), Calendar.OCTOBER)

  /**November */
  val Nov = MonthOfYear(11, DayOfMonth(30), Calendar.NOVEMBER)

  /**December */
  val Dec = MonthOfYear(12, DayOfMonth(31), Calendar.DECEMBER)

  private val values: List[MonthOfYear] = List(Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)

  def apply(value: Int): MonthOfYear = values.find(_.value == value).get

  private[time] def apply(value: Int, lastDayOfThisMonth: DayOfMonth, calendarValue: Int) =
    new MonthOfYear(value, lastDayOfThisMonth, calendarValue)

  private[time] def unapply(monthOfYear: MonthOfYear) =
    Some(monthOfYear.value, monthOfYear.lastDayOfThisMonth, monthOfYear.calendarValue)
}