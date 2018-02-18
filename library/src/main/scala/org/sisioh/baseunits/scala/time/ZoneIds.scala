package org.sisioh.baseunits.scala.time

import java.time.ZoneId
import java.util.TimeZone

object ZoneIds {

  val Default = ZoneId.systemDefault()

  val GMT = TimeZone.getTimeZone("Universal").toZoneId

  val JST = ZoneId.of("JST", ZoneId.SHORT_IDS)

}
