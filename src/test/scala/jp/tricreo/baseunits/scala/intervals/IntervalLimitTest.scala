package jp.tricreo.baseunits.scala.intervals

import org.scalatest.FunSuite

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/03/29
 * Time: 0:58
 * To change this template use File | Settings | File Templates.
 */

class IntervalLimitTest extends FunSuite {
  test("インスタンスを生成する") {
    assert(IntervalLimit.lower(false, LimitValue(10)) == IntervalLimit.lower(false, LimitValue(10)))
    assert(IntervalLimit.lower(true, LimitValue(10)) != IntervalLimit.lower(false, LimitValue(10)))
    assert(IntervalLimit.lower(false, LimitValue(10)) != IntervalLimit.lower(true, LimitValue(10)))
    assert(IntervalLimit.lower(true, LimitValue(10)) == IntervalLimit.lower(true, LimitValue(10)))

    assert(IntervalLimit.upper(false, LimitValue(10)) == IntervalLimit.upper(false, LimitValue(10)))
    assert(IntervalLimit.upper(true, LimitValue(10)) != IntervalLimit.upper(false, LimitValue(10)))
    assert(IntervalLimit.upper(false, LimitValue(10)) != IntervalLimit.upper(true, LimitValue(10)))
    assert(IntervalLimit.upper(true, LimitValue(10)) == IntervalLimit.upper(true, LimitValue(10)))

    assert(IntervalLimit.lower(false, LimitValue(10)) != IntervalLimit.upper(false, LimitValue(10)))
    assert(IntervalLimit.lower(true, LimitValue(10)) != IntervalLimit.upper(false, LimitValue(10)))
    assert(IntervalLimit.lower(false, LimitValue(10)) != IntervalLimit.upper(true, LimitValue(10)))
    assert(IntervalLimit.lower(true, LimitValue(10)) != IntervalLimit.upper(true, LimitValue(10)))

    assert(IntervalLimit.upper(false, LimitValue(10)) != IntervalLimit.lower(false, LimitValue(10)))
    assert(IntervalLimit.upper(true, LimitValue(10)) != IntervalLimit.lower(false, LimitValue(10)))
    assert(IntervalLimit.upper(false, LimitValue(10)) != IntervalLimit.lower(true, LimitValue(10)))
    assert(IntervalLimit.upper(true, LimitValue(10)) != IntervalLimit.lower(true, LimitValue(10)))

    assert(IntervalLimit(false, false, LimitValue(1)) == IntervalLimit(false, false, LimitValue(1)))

		assert(IntervalLimit.lower(false, LimitValue(10)) == IntervalLimit.lower(false, LimitValue(10)))
		assert(IntervalLimit(false, true, LimitValue(10)) == IntervalLimit.lower(false, LimitValue(10)))
//    assert(new IntervalLimit(false, true, Some(10)){ } != IntervalLimit.lower(false, Some(10)))
  }

  test("インスタンスを比較する") {
    val lowerInf = IntervalLimit.lower(false, LimitlessValue)
    val upperInf = IntervalLimit.upper(false, LimitlessValue)
    val lowerOpen2 = IntervalLimit.lower(false, LimitValue(2))
    val lowerClose2 = IntervalLimit.lower(true, LimitValue(2))
    val lowerOpen3 = IntervalLimit.lower(false, LimitValue(3))
    val lowerClose3 = IntervalLimit.lower(true, LimitValue(3))
    val upperOpen5 = IntervalLimit.upper(false, LimitValue(5))
    val upperClose5 = IntervalLimit.upper(true, LimitValue(5))
    val upperOpen6 = IntervalLimit.upper(false, LimitValue(6))
    val upperClose6 = IntervalLimit.upper(true, LimitValue(6))

     // 無限同士比較
		assert(lowerInf < upperInf)
		assert(upperInf > lowerInf)
  }
}