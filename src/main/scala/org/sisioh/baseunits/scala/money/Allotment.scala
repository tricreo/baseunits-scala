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
package org.sisioh.baseunits.scala.money

/**何かに対するお金の割り当てをあらわす。
 *
 * @tparam T 割り当て対象
 * @param entity 割り当て対象
 * @param amount 金額
 */
class Allotment[T]
(private[money] val entity: T,
 private[money] val amount: Money) {

  /**このオブジェクトの`amount`フィールド（金額）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 金額
   */
  def breachEncapsulationOfAmount = amount

  /**このオブジェクトの`entity`フィールド（割り当て対象）を返す。
   *
   * CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。
   *
   * @return 割り当て対象
   */
  def breachEncapsulationOfEntity = entity

  override def equals(obj: Any) = obj match {
    case that: Allotment[T] => entity == that.entity && amount == that.amount
    case _ => false
  }

  override def hashCode = entity.## + amount.##

  /**割り当て量の正負を反転させた新しい割り当てを返す。
   *
   * @return 割り当て
   */
  def negated =
    new Allotment[T](entity, amount.negated)
  def unary_- = negated

  override def toString =
    "" + entity + " --> " + amount

}

/**`Allotment`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object Allotment {

  /**インスタンスを生成する。
   *
   * @param entity 割り当て対象
   * @param amount [[org.sisioh.baseunits.scala.money.Money]]
   * @return [[org.sisioh.baseunits.scala.money.Allotment]]
   */
  def apply[T](entity: T, amount: Money) = new Allotment[T](entity, amount)

  /**抽出子メソッド。
   *
   * @param allotment [[org.sisioh.baseunits.scala.money.Allotment]]
   * @return `Option[(T, Money)]`
   */
  def unapplly[T](allotment: Allotment[T]) = Some(allotment.entity, allotment.amount)

}
