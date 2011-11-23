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
 *
 * @author j5ik2o
 * @tparam T 割り当ての対象
 * @param fans [[jp.tricreo.baseunits.scala.money.MapFan]]の[[scala.Iterable]]
 */
class FanTally[T]
(private[money] val fans: Iterable[MoneyFan[T]])
  extends Iterable[MoneyFan[T]] {

  import collection.Iterator

  /**インスタンスを生成する。
   *
   * @param fans [[jp.tricreo.baseunits.scala.money.MapFan]]
   */
  def this(fan: MoneyFan[T]) = this (Iterable.fill(1)(fan))

  def iterator: Iterator[MoneyFan[T]] = fans.iterator

  /**要素の[[jp.tricreo.baseunits.scala.money.MoneyFan]]を全てマージしたものを返す。
   * @return [[jp.tricreo.baseunits.scala.money.MoneyFan]]
   */
  def net: MoneyFan[T] =
    fans.foldLeft(new MoneyFan[T])(_ plus _)

  override def toString = fans.toString

  /**要素の[[jp.tricreo.baseunits.scala.money.MoneyFan]]が含む[[jp.tricreo.baseunits.scala.money.Allotment]]の合計額を返す。
   * @return 合計額
   */
  def total: Money = net.total

}

/**`FanTally`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object FanTally {

  /**インスタンスを生成する。
   *
   * @param fans [[jp.tricreo.baseunits.scala.money.MoneyFan]]の`Iterable`
   * @return [[jp.tricreo.baseunits.scala.money.FanTally]]
   */
  def apply[T](fans: Iterable[MoneyFan[T]]) = new FanTally[T](fans)

  /**抽出子メソッド。
   *
   * @param fanTally [[jp.tricreo.baseunits.scala.money.FanTally]]
   * @return `Option[Iterable[MoneyFan[T]]]`
   */
  def unapply[T](fanTally: FanTally[T]) = Some(fanTally.fans)

}