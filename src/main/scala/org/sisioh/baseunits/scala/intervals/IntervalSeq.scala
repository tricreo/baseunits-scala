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
package jp.tricreo.baseunits.scala.intervals

import scala.collection._
import mutable.{ListBuffer, Builder}

/**区間同士の比較を行うための`Ordering`の実装(上側優先)
 *
 * 上側限界による比較を優先し、同じであったら下側限界による比較を採用する。
 *
 * @author j5ik2o
 * @param inverseLower 下限が逆順の場合は`true`
 * @param inverseUpper 上限が逆順の場合は`false`
 */
class UpperLowerOrdering[T <% Ordered[T]]
(private val inverseLower: Boolean, private val inverseUpper: Boolean)
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
      val upperComparance = e1.upperLimitObject.compareTo(e2.upperLimitObject)
      val lowerComparance = e1.lowerLimitObject.compareTo(e2.lowerLimitObject)
      if (upperComparance != 0) (upperComparance * upperFactor)
      else (lowerComparance * lowerFactor)
    }
}

/**`UpperLowerOrdering`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object UpperLowerOrdering {

  /**インスタンスを生成する。
   *
   * @param inverseLower
   * @param inverseUpper
   * @return [[jp.tricreo.baseunits.scala.intervals.UpperLowerOrdering]]
   */
  def apply[T <% Ordered[T]](inverseLower: Boolean, inverseUpper: Boolean) =
    new UpperLowerOrdering[T](inverseLower, inverseUpper)

  /**抽出子メソッド。
   *
   * @param upperLowerOrdering [[jp.tricreo.baseunits.scala.intervals.UpperLowerOrdering]]
   * @return `Option[(Boolean, Boolean)]`
   */
  def unapply[T <% Ordered[T]](upperLowerOrdering: UpperLowerOrdering[T]) =
    Some(upperLowerOrdering.inverseLower, upperLowerOrdering.inverseUpper)

}

/**区間同士の比較を行うための`Ordering`の実装(下側優先)
 *
 * 下側限界による比較を優先し、同じであったら上側限界による比較を採用する。
 *
 * @author j5ik2o
 * @param inverseLower 下限が逆順の場合は`true`
 * @param inverseUpper 上限が逆順の場合は`false`
 */
class LowerUpperOrdering[T <% Ordered[T]]
(private val inverseLower: Boolean, private val inverseUpper: Boolean)
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
      val upperComparance = e1.upperLimitObject.compareTo(e2.upperLimitObject)
      val lowerComparance = e1.lowerLimitObject.compareTo(e2.lowerLimitObject)
      if (lowerComparance != 0) (lowerComparance + lowerFactor)
      else (upperComparance * upperFactor)
    }
}

/**`LowerUpperOrdering`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object LowerUpperOrdering {

  /**インスタンスを生成する。
   *
   * @param inverseLower
   * @param inverseUpper
   * @return [[jp.tricreo.baseunits.scala.intervals.LowerUpperOrdering]]
   */
  def apply[T <% Ordered[T]](inverseLower: Boolean, inverseUpper: Boolean) =
    new LowerUpperOrdering[T](inverseLower, inverseUpper)

  /**抽出子メソッド。
   *
   * @param upperLowerOrdering [[jp.tricreo.baseunits.scala.intervals.LowerUpperOrdering]]
   * @return `Option[(Boolean, Boolean)]`
   */

  def unapply[T <% Ordered[T]](lowerUpperOrdering: LowerUpperOrdering[T]) =
    Some(lowerUpperOrdering.inverseLower, lowerUpperOrdering.inverseUpper)
}

/**区間列（複数の [[jp.tricreo.beseunits.scala.intervals.Interval]] の列）を表すクラス。
 *
 * @author j5ik2o
 * @tparam T [[jp.tricreo.beseunits.scala.intervals.Interval]]の型
 * @param intervals [[jp.tricreo.beseunits.scala.intervals.Interval]]の列
 * @param ordering [[jp.tricreo.beseunits.scala.intervals.Ordering]]
 */
