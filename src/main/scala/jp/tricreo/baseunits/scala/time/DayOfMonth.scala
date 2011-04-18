package jp.tricreo.baseunits.scala.time

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/16
 * Time: 1:32
 * To change this template use File | Settings | File Templates.
 */

class DayOfMonth(val value: Int) extends Ordered[DayOfMonth] {
  require(value >= DayOfMonth.MIN && value <= DayOfMonth.MAX,
    "Illegal value for day of month: " + value
      + ", please use a value between 1 and 31")

  def compare(that: DayOfMonth): Int = value - that.value

  override def hashCode: Int = value.hashCode

  override def equals(obj: Any): Boolean = obj match {
    case that: DayOfMonth => value == that.value
    case _ => false
  }

  /**指定した日 {@code other} が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
   *
   * <p>{@code other} が {@code null} である場合と、お互いが同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象日時
   * @return 過去である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isAfter(other: DayOfMonth) = {
    isBefore(other) == false && equals(other) == false
  }

  /**この日を、指定した年月に適用可能かどうか調べる。
   *
   * <p>例えば、31日は6月に適用不可能であるが、7月には適用可能である。
   * また、29日は閏年の2月に適用可能であるが、平年の2月には適用不可能である。</p>
   *
   * @param month 年月
   * @return 適用可能な場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isApplyable(month: CalendarMonth) = {
    month.getLastDayOfMonth.isBefore(this) == false
  }

  /**この日を、指定した年月に適用可能かどうか調べる。
   *
   * <p>例えば、31日は6月に適用不可能であるが、7月には適用可能である。
   * また、29日は閏年の2月に適用可能であるが、平年の2月には適用不可能である。</p>
   *
   * @param year 年
   * @param month 月
   * @return 適用可能な場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isApplyable(year: Int, month: MonthOfYear) = {
    month.getLastDayOfThisMonth(year).isBefore(this) == false;
  }

  /**
   * 指定した日 {@code other} が、このオブジェクトが表現する日よりも未来であるかどうかを検証する。
   *
   * <p>{@code other} が {@code null} である場合と、お互いが同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象日
   * @return 未来である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isBefore(other: DayOfMonth): Boolean = value < other.value

  /**
   * 指定した年月のこの日を返す。
   *
   * @param month 年月
   * @return {@link CalendarDate}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   * @throws IllegalArgumentException 引数{@code month}の月にこの日が存在しない場合
   */
  def on(month: CalendarMonth): CalendarDate = {
    CalendarDate.from(month, this)
  }

  override def toString = String.valueOf(value)

}

object DayOfMonth {
  val MIN = 1
  val MAX = 31

  def apply(value: Int) = new DayOfMonth(value)
}