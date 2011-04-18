package jp.tricreo.baseunits.scala.time

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/17
 * Time: 23:34
 * To change this template use File | Settings | File Templates.
 */

class MinuteOfHour private(private[time] val value: Int) extends Ordered[MinuteOfHour] {
  require(MinuteOfHour.MIN < value && value < MinuteOfHour.MAX,
    "Illegal value for 24 hour: " + value + ", please use a value between 0 and 23")

  /**
   * このオブジェクトの{@link #value}フィールド（時をあらわす正数）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 時をあらわす正数（0〜23）
   */
  def breachEncapsulationOfValue = value

  def compare(other: MinuteOfHour): Int = value - other.value

  override def equals(obj: Any): Boolean = obj match {
    case that: MinuteOfHour => value == that.value
    case _ => false
  }

  override def hashCode = value.hashCode

  /**
   * 同時(hour)において、このインスタンスが表す分が、引数{@code another}で表される時よりも未来かどうか調べる。
   *
   * <p>等価である場合は{@code false}を返す。</p>
   *
   * @param another 基準分
   * @return 同日において、このインスタンスが表す分が、引数{@code another}で表される時よりも未来である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isAfter(another: MinuteOfHour) = value > another.value;

  /**
   * 同時(hour)において、このインスタンスが表す分が、引数{@code another}で表される時よりも過去かどうか調べる。
   *
   * <p>等価である場合は{@code false}を返す。</p>
   *
   * @param another 基準分
   * @return 同日において、このインスタンスが表す分が、引数{@code another}で表される時よりも過去である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isBefore(another: MinuteOfHour) = value < another.value

  override def toString = "%02d".format(value)

}

object MinuteOfHour {

  val MIN = 0

  val MAX = 59


  /**
   * インスタンスを生成する。
   *
   * @param initial 分をあらわす正数
   * @return 分（0〜59）
   * @throws IllegalArgumentException 引数の値が0〜59の範囲ではない場合
   */
  def apply(initial: Int) = new MinuteOfHour(initial)

}