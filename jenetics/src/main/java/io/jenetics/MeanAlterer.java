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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import io.jenetics.util.Mean;

/**
 * Alters a chromosome by replacing two genes by its mean value.
 *
 * <p>
 * The order ({@link #order()}) of this recombination implementation is two.
 * </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version !__version__!
 */
public class MeanAlterer<
	G extends Gene<?, G> & Mean<G>,
	C extends Comparable<? super C>
>
	extends CombineAlterer<G, C>
{

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}.
	 */
	public MeanAlterer(final double probability) {
		super(Mean::mean, probability);
	}

	/**
	 * Create a new alterer with alter probability of {@code 0.05}.
	 */
	public MeanAlterer() {
		this(0.05);
	}

}
