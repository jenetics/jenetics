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
package io.jenetics.ext.engine;

import io.jenetics.BoltzmannSelector;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.EliteSelector;
import io.jenetics.ExponentialRankSelector;
import io.jenetics.GaussianMutator;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.IntermediateCrossover;
import io.jenetics.LineCrossover;
import io.jenetics.LinearRankSelector;
import io.jenetics.MeanAlterer;
import io.jenetics.MultiPointCrossover;
import io.jenetics.Mutator;
import io.jenetics.NumericGene;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.Selector;
import io.jenetics.StochasticUniversalSelector;
import io.jenetics.SwapMutator;
import io.jenetics.TournamentSelector;
import io.jenetics.TruncationSelector;
import io.jenetics.UniformCrossover;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.Factory;
import io.jenetics.util.Mean;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MetaCodecs {

	private MetaCodecs() {
	}

	static void foo() {
		Codec<Selector<IntegerGene, Double>, DoubleGene> selector =
			Codecs.ofSelection(
				ofEliteSelector(DoubleRange.of(1, 2)),
				ofBoltzmannSelector(DoubleRange.of(1, 2))
			);
	}

	private static <G extends Gene<?, G>> Genotype<G>
	toGenotype(final Factory<Genotype<G>> factory) {
		return factory instanceof Genotype<G> gt ? gt : factory.newInstance();
	}

	public static <G extends Gene<?, G>, N extends Number & Comparable<? super N>>
	Codec<BoltzmannSelector<G, N>, DoubleGene>
	ofBoltzmannSelector(DoubleRange b) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(b, 1)),
			gt -> new BoltzmannSelector<>(gt.chromosome().gene().doubleValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<EliteSelector<G, C>, DoubleGene>
	ofEliteSelector(DoubleRange sampleSize) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(sampleSize, 1)),
			gt -> new EliteSelector<>(gt.chromosome().gene().intValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<ExponentialRankSelector<G, C>, DoubleGene>
	ofExponentialRankSelector(DoubleRange c) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(c, 1)),
			gt -> new ExponentialRankSelector<>(gt.chromosome().gene().doubleValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<LinearRankSelector<G, C>, DoubleGene>
	ofLinearRankSelector(DoubleRange nminus) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(nminus, 1)),
			gt -> new LinearRankSelector<>(gt.chromosome().gene().doubleValue())
		);
	}

	public static <G extends Gene<?, G>, N extends Number & Comparable<? super N>>
	Codec<RouletteWheelSelector<G, N>, DoubleGene>
	ofRouletteWheelSelector() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1, 1)),
			gt -> new RouletteWheelSelector<>()
		);
	}

	public static <G extends Gene<?, G>, N extends Number & Comparable<? super N>>
	Codec<StochasticUniversalSelector<G, N>, DoubleGene>
	oStochasticUniversalSelector() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1, 1)),
			gt -> new StochasticUniversalSelector<>()
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<TournamentSelector<G, C>, DoubleGene>
	ofTournamentSelector(DoubleRange sampleSize) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(sampleSize, 1)),
			gt -> new TournamentSelector<>(gt.chromosome().gene().intValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<TruncationSelector<G, C>, DoubleGene>
	ofTruncationSelector(DoubleRange n) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(n, 1)),
			gt -> new TruncationSelector<>(gt.chromosome().gene().intValue())
		);
	}

	/* *************************************************************************
	 * Alterers
	 * ************************************************************************/

	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<GaussianMutator<G, C>, DoubleGene>
	ofGaussianMutator(DoubleRange probability) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(probability, 1)),
			gt -> new GaussianMutator<>(gt.chromosome().gene().doubleValue())
		);
	}

	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<IntermediateCrossover<G, C>, DoubleGene>
	ofIntermediateCrossover(DoubleRange probability, DoubleRange p) {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(probability, 1),
				DoubleChromosome.of(p, 1)
			),
			gt -> new IntermediateCrossover<>(
				gt.get(0).gene().doubleValue(),
				gt.get(1).gene().doubleValue()
			)
		);
	}

	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<LineCrossover<G, C>, DoubleGene>
	ofLineCrossover(DoubleRange probability, DoubleRange p) {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(probability, 1),
				DoubleChromosome.of(p, 1)
			),
			gt -> new LineCrossover<>(
				gt.get(0).gene().doubleValue(),
				gt.get(1).gene().doubleValue()
			)
		);
	}

	public static <G extends Gene<?, G> & Mean<G>, C extends Comparable<? super C>>
	Codec<MeanAlterer<G, C>, DoubleGene>
	ofMeanAlterer(DoubleRange probability) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(probability, 1)),
			gt -> new MeanAlterer<>(gt.get(0).gene().doubleValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<MultiPointCrossover<G, C>, DoubleGene>
	ofMultiPointCrossover(DoubleRange probability, DoubleRange n) {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(probability, 1),
				DoubleChromosome.of(n, 1)
			),
			gt -> new MultiPointCrossover<>(
				gt.get(0).gene().doubleValue(),
				gt.get(1).gene().intValue()
			)
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Mutator<G, C>, DoubleGene>
	ofMutator(DoubleRange probability) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(probability, 1)),
			gt -> new Mutator<>(gt.chromosome().gene().doubleValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<SwapMutator<G, C>, DoubleGene>
	ofSwapMutator(DoubleRange probability) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(probability, 1)),
			gt -> new SwapMutator<>(gt.get(0).gene().doubleValue())
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<UniformCrossover<G, C>, DoubleGene>
	ofUniformCrossover(DoubleRange probability, DoubleRange swapProbability) {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(probability, 1),
				DoubleChromosome.of(swapProbability, 1)
			),
			gt -> new UniformCrossover<>(
				gt.get(0).gene().doubleValue(),
				gt.get(1).gene().intValue()
			)
		);
	}

}
