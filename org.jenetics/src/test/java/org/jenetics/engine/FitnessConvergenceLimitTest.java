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
package org.jenetics.engine;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.engine.FitnessConvergenceLimit.Buffer;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.DoubleMoments;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class FitnessConvergenceLimitTest {

	@Test
	public void bufferLength() {
		final long seed = 0xdeadbeef;
		final int capacity = 10;

		final Random random = new Random(seed);
		final Buffer buffer = new Buffer(capacity);

		for (int i = 0; i < buffer.capacity(); ++i) {
			buffer.accept(random.nextDouble());
			Assert.assertEquals(buffer.length(), i + 1);
		}

		for (int i = 0; i < buffer.capacity(); ++i) {
			buffer.accept(random.nextDouble());
			Assert.assertEquals(buffer.length(), buffer.capacity());
		}
	}

	@Test
	public void stream() {
		final long seed = 0xdeadbeef;
		final int capacity = 10;

		final Random random = new Random(seed);
		final Buffer buffer = new Buffer(capacity);

		for (int i = 0; i < buffer.capacity(); ++i) {
			final double value = random.nextDouble();
			buffer.accept(value);
		}

		random.setSeed(seed);
		buffer.stream().forEach(d -> Assert.assertEquals(d, random.nextDouble()));

		random.setSeed(seed);
		for (int i = 0; i < 5; ++i) random.nextDouble();
		buffer.stream(5).forEach(d -> Assert.assertEquals(d, random.nextDouble()));
	}

	@Test
	public void bufferDoubleMoments() {
		final long seed = 0xdeadbeef;
		final int capacity = 10;

		final Random random = new Random(seed);
		final Buffer buffer = new Buffer(capacity);

		DoubleMomentStatistics statistics = new DoubleMomentStatistics();
		for (int i = 0; i < buffer.capacity(); ++i) {
			final double value = random.nextDouble()*1000;
			buffer.accept(value);
			statistics.accept(value);

			final DoubleMoments moments = DoubleMoments.of(statistics);
			Assert.assertEquals(moments, buffer.doubleMoments(1000));
		}

		final Random sr = new Random(seed);
		for (int i = 0; i < buffer.capacity(); ++i) {
			statistics = statistics(new Random(seed), i + 1, buffer.capacity() - 1);

			final double value = random.nextDouble()*1000;
			buffer.accept(value);
			statistics.accept(value);

			final DoubleMoments moments = DoubleMoments.of(statistics);
			Assert.assertEquals(moments, buffer.doubleMoments(1000));
		}
	}

	public static DoubleMomentStatistics statistics(
		final Random random,
		final int skip,
		final int size
	) {
		final DoubleMomentStatistics statistics = new DoubleMomentStatistics();
		for (int i = 0; i < skip; ++i) random.nextDouble();
		for (int i = 0; i < size; ++i) statistics.accept(random.nextDouble()*1000);

		return statistics;
	}

}
