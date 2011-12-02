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
package org.sisioh.baseunits.scala.intervals

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import collection.mutable.ListBuffer
import util.Random

/**`IntervalLimit`のテストクラス。
 */
class IntervalLimitTest extends AssertionsForJUnit {
  @Test
  def test01_Equals {
    assert(IntervalLimit.lower(false, Limit(10)) == IntervalLimit.lower(false, Limit(10)))
    assert(IntervalLimit.lower(true, Limit(10)) != IntervalLimit.lower(false, Limit(10)))
    assert(IntervalLimit.lower(false, Limit(10)) != IntervalLimit.lower(true, Limit(10)))
    assert(IntervalLimit.lower(true, Limit(10)) == IntervalLimit.lower(true, Limit(10)))

    assert(IntervalLimit.upper(false, Limit(10)) == IntervalLimit.upper(false, Limit(10)))
    assert(IntervalLimit.upper(true, Limit(10)) != IntervalLimit.upper(false, Limit(10)))
    assert(IntervalLimit.upper(false, Limit(10)) != IntervalLimit.upper(true, Limit(10)))
    assert(IntervalLimit.upper(true, Limit(10)) == IntervalLimit.upper(true, Limit(10)))

    assert(IntervalLimit.lower(false, Limit(10)) != IntervalLimit.upper(false, Limit(10)))
    assert(IntervalLimit.lower(true, Limit(10)) != IntervalLimit.upper(false, Limit(10)))
    assert(IntervalLimit.lower(false, Limit(10)) != IntervalLimit.upper(true, Limit(10)))
    assert(IntervalLimit.lower(true, Limit(10)) != IntervalLimit.upper(true, Limit(10)))

    assert(IntervalLimit.upper(false, Limit(10)) != IntervalLimit.lower(false, Limit(10)))
    assert(IntervalLimit.upper(true, Limit(10)) != IntervalLimit.lower(false, Limit(10)))
    assert(IntervalLimit.upper(false, Limit(10)) != IntervalLimit.lower(true, Limit(10)))
    assert(IntervalLimit.upper(true, Limit(10)) != IntervalLimit.lower(true, Limit(10)))

    assert(IntervalLimit(false, false, Limit(1)) == IntervalLimit(false, false, Limit(1)))

    assert(IntervalLimit.lower(false, Limit(10)) == IntervalLimit.lower(false, Limit(10)))
    assert(IntervalLimit(false, true, Limit(10)) == IntervalLimit.lower(false, Limit(10)))
    //assert(new IntervalLimit(false, true, Limit(10)){ } != IntervalLimit.isLower(false, Limit(10)))
  }

