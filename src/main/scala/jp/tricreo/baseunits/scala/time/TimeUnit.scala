package jp.tricreo.baseunits.scala.time

import java.util.Calendar
import java.lang.String

final class TimeUnit
(val id: Int,
 val valueType: TimeUnit.Type,
 val valueBaseType: TimeUnit.Type,
 val factor: TimeUnitConversionFactor)
  extends Ordered[TimeUnit] {

  /**
   * この単位で表される値を、指定した単位に変換できるかどうかを検証する。
   *
   * <p>例えば、分単位はミリ秒単位に変換できるが、四半期単位は（一ヶ月の長さが毎月異なるため）日単位に変換できない。</p>
   *
   * @param other 変換先単位
   * @return 変換できる場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isConvertibleTo(other: TimeUnit) = valueBaseType == other.valueBaseType

  /**
   * この単位で表される値を、ミリ秒単位に変換できるかどうかを検証する。
   *
   * <p>例えば、分単位はミリ秒単位に変換できるが、四半期単位は（一ヶ月の長さが毎月異なるため）ミリ秒単位に変換できない。</p>
   *
   * @return 変換できる場合は{@code true}、そうでない場合は{@code false}
   */
  def isConvertibleToMilliseconds = isConvertibleTo(TimeUnit.millisecond)

  override def toString = valueType.name

  /**
   * この単位の計数の基数とすることができる最小の単位を取得する。
   *
   * <p>例えば、分単位はミリ秒単位で計数できるが、四半期単位は（一ヶ月の長さが毎月異なるため）月単位までしか計数できない。</p>
   *
   * @return この単位の計数の基数とすることができる最小の単位
   */
  def baseUnit: TimeUnit =
    if (valueBaseType == TimeUnit.Type.millisecond) TimeUnit.millisecond else TimeUnit.month

  /**
   * この単位から変換可能な全ての単位を含み、大きい単位から降順にソートした配列を取得する。
   *
   * @return この単位から変換可能な全ての単位を含み、大きい単位から降順にソートした配列
   */
  def descendingUnits =
    if (isConvertibleToMilliseconds) TimeUnit.DESCENDING_MS_BASED else TimeUnit.DESCENDING_MONTH_BASED

  /**
   * この単位から変換可能な単位のうち、しばしば表示に利用する単位を、大きい単位から降順にソートした配列を取得する。
   *
   * @return この単位から変換可能な全ての単位のうち、しばしば表示に利用する単位を、大きい単位から降順にソートした配列
   */
  def descendingUnitsForDisplay =
    if (isConvertibleToMilliseconds) TimeUnit.DESCENDING_MS_BASED_FOR_DISPLAY else TimeUnit.DESCENDING_MONTH_BASED_FOR_DISPLAY;

  private[time] def getFactor = factor.value

  private[time] def javaCalendarConstantForBaseType = {
    if (valueBaseType == TimeUnit.Type.millisecond) {
      Calendar.MILLISECOND
    } else if (valueBaseType == TimeUnit.Type.month) {
      Calendar.MONTH
    } else 0
  }

  /**
   * この単位から変換可能な単位のうち、現在の単位より一つ小さい単位を取得する。
   *
   * @return この単位から変換可能な単位のうち、現在の単位より一つ小さい単位
   */
  def nextFinerUnit = {
    val descending = descendingUnits
    var index = -1;
    for (i <- 0 until descending.length) {
      if (descending(i) == this) {
        index = i
      }
    }
    if (index == descending.length - 1) None
    else descending(index + 1)
  }

  private[time] def toString(quantity: Long) = {
    val buffer = new StringBuffer
    buffer.append(quantity)
    buffer.append(" ")
    buffer.append(valueType.name)
    buffer.append(if (quantity == 1) "" else "s")
    buffer.toString
  }

  def compare(that: TimeUnit): Int = id - that.id
}

object TimeUnit {

  final class Type(val value: Int, val name: String) {
    override def equals(obj: Any): Boolean = obj match {
      case that: Type => value == that.value && name == that.name
      case _ => false
    }
    override def toString: String = name
    override def hashCode: Int = value.hashCode + name.hashCode
  }

  object Type {

    val millisecond = Type(1, "millisecond")
    val second = Type(2, "second")
    val minute = Type(3, "minute")
    val hour = Type(4, "hour")
    val day = Type(5, "day")
    val week = Type(6, "week")
    val month = Type(7, "month")
    val quarter = Type(8, "quarter")
    val year = Type(9, "year")

    private val values = List(Type.millisecond,
                              Type.second,
                              Type.minute,
                              Type.hour,
                              Type.day,
                              Type.week,
                              Type.month,
                              Type.year)

    def apply(value: Int):Type = values.find(_.value == value).get
    def apply(value:Int, name:String):Type = new Type(value, name)

    def unapply(timeUnitType: Type) = Some(timeUnitType.value, timeUnitType.toString)
  }

  private var nextId = 0

  /**ミリ秒単位 */
  val millisecond = new TimeUnit(nextId, Type.millisecond, Type.millisecond, TimeUnitConversionFactor.identical)
  nextId += 1

  /**秒単位 */
  val second = new TimeUnit(nextId, Type.second, Type.millisecond, TimeUnitConversionFactor.millisecondsPerSecond)
  nextId += 1

  /**分単位 */
  val minute = new TimeUnit(nextId, Type.minute, Type.millisecond, TimeUnitConversionFactor.millisecondsPerMinute)
  nextId += 1

  /**時単位 */
  val hour = new TimeUnit(nextId, Type.hour, Type.millisecond, TimeUnitConversionFactor.millisecondsPerHour)
  nextId += 1

  /**日単位 */
  val day = new TimeUnit(nextId, Type.day, Type.millisecond, TimeUnitConversionFactor.millisecondsPerDay)
  nextId += 1

  /**週単位 */
  val week = new TimeUnit(nextId, Type.week, Type.millisecond, TimeUnitConversionFactor.millisecondsPerWeek)
  nextId += 1

  /**月単位 */
  val month = new TimeUnit(nextId, Type.month, Type.month, TimeUnitConversionFactor.identical)
  nextId += 1

  /**四半期単位 */
  val quarter = new TimeUnit(nextId, Type.quarter, Type.month, TimeUnitConversionFactor.monthsPerQuarter)
  nextId += 1

  /**年単位 */
  val year = new TimeUnit(nextId, Type.year, Type.month, TimeUnitConversionFactor.monthsPerYear);
  nextId += 1

  private val DESCENDING_MS_BASED = List(
    week,
    day,
    hour,
    minute,
    second,
    millisecond
  )

  private val DESCENDING_MS_BASED_FOR_DISPLAY = List(
    day,
    hour,
    minute,
    second,
    millisecond
  )

  private val DESCENDING_MONTH_BASED = List(
    year,
    quarter,
    month
  )

  private val DESCENDING_MONTH_BASED_FOR_DISPLAY = List(
    year,
    month
  )

}