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

/**「区間」を表すクラス。
 *
 * 閉区間とは、`lower <= x <= upper`であらわされる区間であり、
 * 開区間とは、`lower < x < upper`であらわされる区間である。
 * どちらか一方のみが `<=` で、他方が `<` である場合は、半開区間と言う。
 *
 * The rules of this class are consistent with the common mathematical
 * definition of "interval". For a simple explanation, see
 * http://en.wikipedia.org/wiki/Interval_(mathematics)
 *
 * Interval (and its "ConcreteInterval" subclass) can be used for any objects
 * that have a natural ordering reflected by implementing the Comparable
 * interface. For example, Integer implements Comparable, so if you want to
 * check if an Integer is within a range, make an Interval. Any class of yours
 * which implements Comparable can have intervals defined this way.
 *
 * @author j5ik2o
 * @tparam T 区間要素の型
 * @param lower 下側限界
 * @param upper 上側限界
 */
class Interval[T <% Ordered[T]]
(private var lower: IntervalLimit[T],
 private var upper: IntervalLimit[T]) extends Serializable {

  import collection.mutable.ListBuffer

  checkLowerIsLessThanOrEqualUpper(lower, upper)

  // 単一要素区間であり、かつ、どちらか片方が開いている場合、両者を開く。
  // [5, 5) や (5, 5] を [5, 5] にする。(5, 5)は空区間だから除外。
  if (upper.infinity == false && lower.infinity == false &&
    upper.value == lower.value && (lower.isOpen ^ upper.isOpen)) {
    if (lower.isOpen) {
      lower = IntervalLimit.lower(true, lower.value)
    }
    if (upper.isOpen) {
      upper = IntervalLimit.upper(true, upper.value)
    }
  }
  val lowerLimitObject = lower
  val upperLimitObject = upper

  /**インスタンスを生成する。
   *
   * @param isLower 下側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @param isLowerClosed 下限値が閉区間である場合は `true`を指定する
   * @param isUpper 上側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @param isUpperClosed 上限値が閉区間である場合は `true`を指定する
   * @throws IllegalArgumentException 下限値が上限値より大きい場合
   */
  def this(lower: LimitValue[T], isLowerClosed: Boolean, upper: LimitValue[T], isUpperClosed: Boolean) = {
    this (IntervalLimit.lower(isLowerClosed, lower), IntervalLimit.upper(isUpperClosed, upper))
  }

  /**この区間の<b>補</b>区間と与えた区間 `other` の共通部分を返す。
   *
   * この区間と与えた区間に共通部分がない場合は、 `other` を要素とする要素数1の区間列を返す。
   * 与えた区間が、この区間を完全に内包する場合は、2つの区間に分かれるため、要素数が2の区間列を返す。
   * 逆にこの区間が、与えた区間を完全に内包する場合は、要素数0の区間列を返す。
   * 上記以外の場合、この区間の補区間と与えた区間の共通部分を要素とする要素数1の区間列を返す。
   *
   * @param other 対照となる区間
   * @return 補区間と対照区間の共通部分のリスト
   * @see <a href="http://en.wikipedia.org/wiki/Set_theoretic_complement">complement (wikipedia)</a>
   */
  def complementRelativeTo(other: Interval[T]): List[Interval[T]] = {
    val intervalSequence = ListBuffer.empty[Interval[T]]
    if (intersects(other) == false) {
      intervalSequence += other
      intervalSequence.result
    } else {
      leftComplementRelativeTo(other) match {
        case Some(left) => intervalSequence += left
        case _ => ()
      }
      rightComplementRelativeTo(other) match {
        case Some(right) => intervalSequence += right
        case _ => ()
      }
      intervalSequence.result
    }
  }


  /**この区間が、指定した区間 `other`を完全に内包するかどうかを検証する。
   *
   * @param other 区間
   * @return 完全に内包する場合は`true`、そうでない場合は`false`
   */
  def covers(other: Interval[T]) = {
    val lowerPass = includes(other.lowerLimit) ||
      (lowerLimit == other.lowerLimit && other.includesLowerLimit == false)
    val upperPass = includes(other.upperLimit) ||
      (upperLimit == other.upperLimit && other.includesUpperLimit == false)
    lowerPass && upperPass
  }

  /**この区間と同じ限界値を持つ、新しい開区間を生成する。
   *
   * @return 新しい開区間
   */
  def emptyOfSameType = newOfSameType(lowerLimit, false, lowerLimit, false)


  /**この区間と、与えた区間 `other`の同一性を検証する。
   *
   * 両者が共に空の区間であった場合は`true`、どちらか一方のみが空の区間であった場合は`false`を返す。
   * 両者とも単一要素区間であった場合は、単一要素となる限界値同士を比較し、一致した場合は`true`を返す。
   * また、どちらか一方のみが単一要素区間であった場合は`false`を返す。
   *
   * `other`が`Interval`以外であった場合は、必ず`false`を返す。
   *
   * @param other 比較対象の区間
   * @return 同一である場合は`true`、そうでない場合は`false`
   */
  override def equals(obj: Any) = obj match {
    case other: Interval[T] =>
      if (isEmpty & other.isEmpty) true
      else if (isEmpty ^ other.isEmpty) false
      else if (isSingleElement & other.isSingleElement) lowerLimit == other.lowerLimit
      else if (isSingleElement ^ other.isSingleElement) false
      else
        upperLimitObject == other.upperLimitObject &&
          lowerLimitObject == other.lowerLimitObject
    case _ => false
  }

  /**この区間と与えた区間 `other` の間にある区間を取得する。
   *
   * 例えば、[3, 5) と [10, 20) の gap は、[5, 19) である。
   * 2つの区間が共通部分を持つ場合は、空の区間を返す。
   *
   * @param other 比較対象の区間
   * @return ギャップ区間
   */
  def gap(other: Interval[T]) =
    if (intersects(other)) emptyOfSameType
    else
      newOfSameType(lesserOfUpperLimits(other), lesserOfUpperIncludedInUnion(other) == false,
        greaterOfLowerLimits(other), greaterOfLowerIncludedInUnion(other) == false)


  override def hashCode = lowerLimit.hashCode ^ upperLimit.hashCode

  /**下側限界があるかどうかを取得する。
   *
   * Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.
   *
   * 警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。
   *
   * @return 下側限界がある場合は`true`、そうでない場合は`false`
   */
  def hasLowerLimit = lowerLimit match {
    case _: Limit[T] => true
    case _: Limitless[T] => false
  }


  /**上側限界があるかどうかを取得する。
   *
   * Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.
   *
   * 警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。
   *
   * @return 上側限界がある場合は`true`、そうでない場合は`false`
   */
  def hasUpperLimit = upperLimit match {
    case _: Limit[T] => true
    case _: Limitless[T] => false
  }

  /**指定した値 `value` が、この区間に含まれるかどうかを検証する。
   *
   * @param value 値
   * @return 含まれる場合は`true`、そうでない場合は`false`
   */
  def includes(value: LimitValue[T]): Boolean = isBelow(value) == false && isAbove(value) == false

  /**下側限界が閉じているかどうかを取得する。
   *
   * Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.
   *
   * 警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。
   *
   * @return 下側限界が閉じている場合は`true`、そうでない場合は`false`
   */
  def includesLowerLimit = lowerLimitObject.isClosed

  /**上側限界が閉じているかどうかを取得する。
   *
   * Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.
   *
   * 警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。
   *
   * @return 上側限界値が閉じている場合は`true`、そうでない場合は`false`
   */
  def includesUpperLimit = upperLimitObject.isClosed

  /**この区間と与えた区間 `other` の積集合（共通部分）を返す。
   *
   * 共通部分が存在しない場合は、空の区間を返す。
   *
   * @param other 比較対象の区間
   * @return 積集合（共通部分）
   */
  def intersect(other: Interval[T]) = {
    val intersectLowerBound = greaterOfLowerLimits(other)
    val intersectUpperBound = lesserOfUpperLimits(other)
    if (intersectLowerBound > intersectUpperBound) emptyOfSameType
    else
      newOfSameType(intersectLowerBound, greaterOfLowerIncludedInIntersection(other), intersectUpperBound,
        lesserOfUpperIncludedInIntersection(other))
  }

  private def equalBothLimitless(me: LimitValue[T], your: LimitValue[T]) =
    (me, your) match {
      case (_: Limitless[T], _: Limitless[T]) => true
      case _ => false
    }

  /**この区間と、与えた区間`other`の間に共通部分が存在するかどうか検証する。
   *
   * @param other 対象区間
   * @return 共通部分が存在する場合は`true`、そうでない場合は`false`
   */
  def intersects(other: Interval[T]) =
    if (equalBothLimitless(upperLimit, other.upperLimit)) true
    else if (equalBothLimitless(lowerLimit, other.lowerLimit)) true
    else greaterOfLowerLimits(other) compare lesserOfUpperLimits(other) match {
      case comparison if (comparison < 0) => true
      case comparison if (comparison > 0) => false
      case _ => greaterOfLowerIncludedInIntersection(other) && lesserOfUpperIncludedInIntersection(other)
    }


  /**指定した値 `value` が、この区間の下側限界を超過していないかどうかを検証する。
   *
   * @param value 値
   * @return 超過していない場合は`true`、そうでない場合は`false`
   */
  def isAbove(value: LimitValue[T]) =
    if (hasLowerLimit == false) false
    else lowerLimit > value || (lowerLimit == value && includesLowerLimit == false)


  /**指定した値 `value` が、この区間の上側限界を超過していないかどうかを検証する。
   *
   * @param value 値
   * @return 超過していない場合は`true`、そうでない場合は`false`
   */
  def isBelow(value: LimitValue[T]): Boolean =
    if (hasUpperLimit == false) false
    else upperLimit < value || (upperLimit == value && includesUpperLimit == false)


  /**この区間が閉区間であるかどうかを検証する。
   *
   * @return 閉区間である場合は`true`、そうでない場合（半開区間を含む）は`false`
   */
  def isClosed = includesLowerLimit && includesUpperLimit

  /**この区間が空であるかどうかを検証する。
   *
   * 区間が空であるとは、上側限界値と下側限界値が同値であり、かつ、開区間であることを示す。
   * 例えば `3 < x < 3`のような状態である。
   *
   * @return 空である場合は`true`、そうでない場合は`false`
   */
  def isEmpty: Boolean = (upperLimit, lowerLimit) match {
    // TODO: Consider explicit isEmpty interval
    // A 'degenerate' interval is an isEmpty set, {}.
    case (_: Limitless[T], _) | (_, _: Limitless[T]) => false
    case _ => isOpen && upperLimit == lowerLimit
  }

  /**この区間が開区間であるかどうかを検証する。
   *
   * @return 開区間である場合は`true`、そうでない場合（半開区間を含む）は`false`
   */
  def isOpen = includesLowerLimit == false && includesUpperLimit == false

  /**この区間が単一要素区間であるかどうかを検証する。
   * 単一要素区間は、上側下側の両限界を持ち、さらにそれらの限界値が同値であり、かつ、開区間ではないことを示す。
   * 例えば `3 <= x < 3`, `3 < x <= 3`, `3 <= x <= 3`のような状態である。
   *
   * @return 単一要素区間である場合は`true`、そうでない場合は`false`
   */
  def isSingleElement =
    if (hasUpperLimit == false) false
    else if (hasLowerLimit == false) false
    //An interval containing a single element, {a}.
    else upperLimit == lowerLimit && isEmpty == false

  /**下側限界値を取得する。
   *
   * Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.
   *
   * 警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。
   *
   * @return 下側限界値. 限界がない場合は、[[jp.tricreo.baseunits.scala.intervals.Limitless]]
   */
  def lowerLimit = lowerLimitObject.value

  /**この区間と同じ型`T`を持つ、新しい区間を生成する。
   *
   * @param isLower 下側限界値. 限界値がない場合は、[[jp.tricreo.baseunits.scala.intervals.Limitless]]
   * @param lowerClosed 下限値を区間に含む（閉じた下側限界）場合は`true`を指定する
   * @param isUpper 上側限界値. 限界値がない場合は、[[jp.tricreo.baseunits.scala.intervals.Limitless]]
   * @param upperClosed 上限値を区間に含む（閉じた上側限界）場合は`true`を指定する
   * @return 新しい区間
   */
  def newOfSameType(lower: LimitValue[T], lowerClosed: Boolean, upper: LimitValue[T], upperClosed: Boolean) =
    new Interval(lower, lowerClosed, upper, upperClosed)

  /**区間の文字列表現を取得する。
   *
   * 空の区間である場合は "&#123;&#125;", 単一要素区間である場合は "&#123;x&#125;"を返す。
   * また、例えば 3〜5 の開区間である場合は "(3, 5)"、閉区間である場合は "[3, 5]"、
   * 半開区間であれば "[3, 5)" または "(3, 5]" の様に、開いた限界を "()"、
   * 閉じた限界を "[]" で表現する。
   *
   * @see java.lang.Object#toString()
   */
  override def toString =
    if (isEmpty) "{}"
    else if (isSingleElement) "{" + lowerLimit.toString + "}"
    else toStringDetail

  /**上側限界値を取得する。
   *
   * Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.
   *
   * 警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。
   *
   * @return 上側限界値. 限界がない場合は、[[jp.tricreo.baseunits.scala.intervals.Limitless]]
   */
  def upperLimit = upperLimitObject.value

  /**この区間と与えた区間 `other` の下側限界値のうち、より大きい（限界の狭い、制約の大きい）限界値を返す。
   *
   * @param other 比較対象の限界値
   * @return より大きい限界値
   */
  private[intervals] def greaterOfLowerLimits(other: Interval[T]): LimitValue[T] =
    if (lowerLimit == Limitless[T]) other.lowerLimit
    else if (other.lowerLimit == Limitless[T]) lowerLimit
    else if (lowerLimit >= other.lowerLimit) lowerLimit
    else other.lowerLimit


  /**この区間と与えた区間 `other` の上側限界値のうち、より小さい（限界の狭い、制約の大きい）限界値を返す。
   *
   * @param other 比較対象の限界値
   * @return より小さい限界値
   */
  private[intervals] def lesserOfUpperLimits(other: Interval[T]) =
    if (upperLimit == Limitless[T]) other.upperLimit
    else if (other.upperLimit == Limitless[T]) upperLimit
    else if (upperLimit <= other.upperLimit) upperLimit
    else other.upperLimit


  private[intervals] def toStringDetail = {
    val buffer = new StringBuilder
    buffer.append(if (includesLowerLimit) "[" else "(")
    buffer.append(if (hasLowerLimit) lowerLimit.toString else "Infinity")
    buffer.append(", ")
    buffer.append(if (hasUpperLimit) upperLimit.toString else "Infinity")
    buffer.append(if (includesUpperLimit) "]" else ")")
    buffer.toString
  }


  /**区間をグラフィカルに確認するためのデバッグ用メソッド。
   *
   * 単一要素区間はキャラクタ`@`で表示する。
   * 下側限界がない場合はキャラクタ`<`で表示し、上側限界がない場合はキャラクタ`>`で表示する。
   * 下側限界が開区間である場合はキャラクタ`(` 、閉区間である場合はキャラクタ{ @code [}で表示する。
   * 上側限界が開区間である場合はキャラクタ{ @code )}、閉区間である場合はキャラクタ`]`で表示する。
   * 区間内の要素はキャラクタ`-`で表示する。
   *
   * @return 文字列
   */
  //  private[intervals] def toStringGraphically = {
  //    val sb = new StringBuilder
  //    sb.append(" ")
  //    if (isEmpty) {
  //      sb.append("EMPTY")
  //    } else if (isSingleElement) {
  //      for (i <- 0 until lowerLimit.asInstanceOf[Limit[T]].value.asInstanceOf[Int]) {
  //        sb.append(" ")
  //      }
  //      sb.append("@")
  //    } else {
  //      if (lowerLimit == Limitless[T]) {
  //        sb.deleteCharAt(0)
  //        sb.append("<")
  //      } else {
  //        for (i <- 0 until lowerLimit.asInstanceOf[Limit[T]].value.asInstanceOf[Int]) {
  //          sb.append(" ")
  //        }
  //        sb.append(if (includesLowerLimit) "[" else "(")
  //      }
  //      if (upperLimit == Limitless[T]) {
  //        sb.append(">")
  //      } else {
  //        val l = if (lowerLimit == Limitless[T]) -1 else lowerLimit.asInstanceOf[Limit[T]].value.asInstanceOf[Int]
  //        val u = if (upperLimit == Limitless[T]) 100 else upperLimit.asInstanceOf[Limit[T]].value.asInstanceOf[Int]
  //        for (i <- 0 until u - l - 1) {
  //          sb.append("-")
  //        }
  //        sb.append(if (includesUpperLimit) "]" else ")")
  //      }
  //    }
  //    sb.toString
  //  }

  private def checkLowerIsLessThanOrEqualUpper(lower: IntervalLimit[T], upper: IntervalLimit[T]) {
    if ((lower.isLower && upper.isUpper && lower.compareTo(upper) <= 0) == false) {
      throw new IllegalArgumentException(lower + " is not before or equal to " + upper)
    }
  }

  private def greaterOfLowerIncludedInIntersection(other: Interval[T]) = {
    val limit = greaterOfLowerLimits(other)
    includes(limit) && other.includes(limit)
  }

  private def greaterOfLowerIncludedInUnion(other: Interval[T]) = {
    val limit = greaterOfLowerLimits(other)
    includes(limit) || other.includes(limit)
  }

  /**この区間と与えた区間 `other` の上側限界値のうち、より大きい（限界の広い、制約の小さい）限界値を返す。
   *
   * @param other 比較対象の限界値
   * @return より小さい限界値. この区間に上側限界がない場合は [[jp.tricreo.baseunits.scala.intervals.Limitless]]
   */
  private def greaterOfUpperLimits(other: Interval[T]): Option[LimitValue[T]] =
    if (upperLimit == Limitless[T]) {
      None
    } else {
      val upperComparison = upperLimit.compareTo(other.upperLimit)
      if (upperComparison >= 0) {
        Some(upperLimit)
      } else
        Some(other.upperLimit)
    }

  /**この区間の下側<b>補</b>区間と与えた区間 `other` の共通部分を返す。
   *
   * @param other 比較対象の区間
   * @return この区間の下側の補区間と、与えた区間の共通部分。存在しない場合は `None`
   */
  private def leftComplementRelativeTo(other: Interval[T]): Option[Interval[T]] =
  // この区間の下側限界値の方が小さいか等しい場合、下側の補区間に共通部分は無い
    if (lowerLimitObject.compareTo(other.lowerLimitObject) <= 0) {
      None
    } else
      Some(newOfSameType(other.lowerLimit, other.includesLowerLimit, lowerLimit,
        includesLowerLimit == false))

  /**この区間と与えた区間 `other` の下側限界値のうち、より小さい（限界の広い、制約の小さい）限界値を返す。
   *
   * @param other 比較対象の限界値
   * @return より小さい限界値. この区間に下側限界がない場合は [[jp.tricreo.baseunits.scala.intervals.Limitless]]
   */
  private def lesserOfLowerLimits(other: Interval[T]) =
    lowerLimit match {
      case _: Limitless[T] => None
      case _ => {
        val lowerComparison = lowerLimit.compareTo(other.lowerLimit)
        lowerComparison match {
          case lc if (lowerComparison <= 0) => Some(lowerLimit)
          case _ => Some(other.lowerLimit)
        }
      }
    }

  private def lesserOfUpperIncludedInIntersection(other: Interval[T]) = {
    val limit = lesserOfUpperLimits(other)
    includes(limit) && other.includes(limit)
  }

  private def lesserOfUpperIncludedInUnion(other: Interval[T]) = {
    val limit = lesserOfUpperLimits(other)
    includes(limit) || other.includes(limit)
  }

  /**この区間の上側<b>補</b>区間と与えた区間 `other` の共通部分を返す。
   *
   * @param other 比較対象の区間
   * @return この区間の上側の補区間と、与えた区間の共通部分。存在しない場合は `None`
   */
  private def rightComplementRelativeTo(other: Interval[T]): Option[Interval[T]] =
  // この区間の上側限界値の方が大きいか等しい場合、上側の補区間に共通部分は無い
    if (upperLimitObject.compareTo(other.upperLimitObject) >= 0) None
    else
      Some(newOfSameType(upperLimit, includesUpperLimit == false, other.upperLimit,
        other.includesUpperLimit))

}

