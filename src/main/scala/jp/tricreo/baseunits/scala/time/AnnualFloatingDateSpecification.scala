package jp.tricreo.baseunits.scala.time

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/19
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */

class AnnualFloatingDateSpecification private[time]
(private[time] val month: Int,
 private[time] val dayOfWeek: DayOfWeek,
 private[time] val occurrence: Int)
  extends AnnualDateSpecification {
  require(1 <= month && month <= 12);
	require(1 <= occurrence && occurrence <= 5);

  override def isSatisfiedBy(date:CalendarDate) =
		ofYear(date.asCalendarMonth.breachEncapsulationOfYear).equals(date)

	override def ofYear(year:Int) = {
		val firstOfMonth = CalendarDate.from(year, month, 1)
		val dayOfWeekOffset = dayOfWeek.value - firstOfMonth.dayOfWeek.value
		val dateOfFirstOccurrenceOfDayOfWeek = dayOfWeekOffset + (if(dayOfWeekOffset < 0) 8 else 1)
		val date = ((occurrence - 1) * 7) + dateOfFirstOccurrenceOfDayOfWeek
		CalendarDate.from(year, month, date)
	}
}