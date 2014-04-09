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

import static java.lang.Double.NaN;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.object.eq;

import java.io.Serializable;
import java.util.concurrent.Executor;

import org.jenetics.internal.util.HashBuilder;

import org.jenetics.stat.Variance;
import org.jenetics.util.Duration;
import org.jenetics.util.FinalReference;
import org.jenetics.util.accumulators;
import org.jenetics.util.accumulators.MinMax;

/**
 * Data object which holds performance indicators of a given {@link Population}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-04-05 $</em>
 */
public class Statistics<G extends Gene<?, G>, C extends Comparable<? super C>>
	implements Serializable
{

	/**
	 * Builder for the Statistics class.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 2.0 &mdash; <em>$Date: 2014-04-05 $</em>
	 */
	public static class Builder<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{
		protected Optimize _optimize = Optimize.MAXIMUM;
		protected int _generation = 0;
		protected Phenotype<G, C> _best = null;
		protected Phenotype<G, C> _worst = null;
		protected int _samples = 0;
		protected double _ageMean = NaN;
		protected double _ageVariance = NaN;
		protected int _killed = 0;
		protected int _invalid = 0;

		/**
		 * Create a new Statistics builder.
		 */
		public Builder() {
		}

		/**
		 * Set the values of this builder with the values of the given
		 * {@code statistics}.
		 *
		 * @param statistics the statistics values. If the {@code statistics}
		 *         is {@code null} nothing is set.
		 * @return this builder instance.
		 */
		public Builder<G, C> statistics(final Statistics<G, C> statistics) {
			if (statistics != null) {
				_optimize = statistics._optimize;
				_generation = statistics._generation;
				_best = statistics._best;
				_worst = statistics._worst;
				_samples = statistics._samples;
				_ageMean = statistics._ageMean;
				_ageVariance = statistics._ageVariance;
				_killed = statistics._killed;
				_invalid = statistics._invalid;
			}
			return this;
		}

		public Builder<G, C> optimize(final Optimize optimize) {
			_optimize = requireNonNull(optimize, "Optimize strategy");
			return this;
		}

		/**
		 * @see Statistics#getGeneration()
		 *
		 * @param generation the current GA generation
		 * @return this builder instance
		 */
		public Builder<G, C> generation(final int generation) {
			_generation = generation;
			return this;
		}

		/**
		 * @see Statistics#getBestPhenotype()
		 *
		 * @param best the best phenotype
		 * @return this builder instance
		 */
		public Builder<G, C> bestPhenotype(final Phenotype<G, C> best) {
			_best = best;
			return this;
		}

		/**
		 * @see Statistics#getWorstPhenotype()
		 *
		 * @param worst the worst phenotype
		 * @return this builder instance
		 */
		public Builder<G, C> worstPhenotype(final Phenotype<G, C> worst) {
			_worst = worst;
			return this;
		}

		/**
		 * @see Statistics#getSamples()
		 *
		 * @param samples the number of samples for the statistics object.
		 * @return this builder instance
		 */
		public Builder<G, C> samples(final int samples) {
			_samples = samples;
			return this;
		}

		/**
		 * @see Statistics#getAgeMean()
		 *
		 * @param ageMean the mean of the population age
		 * @return this builder instance
		 */
		public Builder<G, C> ageMean(final double ageMean) {
			_ageMean = ageMean;
			return this;
		}

		/**
		 * @see Statistics#getAgeVariance()
		 *
		 * @param ageVariance the variance of the population age
		 * @return this builder instance
		 */
		public Builder<G, C> ageVariance(final double ageVariance) {
			_ageVariance = ageVariance;
			return this;
		}

		/**
		 * @see Statistics#getInvalid()
		 *
		 * @param invalid the number of valid individuals
		 * @return this builder instance
		 */
		public Builder<G, C> invalid(final int invalid) {
			_invalid = invalid;
			return this;
		}

		/**
		 * @see Statistics#getKilled()
		 *
		 * @param killed the number of killed individuals
		 * @return this builder instance
		 */
		public Builder<G, C> killed(final int killed) {
			_killed = killed;
			return this;
		}

		/**
		 * Return a new Statistics object with the builder values.
		 *
		 * @return new Statistics object with the builder values.
		 */
		public Statistics<G, C> build() {
			return new Statistics<>(
				_optimize,
				_generation,
				_best,
				_worst,
				_samples,
				_ageMean,
				_ageVariance,
				_killed,
				_invalid
			);
		}
	}

	private static final long serialVersionUID = 3L;

	protected final Optimize _optimize;
	protected final int _generation;
	protected final Phenotype<G, C> _best;
	protected final Phenotype<G, C> _worst;
	protected final int _samples;
	protected final double _ageMean;
	protected final double _ageVariance;
	protected final int _killed;
	protected final int _invalid;

	private final FinalReference<Time> _time = new FinalReference<>(new Time());


	/**
	 * Evaluates statistic values from a given population. The given phenotypes
	 * may be {@code null}
	 *
	 * @param optimize the optimization strategy used
	 * @param generation the generation for this statistics
	 * @param best best phenotype
	 * @param worst worst phenotype
	 * @param samples number of samples of this statistics
	 * @param ageMean the mean value of the individuals age
	 * @param ageVariance the variance value of the individuals ages
	 * @param killed the number of killed individuals
	 * @param invalid the number of invalid individuals
	 */
	protected Statistics(
		final Optimize optimize,
		final int generation,
		final Phenotype<G, C> best,
		final Phenotype<G, C> worst,
		final int samples,
		final double ageMean,
		final double ageVariance,
		final int killed,
		final int invalid
	) {
		_optimize = optimize;
		_generation = generation;
		_best = best;
		_worst = worst;
		_samples = samples;
		_ageMean = ageMean;
		_ageVariance = ageVariance;
		_killed = killed;
		_invalid = invalid;
	}

	/**
	 * Return the optimize strategy of the GA.
	 *
	 * @return the optimize strategy of the GA.
	 */
	public Optimize getOptimize() {
		return _optimize;
	}

	/**
	 * Return the generation of this statistics.
	 *
	 * @return the generation of this statistics.
	 */
	public int getGeneration() {
		return _generation;
	}

	/**
	 * Return the time statistic object which contains the durations of the
	 * different GA execution steps.
	 *
	 * @return the time statistic object.
	 */
	public Time getTime() {
		return _time.get();
	}

	/**
	 * Return the best population Phenotype.
	 *
	 * @return The best population Phenotype.
	 */
	public Phenotype<G, C> getBestPhenotype() {
		return _best;
	}

	/**
	 * Return the worst population Phenotype.
	 *
	 * @return The worst population Phenotype.
	 */
	public Phenotype<G, C> getWorstPhenotype() {
		return _worst;
	}

	/**
	 * Return the best population fitness.
	 *
	 * @return The best population fitness.
	 */
	public C getBestFitness() {
		return _best != null ? _best.getFitness() : null;
	}

	/**
	 * Return the worst population fitness.
	 *
	 * @return The worst population fitness.
	 */
	public C getWorstFitness() {
		return _worst != null ? _worst.getFitness() : null;
	}

	/**
	 * Return the number of samples this statistics has aggregated.
	 *
	 * @return the number of samples this statistics has aggregated.
	 */
	public int getSamples() {
		return _samples;
	}

	/**
	 * Return the average (mean) age of the individuals of the aggregated
	 * population.
	 *
	 * @return the average population age.
	 */
	public double getAgeMean() {
		return _ageMean;
	}

	/**
	 * Return the age variance of the individuals of the aggregated population.
	 *
	 * @return the age variance of the individuals of the aggregated population.
	 */
	public double getAgeVariance() {
		return _ageVariance;
	}

	/**
	 * Return the number of invalid individuals.
	 *
	 * @return the number of invalid individuals.
	 */
	public int getInvalid() {
		return _invalid;
	}

	/**
	 * Return the number of killed individuals.
	 *
	 * @return the number of killed individuals.
	 */
	public int getKilled() {
		return _killed;
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).
				and(_optimize).
				and(_generation).
				and(_ageMean).
				and(_ageVariance).
				and(_best).
				and(_worst).
				and(_invalid).
				and(_samples).
				and(_killed).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final Statistics<?, ?> statistics = (Statistics<?, ?>)obj;
		return eq(_optimize, statistics._optimize) &&
				eq(_generation, statistics._generation) &&
 				eq(_ageMean, statistics._ageMean) &&
				eq(_ageVariance, statistics._ageVariance) &&
				eq(_best, statistics._best) &&
				eq(_worst, statistics._worst) &&
				eq(_invalid, statistics._invalid) &&
				eq(_samples, statistics._samples) &&
				eq(_killed, statistics._killed);
	}

	@Override
	public String toString() {
		final String spattern = "| %28s: %-26s|\n";
		final String fpattern = "| %28s: %-26.11f|\n";
		final String ipattern = "| %28s: %-26d|\n";

		final StringBuilder out = new StringBuilder();
		out.append("+---------------------------------------------------------+\n");
		out.append("|  Population Statistics                                  |\n");
		out.append("+---------------------------------------------------------+\n");
		out.append(format(fpattern, "Age mean", _ageMean));
		out.append(format(fpattern, "Age variance", _ageVariance));
		out.append(format(ipattern, "Samples", _samples));
		out.append(format(spattern, "Best fitness", getBestFitness()));
		out.append(format(spattern, "Worst fitness", getWorstFitness()));
		out.append("+---------------------------------------------------------+");

		return out.toString();
	}

	/**
	 * Class which holds time statistic values.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 2.0 &mdash; <em>$Date: 2014-04-05 $</em>
	 */
	public static final class Time implements Serializable {
		private static final long serialVersionUID = 2L;

		private static final Duration ZERO = Duration.ofNanos(0);

		/**
		 * Create a new time object with zero time values. The time references
		 * can only be set once. If you try to set the values twice an
		 * {@link IllegalStateException} is thrown.
		 */
		public Time() {
		}

		/**
		 * The overall execution time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Duration>
			execution = new FinalReference<>(ZERO);

		/**
		 * The selection time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Duration>
			selection = new FinalReference<>(ZERO);

		/**
		 * The alter time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Duration>
			alter = new FinalReference<>(ZERO);

		/**
		 * Combination time between offspring and survivors.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Duration>
			combine = new FinalReference<>(ZERO);

		/**
		 * The evaluation time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Duration>
			evaluation = new FinalReference<>(ZERO);

		/**
		 * The statistics time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Duration>
			statistics = new FinalReference<>(ZERO);


		@Override
		public int hashCode() {
			return HashBuilder.of(getClass()).
					and(alter).
					and(combine).
					and(evaluation).
					and(execution).
					and(selection).
					and(statistics).value();
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) {
				return true;
			}
			if (object == null || object.getClass() != getClass()) {
				return false;
			}

			final Statistics.Time time = (Statistics.Time)object;
			return eq(alter.get(), time.alter.get()) &&
					eq(combine.get(), time.combine.get()) &&
					eq(evaluation.get(), time.evaluation.get()) &&
					eq(execution.get(), time.execution.get()) &&
					eq(selection.get(), time.selection.get()) &&
					eq(statistics.get(), time.statistics.get());
		}

		@Override
		public String toString() {
			final String pattern = "| %28s: %-26.11f|\n";

			final StringBuilder out = new StringBuilder();
			out.append("+---------------------------------------------------------+\n");
			out.append("|  Time Statistics                                        |\n");
			out.append("+---------------------------------------------------------+\n");
			out.append(format(pattern, "Select time", selection.get().toSeconds()));
			out.append(format(pattern, "Alter time", alter.get().toSeconds()));
			out.append(format(pattern, "Combine time", combine.get().toSeconds()));
			out.append(format(pattern, "Fitness calculation time", evaluation.get().toSeconds()));
			out.append(format(pattern, "Statistics calculation time", statistics.get().toSeconds()));
			out.append(format(pattern, "Overall execution time", execution.get().toSeconds()));
			out.append("+---------------------------------------------------------+");

			return out.toString();
		}
 	}


	/**
	 * Class for calculating the statistics. This class serves also as factory
	 * for the Statistics class.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 2.0 &mdash; <em>$Date: 2014-04-05 $</em>
	 */
	public static class Calculator<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{

		/**
		 * Create a new calculator object.
		 */
		public Calculator() {
		}

		/**
		 * Create a new statistics object from the given {@code population} at
		 * the given {@code generation}.
		 *
		 * @param population the population to aggregate.
		 * @param generation the current GA generation.
		 * @param opt the optimization <i>direction</i>.
		 * @return a new statistics object generated from the given arguments.
		 */
		public Statistics.Builder<G, C> evaluate(
			final Executor executor,
			final Iterable<? extends Phenotype<G, C>> population,
			final int generation,
			final Optimize opt
		) {
			final Builder<G, C> builder = new Builder<>();
			builder.generation(generation);
			builder.optimize(opt);

			final MinMax<Phenotype<G, C>> minMax = new MinMax<>();
			final Variance<Integer> age = new Variance<>();

			accumulators.<Phenotype<G, C>>accumulate(
				executor,
				population,
				minMax,
				age.map(Phenotype.Age(generation))
			);

			builder.bestPhenotype(opt.best(minMax.getMax(), minMax.getMin()));
			builder.worstPhenotype(opt.worst(minMax.getMax(), minMax.getMin()));
			builder.samples((int)minMax.getSamples());
			builder.ageMean(age.getMean());
			builder.ageVariance(age.getVariance());

			return builder;
		}

	}

}
