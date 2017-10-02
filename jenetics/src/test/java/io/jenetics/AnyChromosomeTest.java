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
package io.jenetics;

import org.testng.annotations.Test;

import io.jenetics.util.Factory;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class AnyChromosomeTest extends ChromosomeTester<AnyGene<Integer>> {

	@Override
	protected Factory<Chromosome<AnyGene<Integer>>> factory() {
		return () -> AnyChromosome.of(RandomRegistry.getRandom()::nextInt, 10);
	}

	@Test
	public void create() {
		AnyChromosome.of(
			() -> "foo",
			AnyChromosomeTest::alleleValidator
		);

		AnyChromosome.of(
			() -> "foo",
			AnyChromosomeTest::alleleValidator,
			3
		);

		AnyChromosome.of(
			() -> "foo",
			a -> true,
			AnyChromosomeTest::alleleSeqValidator,
			3
		);

		AnyChromosome.of(
			() -> "foo",
			a -> true,
			a -> true,
			3
		);


		AnyChromosome.of(
			() -> "foo",
			AnyChromosomeTest::alleleValidator,
			AnyChromosomeTest::alleleSeqValidator,
			3
		);
	}

	private static boolean alleleValidator(final String name) {
		return "valid".equals(name);
	}

	private static boolean alleleSeqValidator(final Seq<String> names) {
		return names.forAll(AnyChromosomeTest::alleleValidator);
	}

	@Override
	public void objectSerialize() {
		// Ignore the serialization test. The 'AnyChromosome' shouldn't be
		// Serializable, but the 'AbstractChromosome' is. Will be removed.
	}

}
