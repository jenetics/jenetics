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
package org.jenetix.util;

import java.util.Random;

import org.jscience.mathematics.number.Complex;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.object;

/**
 * Random number generator for {@link Complex} values within a defined range.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-05-21 $</em>
 */
public class ComplexRandom implements NumberRandom<Complex> {

	private Random _random;

	public ComplexRandom(final Random random) {
		_random = object.nonNull(random, "Random");
	}

	public ComplexRandom() {
		this(RandomRegistry.getRandom());
	}

	@Override
	public Complex next(final Complex min, final Complex max) {
		return next(_random, min, max);
	}

	public static Complex next(final Random random, final Complex min, final Complex max) {
		if (min.getReal() > max.getReal() || min.getImaginary() > max.getImaginary()) {
			throw new IllegalArgumentException();
		}

		final double real = random.nextDouble()*(max.getReal() - min.getReal()) +
								min.getReal();
		final double imag = random.nextDouble()*(max.getImaginary() - min.getImaginary()) +
								min.getImaginary();

		return Complex.valueOf(real, imag);
	}

}





















