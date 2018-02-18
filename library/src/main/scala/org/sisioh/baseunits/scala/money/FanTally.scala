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

/**
  * [[org.sisioh.baseunits.scala.money.MoneyFan]]の集合。
  *
  * @author j5ik2o
  * @tparam T 割り当ての対象
  * @param fans [[MoneyFan]]の[[scala.Iterable]]
  */
class FanTally[T](private[money] val fans: Iterable[MoneyFan[T]]) extends Iterable[MoneyFan[T]] {

  import collection.Iterator

  /**
    * インスタンスを生成する。
    *
    * @param fan [[MoneyFan]]
    */
  def this(fan: MoneyFan[T]) = this(Iterable.fill(1)(fan))

  def iterator: Iterator[MoneyFan[T]] = fans.iterator

  /**
    * 要素の[[MoneyFan]]を全てマージしたものを返す。
    * @return [[MoneyFan]]
    */
  lazy val net: MoneyFan[T] =
    fans.foldLeft(new MoneyFan[T])(_ plus _)

  override def toString(): String = fans.toString()

  /**
    * 要素の[[MoneyFan]]が含む[[Allotment]]の合計額を返す。
    * @return 合計額
    */
  lazy val total: Money = net.total

}

/**
  * `FanTally`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object FanTally {

  /**
    * インスタンスを生成する。
    *
    * @param fans [[org.sisioh.baseunits.scala.money.MoneyFan]]の`Iterable`
    * @return [[org.sisioh.baseunits.scala.money.FanTally]]
    */
  def apply[T](fans: Iterable[MoneyFan[T]]): FanTally[T] =
    new FanTally[T](fans)

  /**
    * 抽出子メソッド。
    *
    * @param fanTally [[org.sisioh.baseunits.scala.money.FanTally]]
    * @return `Option[Iterable[MoneyFan[T]]]`
    */
  def unapply[T](fanTally: FanTally[T]): Option[Iterable[MoneyFan[T]]] =
    Some(fanTally.fans)

}
