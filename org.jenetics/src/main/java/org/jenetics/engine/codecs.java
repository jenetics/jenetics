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

import java.util.stream.Stream;

import org.jenetics.internal.util.require;

import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class codecs {

	private codecs() {require.noInstance();}


	public static Codec<Integer, IntegerGene> of(final IntRange domain) {
		return Codec.of(
			Genotype.of(IntegerChromosome.of(domain.getMin(), domain.getMax())),
			gt -> ((IntegerChromosome)gt.getChromosome()).intValue()
		);
	}

	public static Codec<int[], IntegerGene> of(
		final IntRange domain,
		final int length
	) {
		return Codec.of(
			Genotype.of(IntegerChromosome.of(
				domain.getMin(), domain.getMax(), length
			)),
			gt -> ((IntegerChromosome)gt.getChromosome()).toArray()
		);
	}

	public static Codec<int[], IntegerGene> of(
		final IntRange domain1,
		final IntRange domain2,
		final IntRange... domainN
	) {
		final IntegerGene[] genes = Stream
			.concat(Stream.of(domain1, domain2), Stream.of(domainN))
			.map(d -> IntegerGene.of(d.getMin(), d.getMin()))
			.toArray(IntegerGene[]::new);

		final int length = 2 + domainN.length;
		return Codec.of(
			Genotype.of(IntegerChromosome.of(genes)),
			gt -> {
				final int[] args = new int[length];
				for (int i = 2 + domainN.length; --i >= 0;) {
					args[i] = gt.getChromosome(i).getGene().intValue();
				}
				return args;
			}
		);
	}

}
