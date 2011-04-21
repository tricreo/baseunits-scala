package jp.tricreo.baseunits.scala.time

import java.util.TimeZone

/**カレンダー上の特定の「年月日時分」を表すクラス。
 *
 * <p>[[java.util.Date]]と異なり、分未満（秒以下）の概念を持っていない。また、[[TimePoint]]と異なり、
 * その分1分間全ての範囲を表すクラスであり、特定の瞬間をモデリングしたものではない。</p>
 *
 * @param date 年月日
 * @param time 時分
 */
@serializable
class CalendarMinute private[time]
(private[time] val date: CalendarDate,
 private[time] val time: TimeOfDay)
  extends Ordered[CalendarMinute] {


  /**指定したタイムゾーンにおける、このインスタンスが表す「年月日時分」の0秒0ミリ秒の瞬間について [[TimePoint]] 型のインスタンスを返す。
   *
   * @param timeZone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def asTimePoint(timeZone: TimeZone): TimePoint = {
    TimePoint.from(date, time, timeZone)
  }

  /**このオブジェクトの[[#date]]フィールド（年月日）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 年月日
   */
  def breachEncapsulationOfDate = date

  /**このオブジェクトの[[#time]]フィールド（時分）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 時分
   */
  def breachEncapsulationOfTime = time

  def compare(other: CalendarMinute): Int = {
    val dateComparance = date.compareTo(other.date)
    if (dateComparance != 0) {
      dateComparance
    } else {
      time.compareTo(other.time)
    }
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: CalendarMinute => date == that.date && time == that.time
    case _ => false
  }

  override def hashCode = date.hashCode + time.hashCode

  /**指定した年月日時分 {@code other} が、このオブジェクトが表現する年月日時分よりも過去であるかどうかを検証する。
   *
   * <p>{@code other} が {@code null} である場合と、お互いが同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象年月日時分
   * @return 過去である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isAfter(other: CalendarMinute): Boolean = {
    isBefore(other) == false && equals(other) == false
  }

  /**指定した年月日時分 {@code other} が、このオブジェクトが表現する年月日時分よりも未来であるかどうかを検証する。
   *
   * <p>{@code other} が {@code null} である場合と、お互いが同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象年月日時分
   * @return 未来である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isBefore(other: CalendarMinute) = {
    if (date.isBefore(other.date)) {
      true
    } else if (date.isAfter(other.date)) {
      false
    } else {
      time.isBefore(other.time)
    }
  }

  override def toString = {
    date.toString + " at " + time.toString
  }

  /**この年月日時分を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern [[SimpleDateFormat]]に基づくパターン
   * @param zone タイムゾーン
   * @return 整形済み時間文字列
   */
  def toString(pattern: String, zone: TimeZone) = {
    val point = asTimePoint(zone)
    point.toString(pattern, zone)
  }


}

object CalendarMinute {

  def apply(aDate: CalendarDate, aTime: TimeOfDay) = from(aDate, aTime)

  def unapply(calendarMinute:CalendarMinute) =
    Some(calendarMinute.date, calendarMinute.time)


  /**指定した年月日を時分表す、[[CalendarMinute]]のインスタンスを生成する。
   *
   * @param aDate 年月日
   * @param aTime 時分
   * @return [[CalendarMinute]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def from(aDate: CalendarDate, aTime: TimeOfDay): CalendarMinute = new CalendarMinute(aDate, aTime)

  /**指定した年月日を時分表す、[[CalendarMinute]]のインスタンスを生成する。
   *
   * @param year 西暦年をあらわす数
   * @param month 月をあらわす正数（1〜12）
   * @param day 日をあらわす正数（1〜31）
   * @param hour 時をあらわす正数（0〜23）
   * @param minute 分をあらわす正数（0〜59）
   * @return [[CalendarMinute]]
   * @throws IllegalArgumentException 引数{@code month}が1〜12の範囲ではない場合
   * @throws IllegalArgumentException 引数{@code day}が1〜31の範囲ではない場合
   * @throws IllegalArgumentException 引数{@code hour}が0〜23の範囲ではない場合
   * @throws IllegalArgumentException 引数{@code minute}が0〜59の範囲ではない場合
   * @throws IllegalArgumentException 引数{@code day}が{@code yearMonth}の月に存在しない場合
   */
  def from(year: Int, month: Int, day: Int, hour: Int, minute: Int): CalendarMinute =
    new CalendarMinute(CalendarDate.from(year, month, day), TimeOfDay.from(hour, minute))

  /**指定した年月日時分を表す、[[CalendarDate]]のインスタンスを生成する。
   *
   * @param dateTimeString 年月日時分を表す文字列
   * @param pattern 解析パターン文字列
   * @return [[CalendarMinute]]
   * @throws ParseException 文字列の解析に失敗した場合
   */
  def parse(dateTimeString: String, pattern: String): CalendarMinute = {
    val arbitraryZone = TimeZone.getTimeZone("Universal");
    //Any timezone works, as long as the same one is used throughout.
    val point = TimePoint.parse(dateTimeString, pattern, arbitraryZone)
    CalendarMinute.from(point.calendarDate(arbitraryZone), point.asTimeOfDay(arbitraryZone))
  }
}