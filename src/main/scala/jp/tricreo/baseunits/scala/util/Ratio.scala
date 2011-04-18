package jp.tricreo.baseunits.scala.util

import java.math.RoundingMode

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/17
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */

class Ratio
(/**分子をあらわす数 */
 private val numerator: BigDecimal,

 /**分母をあらわす数 */
 private val denominator: BigDecimal) {
  if (denominator == BigDecimal(0)) {
    throw new ArithmeticException("denominator is zero")
  }

  /**
	 * このオブジェクトの{@link #denominator}フィールド（分母をあらわす数）を返す。
	 *
	 * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
	 *
	 * @return 分母をあらわず数
	 */
	def breachEncapsulationOfDenominator = denominator


	/**
	 * このオブジェクトの{@link #numerator}フィールド（分子をあらわす数）を返す。
	 *
	 * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
	 *
	 * @return 分子をあらわす数
	 */
	def breachEncapsulationOfNumerator = numerator


	/**
	 * 比率を {@link BigDecimal}型で取得する。
	 *
	 * @param scale 小数点以下の有効数字
	 * @param roundingMode 丸めモード
	 * @return この比率の {@link BigDecimal} 型の表現
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	def decimalValue(scale:Int,  roundingMode:RoundingMode) =
		BigDecimal(numerator.bigDecimal.divide(denominator.bigDecimal, scale, roundingMode))

	/**
	 * このオブジェクトと、与えたオブジェクトの同一性を検証する。
	 *
	 * <p>与えたオブジェクト {@code anObject} が {@code null}である場合、または{@link Ratio}型や
	 * そのサブクラスではない場合、{@code false}を返す。
	 * 与えたオブジェクトの、分母と分子が共に一致する場合、{@code true}を返す。</p>
	 *
	 * <p>{@code 2/3} と {@code 4/6} は、評価結果としては同一であるが、分母同士、分子同士が
	 * 異なるため、このメソッドでは {@code true} と判断されず、 {@code false} となる。
	 *
	 * @param obj 比較対象オブジェクト
	 * @return 同一の場合は{@code true}、そうでない場合は{@code false}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	override def equals(obj:Any):Boolean = obj match {
    case that:Ratio => denominator == that.denominator && numerator == that.numerator
    case _ => false
  }

  override def hashCode = denominator.hashCode +  numerator.hashCode

	/**
	 * この比率と {@code multiplier} の積からなる比率。
	 *
	 * <p>計算結果は、分母は変化せず、分子は分子と {@code multiplyer} の積からなる比率となる。</p>
	 *
	 * @param multiplier 乗数
	 * @return 積
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	def times( multiplier:BigDecimal):Ratio =
		Ratio(numerator * multiplier, denominator)


	/**
	 * この比率と {@code multiplier} の積からなる比率。
	 *
	 * <p>計算結果は、分子同士・分母同士の積からなる比率となる。</p>
	 *
	 * @param multiplier 乗数比率
	 * @return 積
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	def times( multiplier:Ratio):Ratio = {
		Ratio(numerator * multiplier.numerator, denominator * multiplier.denominator)
	}

	/** この比率の文字列表現を取得する。
	 *
	 * <p>"分子/分母"という表記となる。</p>
	 *
	 * @see java.lang.Object#toString()
	 */

	override def toString = numerator.toString + "/" + denominator

}

object Ratio {

  /**
   * インスタンスを生成する。
   *
   * @param fractional 分数
   * @return 与えた分数であらわされる比率
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def apply(fractional: BigDecimal): Ratio =
    new Ratio(fractional, BigDecimal(1))

  /**
   * インスタンスを生成する。
   *
   * @param numerator 分子
   * @param denominator 分母
   * @return 引数に与えた分子、分母からなる比
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   * @throws ArithmeticException 引数{@code denominator}が0だった場合
   */
  def apply(numerator: BigDecimal, denominator: BigDecimal): Ratio =
    new Ratio(numerator, denominator);

  /**
   * インスタンスを生成する。
   *
   * @param numerator 分子
   * @param denominator 分母
   * @return 引数に与えた分子、分母からなる比率
   * @throws ArithmeticException 引数{@code denominator}が0だった場合
   */
  def apply(numerator:Long, denominator:Long): Ratio =
    new Ratio(BigDecimal(numerator), BigDecimal(denominator));

}