/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.lang.Math.abs;
import static org.jenetics.util.math.min;
import static org.jenetics.util.math.pow;
import static org.jenetics.util.math.sum;
import static org.jenetics.util.math.ulpDistance;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Arrays;

import javolution.lang.Immutable;


/**
 * The roulette-wheel selector is also known as fitness proportional selector,
 * but in the <em>Jenetics</em> library it is implemented as probability selector.
 * The fitness value <i>f<sub>i</sub></i>  is used to calculate the selection
 * probability of individual <i>i</i>.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Roulette_wheel_selection">
 *          Wikipedia: Roulette wheel selection
 *      </a>
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class RouletteWheelSelector<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	extends ProbabilitySelector<G, N>
	implements Immutable
{

	private static final long MAX_ULP_DISTANCE = pow(10, 9);

	public RouletteWheelSelector() {
	}

	@Override
	protected double[] probabilities(
		final Population<G, N> population,
		final int count
	) {
		assert(population != null) : "Population can not be null. ";
		assert(count > 0) : "Population to select must be greater than zero. ";

		// Copy the fitness values to probabilities arrays.
		final double[] probabilities = new double[population.size()];
		for (int i = population.size(); --i >= 0;) {
			probabilities[i] = population.get(i).getFitness().doubleValue();
		}

		final double worst = min(probabilities);
		final double sum = sum(probabilities) - worst*population.size();

		if (abs(ulpDistance(sum, 0.0)) > MAX_ULP_DISTANCE) {
			for (int i = population.size(); --i >= 0;) {
				probabilities[i] = (probabilities[i] - worst)/sum;
			}
		} else {
			Arrays.fill(probabilities, 1.0/population.size());
		}

		assert (sum2one(probabilities)) : "Probabilities doesn't sum to one.";
		return probabilities;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return String.format("%s", getClass().getSimpleName());
	}

}





