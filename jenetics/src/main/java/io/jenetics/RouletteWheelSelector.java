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
package io.jenetics;

import java.util.Arrays;

import io.jenetics.internal.math.DoubleAdder;
import io.jenetics.stat.DoubleSummary;
import io.jenetics.util.BaseSeq;
import io.jenetics.util.Seq;

/**
 * The roulette-wheel selector is also known as fitness proportional selector,
 * but in the <em>Jenetics</em> library it is implemented as probability selector.
 * The fitness value <i>f<sub>i</sub></i>  is used to calculate the selection
 * probability of individual <i>i</i>.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Roulette_wheel_selection">
 *          Wikipedia: Roulette wheel selection
 *      </a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 5.0
 */
public class RouletteWheelSelector<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	extends ProbabilitySelector<G, N>
{

	public RouletteWheelSelector() {
		this(false);
	}

	protected RouletteWheelSelector(final boolean sorted) {
		super(sorted);
	}

	@Override
	protected double[] probabilities(
		final Seq<Phenotype<G, N>> population,
		final int count
	) {
		assert population != null : "Population must not be null. ";
		assert population.nonEmpty() : "Population is empty.";
		assert count > 0 : "Population to select must be greater than zero. ";

		final double[] fitness = fitnessOf(population);
		sub(fitness, Math.min(DoubleSummary.min(fitness), 0.0));
		final double sum = DoubleAdder.sum(fitness);

		if (eq(sum, 0.0)) {
			Arrays.fill(fitness, 1.0/population.size());
		} else {
			for (int i = fitness.length; --i >= 0;) {
				fitness[i] = fitness[i]/sum;
			}
		}

		return fitness;
	}

	private double[] fitnessOf(final BaseSeq<Phenotype<G, N>> population) {
		final double[] fitness = new double[population.length()];
		for (int i = fitness.length; --i >= 0;) {
			final double fit = population.get(i).fitness().doubleValue();
			fitness[i] = Double.isFinite(fit) ? fit : 0.0;
		}
		return fitness;
	}

	private static void sub(final double[] values, final double subtrahend) {
		if (Double.compare(subtrahend, 0.0) != 0) {
			for (int i = values.length; --i >= 0;) {
				values[i] -= subtrahend;
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
