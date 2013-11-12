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

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-11-12 $</em>
 * @since @__version__@
 */
public interface PopulationStatistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{
	/*
	protected Optimize _optimize = Optimize.MAXIMUM;
	protected int _generation = 0;
	protected Phenotype<G, C> _best = null;
	protected Phenotype<G, C> _worst = null;
	protected int _samples = 0;
	protected double _ageMean = NaN;
	protected double _ageVariance = NaN;
	protected int _killed = 0;
	protected int _invalid = 0;
	*/

	public Phenotype<G, C> getBestPhenotype();

	public Phenotype<G, C> getWorstPhenotype();

	//public Moment<Double> getAgeMoment();

	public double getAgeMean();

	public double getAgeVariance();

	public int getPopulationSize();


	public static class MStats<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{
		public Phenotype<G, C> bestPhenotype;
		public Phenotype<G, C> worstPhenotype;
	}

	public static class Collector<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		implements java.util.stream.Collector<Phenotype<G, C>, MStats<G, C>, PopulationStatistics<G, C>>
	{
		@Override
		public Supplier<MStats<G, C>> supplier() {
			return () -> new MStats<>();
		}

		@Override
		public BiConsumer<MStats<G, C>, Phenotype<G, C>> accumulator() {
			return (ms, pt) -> {
				if (pt.compareTo(ms.bestPhenotype) > 0) {
					ms.bestPhenotype = pt;
				}
				if (pt.compareTo(ms.worstPhenotype) < 0) {
					ms.worstPhenotype = pt;
				}
			};
		}

		@Override
		public BinaryOperator<MStats<G, C>> combiner() {
			return (ms1, ms2) -> {
				final MStats<G, C> result = new MStats<>();
				if (ms1.bestPhenotype.compareTo(ms2.bestPhenotype) > 0) {
					result.bestPhenotype = ms1.bestPhenotype;
				} else {
					result.bestPhenotype = ms2.bestPhenotype;
				}
				if (ms1.worstPhenotype.compareTo(ms2.worstPhenotype) < 0) {
					result.worstPhenotype = ms1.worstPhenotype;
				} else {
					result.worstPhenotype = ms2.worstPhenotype;
				}

				return result;
			};
		}

		@Override
		public Function<MStats<G, C>, PopulationStatistics<G, C>> finisher() {
			return ms -> {
				return (PopulationStatistics<G, C>)null;
			};
		}

		@Override
		public Set<Characteristics> characteristics() {
			return null;
		}
	}

}
