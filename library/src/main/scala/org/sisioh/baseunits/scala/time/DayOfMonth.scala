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

/**
  * 1ヶ月間の中の特定の「日」を表すクラス。
  *
  * タイムゾーンの概念はない。
  *
  * @author j5ik2o
  */
class DayOfMonth private[time] (val value: Int) extends Ordered[DayOfMonth] with Serializable {

  require(
    value >= DayOfMonth.Min && value <= DayOfMonth.Max,
    "Illegal value for day of month: " + value
      + ", please use a value between 1 and 31"
  )

  /**
    * このオブジェクトの`value`フィールド（日をあらわす正数）を返す。
    *
    * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
    *
    * @return 日をあらわす正数（1〜31）
    */
  @deprecated("Use value property instead", "0.1.18")
  val breachEncapsulationOfValue = value

  override def compare(that: DayOfMonth): Int = value - that.value

  override def hashCode: Int = 31 * value.hashCode

  override def equals(obj: Any): Boolean = obj match {
    case that: DayOfMonth => (value compare that.value) == 0
    case _                => false
  }

  /**
    * 指定した日 `other` が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
    *
    * お互いが同一日時である場合は `false` を返す。
    *
    * @param other [[DayOfMonth]]
    * @return 過去である場合は`true`、そうでない場合は`false`
    */
  def isAfter(other: DayOfMonth): Boolean = {
    !isBefore(other) && !equals(other)
  }

  /**
    * この日を、指定した年月に適用可能かどうか調べる。
    *
    * 例えば、31日は6月に適用不可能であるが、7月には適用可能である。
    * また、29日は閏年の2月に適用可能であるが、平年の2月には適用不可能である。
    *
    * @param month 年月
    * @return 適用可能な場合は`true`、そうでない場合は`false`
    */
  def isApplyable(month: CalendarYearMonth): Boolean =
    !month.lastDayOfMonth.isBefore(this)

  /**
    * この日を、指定した年月に適用可能かどうか調べる。
    *
    * 例えば、31日は6月に適用不可能であるが、7月には適用可能である。
    * また、29日は閏年の2月に適用可能であるが、平年の2月には適用不可能である。
    *
    * @param year 年
    * @param month 月
    * @return 適用可能な場合は`true`、そうでない場合は`false`
    */
  def isApplyable(year: Int, month: MonthOfYear): Boolean =
    !month.getLastDayOfThisMonth(year).isBefore(this)

  /**
    * 指定した日 `other` が、このオブジェクトが表現する日よりも未来であるかどうかを検証する。
    *
    * お互いが同一日時である場合は `false` を返す。
    *
    * @param other 対象日
    * @return 未来である場合は`true`、そうでない場合は`false`
    */
  def isBefore(other: DayOfMonth): Boolean = value < other.value

  /**
    * 指定した年月のこの日を返す。
    *
    * @param month 年月
    * @return [[org.sisioh.baseunits.scala.time.CalendarDate]]
    * @throws IllegalArgumentException 引数`month`の月にこの日が存在しない場合
    */
  def on(month: CalendarYearMonth): CalendarDate =
    CalendarDate.from(month, this, month.zoneId)

  override def toString: String = String.valueOf(value)

}

/**
  * `DayOfMonth`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object DayOfMonth {
  val Min = 1
  val Max = 31

  /**
    * インスタンスを生成する。
    *
    * @param value 日をあらわす正数（1〜31）
    * @return [[org.sisioh.baseunits.scala.time.DayOfMonth]]
    */
  def apply(value: Int): DayOfMonth = new DayOfMonth(value)

  /**
    * 抽出子メソッド。
    *
    * @param dayOfMonth [[org.sisioh.baseunits.scala.time.DayOfMonth]]
    * @return `Option[Int]`
    */
  def unapply(dayOfMonth: DayOfMonth): Option[Int] = Some(dayOfMonth.value)
}
