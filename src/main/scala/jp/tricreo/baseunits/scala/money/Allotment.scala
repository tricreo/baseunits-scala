package jp.tricreo.baseunits.scala.money

/**何かに対するお金の割り当てをあらわす。
 *
 * @tparam T 割り当て対象
 * @param entity 割り当て対象
 * @param amount 金額
 */
class Allotment[T]
(private[money] val entity:T,
private[money] val amount:Money){

  /**このオブジェクトの{@link #amount}フィールド（金額）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 金額
   */
  def breachEncapsulationOfAmount = amount

  /**このオブジェクトの{@link #entity}フィールド（割り当て対象）を返す。
   *
   * <p>CAUTION: このメソッドは、このオブジェクトがカプセル化する要素を外部に暴露する。取り扱いには充分注意のこと。</p>
   *
   * @return 割り当て対象
   */
  def breachEncapsulationOfEntity = entity

  override def equals(obj:Any) = obj match {
    case that:Allotment[T] => entity == that.entity && amount == that.amount
    case _ => false
  }

  override def hashCode = entity.## + amount.##

  /**割り当て量の正負を反転させた新しい割り当てを返す。
   *
   * @return 割り当て
   */
  def negated =
    new Allotment[T](entity, amount.negated)

  override def toString =
    "" + entity + " --> " + amount

}