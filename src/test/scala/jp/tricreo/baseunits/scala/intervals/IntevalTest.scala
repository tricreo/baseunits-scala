package jp.tricreo.baseunits.scala.intervals

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import collection.mutable.ListBuffer
import util.Random

class IntevalTest extends AssertionsForJUnit {

  val c5_10c = Interval.closed(Limit(BigDecimal(5)), Limit(BigDecimal(10)))

  val c1_10c = Interval.closed(Limit(BigDecimal(1)), Limit(BigDecimal(10)))

  val c4_6c = Interval.closed(Limit(BigDecimal(4)), Limit(BigDecimal(6)))

  val c5_15c = Interval.closed(Limit(BigDecimal(5)), Limit(BigDecimal(15)))

  val c12_16c = Interval.closed(Limit(BigDecimal(12)), Limit(BigDecimal(16)))

  val o10_12c = Interval.over(Limit(BigDecimal(10)), false, Limit(BigDecimal(12)), true)

  val o1_1c = Interval.over(Limit(BigDecimal(1)), false, Limit(BigDecimal(1)), true)

  val c1_1o = Interval.over(Limit(BigDecimal(1)), true, Limit(BigDecimal(1)), false)

  val c1_1c = Interval.over(Limit(BigDecimal(1)), true, Limit(BigDecimal(1)), true)

  val o1_1o = Interval.over(Limit(BigDecimal(1)), false, Limit(BigDecimal(1)), false)

  val _2o = Interval.over(Limitless[BigDecimal], true, Limit(BigDecimal(2)), false)

  val o9_ = Interval.over(Limit(BigDecimal(9)), false, Limitless[BigDecimal], true)

  val empty = Interval.open(Limit(BigDecimal(1)), Limit(BigDecimal(1)))

  val all = Interval.closed(Limitless[BigDecimal], Limitless[BigDecimal])