  @Test
  def test02_compareTo {
    val lowerInf = IntervalLimit.lower(false, Limitless[Int])
    val upperInf = IntervalLimit.upper(false, Limitless[Int])
    val lowerOpen2 = IntervalLimit.lower(false, Limit(2))
    val lowerClose2 = IntervalLimit.lower(true, Limit(2))
    val lowerOpen3 = IntervalLimit.lower(false, Limit(3))
    val lowerClose3 = IntervalLimit.lower(true, Limit(3))
    val upperOpen5 = IntervalLimit.upper(false, Limit(5))
    val upperClose5 = IntervalLimit.upper(true, Limit(5))
    val upperOpen6 = IntervalLimit.upper(false, Limit(6))
    val upperClose6 = IntervalLimit.upper(true, Limit(6))

    // 無限同士比較
    assert(lowerInf < upperInf)
    assert(upperInf > lowerInf)


    // 無限有限比較
    assert(lowerInf < lowerOpen3)
    assert(lowerInf < lowerClose3)
    assert(lowerInf < upperOpen5)
    assert(lowerInf < upperClose5)
    assert(upperInf > lowerOpen3)
    assert(upperInf > lowerClose3)
    assert(upperInf > upperOpen5)
    assert(upperInf > upperClose5)

    // 有限無限比較（上記セクションとの対象性検証）
    assert(lowerOpen3 > lowerInf)
    assert(lowerClose3 > lowerInf)
    assert(upperOpen5 > lowerInf)
    assert(upperClose5 > lowerInf)
    assert(lowerOpen3 < upperInf)
    assert(lowerClose3 < upperInf)
    assert(upperOpen5 < upperInf)
    assert(upperClose5 < upperInf)

    // 有限比較
    assert(lowerClose2 == lowerClose2)
    assert(lowerClose2 < lowerOpen2)
    assert(lowerClose2 < lowerClose3)
    assert(lowerClose2 < lowerOpen3)
    assert(lowerClose2 < upperClose5)
    assert(lowerClose2 < upperOpen5)
    assert(lowerClose2 < upperClose6)
    assert(lowerClose2 < upperOpen6)

    assert(lowerOpen2 > lowerClose2)
    assert(lowerOpen2 == lowerOpen2)
    assert(lowerOpen2 < lowerClose3)
    assert(lowerOpen2 < lowerOpen3)
    assert(lowerOpen2 < upperClose5)
    assert(lowerOpen2 < upperOpen5)
    assert(lowerOpen2 < upperClose6)
    assert(lowerOpen2 < upperOpen6)

    assert(lowerClose3 > lowerClose2)
    assert(lowerClose3 > lowerOpen2)
    assert(lowerClose3 == lowerClose3)
    assert(lowerClose3 < lowerOpen3)
    assert(lowerClose3 < upperClose5)
    assert(lowerClose3 < upperOpen5)
    assert(lowerClose3 < upperClose6)
    assert(lowerClose3 < upperOpen6)

    assert(lowerOpen3 > lowerClose2)
    assert(lowerOpen3 > lowerOpen2)
    assert(lowerOpen3 > lowerClose3)
    assert(lowerOpen3 == lowerOpen3)
    assert(lowerOpen3 < upperClose5)
    assert(lowerOpen3 < upperOpen5)
    assert(lowerOpen3 < upperClose6)
    assert(lowerOpen3 < upperOpen6)


    assert(upperClose5 > lowerClose2)
    assert(upperClose5 > lowerOpen2)
    assert(upperClose5 > lowerClose3)
    assert(upperClose5 > lowerOpen3)
    assert(upperClose5 == upperClose5)
    assert(upperClose5 > upperOpen5)
    assert(upperClose5 < upperClose6)
    assert(upperClose5 < upperOpen6)

    assert(upperOpen5 > lowerClose2)
    assert(upperOpen5 > lowerOpen2)
    assert(upperOpen5 > lowerClose3)
    assert(upperOpen5 > lowerOpen3)
    assert(upperOpen5 < upperClose5)
    assert(upperOpen5 == upperOpen5)
    assert(upperOpen5 < upperClose6)
    assert(upperOpen5 < upperOpen6)

    assert(upperClose6 > lowerClose2)
    assert(upperClose6 > lowerOpen2)
    assert(upperClose6 > lowerClose3)
    assert(upperClose6 > lowerOpen3)
    assert(upperClose6 > upperClose5)
    assert(upperClose6 > upperOpen5)
    assert(upperClose6 == upperClose6)
    assert(upperClose6 > upperOpen6)

    assert(upperOpen6 > lowerClose2)
    assert(upperOpen6 > lowerOpen2)
    assert(upperOpen6 > lowerClose3)
    assert(upperOpen6 > lowerOpen3)
    assert(upperOpen6 > upperClose5)
    assert(upperOpen6 > upperOpen5)
    assert(upperOpen6 < upperClose6)
    assert(upperOpen6 == upperOpen6)

  }

  @Test
  def test03_sort {
    val list = ListBuffer[IntervalLimit[Int]]()

    list += IntervalLimit.upper(false, Limitless[Int])
    list += IntervalLimit.upper(true, Limitless[Int])
    list += IntervalLimit.lower(false, Limitless[Int])
    list += IntervalLimit.lower(true, Limitless[Int])
    list += IntervalLimit.lower(true, Limit(1))
    list += IntervalLimit.lower(false, Limit(1))
    list += IntervalLimit.lower(true, Limit(5))
    list += IntervalLimit.lower(false, Limit(5))
    list += IntervalLimit.upper(true, Limit(1))
    list += IntervalLimit.upper(false, Limit(1))
    list += IntervalLimit.upper(true, Limit(5))
    list += IntervalLimit.upper(false, Limit(5))

    val list2 = Random.shuffle(list)
//    list2.foreach(println)
//    println("---")
    val sortedList = list2.sorted
//    for(i <- 0 until sortedList.size){
//      println("%d:%s".format(i, sortedList(i)))
//    }

    assert(sortedList(0) == IntervalLimit.lower(false, Limitless[Int]))
    assert(sortedList(1) == IntervalLimit.lower(false, Limitless[Int]))

    assert(sortedList(2) == IntervalLimit.lower(true, Limit(1)))
    assert(sortedList(3) == IntervalLimit.lower(false, Limit(1)))
    assert(sortedList(4) == IntervalLimit.upper(false, Limit(1)))
    assert(sortedList(5) == IntervalLimit.upper(true, Limit(1)))
    assert(sortedList(6) == IntervalLimit.lower(true, Limit(5)))
    assert(sortedList(7) == IntervalLimit.lower(false, Limit(5)))
    assert(sortedList(8) == IntervalLimit.upper(false, Limit(5)))
    assert(sortedList(9) == IntervalLimit.upper(true, Limit(5)))

    assert(sortedList(10) == IntervalLimit.upper(false, Limitless[Int]))
    assert(sortedList(11) == IntervalLimit.upper(false, Limitless[Int]))

  }
}