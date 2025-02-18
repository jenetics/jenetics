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
package io.jenetics.incubator.stat;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.nextDown;
import static java.lang.Math.nextUp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class IntervalTest {

	@Test
	public void creation() {
		var interval = new Interval(0.0, 100.0);
		assertThat(interval.min()).isEqualTo(0.0);
		assertThat(interval.max()).isEqualTo(100.0);

		interval = new Interval(NEGATIVE_INFINITY, 2.0);
		assertThat(interval.min()).isEqualTo(NEGATIVE_INFINITY);
		assertThat(interval.max()).isEqualTo(2.0);

		interval = new Interval(0, POSITIVE_INFINITY);
		assertThat(interval.min()).isEqualTo(0);
		assertThat(interval.max()).isEqualTo(POSITIVE_INFINITY);

		interval = new Interval(NEGATIVE_INFINITY, POSITIVE_INFINITY);
		assertThat(interval.min()).isEqualTo(Double.NEGATIVE_INFINITY);
		assertThat(interval.max()).isEqualTo(POSITIVE_INFINITY);
	}

	@Test
	public void notANumber() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> new Interval(NaN, 100.0));

		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> new Interval(0, NaN));

		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> new Interval(NaN, NaN));
	}

	@Test
	public void compareTo() {
		var interval = new Interval(0.0, 100.0);
		assertThat(interval.compareTo(-1)).isEqualTo(-1);
		assertThat(interval.compareTo(0)).isEqualTo(0);
		assertThat(interval.compareTo(50)).isEqualTo(0);
		assertThat(interval.compareTo(101)).isEqualTo(1);
	}

	@Test(dataProvider = "intervals")
	public void size(Interval interval, int size) {
		assertThat(interval.size()).isEqualTo(size);
	}

	@DataProvider
	public Object[][] intervals() {
		return new Object[][] {
			{new Interval(-100.0, up(-100.0, 1)), 1},
			{new Interval(-100.0, up(-100.0, 2)), 2},
			{new Interval(-100.0, up(-100.0, 3)), 3},
			{new Interval(-100.0, up(-100.0, 5)), 5},
			{new Interval(-100.0, up(-100.0, 20)), 20},

			{new Interval(down(-100.0, 1), up(-100.0, 1)), 2},
			{new Interval(down(-100.0, 2), up(-100.0, 2)), 4},
			{new Interval(down(-100.0, 3), up(-100.0, 3)), 6},
			{new Interval(down(-100.0, 5), up(-100.0, 5)), 10},
			{new Interval(down(-100.0, 20), up(-100.0, 20)), 40},

			{new Interval(0.0, up(0.0, 1)), 1},
			{new Interval(0.0, up(0.0, 2)), 2},
			{new Interval(0.0, up(0.0, 3)), 3},
			{new Interval(0.0, up(0.0, 5)), 5},
			{new Interval(0.0, up(0.0, 20)), 20},

			{new Interval(down(0.0, 1), up(0.0, 1)), 2},
			{new Interval(down(0.0, 2), up(0.0, 2)), 4},
			{new Interval(down(0.0, 3), up(0.0, 3)), 6},
			{new Interval(down(0.0, 5), up(0.0, 5)), 10},
			{new Interval(down(0.0, 20), up(0.0, 20)), 40},

			{new Interval(100.0, up(100.0, 1)), 1},
			{new Interval(100.0, up(100.0, 2)), 2},
			{new Interval(100.0, up(100.0, 3)), 3},
			{new Interval(100.0, up(100.0, 5)), 5},
			{new Interval(100.0, up(100.0, 20)), 20},

			{new Interval(down(100.0, 1), up(100.0, 1)), 2},
			{new Interval(down(100.0, 2), up(100.0, 2)), 4},
			{new Interval(down(100.0, 3), up(100.0, 3)), 6},
			{new Interval(down(100.0, 5), up(100.0, 5)), 10},
			{new Interval(down(100.0, 20), up(100.0, 20)), 40}
		};
	}

	private static double up(double d, int steps) {
		var result = d;
		for (int i = 0; i < steps; ++i) {
			result = nextUp(result);
		}
		return result;
	}

	private static double down(double d, int steps) {
		var result = d;
		for (int i = 0; i < steps; ++i) {
			result = nextDown(result);
		}
		return result;
	}

}
