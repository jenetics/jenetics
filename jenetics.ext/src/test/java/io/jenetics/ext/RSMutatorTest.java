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

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.Chromosome;
import io.jenetics.EnumGene;
import io.jenetics.MutatorResult;
import io.jenetics.PermutationChromosome;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RSMutatorTest {

	@Test(invocationCount = 10)
	public void mutate() {
		final PermutationChromosome<Integer> original =
			PermutationChromosome.ofInteger(50);

		final RSMutator<EnumGene<Integer>, Integer> mutator =
			new RSMutator<>(1.0);

		final MutatorResult<Chromosome<EnumGene<Integer>>> result =
			mutator.mutate(original, 1, new Random());

		final int[] a = original.stream()
			.mapToInt(EnumGene::getAllele)
			.toArray();

		final int[] b = result.getResult().stream()
			.mapToInt(EnumGene::getAllele)
			.toArray();

		Assert.assertEquals(
			result.getMutations(),
			endIndex(a, b) - startIndex(a, b)
		);
	}

	private static int startIndex(final int[] a, final int[] b) {
		for (int i = 0; i < a.length; ++i) {
			if (a[i] != b[i]) return i;
		}
		return 0;
	}

	private static int endIndex(final int[] a, final int[] b) {
		for (int i = a.length; --i >= 0;) {
			if (a[i] != b[i]) return i;
		}
		return 0;
	}

}
