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
import collection.mutable.ListBuffer

/**`MoneyFan`のテストクラス。
 */
class MoneyFanTest extends AssertionsForJUnit {
  /**
   * Three roommates are sharing expenses in a house.
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_RoommateExample {
    //apportionment, assignment, attribution, post, assignation, allotment, slice

    var c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Joe", Money.dollars(65.00))
    val electricBill = MoneyFan(c.toSet)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Mary", Money.dollars(650))
    c += Allotment("Jill", Money.dollars(650))
    c += Allotment("Joe", Money.dollars(650))
    val rent = MoneyFan(c.toSet)

    assert(rent.total == Money.dollars(1950))

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Mary", Money.dollars(12))
    c += Allotment("Jill", Money.dollars(344))
    c += Allotment("Joe", Money.dollars(256))
    val groceries = MoneyFan(c.toSet)

    assert(groceries.total == Money.dollars(612))

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Mary", Money.dollars(45.00))
    val internetAccess = MoneyFan(c.toSet)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Joe", Money.dollars(857.00))
    val newSofa = MoneyFan(c.toSet)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Jill", Money.dollars(285.67))
    c += Allotment("Joe", Money.dollars(-285.67))
    val jillReimbursesJoeForSofa = MoneyFan(c.toSet)

    val netSofaContributions = newSofa.plus(jillReimbursesJoeForSofa)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Jill", Money.dollars(285.67))
    c += Allotment("Joe", Money.dollars(571.33))
    val expectedNetSofaContributions = MoneyFan(c.toSet)
    assert(netSofaContributions == expectedNetSofaContributions)

    var c2 = ListBuffer.empty[MoneyFan[String]]
    c2 += electricBill
    c2 += rent
    c2 += groceries
    c2 += internetAccess
    c2 += newSofa
    c2 += jillReimbursesJoeForSofa
    val outlays = FanTally(c2)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Mary", Money.dollars(707))
    c += Allotment("Jill", Money.dollars(1279.67))
    c += Allotment("Joe", Money.dollars(1542.33))
    val expectedNet = MoneyFan(c.toSet)

    assert(outlays.net == expectedNet)

    assert(outlays.total == Money.dollars(3529.00))

    //        Apportionment<String> apportionment = new EqualApportionment<String> ("Joe", "Jill", "Mary")
    //        MoneyFan<String> fairShares = apportionment.(outlays.total(), "Joe", "Jill", "Mary")
    //        // The apportion method could have rounding rule options.
    //        MoneyFan<String> owedToHouse = fairShares.minus(outlays)

  }

  /**{@link MoneyFan#equals(Object)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_Equals {
    var c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Jack", Money.dollars(285.67))
    c += Allotment("Jill", Money.dollars(-285.67))
    val aFan = MoneyFan(c.toSet)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Jill", Money.dollars(-285.67))
    c += Allotment("Jack", Money.dollars(285.67))
    val anotherFan = MoneyFan(c.toSet)

    assert(anotherFan == aFan)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment[String]("Jill", Money.dollars(-285.67))
    c += Allotment[String]("Jack", Money.dollars(285.68))
    val yetAnotherFan = MoneyFan(c.toSet)

    assert(aFan.equals(yetAnotherFan) == false)
  }

  /**
   * {@link MoneyFan#plus(MoneyFan)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test03_Plus {

    val jack17 = MoneyFan(Allotment("Jack", Money.dollars(17)))
    val jill13 = MoneyFan(Allotment("Jill", Money.dollars(13)))
    var c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Jack", Money.dollars(17))
    c += Allotment("Jill", Money.dollars(13))
    val jack17Jill13 = MoneyFan(c.toSet)
    assert(jack17.plus(jill13) == jack17Jill13)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Jack", Money.dollars(34))
    c += Allotment("Jill", Money.dollars(13))
    val jack34Jill13 = MoneyFan(c.toSet)

    assert(jack17.plus(jack17Jill13) == jack34Jill13)

    assert(jack17.plus(jack17Jill13) == jack17Jill13.plus(jack17))
  }

  /**
   * {@link MoneyFan#negated()}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test04_Negation {
    var c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Jack", Money.dollars(285.67))
    c += Allotment("Jill", Money.dollars(-285.67))
    val positive = MoneyFan(c.toSet)

    c = ListBuffer.empty[Allotment[String]]
    c += Allotment("Jack", Money.dollars(-285.67))
    c += Allotment("Jill", Money.dollars(285.67))
    val negative = MoneyFan(c.toSet)

    assert(positive.negated == negative)
  }

  /**
   * {@link MoneyFan#minus(MoneyFan)}のテスト。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test05_Minus {
    val jack = MoneyFan(Allotment("Jack", Money.dollars(285.67)))
    val lessJack = MoneyFan(Allotment("Jack", Money.dollars(10.00)))
    val differenceJack = MoneyFan(Allotment("Jack", Money.dollars(275.67)))

    assert(jack.minus(lessJack) == differenceJack)
    assert(jack.minus(jack) == MoneyFan[String])
  }
}