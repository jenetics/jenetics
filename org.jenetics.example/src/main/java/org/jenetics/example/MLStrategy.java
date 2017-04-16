package org.jenetics.example;

import org.jenetics.DoubleGene;
import org.jenetics.Mutator;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.codecs;
import org.jenetics.util.DoubleRange;

/**
 * The (μ, λ) evolution strategy is among the simplest ES algorithms.
 *
 * 1) We begin with a population of (typically) λ number of individuals,
 *    generated randomly.
 * 2) μ is the number of parents which survive. This is realized by setting the
 *    survivors selector to the TruncationSelector.
 * 3) λ is the number of kids that the μ parents make in total. Since only the
 *    offspring are altered, we also use the TruncationSelector for the offspring.
 *
 * @author Franz Wilhelmstötter <franz.wilhelmstoetter@emarsys.com>
 */
public class MLStrategy {

	static double fitness(final double x) {
		return x;
	}

	public static void main(final String[] args) {
		final int μ = 5;
		final int λ = 20;

		final Codec<Double, DoubleGene> codec = codecs
			.ofScalar(DoubleRange.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(MLStrategy::fitness, codec)
			.populationSize(λ)
			.survivorsSize(μ)
			.selector(new TruncationSelector<>(μ))
			.alterers(new Mutator<>())
			.build();
	}

}
