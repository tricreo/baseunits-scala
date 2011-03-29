package jp.tricreo.baseunits.scala.intervals

/**
 * 区間における「限界」を表すクラス。
 *
 * <p>このクラスを理解するにあたっては、「限界」と「限界値」の区別を正しく認識することが重要となる。
 * 限界とはこのクラス {@code this} で表される値であり、限界値とは、 {@link #value}で表される値である。</p>
 *
 * <p>限界が「閉じている」とは、限界値そのものを超過とみなさないことを表し、
 * 「開いている」とは、これを超過とみなすことを表す。</p>
 *
 * <p>無限限界とは、限界を制限しないことであり、 {@link #value} が {@code null} であることで
 * この状態を表現する。無限限界は常に開いていると考える。
 * 逆に、無限限界ではない限界（{@link #value} が {@code null} ではないもの）を有限限界と呼ぶ。</p>
 *
 * <p>下側限界とは、限界値以下（または未満）の値を超過とみなす限界を表し、
 * 上側限界とは、限界値以上（または超える）の値を超過とみなす限界を表す。</p>
 *
 * @tparam T 限界の型
 * @param closed 限界が閉じている場合 {@code true}
 * @param lower 下側限界を表す場合は {@code true}、上側限界を表す場合は {@code false}
 * @param value 限界値 {@code null}の場合は、限界がないことを表す。
 */

trait IntervalLimitValue[T]{
  val value:T
}

case class LimitValue[T <% Ordered[T]](val value:T) extends IntervalLimitValue[T]

case class NothingOrdered extends Ordered[NothingOrdered]{
  def compare(that: NothingOrdered) = 0
}

case object LimitlessValue extends IntervalLimitValue[NothingOrdered]{
  val value = NothingOrdered()
}

@serializable
class IntervalLimit[T <% Ordered[T]]
(
  val closed: Boolean,
  val lower: Boolean,
  val value: IntervalLimitValue[T]
  )
  extends Ordered[IntervalLimit[T]] {

  private def lowerToInt = if (lower) -1 else 1

  private def closedToInt(t: Int, f: Int) = if (closed) t else f

  /**
   * この限界が無限限界であるかどうかを検証する。
   *
   * @return 無限限界である場合は{@code true}、そうでない場合は{@code false}
   */
  def infinity = value == null

  /**
   * この限界が開いているかどうかを検証する。
   *
   * @return 開いている場合は{@code true}、そうでない場合は{@code false}
   */
  def open = closed == false

  /**
   * この限界が上側限界であるかどうかを検証する。
   *
   * @return 上限値の場合は{@code true}、そうでない場合は{@code false}
   */
  def upper = lower == false

  override def toString = "IntervalLimit(%s, %s, %s)".format(closed, lower, value)

  override def equals(obj: Any) = obj match {
    case that: IntervalLimit[T] => compareTo(that) == 0
    case _ => false
  }

  override def hashCode = closed.hashCode + value.hashCode + lower.hashCode


  /**
   * 限界同士の比較を行う。
   *
   * <p>無限限界に関して。
   * 下側の無限限界は他のいかなる限界よりも「小さい」と判断し、
   * 上側の無限限界は他のいかなる限界よりも「大きい」と判断する。
   * 同じ側の限界同士の比較では「同一」と判断する。</p>
   *
   * <p>有限限界同士の比較に関して。
   * この場合は、それぞれの限界の開閉や上下にかかわらず、限界値が小さい方を「小さい」と判断する。
   * 限界値が同一である場合、下側限界同士の比較である場合は閉じている方を「小さい」と判断し、
   * 上側限界同士の比較である場合は閉じている方を「大きい」と判断する。
   * 限界値が同一で、上側限界と下側限界の比較の場合は、開閉にかかわらず下側を「小さい」と判断する。</p>
   *
   * @param other 比較対象
   * @return 同値であった場合は {@code 0}、このオブジェクトが比較対象よりも小さい場合は負数、大きい場合は正数
   * @throws NullPointerException 引数に{@code null}を与えた場合
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  def compare(obj: IntervalLimit[T]) = obj match {
  // 無限同士の比較
    case IntervalLimit(_, otherLower, otherValue) if (otherValue == LimitlessValue && value == LimitlessValue) => otherLower match {
      case l if (l == lower) => 0
      case _ => lowerToInt
    }
    // 有限と無限の比較（自分が無限の場合）
    case IntervalLimit(_, _, LimitValue(_)) if (value == LimitlessValue) => lowerToInt
    // 有限と無限の比較（otherが無限の場合）
    case that@IntervalLimit(_, _, otherValue ) if ( otherValue == LimitlessValue ) => that.lowerToInt
    // 同値の有限同士の比較
    case that if (value == that.value) => that.lower match {
      case true if (lower == true) => if (closed ^ that.closed) closedToInt(-1, 1) else 0
      case false if (lower == false) => if (closed ^ that.closed) closedToInt(1, -1) else 0
      case _ => lowerToInt
    }
    case that@IntervalLimit(_, _, LimitValue(_)) => value.value compare that.value.value
  }
}

/**
 * 区間における「限界」を表すコンパニオンオブジェクト。
 *
 */
object IntervalLimit {

  /**
	 * インスタンスを生成する。
	 *
	 * <p>無限限界（{@code value}ば{@code null}だった場合は、{@code closed}の指定にかかわらず
	 * 常に閉じた限界のインスタンスを生成する。</p>
	 *
	 * @param closed 閉じた限界を生成する場合は {@code true}を指定する
	 * @param lower 下側限界を生成する場合は {@code true}、上側限界を生成する場合は {@code false}を指定する
	 * @param value 限界値. {@code null}の場合は、限界がないことを表す
	 */
  def apply[T <% Ordered[T]](closed: Boolean, lower: Boolean, value: IntervalLimitValue[T]) =
    new IntervalLimit[T](if (value == null) false else closed, lower, value)

  /**
   * 抽出子メソッド。
   *
   * @param intervalLimit [[IntervalLimit]]
   * @return Option[(Boolean, Boolean, T)]
   */
  def unapply[T <% Ordered[T]](intervalLimit: IntervalLimit[T]):Option[(Boolean, Boolean, IntervalLimitValue[T])] =
    Some(intervalLimit.closed, intervalLimit.lower, intervalLimit.value)

  /**
   * 下側限界インスタンスを生成する。
   *
   * @param <T> 限界値の型
   * @param closed 閉じた限界を生成する場合は {@code true}を指定する
   * @param value 限界値. {@code null}の場合は、限界がないことを表す
   * @return 下側限界インスタンス
   */
  def lower[T <% Ordered[T]](closed: Boolean, value: IntervalLimitValue[T]) = apply(closed, true, value)

  /**
   * 上側限界インスタンスを生成する。
   *
   * @param <T> 限界値の型
   * @param closed 閉じた限界を生成する場合は {@code true}を指定する
   * @param value 限界値. {@code null}の場合は、限界がないことを表す
   * @return 上側限界インスタンス
   */
  def upper[T <% Ordered[T]](closed: Boolean, value: IntervalLimitValue[T]) = apply(closed, false, value)

}
