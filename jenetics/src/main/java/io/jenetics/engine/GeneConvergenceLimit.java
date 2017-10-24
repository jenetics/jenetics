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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.NumericGene;
import io.jenetics.Phenotype;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.stat.DoubleMoments;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class GeneConvergenceLimit<
	N extends Number & Comparable<? super N>,
	G extends NumericGene<N, G>
>
	implements Predicate<EvolutionResult<G, ?>>
{

	private final Predicate<DoubleMoments> _convergence;
	private final double _convergenceRate;

	GeneConvergenceLimit(
		final Predicate<DoubleMoments> convergence,
		final double convergenceRate
	) {
		if (convergenceRate < 0.0 || convergenceRate > 1.0) {
			throw new IllegalArgumentException(format(
				"The given convergence rate is not in the range [0, 1]: %f",
				convergenceRate
			));
		}
		_convergence = requireNonNull(convergence);
		_convergenceRate = convergenceRate;
	}

	@Override
	public boolean test(final EvolutionResult<G, ?> result) {
		final ISeq<DoubleMoments> stat = statistics(result.getPopulation());

		return stat.stream()
			.filter(_convergence)
			.count() < _convergenceRate*stat.size();
	}

	private final ISeq<DoubleMoments>
	statistics(final Seq<? extends Phenotype<G, ?>> population) {
		final Map<Long, DoubleMomentStatistics> statistics = new HashMap<>();

		for (Phenotype<G, ?> pt : population) {
			final Genotype<G> gt = pt.getGenotype();

			for (int i = 0; i < gt.length(); ++i) {
				final Chromosome<G> ch = gt.getChromosome(i);

				for (int j = 0; j < ch.length(); ++j) {
					statistics
						.computeIfAbsent(((long)i << 32) | (j & 0xffffffffL),
							k -> new DoubleMomentStatistics())
						.accept(ch.getGene(j).doubleValue());
				}
			}
		}

		return statistics.values().stream()
			.map(DoubleMomentStatistics::toDoubleMoments)
			.collect(ISeq.toISeq());
	}

}
