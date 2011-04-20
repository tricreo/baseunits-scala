package jp.tricreo.baseunits.scala.time

import java.util.Calendar

/**
 * 1週間の中の特定の「曜日」を表す列挙型。
 *
 * <p>タイムゾーンの概念はない。</p>
 */
final class DayOfWeek private[time]
(private[time] val value: Int,
 private[time] val name: String) {
  def breachEncapsulationOfValue = value
  override def toString = name
}

object DayOfWeek {
  val Sunday = new DayOfWeek(Calendar.SUNDAY, "SUNDAY")
  val Monday = new DayOfWeek(Calendar.MONDAY, "MONDAY")
  val Tuesday = new DayOfWeek(Calendar.TUESDAY, "TUESDAY")
  val Wednesday = new DayOfWeek(Calendar.WEDNESDAY, "WEDNESDAY")
  val Thursday = new DayOfWeek(Calendar.THURSDAY, "THURSDAY")
  val Friday = new DayOfWeek(Calendar.FRIDAY, "FRIDAY")
  val Saturday = new DayOfWeek(Calendar.SATURDAY, "SATURDAY")

  private val values: List[DayOfWeek] = List(Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday)

  def apply(value: Int) = values.find(_.value == value).get

  def unapply(dayOfWeek:DayOfWeek) = Some(dayOfWeek.value, dayOfWeek.name)
}
