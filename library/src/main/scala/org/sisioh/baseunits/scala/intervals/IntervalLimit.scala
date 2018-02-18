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
package org.sisioh.baseunits.scala.intervals

/**
  * 限界値を表すトレイト。
  *
  * @author j5ik2o
  * @tparam T 限界値の型
  */
trait LimitValue[T] extends Ordered[LimitValue[T]] {

  /**
    * 限界値を返す。
    *
    * @return 限界値
    * @throws NoSuchElementException 無限の場合
    */
  def toValue: T = toValueOrElse(throw new NoSuchElementException)

  /**
    * 限界値を返す。
    *
    * @param default 無限の場合の式
    * @return 限界値。無限の場合は`default`を返す。
    */
  def toValueOrElse(default: => T): T = this match {
    case Limit(value)    => value
    case _: Limitless[T] => default
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: LimitValue[T] => (this compare that) == 0
    case that =>
      this match {
        case Limit(value)     => value == that
        case me: Limitless[T] => false
      }
  }

}

/**
  * `LimitValue`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object LimitValue {

  /**
    * `LimitValue`を[[org.sisioh.baseunits.scala.intervals.Limit]]の限界値に変換する。
    *
    * @param limitValue [[org.sisioh.baseunits.scala.intervals.LimitValue]]
    * @return [[org.sisioh.baseunits.scala.intervals.Limit]]
    * @throws IllegalArgumentException limitValueがLimitless[T]の場合
    */
  implicit def toValue[T](limitValue: LimitValue[T])(implicit ev: T => Ordered[T]): T =
    limitValue match {
      case Limit(value) => value
      case _: Limitless[T] =>
        throw new IllegalArgumentException("implicit conversion from Limitless[T] can't do.")
    }

  /**
    * 値を[[org.sisioh.baseunits.scala.intervals.LimitValue]]に変換する。
    *
    * @param value 値
    * @return [[org.sisioh.baseunits.scala.intervals.Limit]]
    */
  def toLimitValue[T](value: Option[T])(implicit ev: T => Ordered[T]): LimitValue[T] =
    value match {
      case None    => Limitless[T]()
      case Some(v) => Limit(v)
    }

  implicit def toLimit[T](value: T)(implicit ev: T => Ordered[T]): Limit[T] =
    Limit(value)

}

/**
  * 有限の限界値を表すクラス。
  *
  * @author j5ik2o
  * @tparam T 限界値の型
  * @param value 限界値
  */
case class Limit[T](value: T)(implicit ev: T => Ordered[T]) extends LimitValue[T] {

  def compare(that: LimitValue[T]): Int = that match {
    case that: Limit[T] => value compare that.value
    case _              => 1
  }

}

/**
  * 無限の限界値を表すクラス。
  *
  * @author j5ik2o
  * @tparam T 限界値の型
  */
case class Limitless[T]()(implicit ev: T => Ordered[T]) extends LimitValue[T] {

  def compare(that: LimitValue[T]): Int = that match {
    case that: Limitless[T] => 0
    case _                  => -1
  }

}

/**
  * 区間における「限界」を表すクラス。
  *
  * このクラスを理解するにあたっては、「限界」と「限界値」の区別を正しく認識することが重要となる。
  * 限界とはこのクラス `this` で表される値であり、限界値とは、 `value`で表される値である。
  *
  * 限界が「閉じている」とは、限界値そのものを超過とみなさないことを表し、
  * 「開いている」とは、これを超過とみなすことを表す。
  *
  * 無限限界とは、限界を制限しないことであり、 `value` が `Limitless[T]` であることで
  * この状態を表現する。無限限界は常に開いていると考える。
  * 逆に、無限限界ではない限界（`value` が `Limitless[T]` ではないもの）を有限限界と呼ぶ。
  *
  * 下側限界とは、限界値以下（または未満）の値を超過とみなす限界を表し、
  * 上側限界とは、限界値以上（または超える）の値を超過とみなす限界を表す。
  *
  * @author j5ik2o
  * @tparam T 限界の型
  * @param closed 限界が閉じている場合 `true`
  * @param lower 下側限界を表す場合は `true`、上側限界を表す場合は `false`
  * @param value 限界値 [[org.sisioh.baseunits.scala.intervals.Limitless]]の場合は、限界がないことを表す。
  */
