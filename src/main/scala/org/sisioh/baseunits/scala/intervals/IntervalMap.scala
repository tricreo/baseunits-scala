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

import collection.immutable.{MapLike, Map}
import collection.Iterator

/**区間に対して値をマッピングする抽象クラス。
 *
 * @author j5ik2o
 * @tparam K キーとなる区間の型
 * @tparam V 値の型
 */
abstract class IntervalMap[A <% Ordered[A], +B]
  extends Map[Interval[A], B]
  with MapLike[Interval[A], B, IntervalMap[A, B]] {

  protected val intervalMap: Map[Interval[A], B]

  override def empty: IntervalMap[A, B] = new LinearIntervalMap

  /**Add a key/value pair to this map.
   * @param key the key
   * @param value the value
   * @return A new map with the new binding added to this map
   */
  override def updated[B1 >: B](key: Interval[A], value: B1): IntervalMap[A, B1] =
    new LinearIntervalMap(intervalMap.updated(key, value))

  def get(key: LimitValue[A]): Option[B]

  def +[B1 >: B](kv: (Interval[A], B1)): IntervalMap[A, B1]

  /**指定した区間と共通部分を持つ区間に対するマッピングがマップに含まれている場合に `true` を返す。
   * @param interval 区間
   * @return 指定した区間と共通部分を持つ区間に対するマッピングがマップに含まれている場合は`true`、そうでない場合は`false`
   */
  def containsIntersectingKey(interval: Interval[A]): Boolean

}

class LinearIntervalMap[A <% Ordered[A], B]
(protected val intervalMap: Map[Interval[A], B])
  extends IntervalMap[A, B] {

  /**インスタンスを生成する。
   *
   * `intervalMap`は空を利用する。
   */
  def this() = this (Map.empty[Interval[A], B])

  override def toString(): String = intervalMap.toString

  def containsIntersectingKey(otherInterval: Interval[A]): Boolean =
    intersectingKeys(otherInterval).isEmpty == false

  private def directPut(source: Map[Interval[A], B], intervalSequence: List[Interval[A]], value: B) = {
    val keyValues = collection.mutable.Map.empty[Interval[A], B]
    keyValues ++= source
    intervalSequence.foreach {
      e =>
        keyValues += (e -> value)
    }
    keyValues.toMap
  }

  def contains(key: LimitValue[A]) = findKeyIntervalContaining(key).isDefined

  private def findKeyIntervalContaining(key: LimitValue[A]): Option[Interval[A]] =
    intervalMap.keys.find(_.includes(key))

  /**この写像が保持するキーとしての区間のうち、指定した区間 `otherInterval`と共通部分を持つ
   * 区間の列を取得する。
   *
   * 戻り値の列は、区間の自然順にソートされている。
   *
   * @param otherInterval 対象区間
   * @return 指定した区間と共通部分を持つ区間の列
   */
  private def intersectingKeys(otherInterval: Interval[A]): List[Interval[A]] =
    intervalMap.keys.map {
      case e if e.intersects(otherInterval) => Some(e)
      case _ => None
    }.flatten.toList

  def iterator: Iterator[(Interval[A], B)] = intervalMap.iterator

  def +[B1 >: B](kv: (Interval[A], B1)): LinearIntervalMap[A, B1] = {
    val removed = this.-(kv._1)
    val result = removed.intervalMap.+(kv)
    new LinearIntervalMap(result)
  }

  def get(key: Interval[A]): Option[B] = intervalMap.get(key)

  def get(key: LimitValue[A]): Option[B] =
    findKeyIntervalContaining(key) match {
      case Some(key) => intervalMap.get(key)
      case None => None
    }

  def -(key: Interval[A]): LinearIntervalMap[A, B] = {
    val intervalSeq = intersectingKeys(key)
    var currentMap = intervalMap
    intervalSeq.foreach {
      oldInterval =>
        val oldValue = currentMap(oldInterval)
        currentMap -= oldInterval
        val complementIntervalSeq = key.complementRelativeTo(oldInterval)
        currentMap = directPut(currentMap, complementIntervalSeq, oldValue)
    }
    new LinearIntervalMap(currentMap)
  }

}

/**`LinearIntervalMap`コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object LinearIntervalMap {

  /**インスタンスを生成する。
   *
   * @tparam A キーの型
   * @tparam B 値の型
   * @return [[jp.tricreo.baseunits.scala.intervals.LinearIntervalMap]]
   */
  def apply[A <% Ordered[A], B]: LinearIntervalMap[A, B] = new LinearIntervalMap

  /**ファクトリメソッド。
   *
   * @tparam A キーの型
   * @tparam B 値の型
   * @return [[jp.tricreo.baseunits.scala.intervals.LinearIntervalMap]]
   */
  def apply[A <% Ordered[A], B](intervalMap: Map[Interval[A], B]): LinearIntervalMap[A, B] = new LinearIntervalMap(intervalMap)

  /**抽出子メソッド。
   *
   * @tparam A キーの型
   * @tparam B 値の型
   * @return [[jp.tricreo.baseunits.scala.intervals.LinearIntervalMap]]
   */
  def unapply[A <% Ordered[A], B](linearIntervalMap: LinearIntervalMap[A, B]) =
    Some(linearIntervalMap.intervalMap)

}


