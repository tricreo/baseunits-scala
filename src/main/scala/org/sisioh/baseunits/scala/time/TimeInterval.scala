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
package jp.tricreo.baseunits.scala.time

import jp.tricreo.baseunits.scala.intervals.{Limit, LimitValue, Interval}

/**期間（時間の区間）を表すクラス。
 * 限界の表現には [[jp.tricreo.baseunits.scala.time.TimePoint]]を利用する。
 *
 * @author j5ik2o
 * @param startValue 開始時間
 * @param startIncluded 開始時間を含める場合は`true`
 * @param endValue 終了時間
 * @param endIncluded 終了時間を含める場合は`false`
 */
class TimeInterval
(startValue: LimitValue[TimePoint], startIncluded: Boolean,
 endValue: LimitValue[TimePoint], endIncluded: Boolean)
  extends Interval[TimePoint](startValue, startIncluded, endValue, endIncluded) {

  import jp.tricreo.baseunits.scala.intervals.Limit

  /**この期間の開始日時を起点として、前回の日時の`1日後`の日時をこの期間の終了日時を超過しない範囲で順次取得する反復子を取得する。
   *
   * 例えば [2009/01/01 13:00, 2009/01/04 05:00) で表される期間に対してこのメソッドを呼び出した場合、
   * その戻り値の反復子からは、以下の要素が取得できる。
   * <ol>
   *   <li>2009/01/01 13:00</li>
   *   <li>2009/01/02 13:00</li>
   *   <li>2009/01/03 13:00</li>
   * </ol>
   *
   *
   * この期間が終了日時（上側限界）を持たない場合、 [[scala.collection.Iterator]] hasNext()は常に
   * `true`を返すので、無限ループに注意すること。
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
          end.toValue.isAfter(_next.toValue)
        }
      }

      override def next = {
        if (hasNext == false) {
          throw new NoSuchElementException()
        }
        val current = _next
        _next = Limit(_next.toValue.nextDay)
        current.toValue
      }
    }
  }

  /**この期間の終了日時を取得する。
   *
   * @return この期間の終了日時. 上側限界がない場合は `Limitless[CalendarDate]`
   */
  def end = upperLimit

  /**この期間と与えた期間 `interval` の積集合（共通部分）を返す。
   *
   * 共通部分が存在しない場合は、空の区間を返す。
   *
   * @param interval 比較対象の期間
   * @return 積集合（共通部分）
   */
  def intersect(interval: TimeInterval) = super.intersect(interval).asInstanceOf[TimeInterval]


  /**指定した日時が、この期間の開始日時以前でないかどうかを検証する。
   *
   * @param point 日時
   * @return 開始日時以前でない場合は`true`、そうでない場合は`false`
   * @see Interval#isAbove(Comparable)
   */
  def isAfter(point: LimitValue[TimePoint]) = isAbove(point)

  /**指定した日時が、この期間の終了日時を以後でないかどうかを検証する。
   *
   * @param point 日時
   * @return 終了日時以後でない場合は`true`、そうでない場合は`false`
   * @see Interval#isBelow(Comparable)
   */
  def isBefore(point: LimitValue[TimePoint]) = isBelow(point)

  /**この期間の長さを取得する。
   *
   * @return 長さ.
   * @throws IllegalStateException この期間が開始日時（下側限界）または終了日時（下側限界）を持たない場合
   */
  def length = {
    if (hasLowerLimit == false || hasUpperLimit == false) {
      throw new IllegalStateException
    }
    val difference = end.toValue.millisecondsFromEpoc - start.toValue.millisecondsFromEpoc
    Duration.milliseconds(difference)
  }

  /**この期間と同じ型を持つ、新しい期間を返す。
   *
   * @param start 下側限界値. 限界値がない場合は、`Limitless[TimePoint]`
   * @param isStartClosed 下限値を期間に含む（閉じた下側限界）場合は`true`を指定する
   * @param end 上側限界値. 限界値がない場合は、`Limitless[TimePoint]`
   * @param isEndClosed 上限値を期間に含む（閉じた上側限界）場合は`true`を指定する
   * @return 新しい期間
   */
  override def newOfSameType(start: LimitValue[TimePoint],
                             isStartClosed: Boolean,
                             end: LimitValue[TimePoint],
                             isEndClosed: Boolean) =
    TimeInterval(start, isStartClosed, end, isEndClosed)

  /**この期間の開始日時を取得する。
   *
   * @return この期間の開始日時. 下側限界がない場合は `Limitless[T]`
   */
  def start = lowerLimit


  /**この期間の開始日時を起点として、指定した時間の長さを持ち前回の終了日時を開始日時とする期間
   * [[jp.tricreo.baseunits.scala.time.TimeInterval]] を
   * この期間の終了日時を超過しない範囲で順次取得する反復子を取得する。
   *
   * 例えば [2009/01/01 02:00, 2009/01/10 15:00) で表される期間に対して、
   * 2日間の `subintervalLength` を与えた場合、
   * その戻り値の反復子からは、以下の要素が取得できる。
   * <ol>
   *   <li>[2009/01/01 02:00, 2009/01/03 02:00)</li>
   *   <li>[2009/01/03 02:00, 2009/01/05 02:00)</li>
   *   <li>[2009/01/05 02:00, 2009/01/07 02:00)</li>
   *   <li>[2009/01/07 02:00, 2009/01/09 02:00)</li>
   * </ol>
   *
   *
   * この期間が終了日時（上側限界）を持たない場合、 [[scala.collection.Iterator]] hasNext()は常に
   * `true`を返すので、無限ループに注意すること。
   *
   * @param subintervalLength 反復子が返す期間の長さ
   * @return 期間の反復子
   * @throws IllegalStateException この期間が開始日時（下側限界）を持たない場合
   */
  def subintervalIterator(subintervalLength: Duration): Iterator[TimeInterval] = {
    if (hasLowerLimit == false) {
      throw new IllegalStateException
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
        current
      }
    }
  }
}

