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
package org.jenetics.internal;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-11-10 $</em>
 * @since @__version__@
 */
public class Statistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private final int _populationSize;
	private final Phenotype<G, C> _bestPhenotype;
	private final Phenotype<G, C> _worstPhenotype;
	private final double _ageMean;
	private final double _ageVariance;

	public Statistics(
		final int populationSize,
		final Phenotype<G, C> bestPhenotype,
		final Phenotype<G, C> worstPhenotype,
		final double ageMean,
		final double ageVariance
	) {
		_populationSize = populationSize;
		_bestPhenotype = bestPhenotype;
		_worstPhenotype = worstPhenotype;
		_ageMean = ageMean;
		_ageVariance = ageVariance;
	}

	public int getPopulationSize() {
		return _populationSize;
	}

	public Phenotype<G, C> getBestPhenotype() {
		return _bestPhenotype;
	}

	public Phenotype<G, C> getWorstPhenotype() {
		return _worstPhenotype;
	}

	public double getAgeMean() {
		return _ageMean;
	}

	public double getAgeVariance() {
		return _ageVariance;
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<Phenotype<G, C>, ?, Statistics<G, C>> collector(
		final Optimize optimize,
		final int currentGeneration
	) {
		return new Collector<Phenotype<G, C>, MStat<G, C>, Statistics<G, C>>() {
			@Override
			public Supplier<MStat<G, C>> supplier() {
				return () -> new MStat<>();
			}

			@Override
			public BiConsumer<MStat<G, C>, Phenotype<G, C>> accumulator() {
				return (ms, pt) -> {
					ms.updateBest(pt, optimize);
					ms.updateWorst(pt, optimize);
					ms.updateAge(pt.getAge(currentGeneration));
				};
			}

			@Override
			public BinaryOperator<MStat<G, C>> combiner() {
				return (ms1, ms2) -> ms1.combine(ms2, optimize);
			}

			@Override
			public Function<MStat<G, C>, Statistics<G, C>> finisher() {
				return MStat::finish;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return EnumSet.of(
					Characteristics.CONCURRENT,
					Characteristics.UNORDERED
				);
			}
		};
	}

	private static final class MStat<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	> {
		Phenotype<G, C> best;
		Phenotype<G, C> worst;
		int samples = 0;
		long ageSum = 0;
		long squareAgeSum = 0;

		void updateBest(final Phenotype<G, C> pt, final Optimize optimize) {
			best = best == null ? best : optimize.best(best, pt);
		}

		void updateWorst(final Phenotype<G, C> pt, final Optimize optimize) {
			worst = worst == null ? worst : optimize.worst(worst, pt);
		}

		void updateAge(final int age) {
			samples += 1;
			ageSum += age;
			squareAgeSum += age*age;
		}

		MStat<G, C> combine(final MStat<G, C> other, final Optimize optimize) {
			final MStat<G, C> result = new MStat<>();
			result.best = optimize.best(best, other.best);
			result.worst = optimize.worst(worst, other.worst);
			result.samples = samples + other.samples;
			result.ageSum = ageSum + other.ageSum;
			result.squareAgeSum = squareAgeSum + other.squareAgeSum;
			return result;
		}

		Statistics<G, C> finish() {
			final double m = samples == 0 ? Double.NaN : ageSum/(double)samples;
			final double v = samples == 0 ? Double.NaN : (
				samples == 1 ? 0 :
				((double)squareAgeSum - (double)ageSum*ageSum/(double)samples)/((double)(samples - 1))
			);

			return new Statistics<G, C>(samples, best, worst, m, v);
		}
	}
}
