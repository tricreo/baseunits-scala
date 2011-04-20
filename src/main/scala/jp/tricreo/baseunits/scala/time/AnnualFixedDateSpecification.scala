package jp.tricreo.baseunits.scala.time

/**毎年X月Y日、を表す日付仕様。
 */
class AnnualFixedDateSpecification private[time]
(private[time] val month: MonthOfYear,
 private[time] val day: DayOfMonth)
  extends AnnualDateSpecification {

  override def isSatisfiedBy(date:CalendarDate) = {
		day == date.breachEncapsulationOfDay &&
      month == date.asCalendarMonth.breachEncapsulationOfMonth
	}

	override def ofYear(year:Int) = CalendarDate.from(year, month, day)

	override def toString = day.toString + " " + month.toString

}