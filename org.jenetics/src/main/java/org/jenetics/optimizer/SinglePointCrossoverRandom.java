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
import org.jenetics.SinglePointCrossover;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class SinglePointCrossoverRandom<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends PROG<SinglePointCrossover<G, C>>
{

	private final Random _random;

	public SinglePointCrossoverRandom(final Random random) {
		_random = requireNonNull(random);
	}

	@Override
	public SinglePointCrossover<G, C> next() {
		return new SinglePointCrossover<>(_random.nextDouble());
	}

}
