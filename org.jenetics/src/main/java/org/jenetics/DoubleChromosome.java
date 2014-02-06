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

import static org.jenetics.util.object.hashCodeOf;

import java.io.Serializable;

import org.jenetics.util.Array;
import org.jenetics.util.ISeq;

/**
 * Number chromosome implementation which holds 64 bit floating point numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
public class DoubleChromosome
	extends NumericChromosome<Double, DoubleGene>
	implements Serializable
{
	private static final long serialVersionUID = 1L;


	protected DoubleChromosome(final ISeq<DoubleGene> genes) {
		super(genes);
	}

	/**
	 * Create a new random DoubleChromosome.
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param length the length of the chromosome.
	 */
	public DoubleChromosome(final Double min,final Double max,final int length) {
		this(
			new Array<DoubleGene>(length).fill(
				new DoubleGene(min, max)
			).toISeq()
		);
		_valid = true;
	}

	/**
	 * Create a new random chromosome of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 */
	public DoubleChromosome(final Double min, final Double max) {
		this(min, max, 1);
	}

	@Override
	public DoubleChromosome newInstance(final ISeq<DoubleGene> genes) {
		return new DoubleChromosome(genes);
	}

	@Override
	public DoubleChromosome newInstance() {
		return new DoubleChromosome(_min, _max, length());
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof DoubleChromosome && super.equals(obj);
	}
}
