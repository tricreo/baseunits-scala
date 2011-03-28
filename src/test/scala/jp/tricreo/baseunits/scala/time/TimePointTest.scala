package jp.tricreo.baseunits.scala.time

import org.scalatest.FunSuite
import java.util.{TimeZone, Calendar, Date => JDate}
import jp.tricreo.baseunits.scala.time.TimePoint._

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/03/28
 * Time: 23:20
 * To change this template use File | Settings | File Templates.
 */

class TimePointTest extends FunSuite {

  test("インスタンスを生成する"){
    val tp = TimePoint(0L)
    assert(tp == TimePoint(0L))

    val date = new JDate
    val tp2 = TimePoint(date)
    assert(tp2 == TimePoint(date))

    val calendar = Calendar.getInstance
    val tp3 = TimePoint(calendar)
    assert(tp3 == TimePoint(calendar))


    val tp4 = TimePoint(TimePoint.GMT, Year(2010), Month(1), Date(1), Hour(1), Min(1))
    assert(tp4 == TimePoint(TimePoint.GMT, Year(2010), Month(1), Date(1), Hour(1), Min(1)))
  }

}