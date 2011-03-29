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
    assert(IntervalLimit.lower(false, 10) == IntervalLimit.lower(false, 10))
    assert(IntervalLimit.lower(true, 10) != IntervalLimit.lower(false, 10))
    assert(IntervalLimit.lower(false, 10) != IntervalLimit.lower(true, 10))
    assert(IntervalLimit.lower(true, 10) == IntervalLimit.lower(true, 10))

    assert(IntervalLimit.upper(false, 10) == IntervalLimit.upper(false, 10))
    assert(IntervalLimit.upper(true, 10) != IntervalLimit.upper(false, 10))
    assert(IntervalLimit.upper(false, 10) != IntervalLimit.upper(true, 10))
    assert(IntervalLimit.upper(true, 10) == IntervalLimit.upper(true, 10))

    assert(IntervalLimit.lower(false, 10) != IntervalLimit.upper(false, 10))
    assert(IntervalLimit.lower(true, 10) != IntervalLimit.upper(false, 10))
    assert(IntervalLimit.lower(false, 10) != IntervalLimit.upper(true, 10))
    assert(IntervalLimit.lower(true, 10) != IntervalLimit.upper(true, 10))

    assert(IntervalLimit.upper(false, 10) != IntervalLimit.lower(false, 10))
    assert(IntervalLimit.upper(true, 10) != IntervalLimit.lower(false, 10))
    assert(IntervalLimit.upper(false, 10) != IntervalLimit.lower(true, 10))
    assert(IntervalLimit.upper(true, 10) != IntervalLimit.lower(true, 10))

    assert(IntervalLimit(false, false, 1) == IntervalLimit(false, false, 1))

		assert(IntervalLimit.lower(false, 10) == IntervalLimit.lower(false, 10))
		assert(IntervalLimit(false, true, 10) == IntervalLimit.lower(false, 10))
//    assert(new IntervalLimit(false, true, Some(10)){ } != IntervalLimit.lower(false, Some(10)))
  }

  test("インスタンスを比較する") {
    val lowerInf = IntervalLimitless.lower(false)
    val upperInf = IntervalLimitless.upper(false)
    val lowerOpen2 = IntervalLimit.lower(false, 2)
    val lowerClose2 = IntervalLimit.lower(true, 2)
    val lowerOpen3 = IntervalLimit.lower(false, 3)
    val lowerClose3 = IntervalLimit.lower(true, 3)
    val upperOpen5 = IntervalLimit.upper(false, 5)
    val upperClose5 = IntervalLimit.upper(true, 5)
    val upperOpen6 = IntervalLimit.upper(false, 6)
    val upperClose6 = IntervalLimit.upper(true, 6)


  }
}