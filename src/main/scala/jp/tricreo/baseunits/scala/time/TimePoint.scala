package jp.tricreo.baseunits.scala.time

import java.text.SimpleDateFormat
import java.util.{Date => JDate, Calendar, TimeZone}
import jp.tricreo.baseunits.scala.intervals.Limit

/**ミリ秒精度で、ある時間の一点をあらわすクラス。
 * <p>タイムゾーンを持っている。</p>
 * @param millisecondsFromEpoc エポックからの経過ミリ秒
 */
@serializable
class TimePoint private[time]
(private[time] val millisecondsFromEpoc: Long)
  extends Ordered[TimePoint] {

  /**このオブジェクトが表現する瞬間をGMTとして扱い、[[Calendar]]型として取得する。
   *
   * @return [[Calendar]]
   */
  def asJavaCalendar: Calendar = asJavaCalendar(TimePoint.GMT)

  /**このオブジェクトが表現する瞬間を指定したタイムゾーンとして扱い、[[Calendar]]型として取得する。
   *
   * @param zone タイムゾーン
   * @return [[Calendar]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def asJavaCalendar(zone: TimeZone): Calendar = {
    val result = Calendar.getInstance(zone)
    result.setTime(asJavaUtilDate)
    result
  }

  /**このオブジェクトが表現する瞬間を、[[Date]]型として取得する。
   *
   * @return [[Date]]
   */
  def asJavaUtilDate = new JDate(millisecondsFromEpoc)

  /**この瞬間を「時分」として返す。
   *
   * @param zone タイムゾーン
   * @return 時分
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def asTimeOfDay(zone: TimeZone) = {
    val calendar = asJavaCalendar(zone)
    TimeOfDay.from(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))
  }

  /**このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日付における午前0時（深夜）の瞬間を表す [[TimePoint]]を取得する。
   *
   * @param zone タイムゾーン
   * @return 午前0時（深夜）の瞬間を表す [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def backToMidnight(zone: TimeZone) = calendarDate(zone).asTimeInterval(zone).start


  /**このオブジェクトの[[#millisecondsFromEpoc]]フィールド（エポックからの経過ミリ秒）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return エポックからの経過ミリ秒
   */
  def breachEncapsulationOfMillisecondsFromEpoc = millisecondsFromEpoc

  /**このインスタンスが表現する瞬間の、指定したタイムゾーンにおける日付を取得する。
   *
   * @param zone タイムゾーン
   * @return 日付
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def calendarDate(zone: TimeZone) = CalendarDate.from(this, zone)

  /**瞬間同士の比較を行う。
   *
   * <p>相対的に過去である方を「小さい」と判断する。</p>
   *
   * @param otherPoint 比較対象
   * @return [[Comparable#compareTo(Object)]]に準じる
   * @throws NullPointerException 引数に{@code null}を与えた場合
   */
  def compare(otherPoint: TimePoint): Int =
    if (isBefore(otherPoint)) -1
    else if (isAfter(otherPoint)) 1
    else 0

  /**このオブジェクトと、与えたオブジェクト {@code other}の同一性を検証する。
   *
   * <p>与えたオブジェクトが {@code null} ではなく、かつ [[TimePoint]]型であった場合、
   * 同じ日時を指している場合は{@code true}、そうでない場合は{@code false}を返す。</p>
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  override def equals(obj: Any): Boolean = obj match {
    case that: TimePoint => millisecondsFromEpoc == that.millisecondsFromEpoc
    case _ => false
  }

  override def hashCode = (millisecondsFromEpoc ^ (millisecondsFromEpoc >>> 32)).asInstanceOf[Int]


  /**このインスタンスがあらわす瞬間が、指定した期間の終了後に位置するかどうか調べる。
   *
   * @param interval 基準期間
   * @return 期間の終了後に位置する場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isAfter(interval: TimeInterval) = interval.isBefore(Limit(this))

  /**指定した瞬間 {@code other} が、このオブジェクトが表現する日時よりも未来であるかどうかを検証する。
   *
   * <p>同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象日時
   * @return 未来である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isAfter(other: TimePoint) = millisecondsFromEpoc > other.millisecondsFromEpoc


  /**このインスタンスがあらわす瞬間が、指定した期間の開始前に位置するかどうか調べる。
   *
   * @param interval 基準期間
   * @return 期間の開始前に位置する場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isBefore(interval: TimeInterval) = interval.isAfter(Limit(this))

  /**指定した瞬間 {@code other} が、このオブジェクトが表現する日時よりも過去であるかどうかを検証する。
   *
   * <p>同一日時である場合は {@code false} を返す。</p>
   *
   * @param other 対象日時
   * @return 過去である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isBefore(other: TimePoint) = millisecondsFromEpoc < other.millisecondsFromEpoc

  /**指定したタイムゾーンにおいて、このインスタンスが表現する瞬間と指定した瞬間{@code other}の年月日が等価であるかを調べる。
   *
   * @param other 対象瞬間
   * @param zone タイムゾーン
   * @return 等価である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isSameDayAs(other: TimePoint, zone: TimeZone) = calendarDate(zone) == other.calendarDate(zone)

  /**
   * この日時の、指定した時間の長さ分過去の日時を取得する。
   *
   * @param duration 時間の長さ
   * @return 過去の日時
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def -(duration: Duration) = duration.subtractedFrom(this)

  /**
   * このオブジェクトが表現する瞬間の、ちょうど1日後を取得する。
   *
   * <p>日内の時間は変化しない。</p>
   *
   * @return 1日後
   */
  def nextDay = this.+(Duration.days(1))


  /**
   * この日時から、指定した時間の長さ分未来の日時を取得する。
   *
   * @param duration 時間の長さ
   * @return 未来の日時
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def +(duration: Duration) = duration.addedTo(this)


  /**
   * この瞬間の文字列表現を取得する。
   *
   * @see java.lang.Object#toString()
   */
  override def toString = asJavaUtilDate.toString


  /**
   * この瞬間を、指定したパターンで整形し、その文字列表現を取得する。
   *
   * @param pattern [[SimpleDateFormat]]に基づくパターン
   * @param zone タイムゾーン
   * @return 整形済み時間文字列
   */
  def toString(pattern: String, zone: TimeZone) = {
    val format = new SimpleDateFormat(pattern)
    format.setTimeZone(zone);
    format.format(asJavaUtilDate)
  }

  /**
   * このインスタンスがあらわす瞬間を開始瞬間、{@code end}を終了瞬間とする、期間を返す。
   *
   * <p>生成する期間の開始日時は期間に含み（閉じている）、終了日時は期間に含まない（開いている）半開区間を生成する。</p>
   *
   * @param end 終了日時（上側限界値）. {@code null}の場合は、限界がないことを表す
   * @return [[TimeInterval]]
   */
  def until(end: TimePoint) = TimeInterval.over(Limit(this), Limit(end))


}

