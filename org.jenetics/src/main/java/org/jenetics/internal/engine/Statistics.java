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
package org.jenetics.internal.engine;

import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;

/**
 * Data object which holds performance indicators of a given
 * {@link org.jenetics.Population}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
public class Statistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{
	private static final long serialVersionUID = 4L;

	protected final Optimize _optimize;
	protected final int _generation;
	protected final Phenotype<G, C> _best;
	protected final Phenotype<G, C> _worst;
	protected final int _samples;
	protected final double _ageMean;
	protected final double _ageVariance;
	protected final int _killed;
	protected final int _invalid;

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
		return Hash.of(getClass()).
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
		return Equality.of(this, obj).test(statistics ->
			eq(_optimize, statistics._optimize) &&
			eq(_generation, statistics._generation) &&
			eq(_ageMean, statistics._ageMean) &&
			eq(_ageVariance, statistics._ageVariance) &&
			eq(_best, statistics._best) &&
			eq(_worst, statistics._worst) &&
			eq(_invalid, statistics._invalid) &&
			eq(_samples, statistics._samples) &&
			eq(_killed, statistics._killed)
		);
	}


}
