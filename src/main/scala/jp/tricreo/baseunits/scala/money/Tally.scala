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