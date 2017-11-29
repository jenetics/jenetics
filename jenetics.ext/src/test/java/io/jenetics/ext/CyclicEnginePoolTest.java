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
package io.jenetics.ext;

import static io.jenetics.ext.ConcatEnginePoolTest.streamable;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;

import io.jenetics.ext.engine.CyclicEnginePool;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CyclicEnginePoolTest {

	@Test
	public void cycle0() {
		final EvolutionStream<IntegerGene, Integer> stream =
			CyclicEnginePool.<IntegerGene, Integer>of()
				.stream();

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{});
	}

	@Test
	public void cycle1() {
		final EvolutionStream<IntegerGene, Integer> stream =
			CyclicEnginePool.of(
				streamable(2),
				streamable(2),
				streamable(2)
			)
			.stream();

		final int[] array = stream.limit(12)
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
	}

	@Test
	public void cycle2() {
		final EvolutionStream<IntegerGene, Integer> stream =
			CyclicEnginePool.of(
				streamable(2),
				streamable(2)
			)
			.stream();

		final int[] array = stream
			.limit(Limits.byFixedGeneration(12))
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
	}

	@Test
	public void cycle3() {
		final EvolutionStream<IntegerGene, Integer> stream =
			CyclicEnginePool.of(
				streamable(2),
				streamable(2)
			)
			.stream();

		final int[] array = stream
			.limit(Limits.byFixedGeneration(12))
			.limit(10)
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
	}

}
