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
import static org.jenetics.internal.util.object.eq;

import java.util.concurrent.Executor;

import org.jenetics.internal.util.HashBuilder;

import org.jenetics.stat.Variance;
import org.jenetics.util.accumulators;
import org.jenetics.util.accumulators.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-04-11 $</em>
 */
public class NumberStatistics<
	G extends Gene<?, G>,
	R extends Number & Comparable<? super R>
>
	extends Statistics<G, R>
{

	/**
	 * Builder for the NumberStatistics class.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 2.0 &mdash; <em>$Date: 2014-04-11 $</em>
	 */
	public static class Builder<
		G extends Gene<?, G>,
		R extends Number & Comparable<? super R>
	>
		extends Statistics.Builder<G, R>
	{
		protected double _fitnessMean = NaN;
		protected double _fitnessVariance = NaN;
		protected double _standardError = NaN;

		/**
		 * Create a new NumberStatistics builder.
		 */
		public Builder() {
		}

		@Override
		public Builder<G, R> statistics(final Statistics<G, R> statistics) {
			super.statistics(statistics);
			return this;
		}

		/**
		 * Set the values of this builder with the values of the given
		 * {@code statistics}.
		 *
		 * @param statistics the statistics values. If the {@code statistics}
		 *         is {@code null} nothing is set.
		 * @return this builder instance.
		 */
		public Builder<G, R> statistics(final NumberStatistics<G, R> statistics) {
			if (statistics != null) {
				super.statistics(statistics);
				_fitnessMean = statistics._fitnessMean;
				_fitnessVariance = statistics._fitnessVariance;
				_standardError = statistics._standardError;
			}
			return this;
		}

		/**
		 * @see NumberStatistics#getFitnessMean()
		 *
		 * @param fitnessMean the mean of the fitness value
		 * @return this builder instance
		 */
		public Builder<G, R> fitnessMean(final double fitnessMean) {
			_fitnessMean = fitnessMean;
			return this;
		}

		/**
		 * @see NumberStatistics#getFitnessVariance()
		 *
		 * @param fitnessVariance the variance of the fitness value
		 * @return this builder instance
		 */
		public Builder<G, R> fitnessVariance(final double fitnessVariance) {
			_fitnessVariance = fitnessVariance;
			return this;
		}

		/**
		 * @see NumberStatistics#getStandardError()
		 *
		 * @param standardError the standard error of the fitness mean value
		 * @return this builder instancett
		 */
		public Builder<G, R> standardError(final double standardError) {
			_standardError = standardError;
			return this;
		}

		@Override
		public NumberStatistics<G, R> build() {
			return new NumberStatistics<>(
				_optimize,
				_generation,
				_best,
				_worst,
				_fitnessMean,
				_fitnessVariance,
				_samples,
				_ageMean,
				_ageVariance,
				_standardError,
				_killed,
				_invalid
			);
		}
	}

	private static final long serialVersionUID = 3L;

	protected final double _fitnessMean;
	protected final double _fitnessVariance;
	protected final double _standardError;

	protected NumberStatistics(
		final Optimize optimize,
		final int generation,
		final Phenotype<G, R> best,
		final Phenotype<G, R> worst,
		final double fitnessMean,
		final double fitnessVariance,
		final int samples,
		final double ageMean,
		final double ageVariance,
		final double errorOfMean,
		final int killed,
		final int invalid
	) {
		super(
			optimize,
			generation,
			best,
			worst,
			samples,
			ageMean,
			ageVariance,
			killed,
			invalid
		);

		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
		_standardError = errorOfMean;
	}

	/**
	 * Return the mean of the fitness values.
	 *
	 * @return the mean of the fitness values.
	 */
	public double getFitnessMean() {
		return _fitnessMean;
	}

	/**
	 * Return the variance of the fitness values.
	 *
	 * @return the variance of the fitness values.
	 */
	public double getFitnessVariance() {
		return _fitnessVariance;
	}

	/**
	 * Return the <a href="https://secure.wikimedia.org/wikipedia/en/wiki/Standard_error_%28statistics%29">
	 * Standard error
	 * </a> of the calculated fitness mean.
	 *
	 * @return the standard error of the calculated fitness mean.
	 */
	public double getStandardError() {
		return _standardError;
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).
				and(super.hashCode()).
				and(_fitnessMean).
				and(_fitnessVariance).
				and(_standardError).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof NumberStatistics<?, ?>)) {
			return false;
		}

		final NumberStatistics<?, ?> statistics = (NumberStatistics<?, ?>) obj;
		return eq(statistics._fitnessMean, _fitnessMean) &&
				eq(statistics._fitnessVariance, _fitnessVariance) &&
				eq(statistics._standardError, _standardError) &&
				super.equals(obj);
	}

	@Override
	public String toString() {
		final String fpattern = "| %28s: %-26.11f|\n";

		final StringBuilder out = new StringBuilder();
		out.append(super.toString()).append("\n");
		out.append("+---------------------------------------------------------+\n");
		out.append("|  Fitness Statistics                                     |\n");
		out.append("+---------------------------------------------------------+\n");
		out.append(format(fpattern, "Fitness mean", _fitnessMean));
		out.append(format(fpattern, "Fitness variance", _fitnessVariance));
		out.append(format(fpattern, "Fitness error of mean", _standardError));
		out.append("+---------------------------------------------------------+");

		return out.toString();
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 2.0 &mdash; <em>$Date: 2014-04-11 $</em>
	 */
	public static class Calculator<
		G extends Gene<?, G>,
		R extends Number & Comparable<? super R>
	>
		extends Statistics.Calculator<G, R>
	{

		public Calculator() {
		}

		@Override
		public NumberStatistics.Builder<G, R> evaluate(
			final Executor executor,
			final Iterable<? extends Phenotype<G, R>> population,
			final int generation,
			final Optimize opt
		) {
			final Builder<G, R> builder = new Builder<>();
			builder.generation(generation);
			builder.optimize(opt);

			final MinMax<Phenotype<G, R>> minMax = new MinMax<>();
			final Variance<Integer> age = new Variance<>();
			final Variance<R> fitness = new Variance<>();

			accumulators.accumulate(
					executor,
					population,
					minMax,
					age.map(Phenotype.Age(generation)),
					fitness.map(Phenotype.<R>Fitness())
				);
			builder.bestPhenotype(opt.best(minMax.getMax(), minMax.getMin()));
			builder.worstPhenotype(opt.worst(minMax.getMax(), minMax.getMin()));
			builder.fitnessMean(fitness.getMean());
			builder.fitnessVariance(fitness.getVariance());
			builder.samples((int)minMax.getSamples());
			builder.ageMean(age.getMean());
			builder.ageVariance(age.getVariance());
			builder.standardError(fitness.getStandardError());

			return builder;
		}
	}

}
