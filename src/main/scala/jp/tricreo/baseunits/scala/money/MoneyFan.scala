package jp.tricreo.baseunits.scala.money

import collection.Iterator

/**
 * 割り当ての集合。
 *
 * @tparam T 割り当て対象
 * @param allotment 割り当ての要素（単一）
 */
class MoneyFan[T]
(private val allotments: Iterable[Allotment[T]])
  extends Iterable[Allotment[T]] {

  def this() = this (Iterable.empty[Allotment[T]])

  def this(allotment: Allotment[T]) = this (Set(allotment))

  def iterator: Iterator[Allotment[T]] = allotments.iterator

  /**{@link MoneyFan}が保持する {@link Allotment}のうち、割り当て対象が {@code anEntity}であるものを返す。
   *
   * @param anEntity 割り当て対象
   * @return {@link Allotment}。見つからなかった場合は{@code null}
   */
  def allotment(anEntity: T): Option[Allotment[T]] =
    allotments.find(_.entity == anEntity)

  override def hashCode: Int = allotments.hashCode

  override def equals(p1: Any): Boolean = p1 match {
    case that: MoneyFan[T] => allotments == that.allotments
    case _ => false
  }
}