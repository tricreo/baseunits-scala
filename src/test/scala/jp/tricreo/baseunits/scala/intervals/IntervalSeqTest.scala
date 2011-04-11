package jp.tricreo.baseunits.scala.intervals

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import collection.mutable.ListBuffer

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/05
 * Time: 10:12
 * To change this template use File | Settings | File Templates.
 */

class IntervalSeqTest extends AssertionsForJUnit {

  private val c5_10c = Interval.closed(Limit(5), Limit(10))

  private val o10_12c = Interval.over(Limit(10), false, Limit(12), true)

  private val o11_20c = Interval.over(Limit(11), false, Limit(20), true)

  private val o12_20o = Interval.open(Limit(12), Limit(20))

  private val c20_25c = Interval.closed(Limit(20), Limit(25))

  private val o25_30c = Interval.over(Limit(25), false, Limit(30), true)

  private val o30_35o = Interval.open(Limit(30), Limit(35))

  private val o11_12c = Interval.over(Limit(11), false, Limit(12), true)

  private val c20_20c = Interval.closed(Limit(20), Limit(20))

  private val _o18 = Interval.under(Limit(18))

  private val empty = Interval.closed(Limit(0), Limit(0))

  private val all = Interval.open(Limitless[Int], Limitless[Int])

  /**
   * {@link IntervalSequence#extent()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_Extent {
    val intervals = ListBuffer.empty[Interval[Int]]
    intervals += c5_10c
    intervals += o10_12c
    intervals += c20_25c

    val intervalSequence1 = IntervalSeq(intervals.result)
    assert(intervalSequence1.extent == Interval.closed(Limit(5), Limit(25)))

    intervals += _o18
    val intervalSequence2 = IntervalSeq(intervals.result)
    assert(intervalSequence2.extent == Interval.closed(Limitless[Int], Limit(25)))

    intervals += all
    val intervalSequence3 = IntervalSeq(intervals.result)
    assert(intervalSequence3.extent == all)

    //		for (seq <- variousSequences()) {
    //			seq.add(c5_10c);
    //			seq.add(o10_12c);
    //			seq.add(c20_25c);
    //			assertThat(seq.extent(), is(Interval.isClosed(5, 25)));
    //
    //			seq.add(_o18);
    //			assertThat(seq.extent(), is(Interval.isClosed(null, 25)));
    //
    //			seq.add(all);
    //			assertThat(seq.extent(), is(all));
    //		}

  }

  /**
   * {@link IntervalSequence#gaps()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_Gaps {
    var intervalSeq = IntervalSeq[Int]()
    intervalSeq :+= c5_10c
    intervalSeq :+= o10_12c
    intervalSeq :+= c20_25c
    intervalSeq :+= o30_35o

    val it = intervalSeq.gaps.iterator
    assert(it.hasNext == true)
    assert(it.next == o12_20o)
    assert(it.hasNext == true)
    assert(it.next == o25_30c)
    assert(it.hasNext == false)
    try {
    it.next();
    fail("Should throw NoSuchElementException");
    } catch {
    case e:NoSuchElementException =>
    // success
    case _ => fail()
    }
  }

}