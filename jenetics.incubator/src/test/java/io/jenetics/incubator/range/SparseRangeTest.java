/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.range;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.util.range.DenseRange;
import io.jenetics.incubator.util.range.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SparseRangeTest {

	@Test(dataProvider = "compositeDateRanges")
	void contains(
		final Range<LocalDate> composite,
		final Range<LocalDate> range,
		final boolean expected
	) {
		assertThat(composite.contains(range)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] compositeDateRanges() {
		return new Object[][] {
			new  Object[] {
				sparse(),
				dense("20200101-20200102"),
				false
			},
			new  Object[] {
				sparse("20200101-20200301"),
				dense("20200101-20200301"),
				true
			},
			new  Object[] {
				sparse("20200101-20200301"),
				dense("20200110-20200220"),
				true
			},
			new  Object[] {
				sparse("20200101-20200201", "20200201-20200301"),
				dense("20200110-20200220"),
				true
			},
			new  Object[] {
				sparse("20200101-20200211", "20200201-20200301"),
				dense("20200110-20200220"),
				true
			},
			new  Object[] {
				sparse("20200115-20200301", "20200101-20200201"),
				dense("20200110-20200220"),
				true
			},
			new  Object[] {
				sparse(
					"20200301-20200401",
					"20191201-20200115",
					"20200115-20200301"
				),
				dense("20200101-20200320"),
				true
			},
			new  Object[] {
				sparse(
					"20200201-20200301",
					"20200115-20200210",
					"20200301-20200401",
					"20200101-20200115"
				),
				dense("20200101-20200401"),
				true
			},
			new  Object[] {
				sparse(
					"20200101-20200115",
					"20200115-20200201",
					"20200201-20200301",
					"20200302-20200401"
				),
				dense("20200101-20200401"),
				false
			},
			new  Object[] {
				sparse(
					"20200401-20200501",
					"20200101-20200115",
					"20200110-20200201",
					"20200130-20200301",
					"20200215-20200401"
				),
				dense("20200110-20200401"),
				true
			},

			new  Object[] {
				sparse(
					"20200401-20200402",
					"20200402-20200403",
					"20200403-20200404",
					"20200404-20200405",
					"20200405-20200406"
				),
				dense("20200401-20200406"),
				true
			},
			new  Object[] {
				sparse(
					"20200101-20200201",
					"20200201-20200301",
					"20200302-20200401",
					"20200401-20200501",
					"20200501-20200601"
				),
				dense("20200101-20200601"),
				false
			},
			new  Object[] {
				sparse("20200101-20200201", "20200202-20200301"),
				dense("20200101-20200301"),
				false
			},
			new  Object[] {
				sparse("20200102-20200201", "20200201-20200301"),
				dense("20200101-20200301"),
				false
			},
			new  Object[] {
				sparse("20200101-20200211", "20200212-20200301"),
				dense("20200110-20200220"),
				false
			},
			new  Object[] {
				sparse("20200101-20200201", "20200201-20200229"),
				dense("20200101-20200301"),
				false
			},
			new  Object[] {
				sparse("20200101-20200301"),
				dense("20200301-20200302"),
				false
			}
		};
	}


	@Test(dataProvider = "intersectedDateRanges")
	void intersect(
		final Range<LocalDate> a,
		final Range<LocalDate> b,
		final Range<LocalDate> expected
	) {
		assertThat(a.intersect(b)).isEqualTo(expected);
		assertThat(b.intersect(a)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] intersectedDateRanges() {
		return new Object[][] {
			new  Object[] {
				sparse(),
				dense("20200101-20200201"),
				sparse()
			},
			new  Object[] {
				sparse("20200101-20200201"),
				dense("20200101-20200201"),
				sparse("20200101-20200201")
			},
			new  Object[] {
				sparse("20200101-20200301"),
				dense("20200110-20200220"),
				sparse("20200110-20200220")
			},
			new  Object[] {
				sparse("20200110-20200220"),
				dense("20200101-20200301"),
				sparse("20200110-20200220")
			},
			new  Object[] {
				sparse(
					"20191201-20200115",
					"20200201-20200301",
					"20200401-20200501"
				),
				dense("20200101-20200415"),
				sparse(
					"20200101-20200115",
					"20200201-20200301",
					"20200401-20200415"
				)
			},
			new  Object[] {
				sparse(
					"20200101-20200301",
					"20200201-20200401"
				),
				dense("20200115-20200315"),
				sparse(
					"20200115-20200301",
					"20200201-20200315"
				)
			},
			new  Object[] {
				sparse("20200101-20200201"),
				dense("20200201-20200301"),
				sparse()
			}
		};
	}

	public static final DateTimeFormatter
		DATE_FORMATTER =
		DateTimeFormatter.ofPattern("yyyyMMdd");

	private static Range<LocalDate> sparse(final String... ranges) {
		return Range.LOCAL_DATE.of(
			Stream.of(ranges)
				.map(SparseRangeTest::dense)
				.toList()
		);
	}

	private static DenseRange<LocalDate> dense(final String value) {
		final var parts = value.split("-");
		return Range.LOCAL_DATE.dense(
			LocalDate.parse(parts[0], DATE_FORMATTER),
			LocalDate.parse(parts[1], DATE_FORMATTER)
		);
	}

}
