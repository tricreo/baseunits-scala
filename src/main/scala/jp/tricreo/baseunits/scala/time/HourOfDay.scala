package jp.tricreo.baseunits.scala.time

/**1日の中の特定の「時」を表すクラス。
 *
 * <p>{@link java.util.Date}と異なり、日付や分以下（時未満）の概念を持っていない。またタイムゾーンの概念もない。</p>
 */
@serializable
class HourOfDay(val value: Int) extends Ordered[HourOfDay] {
  require(value < HourOfDay.MIN || value > HourOfDay.MAX,
    "Illegal value for 24 hour: %d , please use a value between 0 and 23".format(value))

  def compare(that: HourOfDay): Int = value - that.value

  override def equals(obj: Any): Boolean = obj match {
    case that: HourOfDay => this.value == that.value
  }

  override def hashCode: Int = value.hashCode

  /**同日において、このインスタンスが表す時が、引数{@code another}で表される時よりも未来かどうか調べる。
   *
   * @param another 基準時
   * @return 同日において、このインスタンスが表す時が、引数{@code another}で表される時よりも未来である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isAfter(another: HourOfDay) = value > another.value;

  /**
   * 同日において、このインスタンスが表す時が、引数{@code another}で表される時よりも過去かどうか調べる。
   *
   * @param another 基準時
   * @return 同日において、このインスタンスが表す時が、引数{@code another}で表される時よりも過去である場合は{@code true}、そうでない場合は{@code false}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def isBefore(another: HourOfDay) = value < another.value;

}

object HourOfDay {
  val MIN = 0
  val MAX = 23


  /**インスタンスを生成する。
	 *
	 * @param initial 時をあらわす正数
	 * @return 時（0〜23）
	 * @throws IllegalArgumentException 引数の値が0〜23の範囲ではない場合
	 */
	def apply(initial:Int) = new HourOfDay(initial)

	/**
	 * インスタンスを生成する。
	 *
	 * @param initial 時をあらわす正数
	 * @param amPm 午前午後を表す文字列
	 * @return 時（0〜11）
	 * @throws IllegalArgumentException 引数{@code initial}の値が0〜11の範囲ではない場合
	 * @throws IllegalArgumentException 引数{@code amPm}の値が {@code "AM"} または {@code "PM"} ではない場合
	 */
	def apply(initial:Int, amPm:String)  =
	  new HourOfDay(convertTo24hour(initial, amPm));


  def unapply(hourOfDay:HourOfDay) = Some(hourOfDay.value)


	/**午前午後記号付き12時間制の時を24時間制の値に変換する。
	 *
	 * @param hour 時（0〜11）
	 * @param amPm 午前午後を表す文字列
	 * @return 24時間制における時
	 * @throws IllegalArgumentException 引数{@code initial}の値が0〜11の範囲ではない場合
	 * @throws IllegalArgumentException 引数{@code amPm}の値が {@code "AM"} または {@code "PM"} ではない場合
	 */
	def convertTo24hour(hour:Int, amPm:String):Int = {
    require(("AM".equalsIgnoreCase(amPm) || "PM".equalsIgnoreCase(amPm)) == false,
    "AM PM indicator invalid: %s, please use AM or PM".format(amPm))
    require(hour < MIN || hour > 12,
    "Illegal value for 12 hour: %d, please use a value between 0 and 11".format(hour))

		var translatedAmPm = if ("AM".equalsIgnoreCase(amPm)) 0 else 12
		val delta = if (hour == 12) 12 else 0
    translatedAmPm -= delta
		return hour + translatedAmPm;
	}

}