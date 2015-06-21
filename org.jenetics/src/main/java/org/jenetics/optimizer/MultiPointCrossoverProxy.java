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

import java.util.Optional;
import java.util.function.Function;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.MultiPointCrossover;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MultiPointCrossoverProxy<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements AltererProxy<G, C>
{

	private final double _probability;
	private final int _minPointCount;
	private final int _maxPointCount;

	public MultiPointCrossoverProxy(
		final double probability,
		final int minPointCount,
		final int maxPointCount
	) {
		_probability = probability;
		_minPointCount = minPointCount;
		_maxPointCount = maxPointCount;
	}

	@Override
	public Function<double[], Optional<Alterer<G, C>>> factory() {
		return args -> Optional.ofNullable(
			args[0] < _probability
				? new MultiPointCrossover<>(args[1], crossoverPoints(args[2]))
				: null
		);
	}

	private int crossoverPoints(final double value) {
		return (int)(value*(_maxPointCount - _minPointCount)) + _minPointCount;
	}

	@Override
	public int argLength() {
		return 2;
	}
}
