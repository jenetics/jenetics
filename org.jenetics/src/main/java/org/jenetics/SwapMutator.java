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

import java.util.Random;

import org.jenetics.util.IndexStream;
import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * The {@code SwapMutation} changes the order of genes in a chromosome, with the
 * hope of bringing related genes closer together, thereby facilitating the
 * production of building blocks. This mutation operator can also be used for
 * combinatorial problems, where no duplicated genes within a chromosome are
 * allowed, e.g. for the TSP.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public class SwapMutator<G extends Gene<?, G>> extends Mutator<G> {

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public SwapMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public SwapMutator(final double probability) {
		super(probability);
	}

	/**
	 * Swaps the genes in the given array, with the mutation probability of this
	 * mutation.
	 */
	@Override
	protected int mutate(final MSeq<G> genes, final double p) {
		int alterations = 0;

		if (genes.length() > 1) {
			final Random random = RandomRegistry.getRandom();
			final IndexStream stream = IndexStream.Random(genes.length(), p, random);

			for (int i = stream.next(); i != -1; i = stream.next()) {
				final int j = random.nextInt(genes.length());
				genes.swap(i, j);

				++alterations;
			}
		}

		return alterations;
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
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		return super.equals(obj);
	}

	@Override
	public String toString() {
		return String.format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
