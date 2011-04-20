package jp.tricreo.baseunits.scala.money

import java.util.{Locale, Currency}


/** 金額を表すクラス。
 * <p>ある一定の「量」と「通貨単位」から成るクラスである。</p>
 */
class Money(val amount: BigDecimal, val currency: Currency) extends Ordered[Money] {

  override def toString = "Money(%s, %s)".format(amount, currency)

  override def equals(obj: Any) = obj match {
    case that: Money => compareTo(that) == 0
    case bd: BigDecimal => amount == bd
    case n: Int => amount == n
    case f: Float => amount == f
    case d: Double => amount == d
    case _ => false
  }

  override def hashCode = amount.hashCode + currency.hashCode

  /** Returns a [[Money]] whose amount is the absolute amount of this [[Money]], and whose scale is this.scale().
   *
   * @return 絶対金額
   */
  def abs = Money(amount.abs, currency)

  def compare(that: Money) = {
    require(currency == that.currency)
    amount compare that.amount
  }

  def /(other: Money) = {
    require(currency == other.currency)
    Money(amount / other.amount, currency)
  }

  def *(other: Money) = {
    require(currency == other.currency)
    Money(amount * other.amount, currency)
  }

  def +(other: Money) = {
    require(currency == other.currency)
    Money(amount + other.amount, currency)
  }

  def -(other: Money) = {
    require(currency == other.currency)
    Money(amount - other.amount, currency)
  }

}

object Money {

  implicit def bigDecimalToMoney(amount: Int) = apply(amount)

  def apply(amount: BigDecimal,
            currency: Currency = Currency.getInstance(Locale.getDefault)) = new Money(amount, currency)

  def unapply(money: Money): Option[(BigDecimal, Currency)] = Some(money.amount, money.currency)


  val USD = Currency.getInstance("USD")
  val EUR = Currency.getInstance("EUR")
  val JPY = Currency.getInstance("JPY")

  /** {@code amount}で表す量のドルを表すインスタンスを返す。
   *
   * <p>This creation method is safe to use. It will adjust scale, but will not
   * round off the amount.</p>
   *
   * @param amount 量
   * @return {@code amount}で表す量のドルを表すインスタンス
   */
  def dollars(amount: BigDecimal) = apply(amount, USD)

  /** {@code amount}で表す量のユーロを表すインスタンスを返す。
   *
   * This creation method is safe to use. It will adjust scale, but will not
   * round off the amount.
   *
   * @param amount 量
   * @return {@code amount}で表す量のユーロを表すインスタンス
   */
  def euros(amount: BigDecimal) = apply(amount, EUR)

  /** {@code amount}で表す量の円を表すインスタンスを返す。
   *
   * This creation method is safe to use. It will adjust scale, but will not
   * round off the amount.
   *
   * @param amount 量
   * @return {@code amount}で表す量の円を表すインスタンス
   */
  def yens(amount: BigDecimal) = apply(amount, JPY)

  /** 指定した通貨単位を持つ、量が0の金額を返す。
   *
   * @param currency 通貨単位
   * @return 金額
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def zero(currency: Currency) = apply(0, currency)


}