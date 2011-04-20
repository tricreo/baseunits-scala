package jp.tricreo.baseunits.scala.time


class MonthlyFloatingDateSpecification private[time]
(private[time] val dayOfWeek: DayOfWeek,
 private[time] val occurrence: Int)
  extends MonthlyDateSpecification {

  require(1 <= occurrence && occurrence <= 5)

  override def isSatisfiedBy(date:CalendarDate) =
		ofYearMonth(date.asCalendarMonth) == date

	override def ofYearMonth(month:CalendarMonth) = {
		val firstOfMonth = CalendarDate.from(month, DayOfMonth(1))
		val dayOfWeekOffset = dayOfWeek.value - firstOfMonth.dayOfWeek.value
		val dateOfFirstOccurrenceOfDayOfWeek = dayOfWeekOffset + (if(dayOfWeekOffset < 0) 8 else 1)
		val date = ((occurrence - 1) * 7) + dateOfFirstOccurrenceOfDayOfWeek
		Some(CalendarDate.from(month, DayOfMonth(date)))
	}

}