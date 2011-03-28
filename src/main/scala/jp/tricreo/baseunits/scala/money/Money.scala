package jp.tricreo.baseunits.scala.money

import java.util.{Locale, Currency}

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/03/28
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */

class Money(val amount: BigDecimal, val currency: Currency) extends Ordered[Money]{

  override def toString = "Money(%s, %s)".format(amount, currency)

  override def equals(obj: Any) = obj match {
    case that:Money => amount == that.amount && this.currency == that.currency
    case bd:BigDecimal => amount == bd
    case n:Int => amount == n
    case f:Float => amount == f
    case d:Double => amount == d
    case _ => false
  }

  override def hashCode = amount.hashCode + currency.hashCode

  def abs = Money(amount.abs, currency)

  def compare(that: Money) = {
    require(currency == that.currency)
    amount compare that.amount
  }

  def / (other: Money) = {
    require(currency == other.currency)
    Money(amount / other.amount, currency)
  }
  def * (other: Money) = {
    require(currency == other.currency)
    Money(amount * other.amount, currency)
  }
  def + (other: Money) = {
    require(currency == other.currency)
    Money(amount + other.amount, currency)
  }
  def - (other: Money) = {
    require(currency == other.currency)
    Money(amount - other.amount, currency)
  }

}

object Money {

  implicit def bigDecimalToMoney(amount:Int) = apply(amount)

  def apply(amount: BigDecimal,
            currency: Currency = Currency.getInstance(Locale.getDefault)) = new Money(amount, currency)

  def unapply(money: Money):Option[(BigDecimal, Currency)] = Some(money.amount, money.currency)


  val USD = Currency.getInstance("USD")
  val EUR = Currency.getInstance("EUR")
  val JPY = Currency.getInstance("JPY")

  def dollars(amount: BigDecimal) = apply(amount, USD)

  def euros(amount: BigDecimal) = apply(amount, EUR)

  def yes(amount: BigDecimal) = apply(amount, JPY)

  def zero(currency: Currency) = apply(0, currency)


}