object TimePoint {

  val GMT = TimeZone.getTimeZone("Universal")

  def apply(milliseconds: Long): TimePoint = from(milliseconds)

  def unapply(timePoint: TimePoint) = Some(timePoint.millisecondsFromEpoc)

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param yearMonth 年月
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def at(yearMonth: CalendarMonth, date: DayOfMonth, hour: Int,
         minute: Int, second: Int, millisecond: Int, zone: TimeZone): TimePoint = {
    at(yearMonth.breachEncapsulationOfYear,
      yearMonth.breachEncapsulationOfMonth.value,
      date.value, hour, minute, second, millisecond, zone);
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, millisecond: Int, zone: TimeZone): TimePoint = {
    val calendar = Calendar.getInstance(zone)
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DATE, date)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, second)
    calendar.set(Calendar.MILLISECOND, millisecond)
    from(calendar.getTime.getTime)
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, zone: TimeZone): TimePoint = {
    at(year, month, date, hour, minute, second, 0, zone)
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def at(year: Int, month: Int, date: Int, hour: Int, minute: Int, zone: TimeZone): TimePoint = {
    at(year, month, date, hour, minute, 0, 0, zone)
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def at(year: Int, month: MonthOfYear, date: DayOfMonth, hour: Int, minute: Int, second: Int,
         millisecond: Int, zone: TimeZone): TimePoint = {
    at(year, month.value, date.value, hour, minute, second, millisecond, zone)
  }

  /**世界標準時における、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param amPm 午前午後を表す文字列("AM", "PM"など)
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数{@code hour}の値が0〜11の範囲ではない場合
   * @throws IllegalArgumentException 引数{@code amPm}の値が {@code "AM"} または {@code "PM"} ではない場合
   */
  def at12hr(year: Int, month: Int, date: Int, hour: Int, amPm: String, minute: Int, second: Int,
             millisecond: Int) = {
    at(year, month, date, HourOfDay.convertTo24hour(hour, amPm), minute, second, millisecond, GMT)
  }

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param amPm 午前午後を表す文字列("AM", "PM"など)
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数{@code hour}の値が0〜11の範囲ではない場合
   * @throws IllegalArgumentException 引数{@code amPm}の値が {@code "AM"} または {@code "PM"} ではない場合
   * @throws IllegalArgumentException 引数{@code zone}に{@code null}を与えた場合
   */
  def at12hr(year: Int, month: Int, date: Int, hour: Int, amPm: String, minute: Int, second: Int,
             millisecond: Int, zone: TimeZone): TimePoint =
    at(year, month, date, HourOfDay.convertTo24hour(hour, amPm), minute, second, millisecond, zone);

  /**世界標準時における、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @return [[TimePoint]]
   */
  def atGMT(year: Int, month: Int, date: Int, hour: Int, minute: Int): TimePoint =
    atGMT(year, month, date, hour, minute, 0, 0)

  /**世界標準時における、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @return [[TimePoint]]
   */
  def atGMT(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int): TimePoint =
    atGMT(year, month, date, hour, minute, second, 0)

  /**世界標準時における、指定した日時を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param hour 時
   * @param minute 分
   * @param second 秒
   * @param millisecond ミリ秒
   * @return [[TimePoint]]
   */
  def atGMT(year: Int, month: Int, date: Int, hour: Int, minute: Int, second: Int, millisecond: Int): TimePoint =
    at(year, month, date, hour, minute, second, millisecond, GMT);

  /**指定したタイムゾーンにおける、指定した日時の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param calendarDate 日付
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def atMidnight(calendarDate: CalendarDate, zone: TimeZone): TimePoint =
    at(calendarDate.asCalendarMonth,
      calendarDate.breachEncapsulationOfDay, 0, 0, 0, 0, zone)

  /**指定したタイムゾーンにおける、指定した日付の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def atMidnight(year: Int, month: Int, date: Int, zone: TimeZone): TimePoint =
    at(year, month, date, 0, 0, 0, 0, zone)

  /**世界標準時における、指定した日付の午前0時（深夜）を表すインスタンスを取得する。
   *
   * @param year 年
   * @param month 月（1〜12）
   * @param date 日
   * @return [[TimePoint]]
   */
  def atMidnightGMT(year: Int, month: Int, date: Int): TimePoint =
    atMidnight(year, month, date, GMT)

  /**[[Calendar]]を[[TimePoint]]に変換する。
   *
   * @param calendar 元となる日時情報を表す [[Calendar]]インスタンス
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def from(calendar: Calendar): TimePoint =
    from(calendar.getTime)

  /**指定したタイムゾーンにおける、指定した日時を表すインスタンスを取得する。
   *
   * @param date 日付
   * @param time 時間
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def from(date: CalendarDate, time: TimeOfDay, zone: TimeZone): TimePoint =
    at(date.asCalendarMonth, date.breachEncapsulationOfDay,
      time.breachEncapsulationOfHour.value, time.breachEncapsulationOfMinute.value,
      0, 0, zone)

  /**[[Date]]を[[TimePoint]]に変換する。
   *
   * @param javaDate 元となる日時情報を表す [[Date]]インスタンス
   * @return [[TimePoint]]
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def from(javaDate: JDate): TimePoint =
    from(javaDate.getTime)

  /**エポックからの経過ミリ秒を [[TimePoint]] に変換する。
   *
   * @param milliseconds エポックからの経過ミリ秒
   * @return [[TimePoint]]
   */
  def from(milliseconds: Long): TimePoint = {
    val result = new TimePoint(milliseconds);
    //assert FAR_FUTURE == null || result.isBefore(FAR_FUTURE);
    //assert FAR_PAST == null || result.isAfter(FAR_PAST);
    result
  }

  /**日時を表す文字列を、指定したパターンで指定したタイムゾーンとして解析し、その日時を表す [[TimePoint]]を返す。
   *
   * @param dateTimeString 日時を表す文字列
   * @param pattern 解析パターン
   * @param zone タイムゾーン
   * @return [[TimePoint]]
   * @throws ParseException 文字列の解析に失敗した場合
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def parse(dateTimeString: String, pattern: String, timeZone: TimeZone) = {
    val sdf = new SimpleDateFormat(pattern)
    sdf.setTimeZone(timeZone)
    val date = sdf.parse(dateTimeString)
    from(date)
  }

  /**日時を表す文字列を、指定したパターンで世界標準時として解析し、その日時を表す [[TimePoint]]を返す。
   *
   * @param dateString 日時を表す文字列
   * @param pattern 解析パターン
   * @return [[TimePoint]]
   * @throws ParseException 文字列の解析に失敗した場合
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def parseGMTFrom(dateTimeString: String, pattern: String) =
    parse(dateTimeString, pattern, GMT)

}