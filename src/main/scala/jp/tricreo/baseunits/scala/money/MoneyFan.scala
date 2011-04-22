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
package jp.tricreo.baseunits.scala.money

import collection.Iterator
import collection.mutable.ListBuffer

/**割り当ての集合。
 *
 * @author j5ik2o
 * @tparam T 割り当て対象
 * @param allotment 割り当ての要素（単一）
 */
class MoneyFan[T]
(private val allotments: Set[Allotment[T]])
  extends Iterable[Allotment[T]] {

  def this() = this (Set.empty[Allotment[T]])

  def this(allotment: Allotment[T]) = this (Set(allotment))

  def iterator: Iterator[Allotment[T]] = allotments.iterator

  /**`MoneyFan`が保持する `Allotment`のうち、割り当て対象が `anEntity`であるものを返す。
   *
   * @param anEntity 割り当て対象
   * @return `Allotment`。見つからなかった場合は`None`
   */
  def allotment(anEntity: T): Option[Allotment[T]] =
    allotments.find(_.entity == anEntity)

  override def hashCode: Int = allotments.hashCode

  override def equals(obj: Any): Boolean = obj match {
    case that: MoneyFan[T] => allotments == that.allotments
    case _ => false
  }


  /**この[[MoneyFan]]から`subtracted`を引いた差を返す。
   *
   * @param subtracted [[MoneyFan]]
   * @return [[MoneyFan]]
   */
  def minus(subtracted: MoneyFan[T]) = plus(subtracted.negated)

  def -(subtracted: MoneyFan[T]) = minus(subtracted)


  /**この [[MoneyFan]]の [[Allotment}]を `Allotment#negated`した [[Set]]で構成される
   * 新しい [[MoneyFan]]を返す。
   *
   * @return `MoneyFan`
   */
  def negated = {
    val negatedAllotments = allotments.map(_.negated).toSet
    new MoneyFan[T](negatedAllotments)
  }

  def unary_- = negated

  /**この[[MoneyFan}]に`added`を足した和を返す。
   *
   * <p>同じ割り当て対象に対する割当額は、マージする。また、割当額が0の [[Allotment]] は取り除く。</p>
   *
   * @param added [[MoneyFan]]
   * @return [[MoneyFan]]
   */
  def plus(added: MoneyFan[T]) = {
    val allEntities = allotments.map(_.entity) ++ added.allotments.map(_.entity)
    val summedAllotments = allEntities.map {
      entity =>
        allotment(entity) match {
          case None => added.allotment(entity).get
          case Some(thisAllotment) => {
            added.allotment(entity) match {
              case None => thisAllotment
              case Some(addedAllotment) => {
                val sum = thisAllotment.amount.plus(addedAllotment.amount)
                new Allotment[T](entity, sum)
              }
            }
          }
        }
    }
    new MoneyFan[T](summedAllotments).withoutZeros
  }

  def +(added: MoneyFan[T]) = plus(added)

  override def toString = allotments.toString

  /**全ての割り当ての合計額を返す。
   *
   * @return 合計額
   */
  def total = asTally.net

  private def asTally = {
    val moneies = ListBuffer.empty[Money]
    for (allotment <- allotments) {
      moneies += (allotment.amount)
    }
    new Tally(moneies)
  }

  /**このインスタンスが保持する [[Allotment]] のうち、割り当て金額が`0`であるものを取り除いた
   * 新しい [[MoneyFan]]を返す。
   *
   * @return [[MoneyFan]]
   */
  private def withoutZeros = {
    val nonZeroAllotments = allotments.filter(_.breachEncapsulationOfAmount.isZero == false).toSet
    new MoneyFan[T](nonZeroAllotments)
  }
}

object MoneyFan {

  def apply[T](allotments: Set[Allotment[T]]): MoneyFan[T] = new MoneyFan[T](allotments)

  def apply[T](allotment: Allotment[T]): MoneyFan[T] = new MoneyFan[T](allotment)

  def apply[T]: MoneyFan[T] = new MoneyFan[T]

  def unapply[T](moneyFan: MoneyFan[T]) = Some(moneyFan.allotments)

}
