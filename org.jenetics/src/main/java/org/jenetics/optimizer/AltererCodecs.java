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
package org.jenetics.optimizer;

import org.jenetics.Alterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.MultiPointCrossover;
import org.jenetics.Mutator;
import org.jenetics.NumericGene;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.SinglePointCrossover;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Codec;
import org.jenetics.engine.codecs;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.IntRange;
import org.jenetics.util.Mean;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class AltererCodecs {

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> General(final IntRange crossoverPoints) {
		return Codec.of(
			codecs.ofVector(DoubleRange.of(0, 1), 4),
			Mutator(),
			SinglePointCrossover(),
			MultiplePointCrossover(crossoverPoints),
			SwapMutator(),
			(final double[] a,
			final Alterer<G, C> b,
			final Alterer<G, C> c,
			final Alterer<G, C> d,
			final Alterer<G, C> e) ->
			{
				Alterer<G, C> alterer = Alterer.empty();
				if (a[0] > 0.5) alterer = alterer.andThen(b);
				if (a[1] > 0.5) alterer = alterer.andThen(c);
				if (a[2] > 0.5) alterer = alterer.andThen(d);
				if (a[3] > 0.5) alterer = alterer.andThen(e);
				return alterer;
			}
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> Mutator() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new Mutator<>(gt.getGene().doubleValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> SinglePointCrossover() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new SinglePointCrossover<>(gt.getGene().doubleValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> MultiplePointCrossover(final IntRange points) {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(0, 1),
				DoubleChromosome.of(points.doubleRange())
			),
			gt -> new MultiPointCrossover<>(
				gt.getChromosome(0).getGene().doubleValue(),
				gt.getChromosome(1).getGene().intValue()
			)
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> SwapMutator() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new SwapMutator<>(gt.getGene().doubleValue())
		);
	}

	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<GaussianMutator<G, C>, DoubleGene> GaussianMutator() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new GaussianMutator<>(gt.getGene().doubleValue())
		);
	}

	public static <G extends Gene<?, G> & Mean<G>, C extends Comparable<? super C>>
	Codec<MeanAlterer<G, C>, DoubleGene> MeanAlterer() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new MeanAlterer<>(gt.getGene().doubleValue())
		);
	}

	public static <A, C extends Comparable<? super C>>
	Codec<PartiallyMatchedCrossover<A, C>, DoubleGene>
	PartiallyMatchedCrossover() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new PartiallyMatchedCrossover<>(gt.getGene().doubleValue())
		);
	}

}
