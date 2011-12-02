/*
 * Copyright 2011 Sisioh Project and the Others.
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
package org.sisioh.baseunits.scala.money

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test

/**`Allotment`のテストクラス。
 */
class AllotmentTest extends AssertionsForJUnit {
  /**等価性検証。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test01_Equals {
    val abc123dollars = Allotment("ABC", Money.dollars(1.23))
    assert(abc123dollars == Allotment("ABC", Money.dollars(1.23)))
    assert(abc123dollars != Allotment("ABC", Money.euros(1.23)))
    assert(abc123dollars != Allotment("XYZ", Money.dollars(1.23)))
    assert(abc123dollars != Allotment("ABC", Money.dollars(1.24)))
  }

  /**正負転換検証。
   *
   * @throws Exception 例外が発生した場合
   */
  @Test
  def test02_Negated {
    val abc123dollars = Allotment("ABC", Money.dollars(1.23))
    assert(abc123dollars.negated == Allotment("ABC", Money.dollars(-1.23)))
  }

}