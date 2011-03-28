package jp.tricreo.baseunits.scala.money

import org.scalatest.FunSuite
import java.math.MathContext

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/03/28
 * Time: 20:07
 * To change this template use File | Settings | File Templates.
 */

class MoneyTest extends FunSuite {

  test("演算子のテスト"){

    val m = Money(10)

    println(m)

    assert(m == Money(10))
    assert(m == 10)
    assert(m <= Money(10))
    assert(m <= 10)
    assert(m <= Money(20))
    assert(m <= 20)
    assert(m < Money(20))
    assert(m < 20)

    assert(m >= Money(10))
    assert(m >= 10)
    assert(m >= Money(0))
    assert(m >= 0)
    assert(m > Money(0))
    assert(m > 0)
  }

  test("加算のテスト"){
    val m = Money(10)
    val result = m + Money(5)
    assert(result == Money(15))

    val result2 = m + 5
    assert(result2 == 15)
  }

  test("減算のテスト"){
    val m = Money(10)
    val result = m - Money(5)
    assert(result == Money(5))

    val result2 = m - 5
    assert(result2 == 5)
  }

  test("乗算のテスト"){
    val m = Money(10)
    val result = m * Money(5)
    assert(result == Money(50))

    val result2 = m * 5
    assert(result2 == 50)
  }

  test("除算のテスト"){
    val m = Money(10)
    val result = m / Money(5)
    assert(result == Money(2))

    val result2 = m / 5
    assert(result2 == 2)
  }

  test("ドルの生成"){
    val d = Money.dollars(10)
    assert(d == Money(10, Money.USD))
  }

  test("ユーロの生成"){
    val e = Money.euros(10)
    assert(e == Money(10, Money.EUR))
  }

  test("円の生成"){
    val y = Money.yes(10)
    assert(y == Money(10, Money.JPY))
  }

}