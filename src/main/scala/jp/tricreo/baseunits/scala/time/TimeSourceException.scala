package jp.tricreo.baseunits.scala.time

/**
 * 現在時刻の取得に失敗したことをあらわす例外。
 */
case class TimeSourceException(message: String) extends Exception(message)