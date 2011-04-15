package jp.tricreo.baseunits.scala.intervals

import collection.immutable.{MapLike, Map}
import collection.Iterator

/**区間に対して値をマッピングするトレイト。
 *
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

  def +[B1 >: B](kv: (Interval[A], B1)): IntervalMap[A, B1]

  /**指定した区間と共通部分を持つ区間に対するマッピングがマップに含まれている場合に {@code true} を返す。
   *
   * @param interval 区間
   * @return 指定した区間と共通部分を持つ区間に対するマッピングがマップに含まれている場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def containsIntersectingKey(interval: Interval[A]): Boolean

}

class LinearIntervalMap[A <% Ordered[A], B]
(protected val intervalMap: Map[Interval[A], B])
  extends IntervalMap[A, B] {

  def this() = this (Map.empty[Interval[A], B])

  //  override def empty: IntervalMap[A, B] = new LinearIntervalMap[A, B]()

  override def toString(): String = intervalMap.toString

  def containsIntersectingKey(otherInterval: Interval[A]): Boolean =
    intersectingKeys(otherInterval).isEmpty == false

  private def directPut(source:Map[Interval[A], B],intervalSequence: List[Interval[A]], value: B) = {
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

  /**この写像が保持するキーとしての区間のうち、指定した区間 {@code otherInterval}と共通部分を持つ
   * 区間の列を取得する。
   *
   * <p>戻り値の列は、区間の自然順にソートされている。</p>
   *
   * @param otherInterval 対象区間
   * @return 指定した区間と共通部分を持つ区間の列
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  private def intersectingKeys(otherInterval: Interval[A]): List[Interval[A]] =
    intervalMap.keys.map {
      case e if e.intersects(otherInterval) => Some(e)
      case _ => None
    }.flatten.toList

  def iterator: Iterator[(Interval[A], B)] = intervalMap.iterator

  //def +[B1 >: B](kv: (Interval[A], B1)): Map[Interval[A], B1] = intervalMap.+(kv)
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

  //  override def updated[B1 >: B](key: Interval[A], value: B1): IntervalMap[A, B1] =
  //    new LinearIntervalMap(intervalMap.updated(key, value))

}

object LinearIntervalMap {

  //implicit def toMap[A <: Ordered[A], B](src: LinearIntervalMap[A, B]) = src.intervalMap

  def apply[A <% Ordered[A], B] = new LinearIntervalMap[A, B]

}


