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

/**
 * Base interface for genes where the alleles are bound by a minimum and a
 * maximum value.
 *
 * @implSpec
 * <em>Jenetics</em> requires that the individuals ({@link Genotype} and
 * {@link Phenotype}) are not changed after they have been created. Therefore,
 * all implementations of the {@code BoundedGene} interface must also be
 * <em>immutable</em>.
 *
 * @see BoundedChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 6.0
 */
public interface BoundedGene<
	A extends Comparable<? super A>,
	G extends BoundedGene<A, G>
>
	extends Gene<A, G>, Comparable<G>
{

	/**
	 * Return the allowed min value.
	 *
	 * @return The allowed min value.
	 */
	A min();

	/**
	 * Return the allowed max value.
	 *
	 * @return The allowed max value.
	 */
	A max();

	@Override
	default boolean isValid() {
		return
			allele().compareTo(min()) >= 0 &&
			allele().compareTo(max()) <= 0;
	}

	@Override
	default int compareTo(final G other) {
		return allele().compareTo(other.allele());
	}

	/**
	 * Create a new gene from the given {@code value} and the current bounds.
	 *
	 * @param value the value of the new gene.
	 * @return a new gene with the given value.
	 */
	@Override
	G newInstance(final A value);

}
