package jp.tricreo.baseunits.scala.time

/**毎月Y日、を表す日付仕様。
 */
class MonthlyFixedDateSpecification private[time]
(private[time] val day: DayOfMonth)
  extends MonthlyDateSpecification {

  override def isSatisfiedBy(date: CalendarDate) =
    day == date.breachEncapsulationOfDay

  override def ofYearMonth(month: CalendarMonth) =
    CalendarDate.from(month.breachEncapsulationOfYear,
      month.breachEncapsulationOfMonth, day)

}