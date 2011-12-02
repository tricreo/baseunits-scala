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
package org.sisioh.baseunits.scala.tests

import java.io._
import org.junit.Assert._
import org.apache.commons.io.IOUtils

/**シリアライズのテストを行う、ヘルパークラス。
 */
object SerializationTester {

  /**
   * シリアライズできるかどうか検証する。
   *
   * @param serializable シリアライズ対象オブジェクト
   * @throws AssertionError シリアライズに失敗した場合
   */
  def assertCanBeSerialized(serializable: AnyRef) {
    if (classOf[Serializable].isInstance(serializable) == false) {
      fail("Object doesn't implement java.io.Serializable interface: " + serializable.getClass)
    }

    var out: ObjectOutputStream = null
    var in: ObjectInputStream = null
    val byteArrayOut: ByteArrayOutputStream = new ByteArrayOutputStream
    var byteArrayIn: ByteArrayInputStream = null
    try {
      out = new ObjectOutputStream(byteArrayOut)
      out.writeObject(serializable)

      byteArrayIn = new ByteArrayInputStream(byteArrayOut.toByteArray)
      in = new ObjectInputStream(byteArrayIn)
      val deserialized = in.readObject
      if (serializable.equals(deserialized) == false) {
        fail("Reconstituted object is expected to be equal to serialized")
      }
    } catch {
      case e: IOException => fail(e.getMessage)
      case e: ClassNotFoundException => fail(e.getMessage)
    } finally {
      IOUtils.closeQuietly(out)
      IOUtils.closeQuietly(in)
    }
  }
}