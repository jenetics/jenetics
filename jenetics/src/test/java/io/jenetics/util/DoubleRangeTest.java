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
public class DoubleRangeTest extends ObjectTester<DoubleRange> {

	@Override
	protected Factory<DoubleRange> factory() {
		return () -> {
			final var random = RandomRegistry.random();
			return new DoubleRange(random.nextDouble(), random.nextDouble() + 1.1);
		};
	}

	@Test(dataProvider = "containsRanges")
	public void contains(DoubleRange a, double value, boolean result) {
		assertThat(a.contains(value)).isEqualTo(result);
	}

	@DataProvider
	public Object[][] containsRanges() {
		return new Object[][] {
			{new DoubleRange(0, 10), 5, true},
			{new DoubleRange(0, 10), 0, true},
			{new DoubleRange(0, 10), 10, false},
			{new DoubleRange(0, 10), -5, false},
			{new DoubleRange(0, 10), 15, false}
		};
	}

	@Test(dataProvider = "rangeIntersectionPairs")
	public void intersect(DoubleRange a, DoubleRange b, Optional<DoubleRange> ir) {
		assertThat(a.intersect(b)).isEqualTo(ir);
	}

	@DataProvider
	public Object[][] rangeIntersectionPairs() {
		return new Object[][] {
			{new DoubleRange(0, 10), new DoubleRange(5, 20), Optional.of(new DoubleRange(5, 10))},
			{new DoubleRange(6, 10), new DoubleRange(5, 20), Optional.of(new DoubleRange(6, 10))},
			{new DoubleRange(0, 10), new DoubleRange(5, 7), Optional.of(new DoubleRange(5, 7))},
			{new DoubleRange(0, 100), new DoubleRange(5, 20), Optional.of(new DoubleRange(5, 20))},
			{new DoubleRange(0, 10), new DoubleRange(10, 20), Optional.empty()},
			{new DoubleRange(0, 10), new DoubleRange(11, 20), Optional.empty()},
			{new DoubleRange(20, 100), new DoubleRange(1, 5), Optional.empty()}
		};
	}

}
