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
package io.jenetics.engine;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CodecTest {

	@Test
	public void map() {
		final Codec<double[], DoubleGene> codec = Codecs
			.ofVector(new DoubleRange(0, 1), 10)
			.map(v -> {
				for (int i = 0; i < v.length; ++i) {
					v[i] = v[i]/2.0;
				}
				return v;
			});

		for (int i = 0; i < 100; ++i) {
			final Genotype<DoubleGene> gt = Genotype.of(DoubleChromosome.of(
				DoubleGene.of(i, new DoubleRange(0, 100))
			));

			Assert.assertEquals(codec.decode(gt), new double[]{i/2.0});
		}
	}

}
