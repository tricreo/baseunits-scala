package jp.tricreo.baseunits.scala.time

import java.util.{Date => JDate}
import java.util.{Calendar, TimeZone}
import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/03/28
 * Time: 21:52
 * To change this template use File | Settings | File Templates.
 */

/**ミリ秒精度で、ある時間の一点をあらわすクラス。
 * <p>タイムゾーンを持っている。</p>
 */
class TimePoint(private val millisecondsFromEpoc: Long) extends Ordered[TimePoint]{

  override def toString = "TimePoint(%d)".format(millisecondsFromEpoc)

  override def equals(obj: Any) = obj match {
    case that:TimePoint => this.millisecondsFromEpoc == that.millisecondsFromEpoc
    case _ => false
  }

  override def hashCode = millisecondsFromEpoc.hashCode

  def compare(that: TimePoint) =
    millisecondsFromEpoc compare that.millisecondsFromEpoc

}

object TimePoint {

  case class Year(value: Int)

  case class Month(value: Int)

  case class Date(value: Int)

  case class Hour(value: Int)

  case class Min(value: Int)

  case class Second(value: Int)

  case class MilliSecond(value: Int)

  val GMT = TimeZone.getTimeZone("Universal")

  def apply(milliseconds: Long) = new TimePoint(milliseconds)

  def apply(date: JDate) = new TimePoint(date.getTime)

  def apply(calendar: Calendar) = new TimePoint(calendar.getTime.getTime)

  def apply(timeZone: TimeZone, year: Year, month: Month,
            date: Date, hour: Hour,
            minute: Min, second: Second = Second(0),
            millisecond: MilliSecond = MilliSecond(0)) = {
    val calendar = Calendar.getInstance(timeZone)
    calendar.set(Calendar.YEAR, year.value)
    calendar.set(Calendar.MONTH, month.value - 1)
    calendar.set(Calendar.DATE, date.value)
    calendar.set(Calendar.HOUR_OF_DAY, hour.value)
    calendar.set(Calendar.MINUTE, minute.value)
    calendar.set(Calendar.SECOND, second.value)
    calendar.set(Calendar.MILLISECOND, millisecond.value)
    new TimePoint(calendar.getTime.getTime)
  }

  def atGMT(year: Year, month: Month,
            date: Date, hour: Hour,
            minute: Min, second: Second = Second(0),
            millisecond: MilliSecond = MilliSecond(0)) =
    apply(GMT, year, month, date, hour, minute, second, millisecond)

  def parse(dateTimeString:String, pattern:String, timeZone:TimeZone) ={
    val sdf = new SimpleDateFormat(pattern)
    sdf.setTimeZone(timeZone)
    val date = sdf.parse(dateTimeString)
    apply(date)
  }

  def parseGMTFrom(dateTimeString:String, pattern:String) = {
    parse(dateTimeString, pattern, GMT)
  }
}