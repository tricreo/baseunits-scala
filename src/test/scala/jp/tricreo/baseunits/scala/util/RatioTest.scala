/*
 * Copyright 2011 Tricreo Inc and the Others.
 * lastModified : 2011/04/22
 *
 * This file is part of Tricreo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.tricreo.baseunits.scala.util

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test

/**`Ratio`のテストクラス。
 */
class RatioTest extends AssertionsForJUnit {

  /**`BigDecimal`で構成する`Ratio`の挙動テスト。
   *
   * <ul>
   *   <li>`3/2`であらわす割合について、小数点第1位までで丸めなかった場合は1.5である。</li>
   *   <li>`10/3`であらわす割合について、小数点第3位までで切り捨てた場合は3.333である。</li>
   *   <li>`10/3`であらわす割合について、小数点第3位までで切り上げた場合は3.334である。</li>
   *   <li>`9.001/3`であらわす割合（3.00033…）について、小数点第6位までで切り上げた場合は3.000334である。</li>
   *   <li>`9.001/3`であらわす割合（3.00033…）について、小数点第7位までで切り上げた場合は3.0003334である。</li>
   *   <li>`9.001/3`であらわす割合（3.00033…）について、小数点第7位までで四捨五入した場合は3.0003333である。</li>
   * </ul>
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_BigDecimalRatio {
    val r3over2 = Ratio(BigDecimal(3), BigDecimal(2))
    var result = r3over2.decimalValue(1, BigDecimal.RoundingMode.UNNECESSARY)
    assert(result == BigDecimal("1.5"))

    val r10over3 = Ratio(BigDecimal(10), BigDecimal(3))
    result = r10over3.decimalValue(3, BigDecimal.RoundingMode.DOWN)
    assert(result == BigDecimal("3.333"))

    result = r10over3.decimalValue(3, BigDecimal.RoundingMode.UP)
    assert(result == BigDecimal("3.334"))

    val rManyDigits = Ratio(BigDecimal("9.001"), BigDecimal(3))
    result = rManyDigits.decimalValue(6, BigDecimal.RoundingMode.UP)
    assert(result == BigDecimal("3.000334"))

    result = rManyDigits.decimalValue(7, BigDecimal.RoundingMode.UP)
    assert(result == BigDecimal("3.0003334"))

    result = rManyDigits.decimalValue(7, BigDecimal.RoundingMode.HALF_UP)
    assert(result == BigDecimal("3.0003333"))

    try {
      Ratio(0, 0)
      fail()
    } catch {
      case e: ArithmeticException => // success
      case _ => fail()
    }
    try {
      Ratio(10, 0)
      fail()
    } catch {
      case e: ArithmeticException => // success
      case _ => fail()
    }
  }

  /**`Long`で構成する`Ratio`の挙動テスト。
   *
   * `9001/3000`であらわす割合（3.00033…）について、小数点第6位までで切り上げた場合は3.000334である。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_LongRatio {
    val rManyDigits = Ratio(9001L, 3000L)
    val result = rManyDigits.decimalValue(6, BigDecimal.RoundingMode.UP)
    assert(result == BigDecimal("3.000334"))
  }

  /**`Ratio(BigDecimal)`のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_FractionalRatio {
    val ratio = Ratio(BigDecimal("3.14159"))
    assert(ratio.breachEncapsulationOfDenominator == BigDecimal(1))
    assert(ratio.breachEncapsulationOfNumerator == BigDecimal("3.14159"))
    assert(ratio.decimalValue(5, BigDecimal.RoundingMode.UNNECESSARY) == BigDecimal("3.14159"))
  }

  /**`Ratio#equals(Any)``のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_Equals {
    val r = Ratio(100, 200)
    assert(r.equals(r) == true)

    assert(Ratio(100, 200) == Ratio(100, 200))
    assert(Ratio(100, 200) == Ratio(100, 200))
    assert(Ratio(BigDecimal("100"), BigDecimal("200")) == Ratio(100, 200))
    assert(Ratio(BigDecimal("100"), BigDecimal("200")) == Ratio(100, 200))
    assert(Ratio(BigDecimal("100"), BigDecimal("200")) != Ratio(101, 200))
    assert(Ratio(BigDecimal("100"), BigDecimal("200")) != Ratio(100, 201))

    assert(new Ratio(BigDecimal("100"), BigDecimal("200")) == (r))

    assert(Ratio(100, 200).hashCode == Ratio(100, 200).hashCode)
    assert(Ratio(101, 200).hashCode != Ratio(100, 200).hashCode)
    assert(Ratio(100, 201).hashCode != Ratio(100, 200).hashCode)
    assert(Ratio(100, 200).hashCode != Ratio(10, 20).hashCode)

    assert(Ratio(100, 200).reduce == Ratio(10, 20).reduce)
  }

  /**`Ratio#times(BigDecimal)`のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_MultiplyNumerator {
    val rManyDigits = Ratio(9001, 3000)
    val product = rManyDigits.times(BigDecimal("1.1"))
    assert(product == Ratio(BigDecimal("9901.1"), BigDecimal(3000)))
  }

  /**`Ratio#times(Ratio)`のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_MultiplyByRatio {
    val r1 = Ratio(9001, 3000)
    val r2 = Ratio(3, 2)
    val expectedProduct = Ratio(27003, 6000)
    assert(r1.times(r2) == expectedProduct)
  }

  /**`Ratio#toString`のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_toString {
    assert(Ratio(100, 200).toString == "100/200")
    assert(Ratio(10, 20).toString == "10/20")
  }
}