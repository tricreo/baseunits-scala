package org.sisioh.baseunits.scala.time

import java.util.TimeZone

object TimeZones {

  val Default = TimeZone.getDefault

  val GMT = TimeZone.getTimeZone("Universal")

  val JST = TimeZone.getTimeZone("Asia/Tokyo")

}
