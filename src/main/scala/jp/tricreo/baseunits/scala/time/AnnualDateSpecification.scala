package jp.tricreo.baseunits.scala.time

import jp.tricreo.baseunits.scala.intervals.Limit

/**1年間に1度だけ仕様を満たす日付仕様。
 * @author j5ik2o
 */
abstract class AnnualDateSpecification extends DateSpecification {

  override def firstOccurrenceIn(interval: CalendarInterval) = {
    val firstTry = ofYear(interval.start.toLimitObject.asCalendarMonth.breachEncapsulationOfYear)
    if (interval.includes(Limit(firstTry))) {
      Some(firstTry)
    } else {
      val secondTry = ofYear(interval.start.toLimitObject.asCalendarMonth.breachEncapsulationOfYear + 1);
      if (interval.includes(Limit(secondTry))) {
        Some(secondTry)
      } else None
    }
  }

  override def iterateOver(interval:CalendarInterval) = {
    new Iterator[CalendarDate] {

      var _next = firstOccurrenceIn(interval)

      var year = _next match{
        case Some(o) => o.asCalendarMonth.breachEncapsulationOfYear
        case None => -1
      }

      override def hasNext = _next != None

      override def next = {
        if (hasNext == false) {
          throw new NoSuchElementException
        }
        val current = _next
        year += 1
        _next = Some(AnnualDateSpecification.this.ofYear(year))
        if (interval.includes(Limit(_next.get)) == false){
          _next = None
        }
        current.get
      }
    }
  }

  /**
   * 指定した年においてこの日付仕様を満たす年月日を返す。
   *
   * @param year 西暦年をあらわす数
   * @return {@link CalendarDate}
   */
  def ofYear(year: Int): CalendarDate
}