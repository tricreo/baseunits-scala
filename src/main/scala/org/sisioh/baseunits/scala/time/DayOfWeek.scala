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
import org.sisioh.scala.toolbox._

/**1週間の中の特定の「曜日」を表す列挙型。
 *
 * タイムゾーンの概念はない。
 *
 * @author j5ik2o
 * @param value 1 = Calendar.SUNDAY, 2 = Calendar.MONDAY, ...
 * @param _name 名前
 */
final class DayOfWeek private[time]
(private[time] val value: Int,
 _name: String) extends EnumEntry {
  /**このオブジェクトの`value`フィールド（[[java.util.Calendar]]に定義する曜日をあらわす定数値）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return [[java.util.Calendar]]に定義する曜日をあらわす定数値（`SUNDAY`〜`SATURDAY`）
   */
  def breachEncapsulationOfValue = value

  override val name = _name
}

/**コンパニオンオブジェクト。
 *
 * @author j5i2ko
 */
object DayOfWeek extends Enum[DayOfWeek] {

  val Sunday = new DayOfWeek(Calendar.SUNDAY, "SUNDAY")
  val Monday = new DayOfWeek(Calendar.MONDAY, "MONDAY")
  val Tuesday = new DayOfWeek(Calendar.TUESDAY, "TUESDAY")
  val Wednesday = new DayOfWeek(Calendar.WEDNESDAY, "WEDNESDAY")
  val Thursday = new DayOfWeek(Calendar.THURSDAY, "THURSDAY")
  val Friday = new DayOfWeek(Calendar.FRIDAY, "FRIDAY")
  val Saturday = new DayOfWeek(Calendar.SATURDAY, "SATURDAY")

  Sunday % Monday % Tuesday % Wednesday % Thursday % Friday % Saturday

}
