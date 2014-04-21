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

import java.util.Collections;
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
import org.jenetics.stat.Variance;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-21 $</em>
 * @since 3.0
 */
public class Statistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private final Phenotype<G, C> _bestPhenotype;
	private final Phenotype<G, C> _worstPhenotype;
	private final Variance<Integer> _age;

	public Statistics(
		final Phenotype<G, C> bestPhenotype,
		final Phenotype<G, C> worstPhenotype,
		final Variance<Integer> age
	) {
		_bestPhenotype = bestPhenotype;
		_worstPhenotype = worstPhenotype;
		_age = age;
	}

	public Phenotype<G, C> getBestPhenotype() {
		return _bestPhenotype;
	}

	public Phenotype<G, C> getWorstPhenotype() {
		return _worstPhenotype;
	}

	public Variance<Integer> getAge() {
		return _age;
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
				return ms -> new Statistics<>(ms.best, ms.worst, ms.age);
			}

			@Override
			public Set<Characteristics> characteristics() {
				return Collections.unmodifiableSet(EnumSet.of(
					Characteristics.CONCURRENT,
					Characteristics.UNORDERED
				));
			}
		};
	}

	private static final class MStat<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	> {
		Phenotype<G, C> best;
		Phenotype<G, C> worst;
		Variance<Integer> age = new Variance<>();

		void updateBest(final Phenotype<G, C> pt, final Optimize optimize) {
			best = best == null ? pt : optimize.best(best, pt);
		}

		void updateWorst(final Phenotype<G, C> pt, final Optimize optimize) {
			worst = worst == null ? pt : optimize.worst(worst, pt);
		}

		void updateAge(final int value) {
			age.accumulate(value);
		}

		MStat<G, C> combine(final MStat<G, C> other, final Optimize optimize) {
			final MStat<G, C> result = new MStat<>();
			result.best = optimize.best(best, other.best);
			result.worst = optimize.worst(worst, other.worst);
			result.age = age.merge(other.age);
			return result;
		}
	}

}
