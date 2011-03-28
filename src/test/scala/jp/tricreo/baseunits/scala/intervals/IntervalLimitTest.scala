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
    assert(IntervalLimit.lower(false, Some(10)) == IntervalLimit.lower(false, Some(10)))
    assert(IntervalLimit.lower(true, Some(10)) != IntervalLimit.lower(false, Some(10)))
    assert(IntervalLimit.lower(false, Some(10)) != IntervalLimit.lower(true, Some(10)))
    assert(IntervalLimit.lower(true, Some(10)) == IntervalLimit.lower(true, Some(10)))

    assert(IntervalLimit.upper(false, Some(10)) == IntervalLimit.upper(false, Some(10)))
    assert(IntervalLimit.upper(true, Some(10)) != IntervalLimit.upper(false, Some(10)))
    assert(IntervalLimit.upper(false, Some(10)) != IntervalLimit.upper(true, Some(10)))
    assert(IntervalLimit.upper(true, Some(10)) == IntervalLimit.upper(true, Some(10)))

    assert(IntervalLimit.lower(false, Some(10)) != IntervalLimit.upper(false, Some(10)))
    assert(IntervalLimit.lower(true, Some(10)) != IntervalLimit.upper(false, Some(10)))
    assert(IntervalLimit.lower(false, Some(10)) != IntervalLimit.upper(true, Some(10)))
    assert(IntervalLimit.lower(true, Some(10)) != IntervalLimit.upper(true, Some(10)))

    assert(IntervalLimit.upper(false, Some(10)) != IntervalLimit.lower(false, Some(10)))
    assert(IntervalLimit.upper(true, Some(10)) != IntervalLimit.lower(false, Some(10)))
    assert(IntervalLimit.upper(false, Some(10)) != IntervalLimit.lower(true, Some(10)))
    assert(IntervalLimit.upper(true, Some(10)) != IntervalLimit.lower(true, Some(10)))

    assert(IntervalLimit(false, false, Some(1)) == IntervalLimit(false, false, Some(1)))

		assert(IntervalLimit.lower(false, Some(10)) == IntervalLimit.lower(false, Some(10)))
		assert(IntervalLimit(false, true, Some(10)) == IntervalLimit.lower(false, Some(10)))

  }

  test("インスタンスを比較する") {

  }
}