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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.math;

import static java.util.stream.Collectors.summarizingDouble;
import static org.jenetics.util.RandomRegistry.with;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class DoubleAdderTest extends ObjectTester<DoubleAdder> {

	@Override
	protected Factory<DoubleAdder> factory() {
		return () -> {
			final Random random = RandomRegistry.getRandom();
			final DoubleAdder adder = new DoubleAdder();
			for (int i = 0; i < 20; ++i) {
				adder.add(random.nextDouble());
			}

			return adder;
		};
	}

	@Test
	public void add() {
		final int size = 1_000_000;
		final Random random = new Random(12349);
		final List<Double> numbers = new ArrayList<>(size);

		for (int i = 1; i <= size; ++i) {
			numbers.add(random.nextDouble()*i*1_000_000);
		}

		final DoubleAdder adder = new DoubleAdder();
		numbers.forEach(adder::add);

		final double expectedSum = numbers.stream()
			.collect(summarizingDouble(Double::doubleValue))
			.getSum();

		Assert.assertEquals(adder.doubleValue(), expectedSum);
	}

	@Test
	public void sameState() {
		final DoubleAdder da1 = with(new Random(589), r -> factory().newInstance());
		final DoubleAdder da2 = with(new Random(589), r -> factory().newInstance());

		Assert.assertTrue(da1.sameState(da2));

		final Random random = new Random();
		for (int i = 0; i < 100; ++i) {
			final double value = random.nextDouble();
			da1.add(value);
			da2.add(value);

			Assert.assertTrue(da1.sameState(da2));
		}
	}

}
