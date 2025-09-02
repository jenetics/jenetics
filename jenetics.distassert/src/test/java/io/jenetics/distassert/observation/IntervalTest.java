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
package io.jenetics.distassert.observation;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

}
