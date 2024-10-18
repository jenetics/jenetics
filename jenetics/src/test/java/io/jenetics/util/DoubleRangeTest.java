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
package io.jenetics.util;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class DoubleRangeTest extends ObjectTester<DoubleRange> {

	@Override
	protected Factory<DoubleRange> factory() {
		return () -> {
			final var random = RandomRegistry.random();
			return DoubleRange.of(random.nextDouble(), random.nextDouble() + 1.1);
		};
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(DoubleRange.class).verify();
	}

	@Test(dataProvider = "containsRanges")
	public void contains(DoubleRange a, double value, boolean result) {
		assertThat(a.contains(value)).isEqualTo(result);
	}

	@DataProvider
	public Object[][] containsRanges() {
		return new Object[][] {
			{DoubleRange.of(0, 10), 5, true},
			{DoubleRange.of(0, 10), 0, true},
			{DoubleRange.of(0, 10), 10, false},
			{DoubleRange.of(0, 10), -5, false},
			{DoubleRange.of(0, 10), 15, false}
		};
	}

	@Test(dataProvider = "rangeIntersectionPairs")
	public void intersect(DoubleRange a, DoubleRange b, Optional<DoubleRange> ir) {
		assertThat(a.intersect(b)).isEqualTo(ir);
	}

	@DataProvider
	public Object[][] rangeIntersectionPairs() {
		return new Object[][] {
			{DoubleRange.of(0, 10), DoubleRange.of(5, 20), Optional.of(DoubleRange.of(5, 10))},
			{DoubleRange.of(6, 10), DoubleRange.of(5, 20), Optional.of(DoubleRange.of(6, 10))},
			{DoubleRange.of(0, 10), DoubleRange.of(5, 7), Optional.of(DoubleRange.of(5, 7))},
			{DoubleRange.of(0, 100), DoubleRange.of(5, 20), Optional.of(DoubleRange.of(5, 20))},
			{DoubleRange.of(0, 10), DoubleRange.of(10, 20), Optional.empty()},
			{DoubleRange.of(0, 10), DoubleRange.of(11, 20), Optional.empty()},
			{DoubleRange.of(20, 100), DoubleRange.of(1, 5), Optional.empty()}
		};
	}

}
