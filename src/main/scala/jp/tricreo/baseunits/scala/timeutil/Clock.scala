package jp.tricreo.baseunits.scala.timeutil

import java.util.TimeZone
import jp.tricreo.baseunits.scala.time.{CalendarDate, TimeSource}

/**
 * 時計を表すクラス。
 *
 * <p>このクラスはステートを持つstaticなユーティリティクラスである。</p>
 */
object Clock {

  private[this] var _timeSource: TimeSource = _

  /**日付の算出に使用する {@link TimeZone}*/
  private[this] var _defaultTimeZone: TimeZone = _

  /**
   * この時計が日付の算出に使用する {@link TimeZone} を取得する。
   *
   * @return 日付の算出に使用する {@link TimeZone}
   */
  def defaultTimeZone: TimeZone = {
    // There is no reasonable automatic default.
    _defaultTimeZone
  }

  def defaultTimeZone_=(value: TimeZone) {
    _defaultTimeZone = value
  }


  /**
   * 現在時刻を取得する。
   *
   * @return 現在時刻
   */
  def now = timeSource.now

  /**
   * このクラスが保持するステートをリセットする。
   *
   * <p>このクラスは、staticに {@link TimeZone} と {@link TimeSource} を保持している。</p>
   */
  def reset {
    _defaultTimeZone = null
    _timeSource = null
  }

  /**
   * {@link TimeSource}を取得する。
   *
   * <p>デフォルトでは {@link SystemClock} を使用する。</p>
   *
   * @return {@link TimeSource}
   */
  def timeSource: TimeSource = {
    if (_timeSource == null) {
      _timeSource = SystemClock
    }
    _timeSource
  }

  def timeSource_=(value: TimeSource) {
    _timeSource = value
  }

  /**
   * 今日の日付を所得する。
   *
   * <p>日付は、あらかじめ設定済みの {@link TimeZone} に基づき計算する。
   * {@link TimeZone}を未設定の状態でこのメソッドを呼び出してはならない。</p>
   *
   * @return 今日の日付
   * @throws IllegalStateException {@link TimeZone}が未設定の場合
   */
  def today: CalendarDate = {
    if (defaultTimeZone == null) {
      throw new IllegalStateException("CalendarDate cannot be computed without setting a default TimeZone.");
    }
    now.calendarDate(defaultTimeZone)
  }

}