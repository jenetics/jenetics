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
package org.jenetix;

import static java.util.Objects.requireNonNull;

import org.jenetics.NumericGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
abstract class NumericTreeGene<
	N extends Number & Comparable<? super N>,
	G extends AbstractTreeGene<N, G> & NumericGene<N, G>
>
	extends AbstractTreeGene<N, G> implements NumericGene<N, G>
{

	/**
	 * The minimum value of this {@code BoundedGene}.
	 */
	final N _min;

	/**
	 * The maximum value of this {@code BoundedGene}.
	 */
	final N _max;

	protected NumericTreeGene(
		final N value,
		final N min,
		final N max,
		final int[] children
	) {
		super(value, children);
		_min = requireNonNull(min);
		_max = requireNonNull(max);
	}

	@Override
	public N getMin() {
		return _min;
	}

	@Override
	public N getMax() {
		return _max;
	}

}
