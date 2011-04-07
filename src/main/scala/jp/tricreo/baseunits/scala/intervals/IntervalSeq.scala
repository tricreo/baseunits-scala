package jp.tricreo.baseunits.scala.intervals

import collection.Iterator

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/04
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */

class IntervalSeq[T <% Ordered[T]](private val intervals: List[Interval[T]]) extends Seq[Interval[T]] {
  /**
   * 全ての要素区間を内包する、最小の区間を返す。
   *
   * @return 全ての要素区間を内包する、最小の区間
   * @throws IllegalStateException 要素が1つもない場合
   */
  def extent: Interval[T] = {
    require(intervals.isEmpty == false)
    intervals match {
      case List(e) => e
      case firstInterval :: _ => {
        val lower = intervals.map(_.lowerLimitObject).min
        val upper = intervals.map(_.upperLimitObject).max
        firstInterval.newOfSameType(lower.value, lower.isClosed, upper.value, upper.isClosed)
      }
    }
  }

  def iterator: Iterator[Interval[T]] = intervals.iterator

  def length: Int = intervals.length

  def apply(idx: Int): Interval[T] = intervals(idx)
}

object IntervalSeq {
  def apply[T <% Ordered[T]](intervals: List[Interval[T]]) = new IntervalSeq[T](intervals)
}