  def newIntegerIntervalList = {
    val list = ListBuffer.empty[Interval[Int]]

    // 開区間
    list += Interval.over(Limit(0), false, Limit(25), false)
    list += Interval.over(Limit(0), false, Limit(50), false)
    list += Interval.over(Limit(0), false, Limit(75), false)
    list += Interval.over(Limit(0), false, Limit(100), false)
    list += Interval.over(Limit(25), false, Limit(50), false)
    list += Interval.over(Limit(25), false, Limit(75), false)
    list += Interval.over(Limit(25), false, Limit(100), false)
    list += Interval.over(Limit(50), false, Limit(75), false)
    list += Interval.over(Limit(50), false, Limit(100), false)
    list += Interval.over(Limit(75), false, Limit(100), false)

    // 半開区間
    list += Interval.over(Limit(0), true, Limit(25), false)
    list += Interval.over(Limit(0), true, Limit(50), false)
    list += Interval.over(Limit(0), true, Limit(75), false)
    list += Interval.over(Limit(0), true, Limit(100), false)
    list += Interval.over(Limit(25), true, Limit(50), false)
    list += Interval.over(Limit(25), true, Limit(75), false)
    list += Interval.over(Limit(25), true, Limit(100), false)
    list += Interval.over(Limit(50), true, Limit(75), false)
    list += Interval.over(Limit(50), true, Limit(100), false)
    list += Interval.over(Limit(75), true, Limit(100), false)

    list += Interval.over(Limit(0), false, Limit(25), true)
    list += Interval.over(Limit(0), false, Limit(50), true)
    list += Interval.over(Limit(0), false, Limit(75), true)
    list += Interval.over(Limit(0), false, Limit(100), true)
    list += Interval.over(Limit(25), false, Limit(50), true)
    list += Interval.over(Limit(25), false, Limit(75), true)
    list += Interval.over(Limit(25), false, Limit(100), true)
    list += Interval.over(Limit(50), false, Limit(75), true)
    list += Interval.over(Limit(50), false, Limit(100), true)
    list += Interval.over(Limit(75), false, Limit(100), true)

    // 閉区間
    list += Interval.over(Limit(0), true, Limit(25), true)
    list += Interval.over(Limit(0), true, Limit(50), true)
    list += Interval.over(Limit(0), true, Limit(75), true)
    list += Interval.over(Limit(0), true, Limit(100), true)
    list += Interval.over(Limit(25), true, Limit(50), true)
    list += Interval.over(Limit(25), true, Limit(75), true)
    list += Interval.over(Limit(25), true, Limit(100), true)
    list += Interval.over(Limit(50), true, Limit(75), true)
    list += Interval.over(Limit(50), true, Limit(100), true)
    list += Interval.over(Limit(75), true, Limit(100), true)

    // single point
    list += Interval.over(Limit(0), true, Limit(0), false)
    list += Interval.over(Limit(0), false, Limit(0), true)
    list += Interval.over(Limit(0), true, Limit(0), true)
    list += Interval.over(Limit(25), true, Limit(25), false)
    list += Interval.over(Limit(25), false, Limit(25), true)
    list += Interval.over(Limit(25), true, Limit(25), true)
    list += Interval.over(Limit(50), true, Limit(50), false)
    list += Interval.over(Limit(50), false, Limit(50), true)
    list += Interval.over(Limit(50), true, Limit(50), true)
    list += Interval.over(Limit(75), true, Limit(75), false)
    list += Interval.over(Limit(75), false, Limit(75), true)
    list += Interval.over(Limit(75), true, Limit(75), true)
    list += Interval.over(Limit(100), true, Limit(100), false)
    list += Interval.over(Limit(100), false, Limit(100), true)
    list += Interval.over(Limit(100), true, Limit(100), true)

    // empty
    list += Interval.over(Limit(0), false, Limit(0), false)
    list += Interval.over(Limit(25), false, Limit(25), false)
    list += Interval.over(Limit(50), false, Limit(50), false)
    list += Interval.over(Limit(75), false, Limit(75), false)
    list += Interval.over(Limit(100), false, Limit(100), false)

    // 下側限界のみ区間
    list += Interval.over(Limit(0), false, Limitless[Int], false)
    list += Interval.over(Limit(0), true, Limitless[Int], false)
    list += Interval.over(Limit(25), false, Limitless[Int], false)
    list += Interval.over(Limit(25), true, Limitless[Int], false)
    list += Interval.over(Limit(50), false, Limitless[Int], false)
    list += Interval.over(Limit(50), true, Limitless[Int], false)
    list += Interval.over(Limit(75), false, Limitless[Int], false)
    list += Interval.over(Limit(75), true, Limitless[Int], false)
    list += Interval.over(Limit(100), false, Limitless[Int], false)
    list += Interval.over(Limit(100), true, Limitless[Int], false)

    // 上側限界のみ区間
    list += Interval.over(Limitless[Int], false, Limit(0), false)
    list += Interval.over(Limitless[Int], false, Limit(0), true)
    list += Interval.over(Limitless[Int], false, Limit(25), false)
    list += Interval.over(Limitless[Int], false, Limit(25), true)
    list += Interval.over(Limitless[Int], false, Limit(50), false)
    list += Interval.over(Limitless[Int], false, Limit(50), true)
    list += Interval.over(Limitless[Int], false, Limit(75), false)
    list += Interval.over(Limitless[Int], false, Limit(75), true)
    list += Interval.over(Limitless[Int], false, Limit(100), false)
    list += Interval.over(Limitless[Int], false, Limit(100), true)

    // freedom
    list += Interval.over(Limitless[Int], false, Limitless[Int], false)

    Random.shuffle(list.result)
  }

