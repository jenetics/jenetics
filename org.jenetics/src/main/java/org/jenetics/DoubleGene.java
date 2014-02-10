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

import static org.jenetics.util.math.random.nextDouble;

import org.jenetics.util.RandomRegistry;

/**
 * Implementation of the NumericGene which holds a 64 bit floating point number.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-02-10 $</em>
 * @since @__version__@
 */
public final class DoubleGene extends NumericGene<Double, DoubleGene> {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public DoubleGene(final Double value, final Double min, final Double max) {
		super(value, min, max);
	}
	
	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 */
	public static DoubleGene of(final double value, final double min, final double max) {
		return new DoubleGene(value, min, max);
	}

	/**
	 * Create a new random {@code DoubleGene}. It is guaranteed that the value
	 * of the {@code DoubleGene} lies in the interval [min, max).
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 */
	public static DoubleGene of(final double min, final double max) {
		return of(nextDouble(RandomRegistry.getRandom(), min, max), min, max);
	}

	@Override
	public DoubleGene newInstance(final Double value) {
		return new DoubleGene(value, _min, _max);
	}

	@Override
	public DoubleGene newInstance() {
		return newInstance(nextDouble(RandomRegistry.getRandom(), _min, _max));
	}

	@Override
	public DoubleGene mean(final DoubleGene that) {
		return newInstance(_value  + (that._value - _value)/2.0);
	}

}
