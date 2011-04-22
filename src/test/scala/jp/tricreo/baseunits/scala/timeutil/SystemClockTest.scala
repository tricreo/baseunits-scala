package jp.tricreo.baseunits.scala.timeutil

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import jp.tricreo.baseunits.scala.time.TimePoint
import java.util.Date
import scala.math._

/**`SystemClock`のテストクラス。
 * @author j5ik2o
 */
class SystemClockTest extends AssertionsForJUnit {
  /**[[SystemClock]]のテスト。
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_SystemClockTimeSource {
    // The following calls allow polymorphic substitution of TimeSources
    // either in applications or, more often, in testing.
    val source = SystemClock
    val expectedNow = TimePoint.from(new Date).breachEncapsulationOfMillisecondsFromEpoc
    val now = source.now.breachEncapsulationOfMillisecondsFromEpoc

    // タイミングによって成功しない、微妙なテスト…。
    assert(abs((now.toDouble - expectedNow)) <= 50)
  }
}