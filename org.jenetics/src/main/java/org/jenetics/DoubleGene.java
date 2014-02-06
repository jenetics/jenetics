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

import java.util.Comparator;

import org.jenetics.util.RandomRegistry;

/**
 * Implementation of the NumericGene which holds a 64 bit floating point number.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
public final class DoubleGene extends NumericGene<Double, DoubleGene> {

	public DoubleGene(final Double value, final Double min, final Double max) {
		super(value, min, max, new Comparator<Double>() {
			@Override
			public int compare(final Double that, final Double other) {
				return that.compareTo(other);
			}
		});
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
