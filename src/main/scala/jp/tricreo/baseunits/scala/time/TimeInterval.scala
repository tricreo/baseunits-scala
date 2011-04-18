package jp.tricreo.baseunits.scala.time

import jp.tricreo.baseunits.scala.intervals.{Limit, Limitless, LimitValue, Interval}

class TimeInterval
(_start: LimitValue[TimePoint], startIncluded: Boolean,
 _end: LimitValue[TimePoint], endIncluded: Boolean)
  extends Interval[TimePoint](_start, startIncluded, _end, endIncluded) {

  /**
   * この期間の開始日時を起点として、前回の日時の1日後の日時を
   * この期間の終了日時を超過しない範囲で順次取得する反復子を取得する。
   *
   * <p>例えば [2009/01/01 13:00, 2009/01/04 05:00) で表される期間に対してこのメソッドを呼び出した場合、
   * その戻り値の反復子からは、以下の要素が取得できる。
   * <ol>
   *   <li>2009/01/01 13:00</li>
   *   <li>2009/01/02 13:00</li>
   *   <li>2009/01/03 13:00</li>
   * </ol>
   * </p>
   *
   * <p>返す反復子は {@link Iterator#remove()} をサポートしない。</p>
   *
   * <p>この期間が終了日時（上側限界）を持たない場合、 {@link Iterator#hasNext()}は常に
   * {@code true}を返すので、無限ループに注意すること。</p>
   *
   * @return 日時の反復子
   * @throws IllegalStateException この期間が開始日時（下側限界）を持たない場合
   */
  def daysIterator: Iterator[TimePoint] = {
    if (hasLowerLimit == false) {
      throw new IllegalStateException
    }
    new Iterator[TimePoint] {

      var _next = start

      override def hasNext = {
        if (hasUpperLimit == false) {
          true
        } else {
          end match {
            case Limit(end) => end.isAfter(_next match {
              case Limit(n) => n
            })
          }
        }
      }

      override def next = {
        if (hasNext == false) {
          throw new NoSuchElementException();
        }
        val current = _next;
        _next = _next match {
          case Limit(n) => Limit(n.nextDay)
        }
        current.toLimitObject
      }
    }
  }

  /**
   * この期間の終了日時を取得する。
   *
   * @return この期間の終了日時. 上側限界がない場合は {@code null}
   */
  def end = upperLimit

  /**
   * この期間と与えた期間 {@code interval} の積集合（共通部分）を返す。
   *
   * <p>共通部分が存在しない場合は、空の区間を返す。</p>
   *
   * @param interval 比較対象の期間
   * @return 積集合（共通部分）
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def intersect(interval: TimeInterval) = super.intersect(interval).asInstanceOf[TimeInterval]


  /**
   * 指定した日時が、この期間の開始日時以前でないかどうかを検証する。
   *
   * @param point 日時
   * @return 開始日時以前でない場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   * @see Interval#isAbove(Comparable)
   */
  def isAfter(point: LimitValue[TimePoint]) = isAbove(point)

  /**
   * 指定した日時が、この期間の終了日時を以後でないかどうかを検証する。
   *
   * @param point 日時
   * @return 終了日時以後でない場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   * @see Interval#isBelow(Comparable)
   */
  def isBefore(point: LimitValue[TimePoint]) = isBelow(point)

  /**
   * この期間の長さを取得する。
   *
   * @return 長さ. もし開始日時または終了日時が存在しない（無限）場合は{@code null}を返す。
   * @throws IllegalStateException この期間が開始日時（下側限界）または終了日時（下側限界）を持たない場合
   */
  def length = {
    if (hasLowerLimit == false || hasUpperLimit == false) {
      throw new IllegalStateException
    }
    val Limit(_end) = end
    val Limit(_start) = start
    val difference = _end.millisecondsFromEpoc - _start.millisecondsFromEpoc
    Duration.milliseconds(difference)
  }

  /**
   * この期間と同じ型を持つ、新しい期間を返す。
   *
   * @param start 下側限界値. 限界値がない場合は、{@code null}
   * @param isStartClosed 下限値を期間に含む（閉じた下側限界）場合は{@code true}を指定する
   * @param end 上側限界値. 限界値がない場合は、{@code null}
   * @param isEndClosed 上限値を期間に含む（閉じた上側限界）場合は{@code true}を指定する
   * @return 新しい期間
   */
  override def newOfSameType(start: LimitValue[TimePoint],
                             isStartClosed: Boolean,
                             end: LimitValue[TimePoint],
                             isEndClosed: Boolean) = {
    new TimeInterval(start, isStartClosed, end, isEndClosed)
  }

  /**
   * この期間の開始日時を取得する。
   *
   * @return この期間の開始日時. 下側限界がない場合は {@code null}
   */
  def start = lowerLimit


  /**
   * この期間の開始日時を起点として、指定した時間の長さを持ち前回の終了日時を開始日時とする期間 {@link TimeInterval} を
   * この期間の終了日時を超過しない範囲で順次取得する反復子を取得する。
   *
   * <p>例えば [2009/01/01 02:00, 2009/01/10 15:00) で表される期間に対して、
   * 2日間の {@code subintervalLength} を与えた場合、
   * その戻り値の反復子からは、以下の要素が取得できる。
   * <ol>
   *   <li>[2009/01/01 02:00, 2009/01/03 02:00)</li>
   *   <li>[2009/01/03 02:00, 2009/01/05 02:00)</li>
   *   <li>[2009/01/05 02:00, 2009/01/07 02:00)</li>
   *   <li>[2009/01/07 02:00, 2009/01/09 02:00)</li>
   * </ol>
   * </p>
   *
   * <p>返す反復子は {@link Iterator#remove()} をサポートしない。</p>
   *
   * <p>この期間が終了日時（上側限界）を持たない場合、 {@link Iterator#hasNext()}は常に
   * {@code true}を返すので、無限ループに注意すること。</p>
   *
   * @param subintervalLength 反復子が返す期間の長さ
   * @return 期間の反復子
   * @throws IllegalStateException この期間が開始日時（下側限界）を持たない場合
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def subintervalIterator(subintervalLength: Duration): Iterator[TimeInterval] = {
    if (hasLowerLimit == false) {
      throw new IllegalStateException();
    }
    val segmentLength = subintervalLength
    new Iterator[TimeInterval] {

      var _next = segmentLength.startingFromTimePoint(start)

      override def hasNext = TimeInterval.this.covers(_next)

      override def next = {
        if (hasNext == false) {
          throw new NoSuchElementException
        }
        val current = _next
        _next = segmentLength.startingFromTimePoint(_next.end)
        current;
      }
    }
  }
}

object TimeInterval {

  def apply(start: LimitValue[TimePoint],
            startClosed: Boolean,
            end: LimitValue[TimePoint],
            endClosed: Boolean): TimeInterval = {
    new TimeInterval(start, startClosed, end, endClosed)
  }

  /**
   * 開始日時と終了日時より、閉期間を返す。
   *
   * @param start 開始日時（下側限界値）. {@code null}の場合は、限界がないことを表す
   * @param end 終了日時（上側限界値）. {@code null}の場合は、限界がないことを表す
   * @return 期間
   * @throws IllegalArgumentException 下限値が上限値より大きい（未来である）場合
   */
  def closed(start: LimitValue[TimePoint], end: LimitValue[TimePoint]): TimeInterval = {
    over(start, true, end, true)
  }

  /**
   * 開始日時より、下側限界のみを持つ期間を返す。
   *
   * <p>開始日時は期間に含む（閉じている）区間である。</p>
   *
   * @param start 開始日時（下側限界値）. {@code null}の場合は、限界がないことを表す
   * @return 期間
   */
  def everFrom(start: LimitValue[TimePoint]): TimeInterval = {
    over(start, Limitless[TimePoint])
  }

  /**
   * 終了日時より、上側限界のみを持つ期間を返す。
   *
   * <p>終了日時は期間に含まない（開いている）区間である。</p>
   *
   * @param end 終了日時（上側限界値）. {@code null}の場合は、限界がないことを表す
   * @return 期間
   */
  def everPreceding(end: LimitValue[TimePoint]): TimeInterval = {
    over(Limitless[TimePoint], end)
  }

  /**
   * 開始日時と終了日時より、開期間を返す。
   *
   * @param start 開始日時（下側限界値）. {@code null}の場合は、限界がないことを表す
   * @param end 終了日時（上側限界値）. {@code null}の場合は、限界がないことを表す
   * @return 期間
   * @throws IllegalArgumentException 下限値が上限値より大きい（未来である）場合
   */
  def open(start: LimitValue[TimePoint], end: LimitValue[TimePoint]): TimeInterval = {
    over(start, false, end, false)
  }

  /**
   * 開始日時と終了日時より、期間を返す。
   *
   * <p>主に、半開区間（上限下限のどちらか一方だけが開いている区間）の生成に用いる。</p>
   *
   * @param start 開始日時（下側限界値）. {@code null}の場合は、限界がないことを表す
   * @param startClosed 開始日時を期間に含む（閉じた下側限界）場合は{@code true}を指定する
   * @param end 終了日時（上側限界値）. {@code null}の場合は、限界がないことを表す
   * @param endClosed 終了日時を期間に含む（閉じた上側限界）場合は{@code true}を指定する
   * @return 期間
   * @throws IllegalArgumentException 下限値が上限値より大きい（未来である）場合
   */
  def over(start: LimitValue[TimePoint], startClosed: Boolean, end: LimitValue[TimePoint], endClosed: Boolean): TimeInterval = {
    apply(start, startClosed, end, endClosed)
  }

  /**
   * 開始日時と終了日時より、期間を返す。
   *
   * <p>生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を返す。</p>
   *
   * @param start 開始日時（下側限界値）. {@code null}の場合は、限界がないことを表す
   * @param end 終了日時（上側限界値）. {@code null}の場合は、限界がないことを表す
   * @return 期間
   * @throws IllegalArgumentException 開始日時が終了日時より大きい（未来である）場合
   */
  def over(start: LimitValue[TimePoint], end: LimitValue[TimePoint]): TimeInterval = {
    // Uses the common default for time intervals, [start, end).
    over(start, true, end, false)
  }

  /**
   * 終了日時と期間の長さより、期間を返す。
   *
   * @param end 終了日時（上側限界値）. {@code null}の場合は、限界がないことを表す
   * @param startClosed 開始日時を期間に含む（閉じた下側限界）場合は{@code true}を指定する
   * @param length 期間の長さ
   * @param endClosed 終了日時を期間に含む（閉じた上側限界）場合は{@code true}を指定する
   * @return 期間
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def preceding(end: Limit[TimePoint], startClosed: Boolean, length: Duration, endClosed: Boolean): TimeInterval = {
    val start = end.value - length
    over(Limit(start), startClosed, end, endClosed)
  }

  /**
   * 終了日時と期間の長さより、期間を返す。
   *
   * @param end 終了日時（上側限界値）. {@code null}の場合は、限界がないことを表す
   * @param length 期間の長さ
   * @return 期間
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def preceding(end: Limit[TimePoint], length: Duration): TimeInterval = {
    // Uses the common default for time intervals, [start, end).
    preceding(end, true, length, false)
  }

  /**
   * 開始日時と期間の長さより、期間を返す。
   *
   * @param start 開始日時（下側限界値）
   * @param startClosed 開始日時を期間に含む（閉じた下側限界）場合は{@code true}を指定する
   * @param length 期間の長さ
   * @param endClosed 終了日時を期間に含む（閉じた上側限界）場合は{@code true}を指定する
   * @return 期間
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def startingFrom(start: Limit[TimePoint], startClosed: Boolean, length: Duration, endClosed: Boolean): TimeInterval = {
    val end = start.value + length
    over(start, startClosed, Limit(end), endClosed)
  }

  /**
   * 開始日時と期間の長さより、期間を返す。
   *
   * <p>生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を返す。</p>
   *
   * @param start 開始日時（下側限界値）
   * @param length 期間の長さ
   * @return 期間
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def startingFrom(start: Limit[TimePoint], length: Duration): TimeInterval = {
    // Uses the common default for time intervals, [start, end).
    startingFrom(start, true, length, false)
  }

}