class IntervalLimit[T](
    val closed: Boolean,
    val lower: Boolean,
    val value: LimitValue[T]
)(implicit ev: T => Ordered[T])
    extends Ordered[IntervalLimit[T]]
    with Serializable {

  private def lowerToInt(t: Int, f: Int) = if (lower) t else f

  private def closedToInt(t: Int, f: Int) = if (closed) t else f

  /**
    * この限界が無限限界であるかどうかを検証する。
    *
    * @return 無限限界である場合は`true`、そうでない場合は`false`
    */
  def infinity: Boolean = value match {
    case _: Limitless[_] => true
    case _               => false
  }

  /**
    * この限界が開いているかどうかを検証する。
    * @return 開いている場合は`true`、そうでない場合は`false`
    */
  def isOpen: Boolean = !closed

  /**
    * この限界が上側限界であるかどうかを検証する。
    * @return 上限値の場合は`true`、そうでない場合は`false`
    */
  def isUpper: Boolean = !lower

  override def toString: String =
    "IntervalLimit(%s, %s, %s)".format(closed, lower, value)

  override def equals(obj: Any): Boolean = obj match {
    case that: IntervalLimit[T] => compareTo(that) == 0
    case _                      => false
  }

  override def hashCode: Int =
    31 * (closed.hashCode + value.hashCode + lower.hashCode)

  /**
    * 限界同士の比較を行う。
    *
    * 無限限界に関して。
    * 下側の無限限界は他のいかなる限界よりも「小さい」と判断し、
    * 上側の無限限界は他のいかなる限界よりも「大きい」と判断する。
    * 同じ側の限界同士の比較では「同一」と判断する。
    *
    * 有限限界同士の比較に関して。
    * この場合は、それぞれの限界の開閉や上下にかかわらず、限界値が小さい方を「小さい」と判断する。
    * 限界値が同一である場合、下側限界同士の比較である場合は閉じている方を「小さい」と判断し、
    * 上側限界同士の比較である場合は閉じている方を「大きい」と判断する。
    * 限界値が同一で、上側限界と下側限界の比較の場合は、開閉にかかわらず下側を「小さい」と判断する。
    *
    * @param obj 比較対象
    * @return 同値であった場合は `0`、このオブジェクトが比較対象よりも小さい場合は負数、大きい場合は正数
    */
  override def compare(obj: IntervalLimit[T]): Int = {
    if (value.isInstanceOf[Limitless[T]] && obj.value
          .isInstanceOf[Limitless[T]]) {
      if (lower == obj.lower) {
        return 0
      }
      return lowerToInt(-1, 1)
    }
    if (value.isInstanceOf[Limitless[T]]) {
      return lowerToInt(-1, 1)
    }
    if (obj.value.isInstanceOf[Limitless[T]]) {
      return obj.lowerToInt(1, -1)
    }
    if (value == obj.value) {
      if (lower && obj.lower) {
        if (closed ^ obj.closed) {
          return closedToInt(-1, 1)
        }
        return 0
      }
      if (!lower && !obj.lower) {
        if (closed ^ obj.closed) {
          return closedToInt(1, -1)
        }
        return 0
      }
      return lowerToInt(-1, 1)
    }
    value compare obj.value
  }
}

/**
  * `IntervalLimit`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object IntervalLimit {

  /**
    * インスタンスを生成する。
    *
    * 無限限界（`value`ば`Limitless[T]`だった場合は、`isClosed`の指定にかかわらず
    * 常に閉じた限界のインスタンスを生成する。
    *
    * @tparam T 限界値の型
    * @param closed 閉じた限界を生成する場合は `true`を指定する
    * @param lower 下側限界を生成する場合は `true`、上側限界を生成する場合は `false`を指定する
    * @param value 限界値. `Limitless[T]`の場合は、限界がないことを表す
    */
  def apply[T](closed: Boolean, lower: Boolean, value: LimitValue[T])(
      implicit ev: T => Ordered[T]): IntervalLimit[T] =
    new IntervalLimit[T](if (value.isInstanceOf[Limitless[_]]) false else closed, lower, value)

  /**
    * 抽出子メソッド。
    *
    * @tparam T 限界値の型
    * @param intervalLimit [[org.sisioh.baseunits.scala.intervals.IntervalLimit]]
    * @return Option[(Boolean, Boolean, T)]
    */
  def unapply[T](intervalLimit: IntervalLimit[T])(
      implicit ev: T => Ordered[T]): Option[(Boolean, Boolean, LimitValue[T])] =
    Some(intervalLimit.closed, intervalLimit.lower, intervalLimit.value)

  /**
    * 下側限界インスタンスを生成する。
    *
    * @tparam T 限界値の型
    * @param closed 閉じた限界を生成する場合は `true`を指定する
    * @param value 限界値. `Limitless[T]`の場合は、限界がないことを表す
    * @return 下側限界インスタンス
    */
  def lower[T](closed: Boolean, value: LimitValue[T])(
      implicit ev: T => Ordered[T]): IntervalLimit[T] =
    apply(closed, true, value)

  /**
    * 上側限界インスタンスを生成する。
    *
    * @tparam T 限界値の型
    * @param closed 閉じた限界を生成する場合は `true`を指定する
    * @param value 限界値. `Limitless[T]`の場合は、限界がないことを表す
    * @return 上側限界インスタンス
    */
  def upper[T](closed: Boolean, value: LimitValue[T])(
      implicit ev: T => Ordered[T]): IntervalLimit[T] =
    apply(closed, false, value)

}
