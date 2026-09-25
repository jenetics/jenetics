package io.jenetics.incubator.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.DateRange;

public class DateRangeCompositeTest {


	@Test(dataProvider = "compositeDateRanges")
	void contains(
		final DateRange.Composite composite,
		final DateRange.Continuous range,
		final boolean expected
	) {
		assertThat(composite.contains(range)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] compositeDateRanges() {
		return new Object[][] {
			new  Object[] {
				composite(),
				range("20200101-20200102"),
				false
			},
			new  Object[] {
				composite("20200101-20200301"),
				range("20200101-20200301"),
				true
			},
			new  Object[] {
				composite("20200101-20200301"),
				range("20200110-20200220"),
				true
			},
			new  Object[] {
				composite("20200101-20200201", "20200201-20200301"),
				range("20200110-20200220"),
				true
			},
			new  Object[] {
				composite("20200101-20200211", "20200201-20200301"),
				range("20200110-20200220"),
				true
			},
			new  Object[] {
				composite("20200115-20200301", "20200101-20200201"),
				range("20200110-20200220"),
				true
			},
			new  Object[] {
				composite(
					"20200301-20200401",
					"20191201-20200115",
					"20200115-20200301"
				),
				range("20200101-20200320"),
				true
			},
			new  Object[] {
				composite(
					"20200201-20200301",
					"20200115-20200210",
					"20200301-20200401",
					"20200101-20200115"
				),
				range("20200101-20200401"),
				true
			},
			new  Object[] {
				composite(
					"20200101-20200115",
					"20200115-20200201",
					"20200201-20200301",
					"20200302-20200401"
				),
				range("20200101-20200401"),
				false
			},
			new  Object[] {
				composite(
					"20200401-20200501",
					"20200101-20200115",
					"20200110-20200201",
					"20200130-20200301",
					"20200215-20200401"
				),
				range("20200110-20200401"),
				true
			},

			new  Object[] {
				composite(
					"20200401-20200402",
					"20200402-20200403",
					"20200403-20200404",
					"20200404-20200405",
					"20200405-20200406"
				),
				range("20200401-20200406"),
				true
			},
			new  Object[] {
				composite(
					"20200101-20200201",
					"20200201-20200301",
					"20200302-20200401",
					"20200401-20200501",
					"20200501-20200601"
				),
				range("20200101-20200601"),
				false
			},
			new  Object[] {
				composite("20200101-20200201", "20200202-20200301"),
				range("20200101-20200301"),
				false
			},
			new  Object[] {
				composite("20200102-20200201", "20200201-20200301"),
				range("20200101-20200301"),
				false
			},
			new  Object[] {
				composite("20200101-20200211", "20200212-20200301"),
				range("20200110-20200220"),
				false
			},
			new  Object[] {
				composite("20200101-20200201", "20200201-20200229"),
				range("20200101-20200301"),
				false
			},
			new  Object[] {
				composite("20200101-20200301"),
				range("20200301-20200302"),
				false
			}
		};
	}


	@Test(dataProvider = "intersectedDateRanges")
	void intersect(
		final DateRange a,
		final DateRange b,
		final DateRange expected
	) {
		assertThat(a.intersect(b)).isEqualTo(expected);
		assertThat(b.intersect(a)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] intersectedDateRanges() {
		return new Object[][] {
			new  Object[] {
				composite(),
				range("20200101-20200201"),
				composite()
			},
			new  Object[] {
				composite("20200101-20200201"),
				range("20200101-20200201"),
				composite("20200101-20200201")
			},
			new  Object[] {
				composite("20200101-20200301"),
				range("20200110-20200220"),
				composite("20200110-20200220")
			},
			new  Object[] {
				composite("20200110-20200220"),
				range("20200101-20200301"),
				composite("20200110-20200220")
			},
			new  Object[] {
				composite(
					"20191201-20200115",
					"20200201-20200301",
					"20200401-20200501"
				),
				range("20200101-20200415"),
				composite(
					"20200101-20200115",
					"20200201-20200301",
					"20200401-20200415"
				)
			},
			new  Object[] {
				composite(
					"20200101-20200301",
					"20200201-20200401"
				),
				range("20200115-20200315"),
				composite(
					"20200115-20200301",
					"20200201-20200315"
				)
			},
			new  Object[] {
				composite("20200101-20200201"),
				range("20200201-20200301"),
				composite()
			}
		};
	}

	public static final DateTimeFormatter
		DATE_FORMATTER =
		DateTimeFormatter.ofPattern("yyyyMMdd");

	private static DateRange.Composite composite(final String... ranges) {
		return new DateRange.Composite(
			Stream.of(ranges)
				.map(DateRangeCompositeTest::range)
				.toList()
		);
	}

	private static DateRange.Continuous range(final String value) {
		final var parts = value.split("-");
		return new DateRange.Continuous(
			LocalDate.parse(parts[0], DATE_FORMATTER),
			LocalDate.parse(parts[1], DATE_FORMATTER)
		);
	}

}
