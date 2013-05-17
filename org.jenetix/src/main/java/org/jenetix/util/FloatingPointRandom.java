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

import org.jscience.mathematics.number.FloatingPoint;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.object;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-05-17 $</em>
 */
public class FloatingPointRandom implements NumberRandom<FloatingPoint> {

	private final Random _random;

	public FloatingPointRandom(final Random random) {
		_random = object.nonNull(random, "Random");
	}

	public FloatingPointRandom() {
		this(RandomRegistry.getRandom());
	}

	@Override
	public FloatingPoint next(final FloatingPoint min, final FloatingPoint max) {
		return next(_random, min, max);
	}

	public static FloatingPoint next(
		final Random random,
		final FloatingPoint min,
		final FloatingPoint max
	) {
		return null;
	}

}