  def newIntegerIntervalList2 = {
    val list = ListBuffer.empty[Interval[Int]]

    // 開区間
    list += Interval.over(Limit(0), false, Limit(5), false)
    list += Interval.over(Limit(0), false, Limit(10), false)
    list += Interval.over(Limit(0), false, Limit(15), false)
    list += Interval.over(Limit(0), false, Limit(20), false)
    list += Interval.over(Limit(5), false, Limit(10), false)
    list += Interval.over(Limit(5), false, Limit(15), false)
    list += Interval.over(Limit(5), false, Limit(20), false)
    list += Interval.over(Limit(10), false, Limit(15), false)
    list += Interval.over(Limit(10), false, Limit(20), false)
    list += Interval.over(Limit(15), false, Limit(20), false)

    // 半開区間
    list += Interval.over(Limit(0), true, Limit(5), false)
    list += Interval.over(Limit(0), true, Limit(10), false)
    list += Interval.over(Limit(0), true, Limit(15), false)
    list += Interval.over(Limit(0), true, Limit(20), false)
    list += Interval.over(Limit(5), true, Limit(10), false)
    list += Interval.over(Limit(5), true, Limit(15), false)
    list += Interval.over(Limit(5), true, Limit(20), false)
    list += Interval.over(Limit(10), true, Limit(15), false)
    list += Interval.over(Limit(10), true, Limit(20), false)
    list += Interval.over(Limit(15), true, Limit(20), false)

    list += Interval.over(Limit(0), false, Limit(5), true)
    list += Interval.over(Limit(0), false, Limit(10), true)
    list += Interval.over(Limit(0), false, Limit(15), true)
    list += Interval.over(Limit(0), false, Limit(20), true)
    list += Interval.over(Limit(5), false, Limit(10), true)
    list += Interval.over(Limit(5), false, Limit(15), true)
    list += Interval.over(Limit(5), false, Limit(20), true)
    list += Interval.over(Limit(10), false, Limit(15), true)
    list += Interval.over(Limit(10), false, Limit(20), true)
    list += Interval.over(Limit(15), false, Limit(20), true)

    // 閉区間
    list += Interval.over(Limit(0), true, Limit(5), true)
    list += Interval.over(Limit(0), true, Limit(10), true)
    list += Interval.over(Limit(0), true, Limit(15), true)
    list += Interval.over(Limit(0), true, Limit(20), true)
    list += Interval.over(Limit(5), true, Limit(10), true)
    list += Interval.over(Limit(5), true, Limit(15), true)
    list += Interval.over(Limit(5), true, Limit(20), true)
    list += Interval.over(Limit(10), true, Limit(15), true)
    list += Interval.over(Limit(10), true, Limit(20), true)
    list += Interval.over(Limit(15), true, Limit(20), true)

    // single point
    list += Interval.over(Limit(0), true, Limit(0), false)
    list += Interval.over(Limit(0), false, Limit(0), true)
    list += Interval.over(Limit(0), true, Limit(0), true)
    list += Interval.over(Limit(5), true, Limit(5), false)
    list += Interval.over(Limit(5), false, Limit(5), true)
    list += Interval.over(Limit(5), true, Limit(5), true)
    list += Interval.over(Limit(10), true, Limit(10), false)
    list += Interval.over(Limit(10), false, Limit(10), true)
    list += Interval.over(Limit(10), true, Limit(10), true)
    list += Interval.over(Limit(15), true, Limit(15), false)
    list += Interval.over(Limit(15), false, Limit(15), true)
    list += Interval.over(Limit(15), true, Limit(15), true)
    list += Interval.over(Limit(20), true, Limit(20), false)
    list += Interval.over(Limit(20), false, Limit(20), true)
    list += Interval.over(Limit(20), true, Limit(20), true)

    // empty
    list += Interval.over(Limit(0), false, Limit(0), false)
    list += Interval.over(Limit(5), false, Limit(5), false)
    list += Interval.over(Limit(10), false, Limit(10), false)
    list += Interval.over(Limit(15), false, Limit(15), false)
    list += Interval.over(Limit(20), false, Limit(20), false)

    // 下側限界のみ区間
    list += Interval.over(Limit(0), false, Limitless[Int], false)
    list += Interval.over(Limit(0), true, Limitless[Int], false)
    list += Interval.over(Limit(5), false, Limitless[Int], false)
    list += Interval.over(Limit(5), true, Limitless[Int], false)
    list += Interval.over(Limit(10), false, Limitless[Int], false)
    list += Interval.over(Limit(10), true, Limitless[Int], false)
    list += Interval.over(Limit(15), false, Limitless[Int], false)
    list += Interval.over(Limit(15), true, Limitless[Int], false)
    list += Interval.over(Limit(20), false, Limitless[Int], false)
    list += Interval.over(Limit(20), true, Limitless[Int], false)

    // 上側限界のみ区間
    list += Interval.over(Limitless[Int], false, Limit(0), false)
    list += Interval.over(Limitless[Int], false, Limit(0), true)
    list += Interval.over(Limitless[Int], false, Limit(5), false)
    list += Interval.over(Limitless[Int], false, Limit(5), true)
    list += Interval.over(Limitless[Int], false, Limit(10), false)
    list += Interval.over(Limitless[Int], false, Limit(10), true)
    list += Interval.over(Limitless[Int], false, Limit(15), false)
    list += Interval.over(Limitless[Int], false, Limit(15), true)
    list += Interval.over(Limitless[Int], false, Limit(20), false)
    list += Interval.over(Limitless[Int], false, Limit(20), true)

    // freedom
    list += Interval.over(Limitless[Int], false, Limitless[Int], false)

    Random.shuffle(list.result)
  }

