package jp.tricreo.baseunits.scala.time

sealed trait Shifter {
  def shift(date: CalendarDate, cal: BusinessCalendar): CalendarDate
}

object Shifter {
  val Prev = PrevShifter
  val Next = NextShifter
}

case class NextShifter extends Shifter {
  def shift(date: CalendarDate, cal: BusinessCalendar) =
    cal.nearestNextBusinessDay(date)
}

case class PrevShifter extends Shifter {
  def shift(date: CalendarDate, cal: BusinessCalendar) =
    cal.nearestPrevBusinessDay(date)
}

/**指定日が非営業日の場合のシフト戦略。
 */
class MonthlyFixedBusinessDateSpecification
(val day: DayOfMonth,
 val shifter: Shifter,
 val cal: BusinessCalendar) extends MonthlyDateSpecification {

  def ofYearMonth(month: CalendarMonth) =
    shifter.shift(CalendarDate.from(month.breachEncapsulationOfYear,
      month.breachEncapsulationOfMonth, day), cal)

  override def isSatisfiedBy(date: CalendarDate) =
    if (cal.isBusinessDay(date)) {
      val thisMonth = ofYearMonth(date.breachEncapsulationOfYearMonth)
      thisMonth.equals(date)
    } else false

}