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

import collection.Iterator
import scala.collection.mutable.ArrayBuffer

/**
  * 割り当ての集合。
  *
  * @author j5ik2o
  * @tparam T 割り当て対象
  * @param allotments 割り当ての要素（複数）
  */
class MoneyFan[T](val allotments: Set[Allotment[T]]) extends Iterable[Allotment[T]] {

  /**
    * インスタンスを生成する。
    *
    * `allotments`は空のインスタンスが利用される。
    */
  def this() = this(Set.empty[Allotment[T]])

  /**
    * インスタンスを生成する。
    *
    * @param allotment 割り当ての要素（単一)
    */
  def this(allotment: Allotment[T]) = this(Set(allotment))

  def iterator: Iterator[Allotment[T]] = allotments.iterator

  /**
    * `MoneyFan`が保持する `Allotment`のうち、割り当て対象が `anEntity`であるものを返す。
    *
    * @param anEntity 割り当て対象
    * @return `Allotment`。見つからなかった場合は`None`
    */
  def allotment(anEntity: T): Option[Allotment[T]] =
    allotments.find(_.entity == anEntity)

  override def hashCode: Int = 31 * allotments.hashCode

  override def equals(obj: Any): Boolean = obj match {
    case that: MoneyFan[T] => allotments == that.allotments
    case _                 => false
  }

  /**
    * この[[org.sisioh.baseunits.scala.money.MoneyFan]]から`subtracted`を引いた差を返す。
    *
    * @param subtracted [[org.sisioh.baseunits.scala.money.MoneyFan]]
    * @return [[org.sisioh.baseunits.scala.money.MoneyFan]]
    */
  def minus(subtracted: MoneyFan[T]): MoneyFan[T] = plus(subtracted.negated)

  def -(subtracted: MoneyFan[T]): MoneyFan[T] = minus(subtracted)

  /**
    * この [[org.sisioh.baseunits.scala.money.MoneyFan]]の [[org.sisioh.baseunits.scala.money.Allotment]]を\
    * `Allotment.negated`した [[scala.collection.Set]]で構成される
    * 新しい [[org.sisioh.baseunits.scala.money.MoneyFan]]を返す。
    *
    * @return [[org.sisioh.baseunits.scala.money.MoneyFan]]
    */
  lazy val negated = {
    val negatedAllotments = allotments.map(_.negated).toSet
    new MoneyFan[T](negatedAllotments)
  }

  def unary_- : MoneyFan[T] = negated

  /**
    * この[[org.sisioh.baseunits.scala.money.MoneyFan]]に`added`を足した和を返す。
    *
    * 同じ割り当て対象に対する割当額は、マージする。また、割当額が0の [[org.sisioh.baseunits.scala.money.Allotment]] は取り除く。
    *
    * @param added [[org.sisioh.baseunits.scala.money.MoneyFan]]
    * @return [[org.sisioh.baseunits.scala.money.MoneyFan]]
    */
  def plus(added: MoneyFan[T]): MoneyFan[T] = {
    val allEntities = allotments.map(_.entity) ++ added.allotments.map(_.entity)
    val summedAllotments = allEntities.map { entity =>
      allotment(entity) match {
        case None => added.allotment(entity).get
        case Some(thisAllotment) => {
          added.allotment(entity) match {
            case None => thisAllotment
            case Some(addedAllotment) => {
              val sum = thisAllotment.amount.plus(addedAllotment.amount)
              new Allotment[T](entity, sum)
            }
          }
        }
      }
    }
    new MoneyFan[T](summedAllotments).withoutZeros
  }

  def +(added: MoneyFan[T]): MoneyFan[T] = plus(added)

  override def toString: String = allotments.toString

  /**
    * 全ての割り当ての合計額を返す。
    *
    * @return 合計額
    */
  lazy val total = asTally.net

  private def asTally = {
    new Tally(allotments.toVector.map(_.amount))
  }

  /**
    * このインスタンスが保持する [[org.sisioh.baseunits.scala.money.Allotment]] のうち、割り当て金額が`0`であるものを取り除いた
    * 新しい [[org.sisioh.baseunits.scala.money.MoneyFan]]を返す。
    *
    * @return [[org.sisioh.baseunits.scala.money.MoneyFan]]
    */
  private lazy val withoutZeros = {
    val nonZeroAllotments = allotments.filter(_.amount.isZero == false).toSet
    new MoneyFan[T](nonZeroAllotments)
  }
}

/**
  * `MoneyFan`コンパニオンオブジェクト。
  *
  * @author j5ik2o
  */
object MoneyFan {

  /**
    * インスタンスを生成する。
    *
    * @param allotments 割り当ての要素（複数）
    * @return [[org.sisioh.baseunits.scala.money.MoneyFan]]
    */
  def apply[T](allotments: Set[Allotment[T]]): MoneyFan[T] =
    new MoneyFan[T](allotments)

  /**
    * インスタンスを生成する。
    *
    * @param allotment 割り当ての要素（単一）
    * @return [[org.sisioh.baseunits.scala.money.MoneyFan]]
    */
  def apply[T](allotment: Allotment[T]): MoneyFan[T] =
    new MoneyFan[T](allotment)

  /**
    * インスタンスを生成する。
    *
    * @return [[org.sisioh.baseunits.scala.money.MoneyFan]]
    */
  def apply[T]: MoneyFan[T] = new MoneyFan[T]

  /**
    * 抽出子メソッド。
    *
    * @param moneyFan [[MoneyFan]]
    * @return `Option[Set[Allotment[T]]]`
    */
  def unapply[T](moneyFan: MoneyFan[T]): Option[Set[Allotment[T]]] =
    Some(moneyFan.allotments)

}