/**`Interval`コンパニオンオブジェクト
 *
 * @author j5ik2o
 */
object Interval {

  /**インスタンスを生成する。
   *
   * @tparam T 区間要素の型
   * @param lower 下側限界
   * @param upper 上側限界
   * @return [[jp.tricreo.baseunits.scala.intervals.Interval]]
   */
  def apply[T <% Ordered[T]](lower: IntervalLimit[T], upper: IntervalLimit[T]) = new Interval(lower, upper)

  /**抽出子メソッド。
   *
   * @tparam T 限界値の型
   * @return 分解されたフィールドを含むTupleのOption型
   */
  def unapply[T <% Ordered[T]](interval: Interval[T]) = Some(interval.lowerLimitObject, interval.upperLimitObject)

  /**下側限界のみを持つ区間を生成する。
   * 下側限界値は区間に含む（閉じている）区間である。
   *
   * @tparam T 限界値の型
   * @param isLower 下側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @return 区間
   */
  def andMore[T <% Ordered[T]](lower: LimitValue[T]) = closed(lower, Limitless[T])

  /**閉区間を生成する。
   *
   * @tparam T 限界値の型
   * @param isLower 下側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @param isUpper 上側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @return 閉区間
   * @throws IllegalArgumentException 下限値が上限値より大きい場合
   */
  def closed[T <% Ordered[T]](lower: LimitValue[T], upper: LimitValue[T]) =
    new Interval(lower, true, upper, true)