  /**
   * {@link Interval}のインスタンスがシリアライズできるかどうか検証する。
   *
   * @throws Exception 例外が発生した場合
   */
  //	@Test
  //	def test01_Serialization {
  //		SerializationTester.assertCanBeSerialized(c5_10c);
  //	}

  /**
   * {@link Interval}のインスタンス生成テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_Assertions {
    // Redundant, maybe, but with all the compiler default
    // confusion at the moment, I decided to throw this in.
    try {
      Interval.closed(Limit(BigDecimal(2.0)), Limit(BigDecimal(1.0)))
      fail("Lower bound mustn't be above upper bound.");
    } catch {
      case _: IllegalArgumentException => ()
      // success
    }
  }

  /**
   * {@link Interval#upTo(Comparable)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_UpTo {
    val range = Interval.upTo(Limit(5.5D))
    assert(range.includes(Limit(5.5D)) == true)
    assert(range.includes(Limit(-5.5D)) == true)
    assert(range.includes(Limit(Double.NegativeInfinity)) == true)
    assert(range.includes(Limit(5.5001D)) == false)
  }


  /**
   * {@link Interval#andMore(Comparable)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_AndMore {
    val range = Interval.andMore(Limit(5.5D))
    assert(range.includes(Limit(5.5D)) == true)
    assert(range.includes(Limit(5.4999D)) == false)
    assert(range.includes(Limit(-5.5D)) == false)
    assert(range.includes(Limit(Double.PositiveInfinity)) == true)
    assert(range.includes(Limit(5.5001D)) == true)
  }


  /**
   * {@link Interval#newOfSameType(Comparable, boolean, Comparable, boolean)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_AbstractCreation {
    val concrete = new Interval(Limit(1), true, Limit(3), true)
    val newInterval = concrete.newOfSameType(Limit(1), false, Limit(4), false)
    val expected = new Interval(Limit(1), false, Limit(4), false)
    assert(newInterval == expected)
  }

  /**
   * {@link Interval#isBelow(Comparable)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_Below {
    val range = Interval.closed(Limit(BigDecimal(-5.5)), Limit(BigDecimal(6.6)))
    assert(range.below(Limit(BigDecimal(5.0))) == false)
    assert(range.below(Limit(BigDecimal(-5.5))) == false)
    assert(range.below(Limit(BigDecimal(-5.4999))) == false)
    assert(range.below(Limit(BigDecimal(6.6))) == false)
    assert(range.below(Limit(BigDecimal(6.601))) == true)
    assert(range.below(Limit(BigDecimal(-5.501))) == false)
  }

  /**
   * {@link Interval#includes(Comparable)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_Includes {
    val range = Interval.closed(Limit(BigDecimal(-5.5)), Limit(BigDecimal(6.6)))
    assert(range.includes(Limit(BigDecimal(5.0))) == true)
    assert(range.includes(Limit(BigDecimal(-5.5))) == true)
    assert(range.includes(Limit(BigDecimal(-5.4999))) == true)
    assert(range.includes(Limit(BigDecimal(6.6))) == true)
    assert(range.includes(Limit(BigDecimal(6.601))) == false)
    assert(range.includes(Limit(BigDecimal(-5.501))) == false)
  }

  /**
   * {@link Interval}の開閉の境界挙動テスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_OpenInterval {
    val exRange = Interval.over(Limit(BigDecimal(-5.5)), false, Limit(BigDecimal(6.6)), true)
    assert(exRange.includes(Limit(BigDecimal(5.0))) == true)
    assert(exRange.includes(Limit(BigDecimal(-5.5))) == false)
    assert(exRange.includes(Limit(BigDecimal(-5.4999))) == true)
    assert(exRange.includes(Limit(BigDecimal(6.6))) == true)
    assert(exRange.includes(Limit(BigDecimal(6.601))) == false)
    assert(exRange.includes(Limit(BigDecimal(-5.501))) == false)
  }

  /**
   * {@link Interval#isEmpty()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test10_IsEmpty {
    assert(Interval.closed(Limit(5), Limit(6)).empty == false)
    assert(Interval.closed(Limit(6), Limit(6)).empty == false)
    assert(Interval.open(Limit(6), Limit(6)).empty == true)
    assert(c1_10c.emptyOfSameType.empty == true)
  }

  /**
   * {@link Interval#intersects(Interval)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test11_Intersects {
    assert(c5_10c.intersects(c1_10c) == true)
    assert(c1_10c.intersects(c5_10c) == true)
    assert(c4_6c.intersects(c1_10c) == true)
    assert(c1_10c.intersects(c4_6c) == true)
    assert(c5_10c.intersects(c5_15c) == true)
    assert(c5_15c.intersects(c1_10c) == true)
    assert(c1_10c.intersects(c5_15c) == true)

    val check = c1_10c.intersects(c12_16c)
    println(check)

    assert(c1_10c.intersects(c12_16c) == false)
    assert(c12_16c.intersects(c1_10c) == false)
    assert(c5_10c.intersects(c5_10c) == true)
    assert(c1_10c.intersects(o10_12c) == false)
    assert(o10_12c.intersects(c1_10c) == false)

    // ---- 気を取り直して総当たりしてみよう

    assert(c5_10c.intersects(c5_10c) == true)
    assert(c5_10c.intersects(c1_10c) == true)
    assert(c5_10c.intersects(c4_6c) == true)
    assert(c5_10c.intersects(c5_15c) == true)
    assert(c5_10c.intersects(c12_16c) == false)
    assert(c5_10c.intersects(o10_12c) == false)
    assert(c5_10c.intersects(o1_1c) == false)
    assert(c5_10c.intersects(c1_1o) == false)
    assert(c5_10c.intersects(c1_1c) == false)
    assert(c5_10c.intersects(o1_1o) == false)
    assert(c5_10c.intersects(_2o) == false)
    assert(c5_10c.intersects(o9_) == true)
    assert(c5_10c.intersects(empty) == false)
    assert(c5_10c.intersects(all) == true)

    assert(c1_10c.intersects(c5_10c) == true)
    assert(c1_10c.intersects(c1_10c) == true)
    assert(c1_10c.intersects(c4_6c) == true)
    assert(c1_10c.intersects(c5_15c) == true)
    assert(c1_10c.intersects(c12_16c) == false)
    assert(c1_10c.intersects(o10_12c) == false)
    assert(c1_10c.intersects(o1_1c) == true)
    assert(c1_10c.intersects(c1_1o) == true)
    assert(c1_10c.intersects(c1_1c) == true)
    assert(c1_10c.intersects(o1_1o) == false)
    assert(c1_10c.intersects(_2o) == true)
    assert(c1_10c.intersects(o9_) == true)
    assert(c1_10c.intersects(empty) == false)
    assert(c1_10c.intersects(all) == true)

    assert(c4_6c.intersects(c5_10c) == true)
    assert(c4_6c.intersects(c1_10c) == true)
    assert(c4_6c.intersects(c4_6c) == true)
    assert(c4_6c.intersects(c5_15c) == true)
    assert(c4_6c.intersects(c12_16c) == false)
    assert(c4_6c.intersects(o10_12c) == false)
    assert(c4_6c.intersects(o1_1c) == false)
    assert(c4_6c.intersects(c1_1o) == false)
    assert(c4_6c.intersects(c1_1c) == false)
    assert(c4_6c.intersects(o1_1o) == false)
    assert(c4_6c.intersects(_2o) == false)
    assert(c4_6c.intersects(o9_) == false)
    assert(c4_6c.intersects(empty) == false)
    assert(c4_6c.intersects(all) == true)

    assert(c5_15c.intersects(c5_10c) == true)
    assert(c5_15c.intersects(c1_10c) == true)
    assert(c5_15c.intersects(c4_6c) == true)
    assert(c5_15c.intersects(c5_15c) == true)
    assert(c5_15c.intersects(c12_16c) == true)
    assert(c5_15c.intersects(o10_12c) == true)
    assert(c5_15c.intersects(o1_1c) == false)
    assert(c5_15c.intersects(c1_1o) == false)
    assert(c5_15c.intersects(c1_1c) == false)
    assert(c5_15c.intersects(o1_1o) == false)
    assert(c5_15c.intersects(_2o) == false)
    assert(c5_15c.intersects(o9_) == true)
    assert(c5_15c.intersects(empty) == false)
    assert(c5_15c.intersects(all) == true)

    // --- 疲れてきたからあと適当ｗ 総当たり達成ならず。まぁ、大丈夫やろ…。

    assert(c12_16c.intersects(c1_10c) == false)
    assert(o10_12c.intersects(c1_10c) == false)
    assert(o1_1c.intersects(c4_6c) == false)
    assert(c1_1o.intersects(c5_15c) == false)
    assert(c1_1c.intersects(c5_15c) == false)
    assert(o1_1o.intersects(c12_16c) == false)
    assert(empty.intersects(o10_12c) == false)
    assert(all.intersects(o10_12c) == true)
  }


}