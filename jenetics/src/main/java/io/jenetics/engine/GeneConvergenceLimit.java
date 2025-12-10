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
 * A termination method that stops the evolution when a user-specified percentage
 * of the genes that make up a {@code Genotype} are deemed as converged. A gene
 * is deemed as converged when the average value of that gene across all the
 * genotypes in the current population is less than a user-specified percentage
 * away from the maximum gene value across the genotypes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.2
 * @since 4.0
 */
final class GeneConvergenceLimit<G extends NumericGene<?, G>>
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
		final ISeq<DoubleMoments> stat = statistics(result.population());

		return result.totalGenerations() <= 1 ||
			stat.stream()
				.filter(_convergence)
				.count() <= _convergenceRate*stat.size();
	}

	private static <G extends NumericGene<?, G>> ISeq<DoubleMoments>
	statistics(final Seq<? extends Phenotype<G, ?>> population) {
		final Map<Long, DoubleMomentStatistics> statistics = new HashMap<>();

		for (Phenotype<G, ?> pt : population) {
			final Genotype<G> gt = pt.genotype();

			for (int i = 0; i < gt.length(); ++i) {
				final Chromosome<G> ch = gt.get(i);

				for (int j = 0; j < ch.length(); ++j) {
					statistics
						.computeIfAbsent(((long)i << 32) | (j & 0xffffffffL),
							_ -> new DoubleMomentStatistics())
						.accept(ch.get(j).doubleValue());
				}
			}
		}

		return statistics.values().stream()
			.map(DoubleMomentStatistics::toDoubleMoments)
			.collect(ISeq.toISeq());
	}

}
