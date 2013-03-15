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
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
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
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-03-15 $</em>
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





