class IntervalSeq[T <% Ordered[T]]
(val intervals: Seq[Interval[T]], val ordering: Ordering[Interval[T]])
  extends Seq[Interval[T]] with SeqLike[Interval[T], IntervalSeq[T]] {

  import mutable.Builder

  override protected def newBuilder: Builder[Interval[T], IntervalSeq[T]] =
    IntervalSeq.newBuilder[T](ordering)

  /**インスタンスを生成する。
   *
   * `intervals`は空を利用し、`ordering`は`UpperLowerOrdering[T](true, false)`を利用する。
   */
  def this() = this (Seq.empty[Interval[T]], UpperLowerOrdering[T](true, false))

  /**インスタンスを生成する。
   *
   * `ordering`は`UpperLowerOrdering[T](true, false)`を利用する。
   *
   * @param intervals [[jp.tricreo.beseunits.scala.intervals.Interval]]の列
   */
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
   * 結果の区間列の [[java.util.Comparator]] は、この区間列の [[java.util.Comparator]] を流用する。
   *
   * 区間数が2つ未満の場合は、空の区間列を返す。また、区間同士が重なっていたり接していた場合は、
   * その区間は結果の要素に含まない。全てが重なっている場合は、空の区間列を返す。
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
   * 結果の区間列の [[java.util.Comparator]] は、この区間列の [[java.util.Comparator]] を流用する。
   *
   * 区間数が2つ未満の場合は、空の区間列を返す。また、区間同士が重ならなかったり接していた場合は、
   * その区間は結果の要素に含まない。全てが重ならない場合は、空の区間列を返す。
   *
   * @return 共通区間列
   */
  def intersections = {
    if (intervals.size < 2) {
      IntervalSeq[T]()
    } else {
      val seq = (1 until this.intervals.size).flatMap {
        i =>
          val left = this.intervals(i - 1)
          val right = this.intervals(i)
          val gap = left.intersect(right)
          if (gap.isEmpty) None
          else Some(gap)
      }
      IntervalSeq(seq)
    }
  }


  def iterator = this.intervals.iterator

  def length: Int = this.intervals.length

  def apply(idx: Int) = this.intervals(idx)

}

/**[[jp.tricreo.baseunits.scala.intervals.IntervalSeq]]のためのビルダー。
 *
 * @author j5ik2o
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

  def result: IntervalSeq[T] =
    ord match {
      case Some(ord) => IntervalSeq(builder.sorted(ord).result)
      case None => IntervalSeq(builder.result)
    }

}

/**`IntervalSeq`コンパニオンオブジェクト
 *
 * @author j5ik2o
 */
object IntervalSeq {

  import generic.CanBuildFrom

  type From[T] = Seq[Interval[T]]
  type Elem[T] = Interval[T]
  type To[T] = IntervalSeq[T]

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

  /**インスタンスを生成する。
   *
   * @tparam T 限界値の型
   * @param intervals
   * @return [[jp.tricreo.baseunits.scala.intervals.IntervalSeq]]
   */
  def apply[T <% Ordered[T]](intervals: From[T]) = new IntervalSeq(intervals)

  /**インスタンスを生成する。
   *
   * @tparam T 限界値の型
   * @param intervals
   * @return [[jp.tricreo.baseunits.scala.intervals.IntervalSeq]]
   */
  def apply[T <% Ordered[T]](): To[T] = new IntervalSeq[T]()

  /**抽出子メソッド。
   *
   * @tparam T 限界値の型
   * @param intervals
   * @return Some(intervals: Seq[Interval[T]], ordering: Ordering[Interval[T]])
   */
  def unapply[T <% Ordered[T]](intervalSeq: IntervalSeq[T]) =
    Some(intervalSeq.intervals, intervalSeq.ordering)

  /**ビルダーを生成するメソッド。
   *
   * @tparam T 限界値の型
   * @return ビルダー
   */
  def newBuilder[T <% Ordered[T]](ordering: Ordering[Interval[T]]): Builder[Elem[T], To[T]] = new IntervalSeqBuilder[T](Some(ordering))

}
