package io.jenetics.incubator.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.DateRange;

public class DateRangeContinuousTest {

	@Test(dataProvider = "containsDateRanges")
	public void containsDateRange(final DateRange a, final DateRange b, final boolean expected) {
		assertThat(a.contains(b)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] containsDateRanges() {
		return new Object[][] {
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DateRange.range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				true
			},
			new  Object[] {
				DateRange.of(LocalDate.of(2020, 1, 1)),
				DateRange.of(LocalDate.of(2020, 1, 1)),
				true
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DateRange.range(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 2, 20)),
				true
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DateRange.range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 20)),
				true
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DateRange.range(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				true
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DateRange.of(LocalDate.of(2020, 2, 29)),
				true
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DateRange.range(LocalDate.of(2020, 1, 9), LocalDate.of(2020, 3, 1)),
				false
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DateRange.range(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 2)),
				false
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DateRange.of(LocalDate.of(2020, 1, 9)),
				false
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DateRange.range(LocalDate.of(2020, 1, 9), LocalDate.of(2020, 1, 11)),
				false
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DateRange.range(LocalDate.of(2020, 2, 29), LocalDate.of(2020, 3, 2)),
				false
			},
			new  Object[] {
				DateRange.range(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DateRange.of(LocalDate.of(2020, 3, 1)),
				false
			}
		};
	}

	@Test(dataProvider = "unionDateRanges")
	public void union(final DateRange a, final DateRange b, final DateRange expected) {
		assertThat(a.union(b)).isEqualTo(expected);
		assertThat(b.union(a)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] unionDateRanges() {
		return new Object[][] {
			new  Object[] {
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				)
			},
			new  Object[] {
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 15)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				)
			},
			new  Object[] {
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 15),
					LocalDate.of(2020, 1, 30)
				),
				DateRange.of(
					DateRange.range(
						LocalDate.of(2020, 1, 1),
						LocalDate.of(2020, 1, 10)
					),
					DateRange.range(
						LocalDate.of(2020, 1, 15),
						LocalDate.of(2020, 1, 30)
					)
				)
			}
		};
	}

	@Test(dataProvider = "differenceDateRanges")
	public void difference(final DateRange a, final DateRange b, final DateRange expected) {
		System.out.println(a.difference(b));
		assertThat(a.difference(b)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] differenceDateRanges() {
		return new Object[][] {
			new  Object[] {
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				)
			},
			new  Object[] {
				DateRange.range(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				)
			},
			new  Object[] {
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 20)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 5)
				)
			},
			new  Object[] {
				DateRange.range(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 20)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				)
			},
			new  Object[] {
				DateRange.range(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				),
				DateRange.range(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 10)
				),
				DateRange.of(
					DateRange.range(
						LocalDate.of(2020, 1, 1),
						LocalDate.of(2020, 1, 5)
					),
					DateRange.range(
						LocalDate.of(2020, 1, 10),
						LocalDate.of(2020, 1, 20)
					)
				)
			}
		};
	}

}
