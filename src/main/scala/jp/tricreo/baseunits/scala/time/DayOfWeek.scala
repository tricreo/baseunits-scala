package jp.tricreo.baseunits.scala.time

import java.util.Calendar

/**
 * 1週間の中の特定の「曜日」を表す列挙型。
 *
 * <p>タイムゾーンの概念はない。</p>
 */
@serializable
final class DayOfWeek private[time]
(private[time] val value: Int,
 private[time] val name: String) {
  /**このオブジェクトの{@link #value}フィールド（{@link Calendar}に定義する曜日をあらわす定数値）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return {@link Calendar}に定義する曜日をあらわす定数値（{@link Calendar#SUNDAY}〜{@link Calendar#SATURDAY}）
   */
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

  private val values = List(Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday)

  def apply(value: Int) = values.find(_.value == value).get

  def unapply(dayOfWeek: DayOfWeek) = Some(dayOfWeek.value, dayOfWeek.name)
}
