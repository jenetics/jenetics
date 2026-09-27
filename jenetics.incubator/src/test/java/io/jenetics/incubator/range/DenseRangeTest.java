package io.jenetics.incubator.range;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.util.range.Integral;
import io.jenetics.incubator.util.range.Range;

public class DenseRangeTest {

	private static final Range.Factory<LocalDate>
		DATE_RANGE =
		Range.factory(Integral.LOCAL_DATE);

	@Test(dataProvider = "containsDateRanges")
	public void containsDateRange(
		final Range<LocalDate> a,
		final Range<LocalDate> b,
		final boolean expected
	) {
		assertThat(a.contains(b)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] containsDateRanges() {
		return new Object[][] {
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.dense(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				true
			},
			new  Object[] {
				DATE_RANGE.of(LocalDate.of(2020, 1, 1)),
				DATE_RANGE.of(LocalDate.of(2020, 1, 1)),
				true
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.dense(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 2, 20)),
				true
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.dense(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 20)),
				true
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.dense(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				true
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.of(LocalDate.of(2020, 2, 29)),
				true
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.dense(LocalDate.of(2020, 1, 9), LocalDate.of(2020, 3, 1)),
				false
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.dense(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 3, 2)),
				false
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.of(LocalDate.of(2020, 1, 9)),
				false
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.dense(LocalDate.of(2020, 1, 9), LocalDate.of(2020, 1, 11)),
				false
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.dense(LocalDate.of(2020, 2, 29), LocalDate.of(2020, 3, 2)),
				false
			},
			new  Object[] {
				DATE_RANGE.dense(LocalDate.of(2020, 1, 10), LocalDate.of(2020, 3, 1)),
				DATE_RANGE.of(LocalDate.of(2020, 3, 1)),
				false
			}
		};
	}

	@Test(dataProvider = "unionDateRanges")
	public void union(final Range<LocalDate> a, final Range<LocalDate> b, final Range<LocalDate> expected) {
		assertThat(a.union(b)).isEqualTo(expected);
		assertThat(b.union(a)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] unionDateRanges() {
		return new Object[][] {
			new  Object[] {
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				)
			},
			new  Object[] {
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 15)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				)
			},
			new  Object[] {
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 15),
					LocalDate.of(2020, 1, 30)
				),
				DATE_RANGE.of(
					DATE_RANGE.dense(
						LocalDate.of(2020, 1, 1),
						LocalDate.of(2020, 1, 10)
					),
					DATE_RANGE.dense(
						LocalDate.of(2020, 1, 15),
						LocalDate.of(2020, 1, 30)
					)
				)
			}
		};
	}

	@Test(dataProvider = "differenceDateRanges")
	public void difference(final Range<LocalDate> a, final Range<LocalDate> b, final Range<LocalDate> expected) {
		assertThat(a.difference(b)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] differenceDateRanges() {
		return new Object[][] {
			new  Object[] {
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				)
			},
			new  Object[] {
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				)
			},
			new  Object[] {
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 20)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 5)
				)
			},
			new  Object[] {
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 20)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				)
			},
			new  Object[] {
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				),
				DATE_RANGE.dense(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 10)
				),
				DATE_RANGE.of(
					DATE_RANGE.dense(
						LocalDate.of(2020, 1, 1),
						LocalDate.of(2020, 1, 5)
					),
					DATE_RANGE.dense(
						LocalDate.of(2020, 1, 10),
						LocalDate.of(2020, 1, 20)
					)
				)
			}
		};
	}

}
