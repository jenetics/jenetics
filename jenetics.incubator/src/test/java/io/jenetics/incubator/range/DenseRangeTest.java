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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.util.range.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DenseRangeTest {

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
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 3, 1)
				),
				true
			},
			new Object[] {
				Range.LOCAL_DATE.of(LocalDate.of(2020, 1, 1)),
				Range.LOCAL_DATE.of(LocalDate.of(2020, 1, 1)),
				true
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 2, 20)
				),
				true
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 2, 20)
				),
				true
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 3, 1)
				),
				true
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.of(LocalDate.of(2020, 2, 29)),
				true
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 9),
					LocalDate.of(2020, 3, 1)
				),
				false
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 3, 2)
				),
				false
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.of(LocalDate.of(2020, 1, 9)),
				false
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 9),
					LocalDate.of(2020, 1, 11)
				),
				false
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 2, 29),
					LocalDate.of(2020, 3, 2)
				),
				false
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 3, 1)
				),
				Range.LOCAL_DATE.of(LocalDate.of(2020, 3, 1)),
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
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				)
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 15)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				)
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 15),
					LocalDate.of(2020, 1, 30)
				),
				Range.LOCAL_DATE.of(
					Range.LOCAL_DATE.dense(
						LocalDate.of(2020, 1, 1),
						LocalDate.of(2020, 1, 10)
					),
					Range.LOCAL_DATE.dense(
						LocalDate.of(2020, 1, 15),
						LocalDate.of(2020, 1, 30)
					)
				)
			}
		};
	}

	@Test(dataProvider = "differenceDateRanges")
	public void difference(
		final Range<LocalDate> a,
		final Range<LocalDate> b,
		final Range<LocalDate> expected
	) {
		assertThat(a.difference(b)).isEqualTo(expected);
	}

	@DataProvider
	static Object[][] differenceDateRanges() {
		return new Object[][] {
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				)
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				)
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 20)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 5)
				)
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 20)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 10)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 10),
					LocalDate.of(2020, 1, 20)
				)
			},
			new Object[] {
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 1, 20)
				),
				Range.LOCAL_DATE.dense(
					LocalDate.of(2020, 1, 5),
					LocalDate.of(2020, 1, 10)
				),
				Range.LOCAL_DATE.of(
					Range.LOCAL_DATE.dense(
						LocalDate.of(2020, 1, 1),
						LocalDate.of(2020, 1, 5)
					),
					Range.LOCAL_DATE.dense(
						LocalDate.of(2020, 1, 10),
						LocalDate.of(2020, 1, 20)
					)
				)
			}
		};
	}

}
