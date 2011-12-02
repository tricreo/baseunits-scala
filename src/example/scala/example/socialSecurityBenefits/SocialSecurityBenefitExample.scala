/*
 * Copyright 2011 Sisioh Project and the Others.
 * lastModified : 2011/04/22
 *
 * This file is part of Tricreo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package example.socialSecurityBenefits

import org.scalatest.junit.AssertionsForJUnit
import org.sisioh.baseunits.scala.money.Money
import org.sisioh.baseunits.scala.util.Ratio
import org.junit.{Ignore, Test}
import org.junit.Assert._
import org.sisioh.baseunits.scala.time.{CalendarInterval, CalendarDate}
import org.sisioh.baseunits.scala.intervals.Limit

class SocialSecurityBenefitExample extends AssertionsForJUnit {
  /*
   * Money calculations often must follow seemingly arbitrary rules, and the
   * intricate computations must be exact. Real-world, public examples are
   * provided courtesy of our Social Security Agency.
   *
   * The US Social Security regulation 404.430 has the following example
   * calculations. (see http://www.ssa.gov/OP_Home/cfr20/404/404-0430.htm)
   *
   * The examples are 25 years old, but the regulations are current.
   */

  /**Example: (Simplified exerpt from
   * http://www.ssa.gov/OP_Home/cfr20/404/404-0439.htm) Worker is entitled to
   * an old-age insurance benefit of $200 payable for October, which is
   * apportioned as follows after rounding each share down to the nearest
   * dollar. See regulation �404.304(f).
   *
   * Fraction Benefit Worker 2/3 $133 Spouse 1/3 66 Total 199
   */
  @Test
  def testArbitraryRoundingRuleInDeductionsFromFamilyBenefits {
    val benefit = Money.dollars(200)
    val workerShare = Ratio(2, 3)
    val spouseShare = Ratio(1, 3)
    val roundingScale = 0;
    val workerBenefit = benefit.applying(workerShare, roundingScale, BigDecimal.RoundingMode.DOWN)
    val spouseBenefit = benefit.applying(spouseShare, roundingScale, BigDecimal.RoundingMode.DOWN)
    assertEquals(Money.dollars(133), workerBenefit)
    assertEquals(Money.dollars(66), spouseBenefit)
  }

  /**
   * Example.
   */
  @Test
  @Ignore
  def testExcessEarnings {
    /*
     * Example 1. The self-employed beneficiary attained age 72 in July
     * 1979. His net earnings for 1979, his taxable year, were $12,000. The
     * pro rata share of the net earnings for the period prior to July is
     * $6,000. His excess earnings for 1979 for retirement test purposes are
     * $750. This is computed by subtracting $4,500 ($375�12), the exempt
     * amount for 1979, from $6,000 and dividing the result by 2.
     */

    // Does beneficiary attain age 72 during the benefit year 1972?
    val y1979 = CalendarInterval.year(1979)
    val birthday72 = CalendarDate.from(1979, 7, 15)
    assertTrue(y1979.includes(Limit(birthday72)))

    // Note that all calculations are based on entire months.
    // The proration is not based on the number of days prior to
    // turning 72 (the exempt age). It is base on the number
    // of months prior to the month in which he turned 72.

    //		CalendarInterval subintervalOfYearSubjectToExcess =
    //				CalendarInterval.inclusive(y1979.start, birthday72.firstOfMonth.previousDay)
    //
    //		Ratio portionOfYearSubject =
    //				subintervalOfYearSubjectToExcess.lengthInMonths.dividedBy(y1979.lengthInMonths)
    //
    //		Money earningsFor1979 = Money.dollars(12000)
    //		assertEquals(Money.dollars(6000), netEarningsPriorToMonthOfTurning72)

    val exemptMonthlyEarnings = Money.dollars(375)
    val exemptAnnualEarnings = exemptMonthlyEarnings.times(12)
    assertEquals(Money.dollars(4500), exemptAnnualEarnings)
    //		Money annualExcessEarnings = earningsFor1979.minus(exemptAnnualEarnings)
    //
    //		Money excessEarnings = earningsFor1979.minus(exemptEarnings)
    //		assertEquals(Money.dollars(750), excessEarnings)
  }

}