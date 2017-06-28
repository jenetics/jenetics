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
package org.jenetics.example;

import static org.jenetics.engine.limit.byPopulationConvergence;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.jenetics.BitGene;
import org.jenetics.Gene;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Population;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.Selector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionDurations;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.stat.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public class ParallelEvolution {

	public static void main(final String[] args) {
		final Knapsack knapsack = Knapsack.of(15, new Random(123));

		final Engine<BitGene, Double> engine = Engine.builder(knapsack)
			.survivorsSelector(new TournamentSelector<>(5))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.115),
				new SinglePointCrossover<>(0.16))
			.build();

		final AtomicLong count = new AtomicLong();

		final EvolutionResult<BitGene, Double> best = engine.stream()
			.limit(byPopulationConvergence(0.008))
			//.limit(100)
			.parallel()
			.peek(er -> count.incrementAndGet())
			.collect(toBestEvolutionResult());

		System.out.println(count);
		System.out.println(best.getTotalGenerations() + ":::" + best.getGeneration());
		System.out.println(best.getBestPhenotype());
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>>
	toBestEvolutionResult() {
		return Collector.of(
			(Supplier<Merger<G, C>>)Merger::new,
			Merger::accept,
			Merger::combine,
			mm -> mm.getMax() != null
				? mm.getMax().withTotalGenerations(mm.getCount())
				: null
		);
	}

	static final class Merger<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		implements Consumer<EvolutionResult<G, C>>
	{
		private static final AtomicInteger _counter = new AtomicInteger();
		private final int _number = _counter.incrementAndGet();

		private final Selector<G, C> _selector = new TruncationSelector<>();
		private EvolutionResult<G, C> _max;
		private long _count = 0L;

		Merger() {
			System.out.println("CREATE[" + _number + "]");
		}

		public long getCount() {
			return _count;
		}

		public EvolutionResult<G, C> getMax() {
			return _max;
		}

		@Override
		public void accept(final EvolutionResult<G, C> result) {
			System.out.println("ACCEPT[" + _number + "]");
			_max = max(_max, result);
			++_count;
		}

		public Merger<G, C> combine(final Merger<G, C> other) {
			final EvolutionResult<G, C> first = max(_max, other._max);
			final EvolutionResult<G, C> second = min(_max, other._max);
			_count += other._count;

			System.out.println("MERGE[" + _number + ":" + other._number +"] " + _count + ":" + other._count);

			if (first == null || second == null) {
				return this;
			}

			_max = EvolutionResult.of(
				first.getOptimize(),
				merge(first.getPopulation(), second.getPopulation(), first.getOptimize()),
				first.getGeneration(),
				first.getTotalGenerations() + second.getTotalGenerations(),
				first.getDurations().plus(second.getDurations()),
				first.getKillCount() + second.getKillCount(),
				first.getInvalidCount() + second.getInvalidCount(),
				first.getAlterCount() + second.getAlterCount()
			);

			return this;
		}

		private Population<G, C> merge(
			final Population<G, C> a,
			final Population<G, C> b,
			final Optimize opt
		) {
			final Population<G, C> result = new Population<>(a.size());
			if (a.size() == 1) {
				result.add(a.get(0));
			} else {
				result.addAll(_selector.select(a, a.size()/2, opt));
				result.addAll(_selector.select(b, a.size()/2, opt));
			}

			return result;
		}


		private static <T extends Comparable<? super T>> T
		max(final T a, final T b) {
			return a != null ? b != null ? a.compareTo(b) >= 0 ? a : b : a : b;
		}

		private static <T extends Comparable<? super T>> T
		min(final T a, final T b) {
			return a != null ? b != null ? a.compareTo(b) <= 0 ? a : b : a : b;
		}

	}

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	MinMax<EvolutionResult<G, C>> combine(
		final MinMax<EvolutionResult<G, C>> a,
		final MinMax<EvolutionResult<G, C>> b
	) {
		final MinMax<EvolutionResult<G, C>> c = a.combine(b);
		final EvolutionResult<G, C> er = c.getMax();

		System.out.println(
			"MERGE: " +
				a.getMax().getBestFitness() + "::" +
				b.getMax().getBestFitness() + "::" +
				a.getCount() + "::" + b.getCount() + "::" +
				c.getMax().getGeneration() + "::" +
				c.getMax().getTotalGenerations()
		);

		final MinMax<EvolutionResult<G, C>> result = MinMax.of();
		result.accept(er);
		return result;
	}

	/*
	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	MinMax<EvolutionResult<G, C>> combine(
		final MinMax<EvolutionResult<G, C>> a,
		final MinMax<EvolutionResult<G, C>> b
	) {
		final MinMax<EvolutionResult<G, C>> c = a.combine(b);
		final EvolutionResult<G, C> er = c.getMax().withTotalGenerations(
			a.getMax().getGeneration() + b.getMax().getGeneration()
		);

		final MinMax<EvolutionResult<G, C>> result = MinMax.of();
		result.accept(er);
		return result;
	}
	 */

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> combine(
		final EvolutionResult<G, C> a,
		final EvolutionResult<G, C> b
	) {
		//final EvolutionResult<G, C> c = EvolutionResult.of(
		//)

		return null;
	}

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Population<G, C> combine(
		final Population<G, C> a,
		final Population<G, C> b
	) {


		return null;
	}

}
