/**時間及び金額にまつわるドメインモデル群を含むクラスライブラリ。
 *
 * [[http://maven.tricreo.jp/site/baseunits/latest/ baseunits]]ライブラリのScala版です。
 *
 * ===一般仕様===
 *
 * 各クラス・メソッドの仕様は、それぞれのAPIドキュメントに記載しているが、このライブラリ全体を通して適用する仕様を以下に示す。
 *
 * このAPIは、内部の整合性が壊れた場合、java.lang.Errorをスローすることがある。
 * breachEncapsulationOfXXXメソッドは、そのクラスのカプセル化を壊すメソッドである。利用は推奨しない。
 * 利用した場合のオブジェクトの整合性は、利用者の責任において確保すること。
 * このAPIは、APIドキュメントに漏れなく仕様を定義することを理想としている。
 * もしAPIを呼び出した結果、APIドキュメントに記述のない挙動をした場合、それはこのAPIのバグである。
 * Javadocのthrows部はRuntimeExceptionの派生クラスについても記述されていなければならない。
 * つまり、Javadocに記述のない例外（RuntimeExceptionを含む）が飛んだら、バグである。
 * Java版とは異なり、引数にnullを与えるとNullPointerExceptionがスローされる場合があるが、ScalaではnullではなくOption
 * 型のNoneを扱うべきなので、この点について考慮しない仕様となっている。
 *
 * 引数が不正な場合はIllegalArgumentExceptionがスローされる。
 * 「仕様バグ」（APIドキュメント自体の記述漏れ・不整合など）と「実装バグ」（コードの不整合など）がありえる。
 */
package object jp {

}