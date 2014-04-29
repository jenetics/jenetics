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
package org.jenetics;

import java.util.IntSummaryStatistics;
import java.util.stream.Collector;

import org.jenetics.stat.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-29 $</em>
 * @since 3.0
 */
public final class PopulationStatistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private final Phenotype<G, C> _best;
	private final Phenotype<G, C> _worst;

	private final int _samples;
	private final int _ageMin;
	private final int _ageMax;
	private final long _ageSum;
	private final double _ageMean;

	private PopulationStatistics(
		final Phenotype<G, C> best,
		final Phenotype<G, C> worst,
		final int samples,
		final int ageMin,
		final int ageMax,
		final long ageSum,
		final double ageMean
	) {
		_best = best;
		_worst = worst;
		_samples = samples;
		_ageMin = ageMin;
		_ageMax = ageMax;
		_ageSum = ageSum;
		_ageMean = ageMean;
	}

	public Phenotype<G, C> getWorst() {
		return _worst;
	}

	public Phenotype<G, C> getBest() {
		return _best;
	}

	public int getSamples() {
		return _samples;
	}

	public int getAgeMin() {
		return _ageMin;
	}

	public int getAgeMax() {
		return _ageMax;
	}

	public double getAgeSum() {
		return _ageSum;
	}

	public double getAgeMean() {
		return _ageMean;
	}


	private static final class Calculator<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{
		private final Optimize _optimize;
		private final int _generation;

		private final MinMax<Phenotype<G, C>> _minMax = new MinMax<>();
		private final IntSummaryStatistics _age = new IntSummaryStatistics();

		Calculator(final Optimize optimize, final int generation) {
			_optimize = optimize;
			_generation = generation;
		}

		void accept(final Phenotype<G, C> pt) {
			_minMax.accept(pt);
			_age.accept(pt.getAge(_generation));
		}

		void combine(final Calculator<G, C> other) {
			_minMax.combine(other._minMax);
			_age.combine(other._age);
		}

		PopulationStatistics<G, C> finish() {
			return new PopulationStatistics<G, C>(
				(_minMax.getMin() != null) ?
					_optimize.worst(_minMax.getMin(), _minMax.getMax()) : null,
				(_minMax.getMax() != null) ?
					_optimize.best(_minMax.getMin(), _minMax.getMax()) : null,
				(int)_age.getCount(),
				_age.getMin(),
				_age.getMax(),
				_age.getSum(),
				_age.getAverage()
			);
		}
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<Phenotype<G, C>, ?, PopulationStatistics<G, C>>
	collector(final GeneticAlgorithm<G, C> ga)
	{
		return Collector.<Phenotype<G, C>, Calculator<G, C>, PopulationStatistics<G, C>>of(
			() -> new Calculator<>(ga.getOptimization(), ga.getGeneration()),
			Calculator::accept,
			(a, b) -> {a.combine(b); return a;},
			Calculator::finish
		);
	}
}
