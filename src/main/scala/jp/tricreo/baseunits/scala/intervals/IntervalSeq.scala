package jp.tricreo.baseunits.scala.intervals

import scala.collection._
import generic.CanBuildFrom
import mutable.{ListBuffer, Builder}


class UpperLowerOrdering[T <% Ordered[T]]
(inverseLower: Boolean, inverseUpper: Boolean)
  extends Ordering[Interval[T]] {

  private[this] val lowerFactor = if (inverseLower) -1 else 1
  private[this] val upperFactor = if (inverseUpper) -1 else 1

  def compare(e1: Interval[T], e2: Interval[T]): Int =
    if (e1.isEmpty && e2.isEmpty) {
      0
    } else if (e1.isEmpty) {
      -1
    } else if (e2.isEmpty) {
      1
    } else {
      val upperComparance = e1.upperLimitObject.compareTo(e2.upperLimitObject);
      val lowerComparance = e1.lowerLimitObject.compareTo(e2.lowerLimitObject);
      if (upperComparance != 0) (upperComparance * upperFactor) else (lowerComparance * lowerFactor)
    }
}

object UpperLowerOrdering {
  def apply[T <% Ordered[T]](inverseLower: Boolean, inverseUpper: Boolean) = new UpperLowerOrdering[T](inverseLower, inverseUpper)
}

class LowerUpperOrdering[T <% Ordered[T]]
(inverseLower: Boolean, inverseUpper: Boolean)
  extends Ordering[Interval[T]] {

  private[this] val lowerFactor = if (inverseLower) -1 else 1
  private[this] val upperFactor = if (inverseUpper) -1 else 1

  def compare(e1: Interval[T], e2: Interval[T]): Int =
    if (e1.isEmpty && e2.isEmpty) {
      0
    } else if (e1.isEmpty) {
      1
    } else if (e2.isEmpty) {
      -1
    } else {
      val upperComparance = e1.upperLimitObject.compareTo(e2.upperLimitObject);
      val lowerComparance = e1.lowerLimitObject.compareTo(e2.lowerLimitObject);
      if (lowerComparance != 0) (lowerComparance + lowerFactor) else (upperComparance * upperFactor)
    }
}

object LowerUpperOrdering {
  def apply[T <% Ordered[T]](inverseLower: Boolean, inverseUpper: Boolean) = new LowerUpperOrdering[T](inverseLower, inverseUpper)
}

/**区間列（複数の [[jp.tricreo.beseunits.scala.intervals.Interval 区間]] の列）を表すクラス。
 *
 * @param <T> [[jp.tricreo.beseunits.scala.intervals.Interval 区間]]の型
 */
class IntervalSeq[T <% Ordered[T]]
(val intervals: Seq[Interval[T]], val ordering: Ordering[Interval[T]])
  extends Seq[Interval[T]] with SeqLike[Interval[T], IntervalSeq[T]] {

  override protected def newBuilder: Builder[Interval[T], IntervalSeq[T]] = IntervalSeq.newBuilder[T](ordering)

  def this() = this (Seq.empty[Interval[T]], UpperLowerOrdering[T](true, false))

  def this(intervals: Seq[Interval[T]]) = this (intervals, UpperLowerOrdering[T](true, false))


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


  /**ソート済みの区間で、隣り合った区間同士が重なっている区間を区間列として返す。
   *
   * <p>結果の区間列の {@link Comparator} は、この区間列の {@link Comparator} を流用する。</p>
   *
   * <p>区間数が2つ未満の場合は、空の区間列を返す。また、区間同士が重ならなかったり接していた場合は、
   * その区間は結果の要素に含まない。全てが重ならない場合は、空の区間列を返す。</p>
   *
   * @return 共通区間列
   */
  def intersections = {
    val intersections = IntervalSeq
    if (intervals.size < 2) {
      IntervalSeq[T]()
    } else {
      val seq = (1 until this.intervals.size).map {
        i =>
          val left = this.intervals(i - 1)
          val right = this.intervals(i)
          val gap = left.intersect(right)
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

/**[[jp.tricreo.baseunits.scala.intervals.IntervalSeq]]のためのビルダー。
 * @j5ik2o
 */
class IntervalSeqBuilder[T <% Ordered[T]]
(val ord: Option[Ordering[Interval[T]]] = None)
  extends Builder[Interval[T], IntervalSeq[T]] {

  val builder = new ListBuffer[Interval[T]]

  def +=(elem: Interval[T]): this.type = {
    builder += elem
    this
  }

  def clear(): Unit = builder.clear

  def result(): IntervalSeq[T] =
    ord match {
      case Some(ord) => IntervalSeq(builder.sorted(ord).result)
      case None => IntervalSeq(builder.result)
    }

}

/**[[jp.tricreo.baseunits.scala.intervals.IntervalSeq]]のためのコンパニオンオブジェクト
 * @j5ik2o
 */
object IntervalSeq {

  type From[T] = Seq[Interval[T]]
  type Elem[T] = Interval[T]
  type To[T] = IntervalSeq[T]

//  private def newTo[T <% Ordered[T]](s: From[T]): To[T] =
//    new IntervalSeq[T](s)

  implicit def canBuildFrom[T <% Ordered[T]]: CanBuildFrom[From[T], Elem[T], To[T]] =
    new CanBuildFrom[From[T], Elem[T], To[T]] {

      def apply(from: From[T]) = {
        from match {
          case intervalSeq: IntervalSeq[T] => new IntervalSeqBuilder[T](Some(intervalSeq.ordering))
          case _ => throw new Error
        }
      }

      def apply() = new IntervalSeqBuilder[T]

    }

  def apply[T <% Ordered[T]](intervals: From[T]) = new IntervalSeq(intervals)

  def apply[T <% Ordered[T]](): To[T] = new IntervalSeq[T]()

  def newBuilder[T <% Ordered[T]](ordering: Ordering[Interval[T]]): Builder[Elem[T], To[T]] = new IntervalSeqBuilder[T](Some(ordering))

  //  def newBuilder[T <% Ordered[T]]: Builder[Elem[T], To[T]] = new ListBuffer[Elem[T]] mapResult {
  //    x => newTo(x)
  //  }


}
