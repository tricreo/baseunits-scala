package jp.tricreo.baseunits.scala.time

import org.scalatest.FunSuite
import java.util.{Calendar, Date => JDate}
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test


/**`TimePoint`のテストクラス。
 */
class TimePointTest extends AssertionsForJUnit {

  @Test
  def test {
    val tp = TimePoint.from(0L)
    assert(tp == TimePoint.from(0L))

    val date = new JDate
    val tp2 = TimePoint.from(date)
    assert(tp2 == TimePoint.from(date))

    val calendar = Calendar.getInstance
    val tp3 = TimePoint.from(calendar)
    assert(tp3 == TimePoint.from(calendar))


    val tp4 = TimePoint.at(2010, 1, 1, 1, 1, TimePoint.GMT)
    assert(tp4 == TimePoint.at(2010, 1, 1, 1, 1, TimePoint.GMT))
  }

}