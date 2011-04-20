package jp.tricreo.baseunits.scala.time

/**
 * 現在時刻を返す責務を表すトレイト。
 */
trait TimeSource {

  /**
   * 現在時刻を返す。
   *
   * @return 現在時刻
   * @throws TimeSourceException 現在時刻の取得に失敗した場合
   */
  def now: TimePoint

}