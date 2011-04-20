package jp.tricreo.baseunits.scala.time

import jp.tricreo.scala.ddd.base.model.spec.Specification


/**日付の仕様を表現するオブジェクト。
 */
abstract class DateSpecification extends Specification[CalendarDate] {

  /**
   * 指定した期間の中で、この日付仕様を満たす最初の年月日を返す。
   *
   * @param interval 期間
   * @return 年月日。但し、仕様を満たす日がなかった場合は{@code null}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def firstOccurrenceIn(interval: CalendarInterval): Option[CalendarDate]

  /**
   * 与えた日付が、この日付仕様を満たすかどうか検証する。
   *
   * @param date 検証対象の日付
   * @return 仕様を満たす場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  override def isSatisfiedBy(date: CalendarDate): Boolean

  /**
   * 指定した期間の中で、この日付仕様を満たす年月日を順次取得する反復子を返す。
   *
   * @param interval 期間
   * @return 反復子
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def iterateOver(interval: CalendarInterval): Iterator[CalendarDate]

}

object DateSpecification {
  /**
   * 特定のある1日だけにマッチする日付仕様のインスタンスを返す。
   *
   * <p>毎月31日を指定した場合、該当月に31日が存在しなければ、その月にはヒットしない。</p>
   *
   * @param date マッチする日
   * @return 日付仕様
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def fixed(date: CalendarDate) =
    new FixedDateSpecification(date)

  /**
   * 日付仕様「毎月{@code day}日」のインスタンスを生成する。
   *
   * <p>毎月31日を指定した場合、該当月に31日が存在しなければ、その月にはヒットしない。</p>
   *
   * @param day 日を表す正数（1〜31）
   * @throws IllegalArgumentException 引数{@code day}が1〜31の範囲ではない場合
   * @return 日付仕様
   */
  def fixed(day: Int) =
    new MonthlyFixedDateSpecification(DayOfMonth(day))

  /**
   * 日付仕様のインスタンスを生成する。
   *
   * @param month 月を表す正数（1〜12）
   * @param day 日を表す正数（1〜31）
   * @throws IllegalArgumentException 引数{@code month}が1〜12の範囲ではない場合
   * @throws IllegalArgumentException 引数{@code day}が1〜31の範囲ではない場合
   * @return 日付仕様
   */
  def fixed(month: Int, day: Int) =
    new AnnualFixedDateSpecification(MonthOfYear(month), DayOfMonth(day));

  /**
   * どの日付にもマッチしない日付仕様を返す。
   *
   * @return 日付仕様
   */
  def never =
    new DateSpecification {

      override def firstOccurrenceIn(interval: CalendarInterval): Option[CalendarDate] =
        throw new UnsupportedOperationException

      override def isSatisfiedBy(date: CalendarDate): Boolean = false

      override def iterateOver(interval: CalendarInterval): Iterator[CalendarDate] = Iterator.empty

    }

  /**
   * 毎月第Y◎曜日仕様のインスタンスを生成する。
   *
   * @param dayOfWeek 曜日
   * @param n 周回数（1〜5）
   * @return 日付仕様
   * @throws IllegalArgumentException 引数dayOfWeekに{@code null}を与えた場合
   */
  def nthOccuranceOfWeekdayInEveryMonth(dayOfWeek: DayOfWeek, n: Int): DateSpecification =
    new MonthlyFloatingDateSpecification(dayOfWeek, n);

  /**
   * X月の第Y◎曜日仕様のインスタンスを生成する。
   *
   * @param month 月を表す正数（1〜12）
   * @param dayOfWeek 曜日
   * @param n 周回数（1〜5）
   * @return 日付仕様
   * @throws IllegalArgumentException 引数dayOfWeekに{@code null}を与えた場合
   */
  def nthOccuranceOfWeekdayInMonth(month: Int, dayOfWeek: DayOfWeek, n: Int): DateSpecification =
    new AnnualFloatingDateSpecification(month, dayOfWeek, n);

}
