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

/**同じ通貨単位の金額の集合をあらわすクラス。
 */
class Tally
(private[money] val monies: Iterable[Money])
  extends Iterable[Money] {

  def this(monies: Money*) = this (monies.toIterable)

  private[this] val _currency = currency

  monies.find(_.breachEncapsulationOfCurrency == _currency) match {
    case Some(_) => throw new IllegalArgumentException
    case None => ()
  }

  /**通貨単位を返す。
	 * @return 通貨単位
	 */
  def currency =
    iterator.next.breachEncapsulationOfCurrency

	/** 合計金額を返す。
	 * @return 合計
	 */
	def net = Money.sum(monies)

	override def toString = monies.toString

  def iterator: Iterator[Money] = monies.iterator
}