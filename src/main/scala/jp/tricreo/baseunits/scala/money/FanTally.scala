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

/**[[jp.tricreo.baseunits.scala.money.MoneyFan]]の集合。
 * @tparam T 割り当ての対象
 * @param fans [[jp.tricreo.baseunits.scala.money.MapFan]]の[[scala.Iterable]]
 */
class FanTally[T]
(private[money] val fans: Iterable[MoneyFan[T]])
  extends Iterable[MoneyFan[T]] {

  import collection.Iterator

  def this(fan: MoneyFan[T]) = this (Iterable.fill(1)(fan))

  def iterator: Iterator[MoneyFan[T]] = fans.iterator

  /**要素の[[jp.tricreo.baseunits.scala.money.MoneyFan]]を全てマージしたものを返す。
   * @return [[jp.tricreo.baseunits.scala.money.MoneyFan]]
   */
  def net: MoneyFan[T] = {
    val sum = new MoneyFan[T]
    fans.foldLeft(sum)(_.plus(_))
//    for (fan <- fans) {
//      sum = sum.plus(fan)
//    }
//    sum
  }

  override def toString = fans.toString

  /**要素の[[jp.tricreo.baseunits.scala.money.MoneyFan]]が含む[[jp.tricreo.baseunits.scala.money.Allotment]]の合計額を返す。
   * @return 合計額
   */
  def total: Money = net.total

}

/**コンパニオンオブジェクト。
 */
object FanTally {

  /**
   *
   */
  def apply[T](fans: Iterable[MoneyFan[T]]) = new FanTally[T](fans)

  def unapply[T](fanTally: FanTally[T]) = Some(fanTally.fans)

}