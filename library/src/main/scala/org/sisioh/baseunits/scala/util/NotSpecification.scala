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
  * 否定の仕様を表すモデル。
  *
  * <p>ある `Specification` の否定をとる `Specification` 実装クラス。
  * デコレータではないので注意。</p>
  *
  * @tparam T [[NotSpecification]]の型
  * @author j5ik2o
  */
class NotSpecification[T](private[util] val spec1: Specification[T]) extends Specification[T] {

  override def isSatisfiedBy(t: T): Boolean =
    !spec1.isSatisfiedBy(t)

}
