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
package jp.tricreo.baseunits.scala.money

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test

/**`Proration`のテストクラス。
 */
class ProrationTest extends AssertionsForJUnit {
  /**
   * {@link Proration#proratedOver(Money, long[])}
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Allocate1 {
    val proportions = Array(1L, 1L)
    val result = Proration.proratedOver(Money.dollars(0.01), proportions)
    assert(result(0) == Money.dollars(0.01))
    assert(result(1) == Money.dollars(0))
  }

  /**
   * {@link Proration#proratedOver(Money, long[])}
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_ProrateOver2 {
    val proportions = Array(3, 7)
    val result = Proration.proratedOver(Money.dollars(0.05), proportions)
    assert(result(0) == Money.dollars(0.02))
    assert(result(1) == Money.dollars(0.03))
  }

  /**
   * {@link Proration#proratedOver(Money, long[])}
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_ProrateOver10 {
    val proportions = Array(17, 2, 1, 35, 35, 10)
    val result = Proration.proratedOver(Money.dollars(0.10), proportions)
    assert(result(0) == Money.dollars(0.02))
    assert(result(1) == Money.dollars(0.01))
    assert(result(2) == Money.dollars(0.00))
    assert(result(3) == Money.dollars(0.03))
    assert(result(4) == Money.dollars(0.03))
    assert(result(5) == Money.dollars(0.01))
    val sum = result.foldLeft(Money.dollars(0.0))(_.plus(_))
    assert(sum == Money.dollars(0.10))
  }

  /**
   * {@link Proration#proratedOver(Money, long[])}
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_ProrateZeroTotal {
    val proportions = Array(3, 7)
    val result = Proration.proratedOver(Money.dollars(0), proportions)
    assert(result(0) == Money.dollars(0))
    assert(result(1) == Money.dollars(0))
  }

  /**
   * {@link Proration#dividedEvenlyIntoParts(Money, int)}
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_ProrateTotalIndivisibleBy3 {
    val actual = Proration.dividedEvenlyIntoParts(Money.dollars(100), 3)
    val expected = Array(Money.dollars(33.34), Money.dollars(33.33), Money.dollars(33.33))
    for (i <- 0 until expected.length) {
      assert(actual(i) == expected(i))
    }
  }

  /**
   * {@link Proration#dividedEvenlyIntoParts(Money, int)}
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test06_ProrateOnlyOneShortOfEven {
    val prorated = Proration.dividedEvenlyIntoParts(Money.dollars(1.09), 10)
    for (i <- 0 until 9) {
      assert(prorated(i) == Money.dollars(0.11))
    }
    assert(prorated(9) == Money.dollars(0.10))
  }

  /**
   * {@link Proration#distributeRemainderOver(Money[], Money)}のテスト。（内部API）
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test07_DistributeRemainder {
    val startingValues = new Array[Money](4)
    startingValues(0) = Money.dollars(1.00)
    startingValues(1) = Money.dollars(2.00)
    startingValues(2) = Money.dollars(3.00)
    startingValues(3) = Money.dollars(4.00)
    val result = Proration.distributeRemainderOver(startingValues, Money.dollars(0.02))
    assert(result(0) == Money.dollars(1.01))
    assert(result(1) == Money.dollars(2.01))
    assert(result(2) == Money.dollars(3.00))
    assert(result(3) == Money.dollars(4.00))
  }

  /**
   * {@link Proration#sum(Money[])}
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test08_SumMoney {
    val startingValues = new Array[Money](4)
    startingValues(0) = Money.dollars(1.00)
    startingValues(1) = Money.dollars(2.00)
    startingValues(2) = Money.dollars(3.00)
    startingValues(3) = Money.dollars(4.00)
    assert(Proration.sum(startingValues) == Money.dollars(10.00))
  }

  /**
   * {@link Proration#partOfWhole(Money, long, long)}
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test09_PartOfWhole {
    val total = Money.dollars(10.00)
    val portion = 3L;
    val whole = 9L;
    assert(Proration.partOfWhole(total, portion, whole) == Money.dollars(3.33))
  }
}