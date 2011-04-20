package jp.tricreo.baseunits.scala.timeutil

import jp.tricreo.baseunits.scala.time.{TimePoint, TimeSource}

/**システム時計に基づき、現在の時刻を返すクラス。
 */
object SystemClock extends TimeSource {
  def now = TimePoint.from(System.currentTimeMillis())
}