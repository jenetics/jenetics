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

import org.jenetics.util.ISeq;

/**
 * Abstract numeric chromosome.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-04-16 $</em>
 * @since 1.6
 */
abstract class AbstractNumericChromosome<
	N extends Number & Comparable<? super N>,
	G extends AbstractNumericGene<N, G>
>
	extends AbstractBoundedChromosome<N, G>
	implements NumericChromosome<N, G>
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the {@code genes.length()} is smaller
	 *          than one.
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
	protected AbstractNumericChromosome(final ISeq<? extends G> genes) {
		super(genes);
	}

	@Override
	public int intValue(final int index) {
		return getGene(index).getAllele().intValue();
	}

	@Override
	public int intValue() {
		return intValue(0);
	}

	@Override
	public long longValue(final int index) {
		return getGene(index).getAllele().longValue();
	}

	@Override
	public long longValue() {
		return longValue(0);
	}

	@Override
	public float floatValue(final int index) {
		return getGene(index).getAllele().floatValue();
	}

	@Override
	public float floatValue() {
		return floatValue(0);
	}

	@Override
	public double doubleValue(final int index) {
		return getGene(index).getAllele().doubleValue();
	}

	@Override
	public double doubleValue() {
		return doubleValue(0);
	}

}
