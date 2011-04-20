package jp.tricreo.baseunits.scala.time

object Shifter extends Enumeration {
  val Next, Prev = Value

  implicit def toObject(enum: Shifter.Value) = enum match {
    case Next => NextShifter()
    case Prev => PrevShifter()
  }

  implicit def toEnum(shifter: Shifter) = shifter match {
    case NextShifter() => Shifter.Next
    case PrevShifter() => Shifter.Prev
  }
}

sealed trait Shifter {
  def shift(date: CalendarDate, cal: BusinessCalendar): CalendarDate
}

case class NextShifter extends Shifter {
  def shift(date: CalendarDate, cal: BusinessCalendar) =
    cal.nearestNextBusinessDay(date)
}

case class PrevShifter extends Shifter {
  def shift(date: CalendarDate, cal: BusinessCalendar) =
    cal.nearestPrevBusinessDay(date)
}

/**
 * 指定日が非営業日の場合のシフト戦略。
 */
class MonthlyFixedBusinessDateSpecification
(val day: DayOfMonth,
 val shifter: Shifter.Value,
 val cal: BusinessCalendar) extends MonthlyDateSpecification {

  def ofYearMonth(month: CalendarMonth) =
    Some(shifter.shift(CalendarDate.from(month.breachEncapsulationOfYear,
      month.breachEncapsulationOfMonth, day), cal))

  override def isSatisfiedBy(date: CalendarDate) =
    if (cal.isBusinessDay(date)) {
      val thisMonth = ofYearMonth(date.breachEncapsulationOfYearMonth)
      thisMonth.equals(date)
    } else false

}