  /**下側限界のみを持つ区間を生成する。
   *
   * 下側限界値は区間に含まない（開いている）区間である。
   *
   * @tparam T 限界値の型
   * @param isLower 下側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @return 区間
   */
  def moreThan[T <% Ordered[T]](lower: LimitValue[T]) = open(lower, Limitless[T])

  //  def isEmpty[T <% Ordered[T]](someValue: LimitValue[T]) =
  //    new Interval[T](someValue, false, someValue, false)

  /**開区間を生成する。
   *
   * @tparam T 限界値の型
   * @param isLower 下側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @param isUpper 上側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @return 開区間
   * @throws IllegalArgumentException 下限値が上限値より大きい場合
   */
  def open[T <% Ordered[T]](lower: LimitValue[T], upper: LimitValue[T]) = new Interval(lower, false, upper, false)

  /**区間を生成する。
   * 主に、半開区間（上限下限のどちらか一方だけが開いている区間）の生成に用いる。
   *
   * @tparam T 限界値の型
   * @param isLower 下側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @param lowerIncluded 下限値を区間に含む（閉じた下側限界）場合は`true`を指定する
   * @param isUpper 上側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @param upperIncluded 上限値を区間に含む（閉じた上側限界）場合は`true`を指定する
   * @return 区間
   * @throws IllegalArgumentException 下限値が上限値より大きい場合
   */
  def over[T <% Ordered[T]](lower: LimitValue[T], lowerIncluded: Boolean, upper: LimitValue[T],
                            upperIncluded: Boolean) = new Interval(lower, lowerIncluded, upper, upperIncluded)

  /**単一要素区間を生成する。
   *
   * @tparam T 限界値の型
   * @param element 単一要素となる値
   * @return 区間
   */
  def singleElement[T <% Ordered[T]](element: LimitValue[T]) = closed(element, element)

  /**上側限界のみを持つ区間を生成する。
   *
   * 上側限界値は区間に含まない（開いている）区間である。
   *
   * @tparam T 限界値の型
   * @param isUpper 上側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @return 区間
   */
  def under[T <% Ordered[T]](upper: LimitValue[T]) = open(Limitless[T], upper)

  /**上側限界のみを持つ区間を生成する。
   *
   * 上側限界値は区間に含む（閉じている）区間である。
   *
   * @tparam T 限界値の型
   * @param isUpper 上側限界値. [[jp.tricreo.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す
   * @return 区間
   */
  def upTo[T <% Ordered[T]](upper: LimitValue[T]) = closed(Limitless[T], upper)

}