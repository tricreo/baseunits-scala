package jp.tricreo.baseunits.scala.intervals

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/03/28
 * Time: 23:54
 * To change this template use File | Settings | File Templates.
 */

@serializable
class IntervalLimit[T <% Ordered[T]]
(
  val closed: Boolean,
  val lower: Boolean,
  val value: Option[T]
  )
  extends Ordered[IntervalLimit[T]] {

  private def lowerToInt = if (lower) -1 else 1

  def infinity = if (value == None) true else false
  def open = closed == false
  def upper = lower == false

  override def toString = "IntervalLimit(%s, %s, %s)".format(closed, value, lower)

  override def equals(obj: Any) = obj match {
    case that: IntervalLimit[T] => compareTo(that) == 0
    case _ => false
  }

  override def hashCode = closed.hashCode + value.hashCode + lower.hashCode

  def compare(obj: IntervalLimit[T]) = obj match {
  // 無限同士の比較
    case IntervalLimit(_, otherLower, None) if (value == None) => otherLower match {
      case l if (l == lower) => 0
      case _ => lowerToInt
    }
    // 有限と無限の比較（自分が無限の場合）
    case IntervalLimit(_, _, Some(_)) if (value == None) => lowerToInt
    // 有限と無限の比較（otherが無限の場合）
    case that@IntervalLimit(_, _, None) => that.lowerToInt
    // 同値の有限同士の比較
    case that if (value == that.value) => that.lower match {
      case true if (lower == true) => if (closed ^ that.closed) {
        if (closed) -1 else 1
      } else 0
      case false if (lower == false) => if (closed ^ that.closed) {
        if (closed) 1 else -1
      } else 0
      case _ => lowerToInt
    }
    case that@IntervalLimit(_, _, Some(_)) => value.get compare that.value.get
  }
}

object IntervalLimit {

  def apply[T <% Ordered[T]](closed: Boolean, lower: Boolean, value: Option[T]) =
    new IntervalLimit[T](closed, lower, value)

  def unapply[T <% Ordered[T]](intervalLimit: IntervalLimit[T]) =
    Some(intervalLimit.closed, intervalLimit.lower, intervalLimit.value)

  def lower[T <% Ordered[T]](closed:Boolean, value:Option[T]) = apply(closed, true, value)
  def upper[T <% Ordered[T]](closed:Boolean, value:Option[T]) = apply(closed, false, value)

}