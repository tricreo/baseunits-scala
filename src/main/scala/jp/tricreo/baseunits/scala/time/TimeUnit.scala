package jp.tricreo.baseunits.scala.time

import java.util.Calendar

object TimeUnitType extends Enumeration {
  val millisecond = Value("millisecond")
  val second = Value("second")
  val minute = Value("minute")
  val hour = Value("hour")
  val day = Value("day")
  val week = Value("week")
  val month = Value("month")
  val quarter = Value("month")
  val year = Value("year")
}

object TimeUnitConversionFactor extends Enumeration {
  val identical = Value(1, "identical")
  val millisecondsPerSecond = Value(1000, "millisecondsPerSecond")
  val millisecondsPerMinute = Value(60 * millisecondsPerSecond.id, "millisecondsPerMinute")
  val millisecondsPerHour = Value(60 * millisecondsPerMinute.id, "millisecondsPerHour")
  val millisecondsPerDay = Value(24 * millisecondsPerHour.id, "millisecondsPerDay")
  val millisecondsPerWeek = Value(7 * millisecondsPerDay.id, "millisecondsPerWeek")
  val monthsPerQuarter = Value(3, "monthsPerQuarter")
  val monthsPerYear = Value(12, "monthsPerYear")
}

/**時間の単位を表す列挙型。
 */
object TimeUnit extends Enumeration {

  class TimeUnitValue
  (i: Int, name: String,
   val timeUnitType: TimeUnitType.Value,
   val timeUnitBaseType: TimeUnitType.Value,
   val timeUnitConversionFactor: TimeUnitConversionFactor.Value)
    extends Value {
    nextId = i + 1

    def id = i


    override def equals(other: Any): Boolean = other match {
      case that: TimeUnitValue => super.equals(that) &&
        timeUnitType == that.timeUnitType &&
        timeUnitBaseType == that.timeUnitBaseType &&
        timeUnitConversionFactor == that.timeUnitConversionFactor
      case _ => false
    }

    override def toString: String = name

    /**この単位で表される値を、指定した単位に変換できるかどうかを検証する。
     *
     * <p>例えば、分単位はミリ秒単位に変換できるが、四半期単位は（一ヶ月の長さが毎月異なるため）日単位に変換できない。</p>
     *
     * @param other 変換先単位
     * @return 変換できる場合は{@code true}、そうでない場合は{@code false}
     * @throws IllegalArgumentException 引数に{@code null}を与えた場合
     */
    def isConvertibleTo(other: TimeUnitValue): Boolean =
      timeUnitBaseType == other.timeUnitBaseType

    /**この単位で表される値を、ミリ秒単位に変換できるかどうかを検証する。
     *
     * <p>例えば、分単位はミリ秒単位に変換できるが、四半期単位は（一ヶ月の長さが毎月異なるため）ミリ秒単位に変換できない。</p>
     *
     * @return 変換できる場合は{@code true}、そうでない場合は{@code false}
     */
    def isConvertibleToMilliseconds = isConvertibleTo(TimeUnit.millisecond)

    /**この単位の計数の基数とすることができる最小の単位を取得する。
     *
     * <p>例えば、分単位はミリ秒単位で計数できるが、四半期単位は（一ヶ月の長さが毎月異なるため）月単位までしか計数できない。</p>
     *
     * @return この単位の計数の基数とすることができる最小の単位
     */
    def baseUnit: TimeUnitValue =
      if (timeUnitBaseType == TimeUnitType.millisecond) TimeUnit.millisecond else TimeUnit.month

    /**この単位から変換可能な全ての単位を含み、大きい単位から降順にソートした配列を取得する。
     *
     * @return この単位から変換可能な全ての単位を含み、大きい単位から降順にソートした配列
     */
    def descendingUnits =
      if (isConvertibleToMilliseconds) TimeUnit.DESCENDING_MS_BASED else TimeUnit.DESCENDING_MONTH_BASED

    /**この単位から変換可能な単位のうち、しばしば表示に利用する単位を、大きい単位から降順にソートした配列を取得する。
     *
     * @return この単位から変換可能な全ての単位のうち、しばしば表示に利用する単位を、大きい単位から降順にソートした配列
     */
    def descendingUnitsForDisplay =
      if (isConvertibleToMilliseconds) DESCENDING_MS_BASED_FOR_DISPLAY else DESCENDING_MONTH_BASED_FOR_DISPLAY

    def getFactor = timeUnitConversionFactor.id

    def javaCalendarConstantForBaseType = {
      if (timeUnitBaseType == TimeUnitType.millisecond) {
        Calendar.MILLISECOND;
      } else if (timeUnitBaseType == TimeUnitType.month) {
        Calendar.MONTH;
      } else 0
    }

    /**
     * この単位から変換可能な単位のうち、現在の単位より一つ小さい単位を取得する。
     *
     * @return この単位から変換可能な単位のうち、現在の単位より一つ小さい単位
     */
    def nextFinerUnit = {
      val descending = descendingUnits
      var index = -1
      for (i <- 0 until descending.length) {
        if (descending(i) == this) {
          index = i
        }
      }
      if (index == descending.length - 1) {
        None
      } else {
        Some(descending(index + 1))
      }
    }

    def toString(quantity: Long) = {
      val buffer = new StringBuffer
      buffer.append(quantity)
      buffer.append(" ")
      buffer.append(timeUnitType.toString)
      buffer.append(if (quantity == 1) "" else "s")
      buffer.toString
    }


  }


