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

import java.util.Calendar

/**
 * 1週間の中の特定の「曜日」を表す列挙型。
 *
 * <p>タイムゾーンの概念はない。</p>
 */
@serializable
final class DayOfWeek private[time]
(private[time] val value: Int,
 private[time] val name: String) {
  /**このオブジェクトの[[#value]]フィールド（[[Calendar]]に定義する曜日をあらわす定数値）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return [[Calendar]]に定義する曜日をあらわす定数値（[[Calendar#SUNDAY]]〜[[Calendar#SATURDAY]]）
   */
  def breachEncapsulationOfValue = value

  override def toString = name
}

object DayOfWeek {
  val Sunday = new DayOfWeek(Calendar.SUNDAY, "SUNDAY")
  val Monday = new DayOfWeek(Calendar.MONDAY, "MONDAY")
  val Tuesday = new DayOfWeek(Calendar.TUESDAY, "TUESDAY")
  val Wednesday = new DayOfWeek(Calendar.WEDNESDAY, "WEDNESDAY")
  val Thursday = new DayOfWeek(Calendar.THURSDAY, "THURSDAY")
  val Friday = new DayOfWeek(Calendar.FRIDAY, "FRIDAY")
  val Saturday = new DayOfWeek(Calendar.SATURDAY, "SATURDAY")

  private val values = List(Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday)

  def apply(value: Int) = values.find(_.value == value).get

  def unapply(dayOfWeek: DayOfWeek) = Some(dayOfWeek.value, dayOfWeek.name)
}
