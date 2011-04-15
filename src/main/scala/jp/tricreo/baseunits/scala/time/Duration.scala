package jp.tricreo.baseunits.scala.time

/**
 * Created by IntelliJ IDEA.
 * User: junichi
 * Date: 11/04/15
 * Time: 16:58
 * To change this template use File | Settings | File Templates.
 */

/**
 * 時間量（時間の長さ・期間の長さなど）を表すクラス。
 *
 * <p>負の時間量は表現しない。</p>
 */
@serializable
class Duration extends Ordered[Duration] {



  def compare(that: Duration): Int = 0

}