/**コンパニオンオブジェクト。
 *
 * @author j5ik2o
 */
object TimeInterval {

  import jp.tricreo.baseunits.scala.intervals.{Limitless, LimitValue}

  /**インスタンスを生成する。
   *
   * @param startValue 開始時間
   * @param startClosed 開始時間を含める場合は`true`
   * @param endValue 終了時間
   * @param endClosed 終了時間を含める場合は`false`
   * @return [[jp.tricreo.baseunits.scala.time.TimeInterval]]
   */
  def apply(start: LimitValue[TimePoint],
            startClosed: Boolean,
            end: LimitValue[TimePoint],
            endClosed: Boolean): TimeInterval =
    new TimeInterval(start, startClosed, end, endClosed)

  /**開始日時と終了日時より、閉期間を返す。
   *
   * @param start 開始日時（下側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @param end 終了日時（上側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @return 期間
   * @throws IllegalArgumentException 下限値が上限値より大きい（未来である）場合
   */
  def closed(start: LimitValue[TimePoint], end: LimitValue[TimePoint]): TimeInterval =
    over(start, true, end, true)

  /**開始日時より、下側限界のみを持つ期間を返す。
   *
   * 開始日時は期間に含む（閉じている）区間である。
   *
   * @param start 開始日時（下側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @return 期間
   */
  def everFrom(start: LimitValue[TimePoint]): TimeInterval =
    over(start, Limitless[TimePoint])

  /**終了日時より、上側限界のみを持つ期間を返す。
   *
   * 終了日時は期間に含まない（開いている）区間である。
   *
   * @param end 終了日時（上側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @return 期間
   */
  def everPreceding(end: LimitValue[TimePoint]): TimeInterval =
    over(Limitless[TimePoint], end)

  /**開始日時と終了日時より、開期間を返す。
   *
   * @param start 開始日時（下側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @param end 終了日時（上側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @return 期間
   * @throws IllegalArgumentException 下限値が上限値より大きい（未来である）場合
   */
  def open(start: LimitValue[TimePoint], end: LimitValue[TimePoint]): TimeInterval =
    over(start, false, end, false)

  /**開始日時と終了日時より、期間を返す。
   *
   * 主に、半開区間（上限下限のどちらか一方だけが開いている区間）の生成に用いる。
   *
   * @param start 開始日時（下側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @param startClosed 開始日時を期間に含む（閉じた下側限界）場合は`true`を指定する
   * @param end 終了日時（上側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @param endClosed 終了日時を期間に含む（閉じた上側限界）場合は`true`を指定する
   * @return 期間
   * @throws IllegalArgumentException 下限値が上限値より大きい（未来である）場合
   */
  def over(start: LimitValue[TimePoint], startClosed: Boolean, end: LimitValue[TimePoint], endClosed: Boolean): TimeInterval =
    apply(start, startClosed, end, endClosed)

  /**開始日時と終了日時より、期間を返す。
   *
   * 生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を返す。
   *
   * @param start 開始日時（下側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @param end 終了日時（上側限界値）. `Limitless[TimePoint]`の場合は、限界がないことを表す
   * @return 期間
   * @throws IllegalArgumentException 開始日時が終了日時より大きい（未来である）場合
   */
  def over(start: LimitValue[TimePoint], end: LimitValue[TimePoint]): TimeInterval =
  // Uses the common default for time intervals, [start, end).
    over(start, true, end, false)

  /**終了日時と期間の長さより、期間を返す。
   *
   * @param end 終了日時（上側限界値）.
   * @param startClosed 開始日時を期間に含む（閉じた下側限界）場合は`true`を指定する
   * @param length 期間の長さ
   * @param endClosed 終了日時を期間に含む（閉じた上側限界）場合は`true`を指定する
   * @return 期間
   */
  def preceding(end: Limit[TimePoint], startClosed: Boolean, length: Duration, endClosed: Boolean): TimeInterval = {
    val start = end.value - length
    over(Limit(start), startClosed, end, endClosed)
  }

  /**終了日時と期間の長さより、期間を返す。
   *
   * @param end 終了日時（上側限界値）.
   * @param length 期間の長さ
   * @return 期間
   */
  def preceding(end: Limit[TimePoint], length: Duration): TimeInterval = {
    // Uses the common default for time intervals, [start, end).
    preceding(end, true, length, false)
  }

  /**開始日時と期間の長さより、期間を返す。
   *
   * @param start 開始日時（下側限界値）
   * @param startClosed 開始日時を期間に含む（閉じた下側限界）場合は`true`を指定する
   * @param length 期間の長さ
   * @param endClosed 終了日時を期間に含む（閉じた上側限界）場合は`true`を指定する
   * @return 期間
   */
  def startingFrom(start: Limit[TimePoint], startClosed: Boolean, length: Duration, endClosed: Boolean): TimeInterval = {
    val end = start.value + length
    over(start, startClosed, Limit(end), endClosed)
  }

  /**開始日時と期間の長さより、期間を返す。
   *
   * 生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を返す。
   *
   * @param start 開始日時（下側限界値）
   * @param length 期間の長さ
   * @return 期間
   */
  def startingFrom(start: Limit[TimePoint], length: Duration): TimeInterval = {
    // Uses the common default for time intervals, [start, end).
    startingFrom(start, true, length, false)
  }

}