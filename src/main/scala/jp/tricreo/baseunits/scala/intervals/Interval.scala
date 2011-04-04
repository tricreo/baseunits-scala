package jp.tricreo.baseunits.scala.intervals

import collection.mutable.ListBuffer

@serializable
class Interval[T <% Ordered[T]]
(private var lower: IntervalLimit[T],
 private var upper: IntervalLimit[T]) {

  checkLowerIsLessThanOrEqualUpper(lower, upper)

  // 単一要素区間であり、かつ、どちらか片方が開いている場合、両者を開く。
  // [5, 5) や (5, 5] を [5, 5] にする。(5, 5)は空区間だから除外。
  if (upper.infinity == false && lower.infinity == false
    && upper.value == lower.value
    && (lower.open ^ upper.open)) {
    if (lower.open) {
      lower = IntervalLimit.lower(true, lower.value)
    }
    if (upper.open) {
      upper = IntervalLimit.upper(true, upper.value)
    }
  }
  val lowerLimitObject = lower
  val upperLimitObject = upper

  /**
   * インスタンスを生成する。
   *
   * @param lower 下側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @param isLowerClosed 下限値が閉区間である場合は {@code true}を指定する
   * @param upper 上側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @param isUpperClosed 上限値が閉区間である場合は {@code true}を指定する
   * @throws IllegalArgumentException 下限値が上限値より大きい場合
   */
  protected def this(lower: LimitValue[T], isLowerClosed: Boolean, upper: LimitValue[T], isUpperClosed: Boolean) = {
    this (IntervalLimit.lower(isLowerClosed, lower), IntervalLimit.upper(isUpperClosed, upper))
  }

  /**
   * この区間の<b>補</b>区間と与えた区間 {@code other} の共通部分を返す。
   *
   * <p>この区間と与えた区間に共通部分がない場合は、 {@code other} を要素とする要素数1の区間列を返す。
   * 与えた区間が、この区間を完全に内包する場合は、2つの区間に分かれるため、要素数が2の区間列を返す。
   * 逆にこの区間が、与えた区間を完全に内包する場合は、要素数0の区間列を返す。
   * 上記以外の場合、この区間の補区間と与えた区間の共通部分を要素とする要素数1の区間列を返す。</p>
   *
   * @param other 対照となる区間
   * @return 補区間と対照区間の共通部分のリスト
   * @see <a href="http://en.wikipedia.org/wiki/Set_theoretic_complement">complement (wikipedia)</a>
   */
  def complementRelativeTo(other: Interval[T]) = {
    val intervalSequence = ListBuffer.empty[Interval[T]]
    if (intersects(other) == false) {
      intervalSequence += other
      intervalSequence.result
    }
    val left = leftComplementRelativeTo(other)
    left match {
      case Some(left) => intervalSequence += left
      case _ => ()
    }
    val right = rightComplementRelativeTo(other)
    right match {
      case Some(right) => intervalSequence += right
      case _ => ()
    }
    intervalSequence.result
  }


  /**
   * この区間が、指定した区間 {@code other}を完全に内包するかどうかを検証する。
   *
   * @param other 区間
   * @return 完全に内包する場合は{@code true}、そうでない場合は{@code false}
   */
  def covers(other: Interval[T]) = {
    val lowerComparison = lowerLimit.compareTo(other.lowerLimit)
    val lowerPass = includes(other.lowerLimit) || (lowerComparison == 0 && other.includesLowerLimit == false)

    val upperComparison = upperLimit.compareTo(other.upperLimit)
    val upperPass = includes(other.upperLimit) || (upperComparison == 0 && other.includesUpperLimit == false)

    lowerPass && upperPass
  }

  /**
   * この区間と同じ限界値を持つ、新しい開区間を生成する。
   *
   * @return 新しい開区間
   */
  def emptyOfSameType: Interval[T] = newOfSameType(lowerLimit, false, lowerLimit, false)


  /**
   * この区間と、与えた区間 {@code other}の同一性を検証する。
   *
   * <p>両者が共に空の区間であった場合は{@code true}、どちらか一方のみが空の区間であった場合は{@code false}を返す。
   * 両者とも単一要素区間であった場合は、単一要素となる限界値同士を比較し、一致した場合は{@code true}を返す。
   * また、どちらか一方のみが単一要素区間であった場合は{@code false}を返す。</p>
   *
   * <p>{@code other}が{@code Interval}以外であった場合は、必ず{@code false}を返す。</p>
   *
   * @param other 比較対象の区間
   * @return 同一である場合は{@code true}、そうでない場合は{@code false}
   */
  override def equals(obj: Any): Boolean = obj match {
    case other: Interval[T] => {
      val thisEmpty = empty
      val otherEmpty = other.empty
      if (thisEmpty & otherEmpty) {
        return true
      }
      if (thisEmpty ^ otherEmpty) {
        return false
      }
      val thisSingle = singleElement
      val otherSingle = other.singleElement
      if (thisSingle & otherSingle) {
        lowerLimit.equals(other.lowerLimit)
      }
      if (thisSingle ^ otherSingle) {
        return false
      }
      upperLimitObject.compareTo(other.upperLimitObject) == 0 &&
        lowerLimitObject.compareTo(other.lowerLimitObject) == 0
    }
    case _ => false
  }

  /**
   * この区間と与えた区間 {@code other} の間にある区間を取得する。
   *
   * <p>例えば、[3, 5) と [10, 20) の gap は、[5, 19) である。
   * 2つの区間が共通部分を持つ場合は、空の区間を返す。</p>
   *
   * @param other 比較対象の区間
   * @return ギャップ区間
   */
  def gap(other: Interval[T]) = {
    if (intersects(other)) {
      emptyOfSameType
    }
    newOfSameType(lesserOfUpperLimits(other), lesserOfUpperIncludedInUnion(other) == false,
      greaterOfLowerLimits(other), greaterOfLowerIncludedInUnion(other) == false)
  }

  override def hashCode = lowerLimit.hashCode ^ upperLimit.hashCode

  /**
   * 下側限界があるかどうかを取得する。
   *
   * <p>Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.</p>
   *
   * <p>警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。</p>
   *
   * @return 下側限界がある場合は{@code true}、そうでない場合は{@code false}
   */
  def hasLowerLimit = lowerLimit match {
    case _: Limit[T] => true
    case _: Limitless[T] => false
  }


  /**
   * 上側限界があるかどうかを取得する。
   *
   * <p>Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.</p>
   *
   * <p>警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。</p>
   *
   * @return 上側限界がある場合は{@code true}、そうでない場合は{@code false}
   */
  def hasUpperLimit = upperLimit match {
    case _: Limit[T] => true
    case _: Limitless[T] => false
  }


  /**
   * 指定した値 {@code value} が、この区間に含まれるかどうかを検証する。
   *
   * @param value 値
   * @return 含まれる場合は{@code true}、そうでない場合は{@code false}
   */
  def includes(value: LimitValue[T]): Boolean = below(value) == false && above(value) == false

  /**
   * 下側限界が閉じているかどうかを取得する。
   *
   * <p>Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.</p>
   *
   * <p>警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。</p>
   *
   * @return 下側限界が閉じている場合は{@code true}、そうでない場合は{@code false}
   */
  def includesLowerLimit = lowerLimitObject.closed

  /**
   * 上側限界が閉じているかどうかを取得する。
   *
   * <p>Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.</p>
   *
   * <p>警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。</p>
   *
   * @return 上側限界値が閉じている場合は{@code true}、そうでない場合は{@code false}
   */
  def includesUpperLimit = upperLimitObject.closed

  /**
   * この区間と与えた区間 {@code other} の積集合（共通部分）を返す。
   *
   * <p>共通部分が存在しない場合は、空の区間を返す。</p>
   *
   * @param other 比較対象の区間
   * @return 積集合（共通部分）
   */
  def intersect(other: Interval[T]): Interval[T] = {
    val intersectLowerBound = greaterOfLowerLimits(other)
    val intersectUpperBound = lesserOfUpperLimits(other)
    if (intersectLowerBound.compareTo(intersectUpperBound) > 0) {
      return emptyOfSameType
    }
    newOfSameType(intersectLowerBound, greaterOfLowerIncludedInIntersection(other), intersectUpperBound,
      lesserOfUpperIncludedInIntersection(other))
  }

  /**
   * この区間と、与えた区間{@code other}の間に共通部分が存在するかどうか検証する。
   *
   * @param other 対象区間
   * @return 共通部分が存在する場合は{@code true}、そうでない場合は{@code false}
   */
  def intersects(other: Interval[T]): Boolean = {
    if (upperLimit == Limitless[T] && other.upperLimit == Limitless[T]) {
      return true
    }
    if (lowerLimit == Limitless[T] && other.lowerLimit == Limitless[T]) {
      return true
    }
    val comparison = greaterOfLowerLimits(other).compareTo(lesserOfUpperLimits(other))
    if (comparison < 0) {
      return true
    }
    if (comparison > 0) {
      return false
    }
    greaterOfLowerIncludedInIntersection(other) && lesserOfUpperIncludedInIntersection(other)
  }

  /**
   * 指定した値 {@code value} が、この区間の下側限界を超過していないかどうかを検証する。
   *
   * @param value 値
   * @return 超過していない場合は{@code true}、そうでない場合は{@code false}
   */
  def above(value: LimitValue[T]): Boolean =
    if (hasLowerLimit == false) {
      return false
    } else {
      val comparison = lowerLimit.compareTo(value)
      val result = comparison > 0 || (comparison == 0 && includesLowerLimit == false)
      result
    }

  /**
   * 指定した値 {@code value} が、この区間の上側限界を超過していないかどうかを検証する。
   *
   * @param value 値
   * @return 超過していない場合は{@code true}、そうでない場合は{@code false}
   */
  def below(value: LimitValue[T]): Boolean =
    if (hasUpperLimit == false) {
      false
    } else {
      val comparison = upperLimit.compareTo(value)
      val result = comparison < 0 || (comparison == 0 && includesUpperLimit == false)
      result
    }

  /**
   * この区間が閉区間であるかどうかを検証する。
   *
   * @return 閉区間である場合は{@code true}、そうでない場合（半開区間を含む）は{@code false}
   */
  def closed = includesLowerLimit && includesUpperLimit

  /**
   * この区間が空であるかどうかを検証する。
   *
   * <p>区間が空であるとは、上側限界値と下側限界値が同値であり、かつ、開区間であることを示す。
   * 例えば {@code 3 < x < 3}のような状態である。</p>
   *
   * @return 空である場合は{@code true}、そうでない場合は{@code false}
   */
  def empty: Boolean = {
    // TODO: Consider explicit empty interval
    // A 'degenerate' interval is an empty set, {}.
    if (upperLimit == Limitless[T] || lowerLimit == Limitless[T]) {
      return false
    }
    open && upperLimit.equals(lowerLimit)
  }


  /**
   * この区間が開区間であるかどうかを検証する。
   *
   * @return 開区間である場合は{@code true}、そうでない場合（半開区間を含む）は{@code false}
   */
  def open = includesLowerLimit == false && includesUpperLimit == false

  /**
   * この区間が単一要素区間であるかどうかを検証する。
   *
   * <p>単一要素区間は、上側下側の両限界を持ち、さらにそれらの限界値が同値であり、かつ、開区間ではないことを示す。
   * 例えば {@code 3 <= x < 3}, {@code 3 < x <= 3}, {@code 3 <= x <= 3}のような状態である。</p>
   *
   * @return 単一要素区間である場合は{@code true}、そうでない場合は{@code false}
   */
  def singleElement: Boolean = {
    if (hasUpperLimit == false) return false
    if (hasLowerLimit == false) return false
    //An interval containing a single element, {a}.
    upperLimit == lowerLimit && empty == false
  }

  /**
   * 下側限界値を取得する。
   *
   * <p>Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.</p>
   *
   * <p>警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。</p>
   *
   * @return 下側限界値. 限界がない場合は、{@code Limitless[T]}
   */
  def lowerLimit = lowerLimitObject.value

  /**
   * この区間と同じ型{@code T}を持つ、新しい区間を生成する。
   *
   * @param lower 下側限界値. 限界値がない場合は、{@code Limitless[T]}
   * @param lowerClosed 下限値を区間に含む（閉じた下側限界）場合は{@code true}を指定する
   * @param upper 上側限界値. 限界値がない場合は、{@code Limitless[T]}
   * @param upperClosed 上限値を区間に含む（閉じた上側限界）場合は{@code true}を指定する
   * @return 新しい区間
   */
  def newOfSameType(lower: LimitValue[T], lowerClosed: Boolean, upper: LimitValue[T], upperClosed: Boolean) = {
    new Interval[T](lower, lowerClosed, upper, upperClosed)
  }

  /**
   * 区間の文字列表現を取得する。
   *
   * <p>空の区間である場合は "&#123;&#125;", 単一要素区間である場合は "&#123;x&#125;"を返す。
   * また、例えば 3〜5 の開区間である場合は "(3, 5)"、閉区間である場合は "[3, 5]"、
   * 半開区間であれば "[3, 5)" または "(3, 5]" の様に、開いた限界を "()"、
   * 閉じた限界を "[]" で表現する。</p>
   *
   * @see java.lang.Object#toString()
   */
  override def toString: String = {
    if (empty) return "{}"
    if (singleElement) return "{" + lowerLimit.toString + "}"
    toStringDetail
  }

  /**
   * 上側限界値を取得する。
   *
   * <p>Warning: This method should generally be used for display
   * purposes and interactions with closely coupled classes.
   * Look for (or add) other methods to do computations.</p>
   *
   * <p>警告：このメソッドは一般的に、この値の表示目的および、このクラスと結合度の高いクラスとの
   * インタラクションに使用する。不用意なこのメソッドの使用は、このクラスとクライアント側のクラスの
   * 結合度をいたずらに高めてしまうことを表す。この値を計算に使用したい場合は、他の適切なメソッドを探すか、
   * このクラスに新しいメソッドを追加することを検討すること。</p>
   *
   * @return 上側限界値. 限界がない場合は、{@code Limitless[T]}
   */
  def upperLimit = upperLimitObject.value

  /**
   * この区間と与えた区間 {@code other} の下側限界値のうち、より大きい（限界の狭い、制約の大きい）限界値を返す。
   *
   * @param other 比較対象の限界値
   * @return より大きい限界値
   */
  private[intervals] def greaterOfLowerLimits(other: Interval[T]): LimitValue[T] = {
    if (lowerLimit == Limitless[T]) {
      return other.lowerLimit
    }
    if (other.lowerLimit == Limitless[T]) {
      return lowerLimit
    }
    val lowerComparison = lowerLimit.compareTo(other.lowerLimit)
    if (lowerComparison >= 0) lowerLimit
    other.lowerLimit
  }

  /**
   * この区間と与えた区間 {@code other} の上側限界値のうち、より小さい（限界の狭い、制約の大きい）限界値を返す。
   *
   * @param other 比較対象の限界値
   * @return より小さい限界値
   */
  private[intervals] def lesserOfUpperLimits(other: Interval[T]): LimitValue[T] = {
    if (upperLimit == Limitless[T]) return other.upperLimit
    if (other.upperLimit == Limitless[T]) return upperLimit
    val upperComparison = upperLimit.compareTo(other.upperLimit)
    if (upperComparison <= 0) upperLimit
    other.upperLimit
  }

  private[intervals] def toStringDetail = {
    val buffer = new StringBuilder
    buffer.append(if (includesLowerLimit) "[" else "(")
    buffer.append(if (hasLowerLimit) lowerLimit.toString else "Infinity")
    buffer.append(", ")
    buffer.append(if (hasUpperLimit) upperLimit.toString else "Infinity")
    buffer.append(if (includesUpperLimit) "]" else ")")
    buffer.toString
  }


  /**
   * 区間をグラフィカルに確認するためのデバッグ用メソッド。
   *
   * <p>単一要素区間はキャラクタ{@code @}で表示する。
   * 下側限界がない場合はキャラクタ{@code <}で表示し、上側限界がない場合はキャラクタ{@code >}で表示する。
   * 下側限界が開区間である場合はキャラクタ{@code (} 、閉区間である場合はキャラクタ{ @code [}で表示する。
   * 上側限界が開区間である場合はキャラクタ{ @code )}、閉区間である場合はキャラクタ{@code ]}で表示する。
   * 区間内の要素はキャラクタ{@code -}で表示する。</p>
   *
   * @return 文字列
   */
  private[intervals] def toStringGraphically = {
    val sb = new StringBuilder
    sb.append(" ")
    if (empty) {
      sb.append("EMPTY")
    } else if (singleElement) {
      for (i <- 0 until lowerLimit.asInstanceOf[Limit[T]].value.asInstanceOf[Int]) {
        sb.append(" ")
      }
      sb.append("@")
    } else {
      if (lowerLimit == Limitless[T]) {
        sb.deleteCharAt(0)
        sb.append("<")
      } else {
        for (i <- 0 until lowerLimit.asInstanceOf[Limit[T]].value.asInstanceOf[Int]) {
          sb.append(" ")
        }
        sb.append(if (includesLowerLimit) "[" else "(")
      }
      if (upperLimit == Limitless[T]) {
        sb.append(">")
      } else {
        val l = if (lowerLimit == Limitless[T]) -1 else lowerLimit.asInstanceOf[Limit[T]].value.asInstanceOf[Int]
        val u = if (upperLimit == Limitless[T]) 100 else upperLimit.asInstanceOf[Limit[T]].value.asInstanceOf[Int]
        for (i <- 0 until u - l - 1) {
          sb.append("-")
        }
        sb.append(if (includesUpperLimit) "]" else ")")
      }
    }
    sb.toString
  }

  private def checkLowerIsLessThanOrEqualUpper(lower: IntervalLimit[T], upper: IntervalLimit[T]) {
    if ((lower.lower && upper.upper && lower.compareTo(upper) <= 0) == false) {
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

  /**
   * この区間と与えた区間 {@code other} の上側限界値のうち、より大きい（限界の広い、制約の小さい）限界値を返す。
   *
   * @param other 比較対象の限界値
   * @return より小さい限界値. この区間に上側限界がない場合は {@code Limitless[T]}
   */
  private def greaterOfUpperLimits(other: Interval[T]): Option[LimitValue[T]] = {
    if (upperLimit == Limitless[T]) {
      return None
    }
    val upperComparison = upperLimit.compareTo(other.upperLimit)
    if (upperComparison >= 0) {
      return Some(upperLimit)
    }
    Some(other.upperLimit)
  }

  /**
   * この区間の下側<b>補</b>区間と与えた区間 {@code other} の共通部分を返す。
   *
   * @param other 比較対象の区間
   * @return この区間の下側の補区間と、与えた区間の共通部分。存在しない場合は {@code None}
   */
  private def leftComplementRelativeTo(other: Interval[T]): Option[Interval[T]] = {
    // この区間の下側限界値の方が小さいか等しい場合、下側の補区間に共通部分は無い
    if (lowerLimitObject.compareTo(other.lowerLimitObject) <= 0) {
      return None
    }
    Some(newOfSameType(other.lowerLimit, other.includesLowerLimit, lowerLimit,
      includesLowerLimit == false))
  }

  /**
   * この区間と与えた区間 {@code other} の下側限界値のうち、より小さい（限界の広い、制約の小さい）限界値を返す。
   *
   * @param other 比較対象の限界値
   * @return より小さい限界値. この区間に下側限界がない場合は {@code Limitless[T]}
   */
  private def lesserOfLowerLimits(other: Interval[T]) = {
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
    //    if (lowerLimit == Limitless[T]) {
    //      None
    //    }
    //    val lowerComparison = lowerLimit.compareTo(other.lowerLimit)
    //    if (lowerComparison <= 0) {
    //      Some(lowerLimit)
    //    }
    //    Some(other.lowerLimit)
  }

  private def lesserOfUpperIncludedInIntersection(other: Interval[T]) = {
    val limit = lesserOfUpperLimits(other)
    includes(limit) && other.includes(limit)
  }

  private def lesserOfUpperIncludedInUnion(other: Interval[T]) = {
    val limit = lesserOfUpperLimits(other)
    includes(limit) || other.includes(limit)
  }

  /**
   * この区間の上側<b>補</b>区間と与えた区間 {@code other} の共通部分を返す。
   *
   * @param other 比較対象の区間
   * @return この区間の上側の補区間と、与えた区間の共通部分。存在しない場合は {@code None}
   */
  private def rightComplementRelativeTo(other: Interval[T]): Option[Interval[T]] = {
    // この区間の上側限界値の方が大きいか等しい場合、上側の補区間に共通部分は無い
    if (upperLimitObject.compareTo(other.upperLimitObject) >= 0) {
      return None
    }
    Some(newOfSameType(upperLimit, includesUpperLimit == false, other.upperLimit,
      other.includesUpperLimit))
  }
}


object Interval {

  /**
   * 下側限界のみを持つ区間を生成する。
   *
   * <p>下側限界値は区間に含む（閉じている）区間である。</p>
   *
   * @param <T> 限界値の型
   * @param lower 下側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @return 区間
   */
  def andMore[T <% Ordered[T]](lower: LimitValue[T]): Interval[T] = closed(lower, Limitless[T])

  /**
   * 閉区間を生成する。
   *
   * @param <T> 限界値の型
   * @param lower 下側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @param upper 上側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @return 閉区間
   * @throws IllegalArgumentException 下限値が上限値より大きい場合
   */
  def closed[T <% Ordered[T]](lower: LimitValue[T], upper: LimitValue[T]): Interval[T] =
    new Interval[T](lower, true, upper, true)

  /**
   * 下側限界のみを持つ区間を生成する。
   *
   * <p>下側限界値は区間に含まない（開いている）区間である。</p>
   *
   * @param <T> 限界値の型
   * @param lower 下側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @return 区間
   */
  def moreThan[T <% Ordered[T]](lower: LimitValue[T]) = open(lower, Limitless[T])

  def empty[T <% Ordered[T]](someValue: LimitValue[T]) =
    new Interval[T](someValue, false, someValue, false)

  /**
   * 開区間を生成する。
   *
   * @param <T> 限界値の型
   * @param lower 下側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @param upper 上側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @return 開区間
   * @throws IllegalArgumentException 下限値が上限値より大きい場合
   */
  def open[T <% Ordered[T]](lower: LimitValue[T], upper: LimitValue[T]) = new Interval[T](lower, false, upper, false)

  /**
   * 区間を生成する。
   *
   * <p>主に、半開区間（上限下限のどちらか一方だけが開いている区間）の生成に用いる。</p>
   *
   * @param <T> 限界値の型
   * @param lower 下側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @param lowerIncluded 下限値を区間に含む（閉じた下側限界）場合は{@code true}を指定する
   * @param upper 上側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @param upperIncluded 上限値を区間に含む（閉じた上側限界）場合は{@code true}を指定する
   * @return 区間
   * @throws IllegalArgumentException 下限値が上限値より大きい場合
   */
  def over[T <% Ordered[T]](lower: LimitValue[T], lowerIncluded: Boolean, upper: LimitValue[T],
                            upperIncluded: Boolean) = new Interval[T](lower, lowerIncluded, upper, upperIncluded)

  /**
   * 単一要素区間を生成する。
   *
   * @param element 単一要素となる値
   * @param <T> 限界値の型
   * @return 区間
   * @throws IllegalArgumentException 引数に{@code Limitless[T]}を与えた場合
   */
  def singleElement[T <% Ordered[T]](element: LimitValue[T]) = closed(element, element)

  /**
   * 上側限界のみを持つ区間を生成する。
   *
   * <p>上側限界値は区間に含まない（開いている）区間である。</p>
   *
   * @param <T> 限界値の型
   * @param upper 上側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @return 区間
   */
  def under[T <% Ordered[T]](upper: LimitValue[T]) = open(Limitless[T], upper)

  /**
   * 上側限界のみを持つ区間を生成する。
   *
   * <p>上側限界値は区間に含む（閉じている）区間である。</p>
   *
   * @param <T> 限界値の型
   * @param upper 上側限界値. {@code Limitless[T]}の場合は、限界がないことを表す
   * @return 区間
   */
  def upTo[T <% Ordered[T]](upper: LimitValue[T]) = closed(Limitless[T], upper)

}