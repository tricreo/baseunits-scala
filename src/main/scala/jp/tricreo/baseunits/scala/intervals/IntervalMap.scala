package jp.tricreo.baseunits.scala.intervals

import collection.generic.{CanBuildFrom, ImmutableMapFactory}
import collection.immutable.{MapLike, Map}
import collection.Iterator

/**区間に対して値をマッピングするトレイト。
 *
 * @tparam K キーとなる区間の型
 * @tparam V 値の型
 */
trait IntervalMap[A <: Ordered[A], +B] {

  protected val intervalMap: Map[Interval[A], B]

  /**指定した区間と共通部分を持つ区間に対するマッピングがマップに含まれている場合に {@code true} を返す。
   *
   * @param interval 区間
   * @return 指定した区間と共通部分を持つ区間に対するマッピングがマップに含まれている場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
   def containsIntersectingKey(interval: Interval[A]): Boolean

}

class LinearIntervalMap[A <: Ordered[A], +B]
  (protected val intervalMap: Map[Interval[A], B])
  extends IntervalMap[A, B]{

  def containsIntersectingKey(interval: Interval[A]): Boolean = {
    false
  }

  private def findKeyIntervalContaining(key:LimitValue[A]):Interval[A] =
    intervalMap.keys.find{
      case e if (e.includes(key)) => true
      case _ => false
    }.get

  /**
	 * この写像が保持するキーとしての区間のうち、指定した区間 {@code otherInterval}と共通部分を持つ
	 * 区間の列を取得する。
	 *
	 * <p>戻り値の列は、区間の自然順にソートされている。</p>
	 *
	 * @param otherInterval 対象区間
	 * @return 指定した区間と共通部分を持つ区間の列
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	private def intersectingKeys(otherInterval:Interval[A]):List[Interval[A]] =
    intervalMap.keys.map{
      case e if e.intersects(otherInterval) => e
    }.toList

}

object LinearIntervalMap{

  implicit def toMap[A <: Ordered[A], B](src:LinearIntervalMap[A,B]) = src.intervalMap

}


