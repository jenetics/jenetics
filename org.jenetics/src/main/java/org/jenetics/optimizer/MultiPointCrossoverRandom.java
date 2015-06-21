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
package org.jenetics.optimizer;

import static java.util.Objects.requireNonNull;

import java.util.Random;

import org.jenetics.Gene;
import org.jenetics.MultiPointCrossover;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MultiPointCrossoverRandom<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
	>
	extends PROG<MultiPointCrossover<G, C>>
{

	private final Random _random;
	private final int _minPointCount;
	private final int _maxPointCount;

	public MultiPointCrossoverRandom(
		final Random random,
		final int minPointCount,
		final int maxPointCount
	) {
		_random = requireNonNull(random);
		_minPointCount = minPointCount;
		_maxPointCount = maxPointCount;
	}

	@Override
	public MultiPointCrossover<G, C> next() {
		return new MultiPointCrossover<>(
			_random.nextDouble(),
			_random.nextInt(_maxPointCount + _minPointCount) + _minPointCount
		);
	}

}
