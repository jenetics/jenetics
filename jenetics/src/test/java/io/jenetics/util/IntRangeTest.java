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

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.util.Optional;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class IntRangeTest extends ObjectTester<IntRange> {

	@Override
	protected Factory<IntRange> factory() {
		return () -> {
			final var random = RandomRegistry.random();
			return IntRange.of(random.nextInt(10), random.nextInt(1000) + 20);
		};
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(IntRange.class).verify();
	}

	@Test(dataProvider = "containsRanges")
	public void contains(IntRange a, int value, boolean result) {
		assertThat(a.contains(value)).isEqualTo(result);
	}

	@DataProvider
	public Object[][] containsRanges() {
		return new Object[][] {
			{IntRange.of(0, 10), 5, true},
			{IntRange.of(0, 10), 0, true},
			{IntRange.of(0, 10), 10, false},
			{IntRange.of(0, 10), -5, false},
			{IntRange.of(0, 10), 15, false}
		};
	}

	@Test(dataProvider = "rangeIntersectionPairs")
	public void intersect(IntRange a, IntRange b, Optional<IntRange> ir) {
		assertThat(a.intersect(b)).isEqualTo(ir);
	}

	@DataProvider
	public Object[][] rangeIntersectionPairs() {
		return new Object[][] {
			{IntRange.of(0, 10), IntRange.of(5, 20), Optional.of(IntRange.of(5, 10))},
			{IntRange.of(6, 10), IntRange.of(5, 20), Optional.of(IntRange.of(6, 10))},
			{IntRange.of(0, 10), IntRange.of(5, 7), Optional.of(IntRange.of(5, 7))},
			{IntRange.of(0, 100), IntRange.of(5, 20), Optional.of(IntRange.of(5, 20))},
			{IntRange.of(0, 10), IntRange.of(10, 20), Optional.empty()},
			{IntRange.of(0, 10), IntRange.of(11, 20), Optional.empty()},
			{IntRange.of(20, 100), IntRange.of(1, 5), Optional.empty()}
		};
	}

}
