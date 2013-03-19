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

import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.math;
import org.jenetics.util.object;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-03-19 $</em>
 */
public class Integer64Random implements NumberRandom<Integer64> {

	private final Random _random;

	public Integer64Random(final Random random) {
		_random = object.nonNull(random, "Random");
	}

	public Integer64Random() {
		this(RandomRegistry.getRandom());
	}

	@Override
	public Integer64 next(final Integer64 min, final Integer64 max) {
		return next(_random, min, max);
	}

	public static Integer64 next(
		final Random random,
		final Integer64 min,
		final Integer64 max
	) {
		return Integer64.valueOf(
			math.random.nextLong(random, min.longValue(), max.longValue())
		);
	}

}
