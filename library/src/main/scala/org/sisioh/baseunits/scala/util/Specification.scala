/*
 * Copyright 2014 Sisioh Project and others. (http://www.sisioh.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.sisioh.baseunits.scala.util

/**
 * 仕様を表すモデル。
 *
 * <p>DDD本の中で説明している Specification パターンに則ったオブジェクトを表すインターフェイス。
 * {@link Specification}の実装は、 {@link AbstractSpecification}を基底クラスとして実装するとよい。
 * その場合、 {@link #isSatisfiedBy(Object)} を実装する必要しかない。</p>
 *
 * @tparam T [[Specification]]の型
 * @author j5ik2o
 */
trait Specification[T] {

  /**
   * Check if {@code t} is satisfied by the specification.
   *
   * @param t Object to test.
   * @return `true` if `t` satisfies the specification.
   */
  def isSatisfiedBy(t: T): Boolean

  /**
   * Create a new specification that is the NOT operation of `this` specification.
   *
   * @return A new specification.
   */
  def not: Specification[T] = new NotSpecification[T](this)

  /**
   * Create a new specification that is the AND operation of `this` specification and another specification.
   *
   * @param specification Specification to AND.
   * @return A new specification.
   */
  def and(specification: Specification[T]): Specification[T] = new AndSpecification(this, specification)

  /**
   * Create a new specification that is the OR operation of `this` specification and another specification.
   *
   * @param specification Specification to OR.
   * @return A new specification.
   */
  def or(specification: Specification[T]): Specification[T] = new OrSpecification(this, specification)

}
