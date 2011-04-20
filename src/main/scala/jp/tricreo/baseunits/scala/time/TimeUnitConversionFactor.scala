package jp.tricreo.baseunits.scala.time

@serializable
private[time] final class TimeUnitConversionFactor
(val value: Int)
  extends Ordered[TimeUnitConversionFactor] {
  def compare(that: TimeUnitConversionFactor) = value - that.value
}

private[time] object TimeUnitConversionFactor {
  val identical = new TimeUnitConversionFactor(1)
  val millisecondsPerSecond = new TimeUnitConversionFactor(1000)
  val millisecondsPerMinute = new TimeUnitConversionFactor(60 * millisecondsPerSecond.value)
  val millisecondsPerHour = new TimeUnitConversionFactor(60 * millisecondsPerMinute.value)
  val millisecondsPerDay = new TimeUnitConversionFactor(24 * millisecondsPerHour.value)
  val millisecondsPerWeek = new TimeUnitConversionFactor(7 * millisecondsPerDay.value)
  val monthsPerQuarter = new TimeUnitConversionFactor(3)
  val monthsPerYear = new TimeUnitConversionFactor(12)

  private val values = List(identical, millisecondsPerSecond, millisecondsPerMinute, millisecondsPerHour,
    millisecondsPerDay, millisecondsPerWeek, monthsPerQuarter, monthsPerYear)

  def apply(value: Int) = values.find(_.value == value).get

  def unapply(timeUnitConversionFactor: TimeUnitConversionFactor) = Some(timeUnitConversionFactor.value)

}