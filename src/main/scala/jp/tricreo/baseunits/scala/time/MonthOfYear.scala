package jp.tricreo.baseunits.scala.time

import java.util.{GregorianCalendar, Calendar}

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/19
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
sealed class MonthOfYear
(/**1 based: January = 1, February = 2, ... */
 private[time] val value: Int,

 /**その月の最終日 */
 private[time] val lastDayOfThisMonth: DayOfMonth,

 /**{@link Calendar}に定義する月をあらわす定数値 */
 private[time] val calendarValue: Int) {

  /**
	 * このオブジェクトの{@link #calendarValue}フィールド（{@link Calendar}に定義する月をあらわす定数値）を返す。
	 *
	 * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
	 *
	 * @return {@link Calendar}に定義する月をあらわす定数値（{@link Calendar#JANUARY}〜{@link Calendar#DECEMBER}）
	 */
	def breachEncapsulationOfCalendarValue = calendarValue;

	/**
	 * このオブジェクトの{@link #value}フィールド（月をあらわす数 1〜12）を返す。
	 *
	 * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
	 *
	 * @return 月をあらわす数（1〜12）
	 */
	def breachEncapsulationOfValue = value

	/**
	 * 指定した日 {@code other} が、このオブジェクトが表現する日よりも過去であるかどうかを検証する。
	 *
	 * <p>{@code other} が {@code null} である場合と、お互いが同一日時である場合は {@code false} を返す。</p>
	 *
	 * @param other 対象日時
	 * @return 過去である場合は{@code true}、そうでない場合は{@code false}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	def isAfter(other:MonthOfYear) = isBefore(other) == false && equals(other) == false;

	/**
	 * 指定した日 {@code other} が、このオブジェクトが表現する日よりも未来であるかどうかを検証する。
	 *
	 * <p>{@code other} が {@code null} である場合と、お互いが同一日時である場合は {@code false} を返す。</p>
	 *
	 * @param other 対象日
	 * @return 未来である場合は{@code true}、そうでない場合は{@code false}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	def isBefore( other:MonthOfYear) = value < other.value

//	public DayOfYear at(DayOfMonth month) {
//		// ...
//	}

	/**
	 * 指定した年の、この月を表す年月を返す。
	 *
	 * @param year 年
	 * @return 年月
	 */
	def on(year:Int):CalendarMonth = CalendarMonth.from(year, this);

	/**
	 * その月の最終日を取得する。
	 *
	 * @param year 該当年. 2月の閏年判定に関わらない場合は、何でも良い。
	 * @return 最終日
	 */
	private[time] def getLastDayOfThisMonth(year:Int) = lastDayOfThisMonth;

}

object MonthOfYear {
  /**January */
  val Jan = new MonthOfYear(1, DayOfMonth(31), Calendar.JANUARY)

  /**Feburary */
  val Feb = new MonthOfYear(2, null, Calendar.FEBRUARY) {
    override def getLastDayOfThisMonth(year: Int): DayOfMonth = {
      val calendar = new GregorianCalendar(year, 2, 1);
      if (calendar.isLeapYear(year)) DayOfMonth(29) else DayOfMonth(28)
    }
  }
  /**March */
  val Mar = new MonthOfYear(3, DayOfMonth(31), Calendar.MARCH)

  /**April */
  val Apr = new MonthOfYear(4, DayOfMonth(30), Calendar.APRIL)

  /**May */
  val May = new MonthOfYear(5, DayOfMonth(31), Calendar.MAY)

  /**June */
  val Jun = new MonthOfYear(6, DayOfMonth(30), Calendar.JUNE)

  /**July */
  val Jul = new MonthOfYear(7, DayOfMonth(31), Calendar.JULY)

  /**August */
  val Aug = new MonthOfYear(8, DayOfMonth(31), Calendar.AUGUST)

  /**September */
  val Sep = new MonthOfYear(9, DayOfMonth(30), Calendar.SEPTEMBER)

  /**October */
  val Oct = new MonthOfYear(10, DayOfMonth(31), Calendar.OCTOBER)

  /**November */
  val Nov = new MonthOfYear(11, DayOfMonth(30), Calendar.NOVEMBER)

  /**December */
  val Dec = new MonthOfYear(12, DayOfMonth(31), Calendar.DECEMBER)

  private val values:List[MonthOfYear] = List(Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec)

  def apply(value:Int):MonthOfYear = values.find(_.value == value).get

}