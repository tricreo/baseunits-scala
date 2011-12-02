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
package example.insuranceRates

import org.scalatest.junit.AssertionsForJUnit
import org.junit.{Ignore, Test}
import org.hamcrest.CoreMatchers.is
import org.junit.Assert.assertThat
import org.sisioh.baseunits.scala.util.Ratio
import org.sisioh.baseunits.scala.money.{Proration, Money}
import org.sisioh.baseunits.scala.time.{Duration, CalendarDate}
import org.sisioh.baseunits.scala.intervals.{Limit, LinearIntervalMap, Interval, IntervalMap}

class CalculateRate extends AssertionsForJUnit {
  /** 契約日 */
	val policyEffectiveDate = CalendarDate.from(2004, 11, 7)


	/**Example.
	 */
	@Test
	//@Ignore
	def testLookUpMoreComplicated {
//		BusinessCalendar paymentCalendar = new BusinessCalendar()
//		CalendarInterval paymentQuarter = paymentCalendar.currentQuarter()
//
//		CalendarDate birthdate = null;
//		Duration age = birthdate.through(paymentQuarter.start()).length()
//		Rate rate = insuranceSchedule().get(age)
//		Money quarterlyPayment = rate.times(Duration.quarters(1))
//		CalendarDate effectiveDate = null;
//		CalendarInterval remainingQuarter = paymentQuarter.cropForwardFrom(effectiveDate)
//		BigDecimal ratio = remainingQuarter.duration().dividedBy(paymentQuarter)
//		Money firstPayment = quarterlyPayment.prorate(ratio)
	}

	/**Example.
	 */

	@Test
	def testLookUpRate {
		val birthdate = CalendarDate.from(1963, 4, 6)
		val ageOnEffectiveDate = birthdate.through(policyEffectiveDate).lengthInMonths
		val monthlyPremium = insuranceSchedule.get(Limit(ageOnEffectiveDate)).get
		assertThat(monthlyPremium, is(Money.dollars(150.00)))
	}

	/**
	 * 初月の日割り計算の例。
	 */
	@Test
	def testProrateFirstMonth {
		// 月額 150.00 USD
		val monthlyPremium = Money.dollars(150.00)

		// 契約月の残り期間
		val entireMonth = policyEffectiveDate.asMonthInterval
		val remainderOfMonth = policyEffectiveDate.through(entireMonth.end)

		// 契約月の残り日数 ÷ 契約月の全日数
		var partOfPayment = Ratio(remainderOfMonth.lengthInDaysInt, entireMonth.lengthInDaysInt)

		// 切り捨てで日割り適用
		var firstPayment = monthlyPremium.applying(partOfPayment, BigDecimal.RoundingMode.DOWN)
		assertThat(firstPayment, is(Money.dollars(120.00)))

		// Alternative, equivalent calculation
		partOfPayment = remainderOfMonth.length.dividedBy(entireMonth.length)
		firstPayment = Proration.partOfWhole(monthlyPremium, partOfPayment)
		assertThat(firstPayment, is(Money.dollars(120.00)))
	}

	/**
	 * Example.
	 */
	@Test
	def testQuarterlyPremiumPayment() {
		val premium = Money.dollars(150.00).per(Duration.months(1))
		val quarterlyPayment = premium.over(Duration.quarters(1))
		assertThat(quarterlyPayment, is(Money.dollars(450.00)))
	}

	private def insuranceSchedule : IntervalMap[Duration, Money] = {
		val age25_35 = Interval.over(Limit(Duration.years(25)), true, Limit(Duration.years(35)), false)
		val age35_45 = Interval.over(Limit(Duration.years(35)), true, Limit(Duration.years(45)), false)
		val age45_55 = Interval.over(Limit(Duration.years(45)), true, Limit(Duration.years(55)), false)
		val age55_65 = Interval.over(Limit(Duration.years(55)), true, Limit(Duration.years(65)), false)

		var schedule = new LinearIntervalMap[Duration, Money]()
		schedule += (age25_35 -> Money.dollars(100.00))
		schedule += (age35_45 -> Money.dollars(150.00))
		schedule += (age45_55 -> Money.dollars(200.00))
		schedule += (age55_65 -> Money.dollars(250.00))
		schedule
	}
}
