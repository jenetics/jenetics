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
			return new IntRange(random.nextInt(10), random.nextInt(1000) + 20);
		};
	}

	@Test(dataProvider = "containsRanges")
	public void contains(IntRange a, int value, boolean result) {
		assertThat(a.contains(value)).isEqualTo(result);
	}

	@DataProvider
	public Object[][] containsRanges() {
		return new Object[][] {
			{new IntRange(0, 10), 5, true},
			{new IntRange(0, 10), 0, true},
			{new IntRange(0, 10), 10, false},
			{new IntRange(0, 10), -5, false},
			{new IntRange(0, 10), 15, false}
		};
	}

	@Test(dataProvider = "rangeIntersectionPairs")
	public void intersect(IntRange a, IntRange b, Optional<IntRange> ir) {
		assertThat(a.intersect(b)).isEqualTo(ir);
	}

	@DataProvider
	public Object[][] rangeIntersectionPairs() {
		return new Object[][] {
			{new IntRange(0, 10), new IntRange(5, 20), Optional.of(new IntRange(5, 10))},
			{new IntRange(6, 10), new IntRange(5, 20), Optional.of(new IntRange(6, 10))},
			{new IntRange(0, 10), new IntRange(5, 7), Optional.of(new IntRange(5, 7))},
			{new IntRange(0, 100), new IntRange(5, 20), Optional.of(new IntRange(5, 20))},
			{new IntRange(0, 10), new IntRange(10, 20), Optional.empty()},
			{new IntRange(0, 10), new IntRange(11, 20), Optional.empty()},
			{new IntRange(20, 100), new IntRange(1, 5), Optional.empty()}
		};
	}

}
