package jp.tricreo.baseunits.scala.intervals

import scala.collection._
import generic.CanBuildFrom
import mutable.{ListBuffer, Builder}


class IntervalSeq[T <% Ordered[T]](intervals: Seq[Interval[T]]) extends Seq[Interval[T]] with SeqLike[Interval[T], IntervalSeq[T]] {

  private val this.intervals = intervals

  override protected def newBuilder: Builder[Interval[T], IntervalSeq[T]] = IntervalSeq.newBuilder[T]

  def this() = this (Seq.empty[Interval[T]])

  /**全ての要素区間を内包する、最小の区間を返す。
   *
   * @return 全ての要素区間を内包する、最小の区間
   * @throws IllegalStateException 要素が1つもない場合
   */
  def extent = {
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


  /**ソート済みの区間で、隣り合った区間同士に挟まれる区間を区間列として返す。
   *
   * <p>結果の区間列の {@link Comparator} は、この区間列の {@link Comparator} を流用する。</p>
   *
   * <p>区間数が2つ未満の場合は、空の区間列を返す。また、区間同士が重なっていたり接していた場合は、
   * その区間は結果の要素に含まない。全てが重なっている場合は、空の区間列を返す。</p>
   *
   * @return ギャップ区間列
   */
    def gaps = {
      if (intervals.size < 2) {
        IntervalSeq(Seq.empty[Interval[T]])
      } else {
        val seq = (1 until this.intervals.size).map {
          i =>
            val left = this.intervals(i - 1)
            val right = this.intervals(i)
            val gap = left.gap(right)
            if (gap.isEmpty) {
              None
            } else {
              Some(gap)
            }
        }.flatten
        IntervalSeq(seq)
      }
    }

  def iterator = this.intervals.iterator

  def length: Int = this.intervals.length

  def apply(idx: Int) = this.intervals(idx)

}


object IntervalSeq {


  type From[T] = Seq[Interval[T]]
  type Elem[T] = Interval[T]
  type To[T] = IntervalSeq[T]

  private def newTo[T <% Ordered[T]](s: From[T]): To[T] =
    new IntervalSeq[T](s)

  implicit def canBuildFrom[T <% Ordered[T]]: CanBuildFrom[From[T], Elem[T], To[T]] =
    new CanBuildFrom[From[T], Elem[T], To[T]] {

      def apply(from: From[T]) = newBuilder

      def apply() = newBuilder

    }

  def apply[T <% Ordered[T]](intervals: From[T]) = new IntervalSeq(intervals)
  def apply[T <% Ordered[T]]():To[T] = new IntervalSeq[T]()

  def newBuilder[T <% Ordered[T]]: Builder[Elem[T], To[T]] = new ListBuffer[Elem[T]] mapResult {
    x => newTo(x)
  }


}