  def TimeUnitValue(name: String, timeUnitType: TimeUnitType.Value,
                    timeUnitBaseType: TimeUnitType.Value,
                    timeUnitConversionFactor: TimeUnitConversionFactor.Value) = new TimeUnitValue(nextId, name, timeUnitType, timeUnitBaseType, timeUnitConversionFactor)


  def DESCENDING_MS_BASED = Array(
    week,
    day,
    hour,
    minute,
    second,
    millisecond
  )

  def DESCENDING_MS_BASED_FOR_DISPLAY = Array(
    day,
    hour,
    minute,
    second,
    millisecond
  )

  def DESCENDING_MONTH_BASED = Array(
    year,
    quarter,
    month
  )

  def DESCENDING_MONTH_BASED_FOR_DISPLAY = Array(
    year,
    month
  )


  /**ミリ秒単位 */
  val millisecond = TimeUnitValue("millisecond", TimeUnitType.millisecond, TimeUnitType.millisecond, TimeUnitConversionFactor.identical)

  /**秒単位 */
  val second = TimeUnitValue("second", TimeUnitType.second, TimeUnitType.millisecond, TimeUnitConversionFactor.millisecondsPerSecond)

  /**分単位 */
  val minute = TimeUnitValue("minute", TimeUnitType.minute, TimeUnitType.millisecond, TimeUnitConversionFactor.millisecondsPerMinute)

  /**時単位 */
  val hour = TimeUnitValue("hour", TimeUnitType.hour, TimeUnitType.millisecond, TimeUnitConversionFactor.millisecondsPerHour)

  /**日単位 */
  val day = TimeUnitValue("day", TimeUnitType.day, TimeUnitType.millisecond, TimeUnitConversionFactor.millisecondsPerDay)

  /**週単位 */
  val week = TimeUnitValue("week", TimeUnitType.week, TimeUnitType.millisecond, TimeUnitConversionFactor.millisecondsPerWeek)

  /**月単位 */
  val month = TimeUnitValue("month", TimeUnitType.month, TimeUnitType.month, TimeUnitConversionFactor.identical)

  /**四半期単位 */
  val quarter = TimeUnitValue("quarter", TimeUnitType.quarter, TimeUnitType.month, TimeUnitConversionFactor.monthsPerQuarter)

  /**年単位 */
  val year = TimeUnitValue("year", TimeUnitType.year, TimeUnitType.month, TimeUnitConversionFactor.monthsPerYear)

}
