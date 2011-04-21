/*
 * Copyright 2011 Tricreo Inc and the Others.
 * lastModified : 2011/04/21
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

/**[[MoneyFan]]の集合。
 *
 * @param <T> 割り当ての対象
 */
class FanTally[T]
(private[money] val fans: Iterable[MoneyFan[T]])
  extends Iterable[MoneyFan[T]] {

  def this(fan: MoneyFan[T]) = this (Iterable.fill(1)(fan))

  def iterator: Iterator[MoneyFan[T]] = fans.iterator

  /**要素の {@link MoneyFan}を全てマージしたものを返す。
   * @return {@link MoneyFan}
   */
  def net: MoneyFan[T] = {
    var sum = new MoneyFan[T]
    for (val fan <- fans) {
      sum = sum.plus(fan)
    }
    sum
  }

  override def toString = fans.toString

  /**要素の {@link MoneyFan}が含む {@link Allotment}の合計額を返す。
   * @return 合計額
   */
  def total: Money = net.total

}

object FanTally {

  def apply[T](fans: Iterable[MoneyFan[T]]) = new FanTally[T](fans)

  def unapply[T](fanTally: FanTally[T]) = Some(fanTally.fans)

}