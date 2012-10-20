/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.lang.Math.exp;
import static org.jenetics.util.math.divide;
import static org.jenetics.util.math.max;
import static org.jenetics.util.math.normalize;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import javolution.lang.Immutable;

/**
 * <p>
 * In this <code>Selector</code>, the probability for selection is defined as.
 * </p>
 * <p/><img
 *        src="doc-files/boltzmann-formula1.gif"
 *        alt="P(i)=\frac{\textup{e}^{b\cdot f_i}}{Z}"
 *     />
 * </p>
 * where <i>b</i> controls the selection intensity, and
 * <p/><img
 *        src="doc-files/boltzmann-formula2.gif"
 *        alt="Z=\sum_{j=1}^{n}\textrm{e}^{f_j}"
 *     />.
 * </p>
 *
 * <i>f</i><sub><i>j</i></sub> denotes the fitness value of the
 * <i>j<sup>th</sup></i> individual.
 * <br/>
 * Positive values of <i>b</i> increases the selection probability of the phenotype
 * with high fitness values. Negative values of <i>b</i> increases the selection
 * probability of phenotypes with low fitness values. If <i>b</i> is zero the
 * selection probability of all phenotypes is set to <sup>1</sup>/<sub>N</sub>.
 *
 * @param <G> the gene type.
 * @param <N> the BoltzmannSelector requires a number type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0
 */
public final class BoltzmannSelector<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	extends ProbabilitySelector<G, N>
	implements Immutable
{

	private final double _b;

	/**
	 * Create a new BoltzmannSelector with a default beta of 0.2.
	 */
	public BoltzmannSelector() {
		this(0.2);
	}

	/**
	 * Create a new BolzmanSelector with the given <i>b</i> value. <b>High
	 * absolute values of <i>b</i> can create numerical overflows while
	 * calculating the selection probabilities.</b>
	 *
	 * @param b the <i>b</i> value of this BolzmanSelector
	 */
	public BoltzmannSelector(final double b) {
		_b = b;
	}

	@Override
	protected double[] probabilities(
		final Population<G, N> population,
		final int count
	) {
		assert (population != null) : "Population must not be null. ";
		assert (count > 0) : "Population to select must be greater than zero. ";

		// Copy the fitness values to probabilities arrays.
		final double[] probabilities = new double[population.size()];
		for (int i = population.size(); --i >= 0;) {
			probabilities[i] = population.get(i).getFitness().doubleValue();
		}

		// Scale the fitness values to avoid overflows.
		divide(probabilities, max(probabilities));

		for (int i = probabilities.length; --i >= 0;) {
			probabilities[i] = exp(_b*probabilities[i]);
		}

		normalize(probabilities);

		assert (sum2one(probabilities)) : "Probabilities doesn't sum to one.";
		return probabilities;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_b).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final BoltzmannSelector<?, ?> selector = (BoltzmannSelector<?, ?>)obj;
		return eq(_b, selector._b);
	}

	@Override
	public String toString() {
		return String.format("BoltzmannSelector[b=%f]", _b);
	}

}




