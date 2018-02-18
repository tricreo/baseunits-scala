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

import java.util.Calendar

/**
  * 1週間の中の特定の「曜日」を表す列挙型。
  *
  * タイムゾーンの概念はない。
  *
  * @author j5ik2o
  * @param value 1 = Calendar.SUNDAY, 2 = Calendar.MONDAY, ...
  * @param name 名前
  */
final class DayOfWeek private[time] (
    val value: Int,
    val name: String
) {

  /**
    * このオブジェクトの`value`フィールド（[[java.util.Calendar]]に定義する曜日をあらわす定数値）を返す。
    *
    * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
    *
    * @return [[java.util.Calendar]]に定義する曜日をあらわす定数値（`SUNDAY`〜`SATURDAY`）
    */
  @deprecated("Use value property instead", "0.1.18")
  val breachEncapsulationOfValue: Int = value

}

/**
  * コンパニオンオブジェクト。
  *
  * @author j5i2ko
  */
object DayOfWeek {

  val Mapping = Map(
    java.time.DayOfWeek.SUNDAY    -> Calendar.SUNDAY,
    java.time.DayOfWeek.MONDAY    -> Calendar.MONDAY,
    java.time.DayOfWeek.TUESDAY   -> Calendar.TUESDAY,
    java.time.DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY,
    java.time.DayOfWeek.THURSDAY  -> Calendar.THURSDAY,
    java.time.DayOfWeek.FRIDAY    -> Calendar.FRIDAY,
    java.time.DayOfWeek.SATURDAY  -> Calendar.SATURDAY
  )

  val Sunday    = new DayOfWeek(1, "SUNDAY")
  val Monday    = new DayOfWeek(2, "MONDAY")
  val Tuesday   = new DayOfWeek(3, "TUESDAY")
  val Wednesday = new DayOfWeek(4, "WEDNESDAY")
  val Thursday  = new DayOfWeek(5, "THURSDAY")
  val Friday    = new DayOfWeek(6, "FRIDAY")
  val Saturday  = new DayOfWeek(7, "SATURDAY")

  val values =
    Seq(Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday)

  def apply(value: Int): DayOfWeek = values.find(_.value == value).get

  def apply(value: java.time.DayOfWeek): DayOfWeek =
    apply(Mapping(value))

}
